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

    assertThat(jumpiResult.getHaltReason()).isEqualTo(ExceptionalHaltReason.INVALID_JUMP_DESTINATION);
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
