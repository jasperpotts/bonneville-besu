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
import org.hyperledger.besu.evm.operation.SubOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SubOperationTest {
  private final GasCalculator gasCalculator = new BerlinGasCalculator();
  private static final Random RANDOM = new Random(4302025L);

  // Test subtract function
  @ParameterizedTest
  @MethodSource("provideSubtractionTestCases")
  void testSubtraction(final Word a, final Word b, final Word expected) {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(b) // Second operand (b)
            .pushStackItem(a) // First operand (a)
            .build();
    final var op = new SubOperation(gasCalculator);
    final var result = op.executeFixedCostOperation(frame, mock(EVM.class));
    final var difference = frame.stack().popUnsafe();
    assertThat(difference).isEqualTo(expected);
    assertThat(result.getGasCost()).isEqualTo(3);
    assertThat(result.getHaltReason()).isNull();
    assertThat(result.getPcIncrement()).isEqualTo(1);
  }

  // Provide test cases for subtract
  static Stream<Arguments> provideSubtractionTestCases() {
    return Stream.of(
            // Basic subtraction
            Arguments.of(Word.ZERO, Word.ZERO, Word.ZERO),
            Arguments.of(Word.of(2), Word.ONE, Word.ONE),
            Arguments.of(Word.of(300), Word.of(200), Word.of(100)),

            // Edge cases: max 256-bit value and underflow
            Arguments.of(Word.MAX, Word.ZERO, Word.MAX),
            Arguments.of(Word.ZERO, Word.ONE, Word.MAX), // Underflow: 0 - 1 = 2^256 - 1
            Arguments.of(
                    Word.ONE, Word.of(2), Word.MAX), // Underflow: 1 - 2 = 2^256

            // Large numbers
            Arguments.of(
                Word.of(new BigInteger("9876543210987654321098765432109876543210")),
                Word.of(new BigInteger("1234567890123456789012345678901234567890")),
                Word.of(new BigInteger("8641975320864197532086419753208641975320"))),

            // Random large numbers
            Arguments.of(
                generateRandomU256(), generateRandomU256(), null) // Expected computed in test
            )
        .map(
            args -> {
              // Compute expected for random cases
              if (args.get()[2] == null) {
                Word a = (Word) args.get()[0];
                Word b = (Word) args.get()[1];
                Word expected = a.subtract(b);
                return Arguments.of(a, b, expected);
              }
              return args;
            });
  }

  // Helper: Generate a random 256-bit number
  private static Word generateRandomU256() {
    byte[] bytes = new byte[32];
    RANDOM.nextBytes(bytes);
    return Word.of(bytes);
  }
}
