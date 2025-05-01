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
import static org.mockito.Mockito.mock;

import org.hyperledger.besu.evm.EVM;
import org.hyperledger.besu.evm.gascalculator.BerlinGasCalculator;
import org.hyperledger.besu.evm.gascalculator.GasCalculator;
import org.hyperledger.besu.evm.operation.ModOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ModOperationTest {
  private final GasCalculator gasCalculator = new BerlinGasCalculator();
  private static final BigInteger TWO_POW_256 = BigInteger.ONE.shiftLeft(256);
  private static final BigInteger MAX_U256 = TWO_POW_256.subtract(BigInteger.ONE);
  private static final Random RANDOM = new Random(4302025L);

  // Test BigInteger modulo function
  @Test
  void testModByZero() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.ZERO) // Denominator (b)
            .pushStackItem(BigInteger.TEN) // Numerator (a)
            .build();
    final var op = new ModOperation(gasCalculator);
    final var result = op.executeFixedCostOperation(frame, mock(EVM.class));
    final var remainder = frame.stack().popUnsafe();
    assertThat(remainder).isEqualTo(BigInteger.ZERO);
    assertThat(result.getGasCost()).isEqualTo(5);
    assertThat(result.getHaltReason()).isNull();
    assertThat(result.getPcIncrement()).isEqualTo(1);
  }

  // Test BigInteger modulo function
  @Test
  void testModByZeroSigned() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(-1, new byte[32])) // Denominator (b)
            .pushStackItem(BigInteger.TEN) // Numerator (a)
            .build();
    final var op = new ModOperation(gasCalculator);
    final var result = op.executeFixedCostOperation(frame, mock(EVM.class));
    final var remainder = frame.stack().popUnsafe();
    assertThat(remainder).isEqualTo(BigInteger.ZERO);
    assertThat(result.getGasCost()).isEqualTo(5);
    assertThat(result.getHaltReason()).isNull();
    assertThat(result.getPcIncrement()).isEqualTo(1);
  }

  // Test BigInteger modulo function
  @Test
  void testModByZeroSignedPos() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(1, new byte[32])) // Denominator (b)
            .pushStackItem(BigInteger.TEN) // Numerator (a)
            .build();
    final var op = new ModOperation(gasCalculator);
    final var result = op.executeFixedCostOperation(frame, mock(EVM.class));
    final var remainder = frame.stack().popUnsafe();
    assertThat(remainder).isEqualTo(BigInteger.ZERO);
    assertThat(result.getGasCost()).isEqualTo(5);
    assertThat(result.getHaltReason()).isNull();
    assertThat(result.getPcIncrement()).isEqualTo(1);
  }

  // Test BigInteger modulo function
  @ParameterizedTest
  @MethodSource("provideBigIntegerTestCases")
  void testModBigInteger(final BigInteger a, final BigInteger b, final BigInteger expected) {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(b) // Denominator (b)
            .pushStackItem(a) // Numerator (a)
            .build();
    final var op = new ModOperation(gasCalculator);
    final var result = op.executeFixedCostOperation(frame, mock(EVM.class));
    final var remainder = frame.stack().popUnsafe();
    assertThat(remainder).isEqualTo(expected);
    assertThat(result.getGasCost()).isEqualTo(5);
    assertThat(result.getHaltReason()).isNull();
    assertThat(result.getPcIncrement()).isEqualTo(1);
  }

  // Provide test cases for BigInteger modulo
  static Stream<Arguments> provideBigIntegerTestCases() {
    return Stream.of(
            // Basic modulo
            Arguments.of(BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO),
            Arguments.of(BigInteger.TEN, BigInteger.valueOf(3), BigInteger.ONE), // 10 % 3 = 1
            Arguments.of(
                BigInteger.valueOf(17), BigInteger.valueOf(5), BigInteger.valueOf(2)), // 17 % 5 = 2

            // Edge cases: zero denominator
            Arguments.of(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO), // 0 % 0 = 0
            Arguments.of(BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO), // 10 % 0 = 0
            Arguments.of(MAX_U256, BigInteger.ZERO, BigInteger.ZERO), // (2^256 - 1) % 0 = 0

            // Edge cases: max 256-bit value
            Arguments.of(MAX_U256, BigInteger.ONE, BigInteger.ZERO), // (2^256 - 1) % 1 = 0
            Arguments.of(MAX_U256, BigInteger.TWO, BigInteger.ONE), // (2^256 - 1) % 2 = 1
            Arguments.of(MAX_U256, MAX_U256, BigInteger.ZERO), // (2^256 - 1) % (2^256 - 1) = 0

            // Large numbers
            Arguments.of(
                new BigInteger("9876543210987654321098765432109876543210"),
                new BigInteger("1234567890123456789012345678901234567890"),
                new BigInteger("9876543210987654321098765432109876543210")
                    .mod(new BigInteger("1234567890123456789012345678901234567890"))),

            // Random large numbers
            Arguments.of(
                generateRandomU256(), generateRandomU256(), null) // Expected computed in test
            )
        .map(
            args -> {
              // Compute expected for random cases
              if (args.get()[2] == null) {
                BigInteger a = (BigInteger) args.get()[0];
                BigInteger b = (BigInteger) args.get()[1];
                BigInteger expected = b.signum() == 0 ? BigInteger.ZERO : a.mod(b);
                return Arguments.of(a, b, expected);
              }
              return args;
            });
  }

  // Helper: Generate a random 256-bit number
  private static BigInteger generateRandomU256() {
    byte[] bytes = new byte[32];
    RANDOM.nextBytes(bytes);
    // Ensure non-negative and within 256 bits
    return new BigInteger(1, bytes).mod(TWO_POW_256);
  }
}
