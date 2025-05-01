package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.LtOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class LtOperationTest {

  @Test
  public void testLtOperationLeftLesser() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TEN)
            .pushStackItem(BigInteger.TWO)
            .build();
    LtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE);
  }

  @Test
  public void testLtOperationLeftGreater() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TWO)
            .pushStackItem(BigInteger.TEN)
            .build();
    LtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testLtOperationLeftAndRightEqual() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TEN)
            .pushStackItem(BigInteger.TEN)
            .build();
    LtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }
}
