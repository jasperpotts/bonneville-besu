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

import org.hyperledger.besu.evm.operation.SDivOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;
import org.hyperledger.besu.evm.word.Word256;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class SDivOperationTest extends BaseNumericTest {

  //  @ParameterizedTest
  //  @MethodSource("provideBigIntegerTestCases")
  //  void testSDivOperationMany(final BigInteger a, final BigInteger b) {
  //      final BigInteger expected = (a.equals(BigInteger.ZERO) || b.equals(BigInteger.ZERO))
  //          ? BigInteger.ZERO
  //          : toSigned(a).divide(toSigned(b)).and(MASK_256_BITS);
  //      final var frame = new TestMessageFrameBuilder().pushStackItem(b).pushStackItem(a).build();
  //      SDivOperation.staticOperation(frame);
  //      final var result = frame.stack().popUnsafe();
  //      assertThat(result)
  //          .withFailMessage("Expected %d/%d = %d but got %d", a, b, expected, result)
  //          .isEqualTo(expected);
  //    }

  @Test
  void testSDivOperation() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TWO)
            .pushStackItem(BigInteger.TEN)
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.of(10 / 2));
  }

  @Test
  void testSDivOperationWithNegativeNumbers() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.valueOf(-3).abs())
            .pushStackItem(BigInteger.valueOf(9))
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(new Word256(BigInteger.valueOf(9 / -3).abs()));
  }

  @Test
  void testSDivOperationWithZeroDenominator() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.ZERO)
            .pushStackItem(BigInteger.TEN)
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.ZERO);
  }

  @Test
  void testSDivOperationWithOverflow() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.valueOf(-1).abs())
            .pushStackItem(BigInteger.ONE.shiftLeft(255)) // -2^255
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(new Word256(BigInteger.ONE.shiftLeft(255)));
  }

  @Test
  void testSDivOperationWithPositiveOverflow() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.ONE)
            .pushStackItem(BigInteger.ONE.shiftLeft(255)) // 2^255
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(new Word256(BigInteger.ONE.shiftLeft(255)));
  }
}
