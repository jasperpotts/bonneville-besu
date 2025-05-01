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

/** The Operand stack. */
public class OperandStack extends FlexStack<BigInteger> {

  private static final BigInteger twoToThe256 = BigInteger.ONE.shiftLeft(256); // 2^256
  private static final BigInteger twoToThe255 = BigInteger.ONE.shiftLeft(255); // 2^255

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
    if (unsigned.compareTo(twoToThe255) >= 0) {
      return unsigned.subtract(twoToThe256);
    } else {
      return unsigned;
    }
  }
}
