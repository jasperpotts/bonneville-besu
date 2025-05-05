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

import org.hyperledger.besu.evm.word.Word;
import org.hyperledger.besu.evm.word.Word256;

import java.math.BigInteger;
import java.util.Arrays;

/** The Operand stack. */
public class OperandStack extends FlexStack<Word> {

  /**
   * Instantiates a new Operand stack.
   *
   * @param maxSize the max size
   */
  public OperandStack(final int maxSize) {
    super(maxSize, Word.class);
  }

  /**
   * Pop operand as signed BigInteger. This method does not check for underflow and should only be
   * used when the caller is sure that the stack is not empty.
   *
   * @return the operand as signed BigInteger
   */
  public final BigInteger popUnsafeSigned() {
    final BigInteger unsigned = popUnsafe().as256Bit().asBigInteger();
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
}
