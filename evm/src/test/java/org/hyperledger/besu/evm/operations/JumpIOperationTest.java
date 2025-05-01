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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hyperledger.besu.evm.Code;
import org.hyperledger.besu.evm.frame.ExceptionalHaltReason;
import org.hyperledger.besu.evm.frame.MessageFrame;
import org.hyperledger.besu.evm.gascalculator.GasCalculator;
import org.hyperledger.besu.evm.operation.JumpiOperation;
import org.hyperledger.besu.evm.operation.Operation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import org.apache.tuweni.bytes.Bytes;
import org.junit.jupiter.api.Test;

class JumpIOperationTest {

  @Test
  void jumpiHappyPath() {
    final GasCalculator gasCalculator = mock(GasCalculator.class);
    final Code mockCode = mock(Code.class);
    final Bytes code = Bytes.fromHexString("00" + "57" + "0001");
    when(mockCode.getBytes()).thenReturn(code);
    when(mockCode.isJumpDestInvalid(1)).thenReturn(false);

    MessageFrame messageFrame =
        new TestMessageFrameBuilder()
            .code(mockCode)
            .pc(1)
            .initialGas(10L)
            .pushStackItem(Bytes.of(1)) // destination
            .pushStackItem(Bytes.of(1)) // condition
            .build();

    JumpiOperation jumpi = new JumpiOperation(gasCalculator);
    Operation.OperationResult jumpiResult = jumpi.execute(messageFrame, null);

    assertThat(jumpiResult.getHaltReason()).isNull();
    assertThat(messageFrame.getPC()).isEqualTo(1);
  }

  @Test
  void jumpiInvalidDestination() {
    final GasCalculator gasCalculator = mock(GasCalculator.class);
    final Code mockCode = mock(Code.class);
    final Bytes code = Bytes.fromHexString("00" + "57" + "0001");
    when(mockCode.getBytes()).thenReturn(code);
    when(mockCode.isJumpDestInvalid(1)).thenReturn(true);

    MessageFrame messageFrame =
        new TestMessageFrameBuilder()
            .code(mockCode)
            .pc(1)
            .initialGas(10L)
            .pushStackItem(Bytes.of(1)) // destination
            .pushStackItem(Bytes.of(1)) // condition
            .build();

    JumpiOperation jumpi = new JumpiOperation(gasCalculator);
    Operation.OperationResult jumpiResult = jumpi.execute(messageFrame, null);

    assertThat(jumpiResult.getHaltReason())
        .isEqualTo(ExceptionalHaltReason.INVALID_JUMP_DESTINATION);
    assertThat(messageFrame.getPC()).isEqualTo(1);
  }

  @Test
  void jumpiConditionFalse() {
    final GasCalculator gasCalculator = mock(GasCalculator.class);
    final Code mockCode = mock(Code.class);
    final Bytes code = Bytes.fromHexString("00" + "57" + "0001");
    when(mockCode.getBytes()).thenReturn(code);

    MessageFrame messageFrame =
        new TestMessageFrameBuilder()
            .code(mockCode)
            .pc(1)
            .initialGas(10L)
            .pushStackItem(Bytes.of(1)) // destination
            .pushStackItem(Bytes.of(0)) // condition
            .build();

    JumpiOperation jumpi = new JumpiOperation(gasCalculator);
    Operation.OperationResult jumpiResult = jumpi.execute(messageFrame, null);

    assertThat(jumpiResult.getHaltReason()).isNull();
    assertThat(messageFrame.getPC()).isEqualTo(0); // No jump, increment PC
  }
}
