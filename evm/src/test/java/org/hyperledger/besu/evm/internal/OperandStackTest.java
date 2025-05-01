package org.hyperledger.besu.evm.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class OperandStackTest {

  @Test
  public void testPopUnsafeSigned() {
    final int maxSize = 1024;
    OperandStack stack = new OperandStack(maxSize);

    // Push a 256-bit unsigned integer (2^255) onto the stack
    BigInteger unsignedValue = BigInteger.ONE.shiftLeft(255);
    stack.push(unsignedValue);

    // Pop and convert to signed
    BigInteger signedValue = stack.popUnsafeSigned();

    // Assert the signed value is -2^255
    assertEquals(unsignedValue.subtract(BigInteger.ONE.shiftLeft(256)), signedValue);

    // Push a smaller unsigned value (e.g., 123)
    unsignedValue = BigInteger.valueOf(123);
    stack.push(unsignedValue);

    // Pop and convert to signed
    signedValue = stack.popUnsafeSigned();

    // Assert the signed value remains the same
    assertEquals(unsignedValue, signedValue);
  }
}
