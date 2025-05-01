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

import org.hyperledger.besu.evm.operation.DupOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class DupOperationTest {

  @Test
  void testDupOperation() {
    for (int offset = 0; offset < 16; offset++) {
      final var frame =
          new TestMessageFrameBuilder()
              .pushStackItem(BigInteger.valueOf(15))
              .pushStackItem(BigInteger.valueOf(14))
              .pushStackItem(BigInteger.valueOf(13))
              .pushStackItem(BigInteger.valueOf(12))
              .pushStackItem(BigInteger.valueOf(11))
              .pushStackItem(BigInteger.valueOf(10))
              .pushStackItem(BigInteger.valueOf(9))
              .pushStackItem(BigInteger.valueOf(8))
              .pushStackItem(BigInteger.valueOf(7))
              .pushStackItem(BigInteger.valueOf(6))
              .pushStackItem(BigInteger.valueOf(5))
              .pushStackItem(BigInteger.valueOf(4))
              .pushStackItem(BigInteger.valueOf(3))
              .pushStackItem(BigInteger.valueOf(2))
              .pushStackItem(BigInteger.valueOf(1))
              .pushStackItem(BigInteger.valueOf(0))
              .build();
      DupOperation.staticOperation(frame, offset + 1);
      final var result = frame.getStackItemBigInteger(0);
      assertThat(result).isEqualTo(BigInteger.valueOf(offset));
      final var stackTop = frame.getStackItemBigInteger(1);
      assertThat(stackTop).isEqualTo(BigInteger.valueOf(0));
    }
  }
}
