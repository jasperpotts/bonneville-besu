package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.SLtOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class SLtOperationTest {

  @Test
  public void testSLtOperationLeftLesser() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(-1)))
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .build();
    SLtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE);
  }

  @Test
  public void testSLtOperationLeftLesserWithPositiveAndNegative() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(1)))
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .build();
    SLtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE);
  }

  @Test
  public void testSLtOperationLeftLesserWithZero() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(0)))
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .build();
    SLtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE);
  }

  @Test
  public void testSLtOperationLeftGreater() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .pushStackItem(new BigInteger(String.valueOf(-1)))
            .build();
    SLtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testSLtOperationLeftGreaterWithPositiveAndNegative() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .pushStackItem(new BigInteger(String.valueOf(1)))
            .build();
    SLtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testSLtOperationLeftGreaterWithZero() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .pushStackItem(new BigInteger(String.valueOf(0)))
            .build();
    SLtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testSLtOperationLeftAndRightEqual() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .build();
    SLtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testSLtOperationLeftAndRightEqualZero() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(0)))
            .pushStackItem(new BigInteger(String.valueOf(0)))
            .build();
    SLtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testSLtOperationLeftAndRightEqualPositives() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(10)))
            .pushStackItem(new BigInteger(String.valueOf(10)))
            .build();
    SLtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }
}
