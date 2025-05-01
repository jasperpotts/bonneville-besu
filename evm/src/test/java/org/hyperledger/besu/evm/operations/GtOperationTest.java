package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.GtOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class GtOperationTest {

  @Test
  public void testGtOperationLeftGreater() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TWO)
            .pushStackItem(BigInteger.TEN)
            .build();
    GtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE);
  }

  @Test
  public void testGtOperationLeftLesser() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TEN)
            .pushStackItem(BigInteger.TWO)
            .build();
    GtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testGtOperationLeftAndRightEqual() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TEN)
            .pushStackItem(BigInteger.TEN)
            .build();
    GtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }
}
