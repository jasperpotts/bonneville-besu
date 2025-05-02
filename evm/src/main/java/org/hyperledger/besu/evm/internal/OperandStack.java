/*
 * Copyright contributors to Hyperledger Besu
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
 *
 */

package org.hyperledger.besu.evm.internal;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HexFormat;

/** The Operand stack. */
public class OperandStack extends FlexStack<BigInteger> {

  /**
   * Instantiates a new Operand stack.
   *
   * @param maxSize the max size
   */
  public OperandStack(final int maxSize) {
    super(maxSize, BigInteger.class);
  }

  /**
   * Pop operand as signed BigInteger. This method does not check for underflow and should only be
   * used when the caller is sure that the stack is not empty.
   *
   * @return the operand as signed BigInteger
   */
  public final BigInteger popUnsafeSigned() {
    final BigInteger unsigned = popUnsafe();
    System.out.println("unsigned = " + HexFormat.of().formatHex(unsigned.toByteArray()));
    if (unsigned.testBit(255)) {
      // Step 2: Convert it to a two's complement representation
      byte[] bytes = unsigned.toByteArray();

      // Step 3: Remove the leading zero byte if present
      if (bytes[0] == 0) {
        byte[] trimmedBytes = new byte[bytes.length - 1];
        System.arraycopy(bytes, 1, trimmedBytes, 0, trimmedBytes.length);
        bytes = trimmedBytes;
      }
      return new BigInteger(bytes);
    } else {
      return unsigned;
    }
  }

  /**
   * Push signed operand. Takes a signed biginteger and pushes it to the stack as an unsigned value
   * with two's complement format.
   *
   * @param value the value
   */
  public final void pushSigned(final BigInteger value) {
    if (value.signum() < 0) {
      // Step 2: Get the two's complement byte array
      byte[] twosComplementBytes = value.toByteArray();
      // Step 3: Ensure the byte array is 32 bytes (256 bits)
      byte[] paddedBytes = new byte[32];
      Arrays.fill(paddedBytes, (byte) 0xFF);
      int start = 32 - twosComplementBytes.length;
      System.arraycopy(twosComplementBytes, 0, paddedBytes, start, twosComplementBytes.length);
      // Step 4: push
      push(new BigInteger(1, paddedBytes));
    } else {
      push(value);
    }
  }
}
