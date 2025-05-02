/*
 * Copyright Hyperledger Besu Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.besu.evm.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hyperledger.besu.evm.operation.AddModOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.SplittableRandom;
import java.util.stream.Stream;

import org.apache.tuweni.bytes.Bytes;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AddModOperationTest {
  static final BigInteger MASK_256_BITS = BigInteger.valueOf(2).pow(256).subtract(BigInteger.ONE);

  @ParameterizedTest
  @MethodSource("randomTestCases")
  void compareOriginal(final AddModTestCase testCase) {
    final Bytes operand1 = testCase.operand1;
    final Bytes operand2 = testCase.operand2;
    final Bytes modulus = testCase.modulus;

    Assumptions.assumeFalse(modulus.isZero());

    final var expected = originalAddMod(operand1, operand2, modulus);

    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(Word.of(modulus.toArrayUnsafe()))
            .pushStackItem(Word.of(operand2.toArrayUnsafe()))
            .pushStackItem(Word.of(operand1.toArrayUnsafe()))
            .build();

    AddModOperation.staticOperation(frame);
    final var actual = frame.stack().popUnsafe();
    assertEquals(expected, Bytes.wrap(actual.asArray32()));
  }

  private static Stream<AddModTestCase> randomTestCases() {
    SplittableRandom random = new SplittableRandom();
    return Stream.generate(
            () -> {
              // Generate random operand1, operand2, and modulus (up to 32 bytes)
              Bytes operand1 = generateRandomBytes(random);
              Bytes operand2 = generateRandomBytes(random);
              Bytes modulus = generateRandomBytes(random);

              // Return a test case object
              return new AddModTestCase(operand1, operand2, modulus);
            })
        .limit(10_000);
  }

  private static Bytes generateRandomBytes(final SplittableRandom random) {
    final int len = random.nextInt(33);
    byte[] bytes = new byte[len];
    random.nextBytes(bytes);
    return Bytes.wrap(bytes);
  }

  // A helper class to store test case parameters
  static class AddModTestCase {
    final Bytes operand1;
    final Bytes operand2;
    final Bytes modulus;

    AddModTestCase(final Bytes operand1, final Bytes operand2, final Bytes modulus) {
      this.operand1 = operand1;
      this.operand2 = operand2;
      this.modulus = modulus;
    }

    @Override
    public String toString() {
      return "AddModTestCase{"
          + "operand1="
          + operand1
          + ", operand2="
          + operand2
          + ", modulus="
          + modulus
          + '}';
    }
  }

  /**
   * The original Besu implementation of the ADDMOD operation.
   *
   * @param operand1 the first operand
   * @param operand2 the second operand
   * @param modulus the modulus
   * @return the result of the addition modulo the modulus
   */
  public static Bytes originalAddMod(
      @NonNull final Bytes operand1, @NonNull final Bytes operand2, @NonNull final Bytes modulus) {
    if (modulus.isZero()) {
      return Bytes.EMPTY;
    } else {
      BigInteger b0 = new BigInteger(1, operand1.toArrayUnsafe());
      BigInteger b1 = new BigInteger(1, operand2.toArrayUnsafe());
      BigInteger b2 = new BigInteger(1, modulus.toArrayUnsafe());
      BigInteger result = b0.add(b1).mod(b2).and(MASK_256_BITS);
      Bytes resultBytes = Bytes.wrap(result.toByteArray());
      if (resultBytes.size() > 32) {
        resultBytes = resultBytes.slice(resultBytes.size() - 32, 32);
      }
      final byte[] padding = new byte[32 - resultBytes.size()];
      Arrays.fill(padding, result.signum() < 0 ? (byte) 0xFF : 0x00);
      return Bytes.concatenate(Bytes.wrap(padding), resultBytes);
    }
  }
}
