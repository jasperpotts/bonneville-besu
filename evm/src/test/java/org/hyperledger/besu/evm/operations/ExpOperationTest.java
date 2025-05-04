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

import org.hyperledger.besu.evm.gascalculator.CancunGasCalculator;
import org.hyperledger.besu.evm.operation.ExpOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;

import java.math.BigInteger;
import java.util.SplittableRandom;
import java.util.stream.Stream;

import org.apache.tuweni.bytes.Bytes;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ExpOperationTest {
  @Test
  void testExpOperationSmallExponent() {
    final Word base = Word.of(2);
    final Word exponent = Word.of(3);
    final Word expected = Word.of(8); // 2^3 % 2^256 = 8

    final var frame =
        new TestMessageFrameBuilder().pushStackItem(exponent).pushStackItem(base).build();
    ExpOperation.staticOperation(frame, new CancunGasCalculator());
    final var result = frame.stack().popUnsafe();

    assertThat(result)
        .withFailMessage("Expected %s ^ %s = %s but got %s", base, exponent, expected, result)
        .isEqualTo(expected);
  }

  @Test
  void testExpOperation63BitOverflow() {
    final long baseValue = 1L << 62; // 2^62
    final long expValue = 2; // Small exponent to cause overflow
    final Word base = Word.of(baseValue);
    final Word exponent = Word.of(expValue);
    final Word expected = Word.of(BigInteger.valueOf(baseValue).pow(2)); // 2^62 ^ 2 = 2^124

    final var frame =
        new TestMessageFrameBuilder().pushStackItem(exponent).pushStackItem(base).build();
    ExpOperation.staticOperation(frame, new CancunGasCalculator());
    final var result = frame.stack().popUnsafe();

    assertThat(result)
        .withFailMessage("Expected %s ^ %s = %s but got %s", base, exponent, expected, result)
        .isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("randomTestCases")
  void compareOriginal(final ExpTestCase testCase) {
    final Bytes operand1 = testCase.operand1;
    final Bytes operand2 = testCase.operand2;

    final var expected = originalExpLogic(operand1, operand2);
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(Word.of(operand2.toArrayUnsafe()))
            .pushStackItem(Word.of(operand1.toArrayUnsafe()))
            .build();

    ExpOperation.staticOperation(frame, new CancunGasCalculator());
    final var actual = frame.stack().popUnsafe();
    assertEquals(Word.of(expected.toArrayUnsafe()), actual);
  }

  private static Stream<ExpTestCase> randomTestCases() {
    SplittableRandom random = new SplittableRandom(3052025);
    return Stream.generate(
            () -> {
              // Generate random operand1, operand2, and modulus (up to 32 bytes)
              Bytes operand1 = generateRandomBytes(random);
              Bytes operand2 = generateRandomBytes(random);
              Bytes modulus = generateRandomBytes(random);

              // Return a test case object
              return new ExpTestCase(operand1, operand2, modulus);
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
  record ExpTestCase(Bytes operand1, Bytes operand2, Bytes modulus) {}

  /**
   * The original Besu implementation of the EXP operation.
   *
   * @param number the base
   * @param power the exponent
   * @return the result of the exponentiation
   */
  public static Bytes originalExpLogic(@NonNull final Bytes number, @NonNull final Bytes power) {
    byte[] numberBytes = number.toArrayUnsafe();
    BigInteger numBI = numberBytes.length > 0 ? new BigInteger(1, numberBytes) : BigInteger.ZERO;
    byte[] powBytes = power.toArrayUnsafe();
    BigInteger powBI = powBytes.length > 0 ? new BigInteger(1, powBytes) : BigInteger.ZERO;

    final BigInteger result = numBI.modPow(powBI, BigInteger.TWO.pow(256));

    byte[] resultArray = result.toByteArray();
    int length = resultArray.length;
    if (length > 32) {
      return Bytes.wrap(resultArray, length - 32, 32);
    } else {
      return Bytes.wrap(resultArray);
    }
  }
}
