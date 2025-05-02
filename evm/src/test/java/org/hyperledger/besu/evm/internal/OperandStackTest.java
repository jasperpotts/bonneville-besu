/*
 * Copyright Hyperledger Besu Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.besu.evm.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hyperledger.besu.evm.word.Word256;

import java.math.BigInteger;
import java.util.HexFormat;

import org.junit.jupiter.api.Test;

class OperandStackTest {

  @Test
  void testPopUnsafeSigned() {
    final int maxSize = 1024;
    OperandStack stack = new OperandStack(maxSize);

    // Push a 256-bit unsigned integer (2^255) onto the stack
    BigInteger unsignedValue = BigInteger.ONE.shiftLeft(255);
    stack.push(new Word256(unsignedValue));

    // Pop and convert to signed
    BigInteger signedValue = stack.popUnsafeSigned();

    // Assert the signed value is -2^255
    assertEquals(unsignedValue.subtract(BigInteger.ONE.shiftLeft(256)), signedValue);

    // Push a smaller unsigned value (e.g., 123)
    unsignedValue = BigInteger.valueOf(123);
    stack.push(new Word256(unsignedValue));

    // Pop and convert to signed
    signedValue = stack.popUnsafeSigned();

    // Assert the signed value remains the same
    assertEquals(unsignedValue, signedValue);
  }

  @Test
  void testPushSigneSimple() {
    final int maxSize = 1024;
    OperandStack stack = new OperandStack(maxSize);

    // Push a negative signed value
    BigInteger signedValue = BigInteger.valueOf(-2);
    stack.pushSigned(signedValue);
    stack.pushSigned(signedValue); // push second time so we can pop twice
    System.out.println("Signed value: " + HexFormat.of().formatHex(signedValue.toByteArray()));

    // Pop and convert to unsigned
    BigInteger unsignedValue = stack.popUnsafe().as256Bit().asBigInteger();
    System.out.println("Unsigned value: " + HexFormat.of().formatHex(unsignedValue.toByteArray()));

    BigInteger readSigned = stack.popUnsafeSigned();
    System.out.println("Read signed value: " + HexFormat.of().formatHex(readSigned.toByteArray()));

    // Assert the unsigned value is the two's complement representation
    assertEquals(signedValue.add(BigInteger.ONE.shiftLeft(256)), unsignedValue);

    assertEquals(signedValue, readSigned);
  }

  @Test
  void testPushSigned() {
    final int maxSize = 1024;
    OperandStack stack = new OperandStack(maxSize);

    // Push a negative signed value
    BigInteger signedValue = BigInteger.valueOf(-123);
    stack.pushSigned(signedValue);
    stack.pushSigned(signedValue); // push second time so we can pop twice

    // Pop and convert to unsigned
    BigInteger unsignedValue = stack.popUnsafe().as256Bit().asBigInteger();

    // Assert the unsigned value is the two's complement representation
    assertEquals(signedValue.add(BigInteger.ONE.shiftLeft(256)), unsignedValue);

    assertEquals(signedValue, stack.popUnsafeSigned());
  }
}
