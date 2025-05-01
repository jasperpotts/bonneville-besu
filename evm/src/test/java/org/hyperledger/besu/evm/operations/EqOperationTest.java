package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.EqOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class EqOperationTest {

  @Test
  public void testEqOperationEqual() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TEN)
            .pushStackItem(BigInteger.TEN)
            .build();
    EqOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE);
  }

  @Test
  public void testEqOperationEqualWithZero() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.ZERO)
            .pushStackItem(BigInteger.ZERO)
            .build();
    EqOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE);
  }

  @Test
  public void testEqOperationNotEqual() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TWO)
            .pushStackItem(BigInteger.TEN)
            .build();
    EqOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }
}
