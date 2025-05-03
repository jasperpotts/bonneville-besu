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

import java.math.BigInteger;
import org.hyperledger.besu.evm.operation.SDivOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SDivOperationTest extends BaseNumericTest {

    @ParameterizedTest
    @MethodSource("provideWordTestCases")
    void testSDivOperationMany(final Word a, final Word b) {
        final Word expected = (a.equals(Word.ZERO) || b.equals(Word.ZERO))
            ? Word.ZERO
            : a.signedDivide(b);
        final var frame = new TestMessageFrameBuilder().pushStackItem(b).pushStackItem(a).build();
        SDivOperation.staticOperation(frame);
        final var result = frame.stack().popUnsafe();
        assertThat(result)
            .withFailMessage("Expected %s/%s = %s but got %s", a, b, expected, result)
            .isEqualTo(expected);
      }

  @Test
  void testSDivOperation() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(TWO)
            .pushStackItem(TEN)
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.of(5));
  }

  @Test
  void testSDivOperationWithNegativeNumbers() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(Word.of(BigInteger.valueOf(-3).abs()))
            .pushStackItem(Word.of(9))
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.of(BigInteger.valueOf(-3).abs()));
  }

  @Test
  void testSDivOperationWithZeroDenominator() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(Word.ZERO)
            .pushStackItem(TEN)
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.ZERO);
  }

  @Test
  void testSDivOperationWithOverflow() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(Word.of(BigInteger.valueOf(-1).abs()))
            .pushStackItem(Word.ONE.shiftLeft(255)) // -2^255
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.ONE.shiftLeft(255));
  }

  @Test
  void testSDivOperationWithPositiveOverflow() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(Word.ONE)
            .pushStackItem(Word.ONE.shiftLeft(255)) // 2^255
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.ONE.shiftLeft(255));
  }
}
