package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.AndOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class AndOperationTest {

  @Test
  public void testAndOperation2And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TWO)
            .pushStackItem(BigInteger.TEN)
            .build();
    AndOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(2));
  }

  @Test
  public void testAndOperation0And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.ZERO)
            .pushStackItem(BigInteger.TEN)
            .build();
    AndOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(0));
  }

  @Test
  public void testAndOperation255And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(new BigInteger(String.valueOf(255)))
            .pushStackItem(BigInteger.TEN)
            .build();
    AndOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(10));
  }
}
