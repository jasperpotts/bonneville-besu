package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.SDivOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class SDivOperationTest {

  @Test
  public void testSDivOperation() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.TWO)
            .pushStackItem(BigInteger.TEN)
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(10 / 2));
  }

  @Test
  public void testSDivOperationWithNegativeNumbers() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.valueOf(-3).abs())
            .pushStackItem(BigInteger.valueOf(9))
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.valueOf(9 / -3).abs());
  }

  @Test
  public void testSDivOperationWithZeroDenominator() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.ZERO)
            .pushStackItem(BigInteger.TEN)
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ZERO);
  }

  @Test
  public void testSDivOperationWithOverflow() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.valueOf(-1).abs())
            .pushStackItem(BigInteger.ONE.shiftLeft(255)) // -2^255
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE.shiftLeft(255));
  }

  @Test
  public void testSDivOperationWithPositiveOverflow() {
    final var frame =
        new TestMessageFrameBuilder()
            .pushStackItem(BigInteger.ONE)
            .pushStackItem(BigInteger.ONE.shiftLeft(255)) // 2^255
            .build();
    SDivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result).isEqualTo(BigInteger.ONE.shiftLeft(255));
  }
}
