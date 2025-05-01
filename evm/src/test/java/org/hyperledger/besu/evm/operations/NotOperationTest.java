package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.NotOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class NotOperationTest {

  @Test
  public void testNotOperationFF() {
    final var frame = new TestMessageFrameBuilder().pushStackItem(BigInteger.valueOf(-1)).build();
    NotOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(0x0));
  }

  @Test
  public void testNotOperation00() {
    final var frame = new TestMessageFrameBuilder().pushStackItem(BigInteger.valueOf(0x00)).build();
    NotOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(-1));
  }
}
