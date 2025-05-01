package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.hyperledger.besu.evm.EVM;
import org.hyperledger.besu.evm.operation.AddOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AddOperationTest extends BaseNumericTest {

  // Test BigInteger add function
  @ParameterizedTest
  @MethodSource("provideBigIntegerTestCases")
  void testAddBigInteger(final BigInteger a, final BigInteger b) {
    final BigInteger expected = a.add(b).and(MASK_256_BITS);
    final var frame = new TestMessageFrameBuilder().pushStackItem(a).pushStackItem(b).build();
    final var op = new AddOperation(gasCalculator);
    final var result = op.executeFixedCostOperation(frame, mock(EVM.class));
    final var sum = frame.stack().popUnsafe();
    assertThat(sum).isEqualTo(expected);
    assertThat(result.getGasCost()).isEqualTo(3);
    assertThat(result.getHaltReason()).isNull();
    assertThat(result.getPcIncrement()).isEqualTo(1);
  }
}
