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

import org.hyperledger.besu.evm.operation.LtOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class LtOperationTest extends BaseNumericTest {

  @ParameterizedTest
  @MethodSource("provideWordTestCases")
  void testLtOperation(final Word a, final Word b) {
    final Word expected = a.isLessThan(b) ? Word.ONE : Word.ZERO;
    final var frame = new TestMessageFrameBuilder().pushStackItem(b).pushStackItem(a).build();
    LtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result)
        .withFailMessage("Expected %s < %s = %s but got %s", a, b, expected, result)
        .isEqualTo(expected);
  }

  @Test
  void testLtOperationLeftLesser() {
    final var frame = new TestMessageFrameBuilder().pushStackItem(TEN).pushStackItem(TWO).build();
    LtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.ONE);
  }

  @Test
  void testLtOperationLeftGreater() {
    final var frame = new TestMessageFrameBuilder().pushStackItem(TWO).pushStackItem(TEN).build();
    LtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.ZERO);
  }

  @Test
  void testLtOperationLeftAndRightEqual() {
    final var frame = new TestMessageFrameBuilder().pushStackItem(TEN).pushStackItem(TEN).build();
    LtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.ZERO);
  }
}
