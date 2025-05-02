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

import org.hyperledger.besu.evm.operation.OrOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;
import org.hyperledger.besu.evm.word.Word256;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class OrOperationTest extends BaseNumericTest {

  @ParameterizedTest
  @MethodSource("provideBigIntegerTestCases")
  void testOrOperation(final BigInteger a, final BigInteger b) {
    final Word expected = new Word256(a.or(b));
    final var frame = new TestMessageFrameBuilder().pushStackItem(b).pushStackItem(a).build();
    OrOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result)
        .withFailMessage("Expected %s | %s = %s but got %s", a, b, expected, result)
        .isEqualTo(expected);
  }

  @Test
  void testOrOperation1And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.ONE)
            .pushStackItem(BigInteger.TEN)
            .build();
    OrOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.of(11));
  }

  @Test
  void testOrOperation0And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.ZERO)
            .pushStackItem(BigInteger.TEN)
            .build();
    OrOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.of(10));
  }

  @Test
  void testOrOperation255And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(255)))
            .pushStackItem(BigInteger.TEN)
            .build();
    OrOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.of(255));
  }
}
