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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class OrOperationTest extends BaseNumericTest {

  @ParameterizedTest
  @MethodSource("provideWordTestCases")
  void testOrOperation(final Word a, final Word b) {
    final Word expected = a.or(b);
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
        new TestMessageFrameBuilder().pushStackItem(Word.ONE).pushStackItem(TEN).build();
    OrOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.of(11));
  }

  @Test
  void testOrOperation0And10() {
    final var frame =
        new TestMessageFrameBuilder().pushStackItem(Word.ZERO).pushStackItem(TEN).build();
    OrOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.of(10));
  }

  @Test
  void testOrOperation255And10() {
    final var frame =
        new TestMessageFrameBuilder().pushStackItem(Word.of(255)).pushStackItem(TEN).build();
    OrOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.of(255));
  }
}
