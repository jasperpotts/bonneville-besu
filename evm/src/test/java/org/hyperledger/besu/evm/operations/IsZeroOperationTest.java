package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.IsZeroOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class IsZeroOperationTest {

  @Test
  public void testIsZeroOperationZero() {
    final var frame = new TestMessageFrameBuilder().pushStackItem(BigInteger.ZERO).build();
    IsZeroOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE);
  }

  @Test
  public void testIsZeroOperationNonZero() {
    final var frame = new TestMessageFrameBuilder().pushStackItem(BigInteger.TEN).build();
    IsZeroOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testIsZeroOperationNegative() {
    final var frame =
        new TestMessageFrameBuilder().pushStackItem(new BigInteger(String.valueOf(-10))).build();
    IsZeroOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }
}
