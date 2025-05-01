package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.SGtOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class SGtOperationTest {

  @Test
  public void testSGtOperationLeftLesser() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .pushStackItem(new BigInteger(String.valueOf(-1)))
            .build();
    SGtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE);
  }

  @Test
  public void testSGtOperationLeftLesserWithPositiveAndNegative() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .pushStackItem(new BigInteger(String.valueOf(1)))
            .build();
    SGtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE);
  }

  @Test
  public void testSGtOperationLeftLesserWithZero() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .pushStackItem(new BigInteger(String.valueOf(0)))
            .build();
    SGtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE);
  }

  @Test
  public void testSGtOperationLeftGreater() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(-1)))
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .build();
    SGtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testSGtOperationLeftGreaterWithPostiveAndNegative() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(-1)))
            .pushStackItem(new BigInteger(String.valueOf(10)))
            .build();
    SGtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE);
  }

  @Test
  public void testSGtOperationLeftGreaterWithZero() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(0)))
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .build();
    SGtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testSGtOperationLeftAndRightEqual() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .pushStackItem(new BigInteger(String.valueOf(-10)))
            .build();
    SGtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testSGtOperationLeftAndRightEqualZero() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(0)))
            .pushStackItem(new BigInteger(String.valueOf(0)))
            .build();
    SGtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testSGtOperationLeftAndRightEqualPositives() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(10)))
            .pushStackItem(new BigInteger(String.valueOf(10)))
            .build();
    SGtOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }
}
