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

import org.hyperledger.besu.evm.operation.IsZeroOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.hyperledger.besu.evm.word.Word;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class IsZeroOperationTest {

  @Test
  void testIsZeroOperationZero() {
    final var frame = new TestMessageFrameBuilder().pushStackItem(BigInteger.ZERO).build();
    IsZeroOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.ONE);
  }

  @Test
  void testIsZeroOperationNonZero() {
    final var frame = new TestMessageFrameBuilder().pushStackItem(BigInteger.TEN).build();
    IsZeroOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.ZERO);
  }

  @Test
  void testIsZeroOperationNegative() {
    final var frame =
        new TestMessageFrameBuilder().pushStackItem(new BigInteger(String.valueOf(-10))).build();
    IsZeroOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(Word.ZERO);
  }
}
