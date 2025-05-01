package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.SwapOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class SwapOperationTest {

  @Test
  public void testSwapOperation1() {
    for (int offset = 0; offset < 16; offset++) {
      final var frame =
          new TestMessageFrameBuilder()
              .pushStackItem(BigInteger.valueOf(15))
              .pushStackItem(BigInteger.valueOf(14))
              .pushStackItem(BigInteger.valueOf(13))
              .pushStackItem(BigInteger.valueOf(12))
              .pushStackItem(BigInteger.valueOf(11))
              .pushStackItem(BigInteger.valueOf(10))
              .pushStackItem(BigInteger.valueOf(9))
              .pushStackItem(BigInteger.valueOf(8))
              .pushStackItem(BigInteger.valueOf(7))
              .pushStackItem(BigInteger.valueOf(6))
              .pushStackItem(BigInteger.valueOf(5))
              .pushStackItem(BigInteger.valueOf(4))
              .pushStackItem(BigInteger.valueOf(3))
              .pushStackItem(BigInteger.valueOf(2))
              .pushStackItem(BigInteger.valueOf(1))
              .pushStackItem(BigInteger.valueOf(0))
              .build();
      SwapOperation.staticOperation(frame, offset);
      final var result = frame.getStackItemBigInteger(0);
      assertThat(result).isEqualTo(BigInteger.valueOf(offset));

      // Now swap it back and make sure you get back the original top of stack value back
      SwapOperation.staticOperation(frame, offset);
      final var original = frame.getStackItemBigInteger(0);
      assertThat(original).isEqualTo(BigInteger.valueOf(0));
    }
  }
}
