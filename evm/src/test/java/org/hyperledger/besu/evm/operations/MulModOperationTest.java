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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hyperledger.besu.evm.operation.MulModOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.SplittableRandom;
import java.util.stream.Stream;

import org.apache.tuweni.bytes.Bytes;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class MulModOperationTest {
  @Test
  void testOverflow63Bits() {
    // This test scenario was found during development
    final var u0 = Word.ofHexString("0x04");
    final var u1 = Word.ofHexString("0x5ab9db656ae92d81");
    final var u2 = Word.ofHexString("0x64");

    final var frame =
        new TestMessageFrameBuilder().pushStackItem(u2).pushStackItem(u1).pushStackItem(u0).build();

    final var expected = Word.ofHexString("0x5c");
    MulModOperation.staticOperation(frame);
    final var actual = frame.stack().popUnsafe();
    assertThat(actual).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("randomTestCases")
  void compareOriginal(final MulModTestCase testCase) {
    final Bytes operand1 = testCase.operand1;
    final Bytes operand2 = testCase.operand2;
    final Bytes modulus = testCase.modulus;

    Assumptions.assumeFalse(modulus.isZero());

    final var expected = originalMulMod(operand1, operand2, modulus);

    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(Word.of(modulus.toArrayUnsafe()))
            .pushStackItem(Word.of(operand2.toArrayUnsafe()))
            .pushStackItem(Word.of(operand1.toArrayUnsafe()))
            .build();

    MulModOperation.staticOperation(frame);
    final var actual = frame.stack().popUnsafe();
    assertEquals(expected, Bytes.wrap(actual.asArray32()));
  }

  private static Stream<MulModTestCase> randomTestCases() {
    SplittableRandom random = new SplittableRandom(3052025);
    return Stream.generate(
            () -> {
              // Generate random operand1, operand2, and modulus (up to 32 bytes)
              Bytes operand1 = generateRandomBytes(random);
              Bytes operand2 = generateRandomBytes(random);
              Bytes modulus = generateRandomBytes(random);

              // Return a test case object
              return new MulModTestCase(operand1, operand2, modulus);
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
  record MulModTestCase(Bytes operand1, Bytes operand2, Bytes modulus) {}

  /**
   * The original Besu implementation of the MULMOD operation.
   *
   * @param operand1 the first operand
   * @param operand2 the second operand
   * @param modulus the modulus
   * @return the result of the multiplication modulo the modulus
   */
  public static Bytes originalMulMod(
      @NonNull final Bytes operand1, @NonNull final Bytes operand2, @NonNull final Bytes modulus) {
    if (modulus.isZero()) {
      return Bytes.EMPTY;
    } else {
      BigInteger b0 = new BigInteger(1, operand1.toArrayUnsafe());
      BigInteger b1 = new BigInteger(1, operand2.toArrayUnsafe());
      BigInteger b2 = new BigInteger(1, modulus.toArrayUnsafe());

      BigInteger result = b0.multiply(b1).mod(b2);
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
