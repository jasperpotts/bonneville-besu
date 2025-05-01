package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.OrOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class OrOperationTest {

  @Test
  public void testOrOperation1And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.ONE)
            .pushStackItem(BigInteger.TEN)
            .build();
    OrOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(11));
  }

  @Test
  public void testOrOperation0And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.ZERO)
            .pushStackItem(BigInteger.TEN)
            .build();
    OrOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(10));
  }

  @Test
  public void testOrOperation255And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(255)))
            .pushStackItem(BigInteger.TEN)
            .build();
    OrOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(255));
  }
}
