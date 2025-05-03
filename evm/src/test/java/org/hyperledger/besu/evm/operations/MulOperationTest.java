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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.MulOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;

import java.math.BigInteger;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class MulOperationTest extends BaseNumericTest {

  @ParameterizedTest
  @MethodSource("provideWordTestCases")
  void testMulOperation(final Word a, final Word b) {
    final Word expected = a.multiply(b);
    final var frame = new TestMessageFrameBuilder().pushStackItem(b).pushStackItem(a).build();
    MulOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result)
        .withFailMessage("Expected %s * %s = %s but got %s", a, b, expected, result)
        .isEqualTo(expected);
  }

  @Test
  void testMulOperation() {
    final var frame = new TestMessageFrameBuilder().pushStackItem(TWO).pushStackItem(TEN).build();
    MulOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.of(20));
  }

  @Test
  void testMoreMulOperations() {
    final Random rand = new Random(129828978189L);
    for (int i = 0; i < 100; i++) {
      final long a = rand.nextLong(0, Long.MAX_VALUE / 10_000);
      final long b = rand.nextLong(0, 10_000);
      final long expected = a * b;
      final var frame =
          new TestMessageFrameBuilder().pushStackItem(Word.of(a)).pushStackItem(Word.of(b)).build();
      MulOperation.staticOperation(frame);
      final var result = frame.stack().popUnsafe();
      assertThat(result).isEqualTo(Word.of(expected));
    }
  }

  @Test
  void testOverflow63Bit() {
    // Choose two 63-bit values: 2^62 and 2^62
    final long a = 1L << 62; // 2^62 = 4,611,686,018,427,387,904
    final long b = 1L << 62; // 2^62
    // Expected: 2^62 * 2^62 = 2^124
    final BigInteger expected = BigInteger.valueOf(a).multiply(BigInteger.valueOf(b));
    // Expected in hex: 0x100000000000000000000000000000000 (256-bit)

    final var frame =
        new TestMessageFrameBuilder().pushStackItem(Word.of(b)).pushStackItem(Word.of(a)).build();
    MulOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();

    assertThat(result)
        .withFailMessage(
            "Expected %s * %s = %s but got %s", Word.of(a), Word.of(b), Word.of(expected), result)
        .isEqualTo(Word.of(expected));
  }
}
