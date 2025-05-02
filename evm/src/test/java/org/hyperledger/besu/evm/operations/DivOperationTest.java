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

import org.hyperledger.besu.evm.operation.DivOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DivOperationTest extends BaseNumericTest {

  @ParameterizedTest
  @MethodSource("provideWordTestCases")
  void testDivOperation(final Word a, final Word b) {
    final Word expected =
        (a.equals(Word.ZERO) || b.equals(Word.ZERO))
            ? Word.ZERO
            : a.divide(b);
    final var frame = new TestMessageFrameBuilder().pushStackItem(b).pushStackItem(a).build();
    DivOperation.staticOperation(frame);
    final var result = frame.stack2().popUnsafe();
    assertThat(result)
        .withFailMessage("Expected %s/%s = %s but got %s", a, b, expected, result)
        .isEqualTo(expected);
  }
}
