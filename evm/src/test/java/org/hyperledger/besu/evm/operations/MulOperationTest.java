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
import org.hyperledger.besu.evm.word.Word256;

import java.math.BigInteger;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class MulOperationTest extends BaseNumericTest {

  @ParameterizedTest
  @MethodSource("provideBigIntegerTestCases")
  void testMulOperation(final BigInteger a, final BigInteger b) {
    final Word expected = new Word256(a.multiply(b));
    final var frame = new TestMessageFrameBuilder().pushStackItem(b).pushStackItem(a).build();
    MulOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result)
        .withFailMessage("Expected %s * %s = %s but got %s", a, b, expected, result)
        .isEqualTo(expected);
  }

  @Test
  void testMulOperation() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TWO)
            .pushStackItem(BigInteger.TEN)
            .build();
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
          new TestMessageFrameBuilder()
              .pushStackItem(BigInteger.valueOf(a))
              .pushStackItem(BigInteger.valueOf(b))
              .build();
      MulOperation.staticOperation(frame);
      final var result = frame.stack().popUnsafe();
      assertThat(result).isEqualTo(Word.of(expected));
    }
  }
}
