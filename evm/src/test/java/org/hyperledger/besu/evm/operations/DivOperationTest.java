package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.DivOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class DivOperationTest {

  @Test
  public void testDivOperation() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TWO)
            .pushStackItem(BigInteger.TEN)
            .build();
    DivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(10 / 2));
  }

  @Test
  public void testMoreDivOperations() {
    final Random rand = new Random(129828978189L);
    for (int i = 0; i < 100; i++) {
      final long a = rand.nextLong(0, Long.MAX_VALUE / 10_000);
      final long b = rand.nextLong(0, 10_000);
      final long expected = b / a;
      final var frame =
          new TestMessageFrameBuilder()
              .pushStackItem(BigInteger.valueOf(a))
              .pushStackItem(BigInteger.valueOf(b))
              .build();
      DivOperation.staticOperation(frame);
      final var result = frame.stack().popUnsafe();
      assertThat(result)
          .withFailMessage("Expected %d/%d = %d but got %d", b, a, expected, result)
          .isEqualTo(BigInteger.valueOf(expected));
    }
  }
}
