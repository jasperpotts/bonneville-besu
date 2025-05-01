package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.XorOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class XorOperationTest {

  @Test
  public void testXorOperation1And10() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.ONE)
            .pushStackItem(BigInteger.TEN)
            .build();
    XorOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(11));
  }

  @Test
  public void testXorOperation55AndAA() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.valueOf(0x55))
            .pushStackItem(BigInteger.valueOf(0xAA))
            .build();
    XorOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(0xff));
  }

  @Test
  public void testXorOperation55And55() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.valueOf(0x55))
            .pushStackItem(BigInteger.valueOf(0x55))
            .build();
    XorOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(0));
  }
}
