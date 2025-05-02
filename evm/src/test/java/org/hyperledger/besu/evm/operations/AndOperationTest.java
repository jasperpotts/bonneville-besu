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

import org.hyperledger.besu.evm.operation.AndOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;
import org.hyperledger.besu.evm.word.Word63;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class AndOperationTest extends BaseNumericTest {

  @ParameterizedTest
  @MethodSource("provideWordTestCases")
  void testAndOperation(final Word a, final Word b) {
    final Word expected = a.and(b);
    final var frame = new TestMessageFrameBuilder().pushStackItem(b).pushStackItem(a).build();
    AndOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result)
        .withFailMessage("Expected %s & %s = %s but got %s", a, b, expected, result)
        .isEqualTo(expected);
  }

  @Test
  void testAndOperation2And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new Word63(2))
            .pushStackItem(new Word63(10))
            .build();
    AndOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(new Word63(2));
  }

  @Test
  void testAndOperation0And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(Word.ZERO)
            .pushStackItem(new Word63(10))
            .build();
    AndOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.ZERO);
  }

  @Test
  void testAndOperation255And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new Word63(255))
            .pushStackItem(new Word63(10))
            .build();
    AndOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(new Word63(10));
  }
}
