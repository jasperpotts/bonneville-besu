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

package org.hyperledger.besu.evm.word;

import java.math.BigInteger;
import java.util.Arrays;

public interface Word {
  // These two things shouldn't actually be in the interface....
  BigInteger TWO_TO_THE_256 = BigInteger.ONE.shiftLeft(256);
  BigInteger MASK_256_BITS = BigInteger.valueOf(2).pow(256).subtract(BigInteger.ONE);

  Word ZERO = new Word63(0L);
  Word ONE = new Word63(1L);
  Word MAX = Word.ofHexString("0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");

  boolean isZero();

  default boolean isPositive() {
    return !isZero();
  }

  boolean isGreaterThan(final Word other);

  boolean isLessThan(final Word other);

  boolean isGreaterThanOrEqualTo(final Word other);

  boolean isLessThanOrEqualTo(final Word other);

  boolean isEqualTo(final Word other);

  boolean isNotEqualTo(final Word other);

  boolean is63Bit();

  int numBytes();

  Word256 as256Bit();

  int asInteger();

  BigInteger asBigInteger();

  // Returns the fewest bytes possible to represent the number
  byte[] asByteArray();

  default byte[] asArray32() {
    final byte[] bytes = asByteArray();
    if (bytes.length == 32) {
      return bytes;
    }
    final byte[] paddedBytes = new byte[32];
    Arrays.fill(paddedBytes, (byte) 0);
    System.arraycopy(bytes, 0, paddedBytes, 32 - bytes.length, bytes.length);
    return paddedBytes;
  }

  Word add(final Word other);

  Word subtract(final Word other);

  Word multiply(final Word other);

  Word divide(final Word other);

  Word signedDivide(final Word other);

  Word mod(final Word other);

  Word signedMod(final Word other);

  /**
   * Adds the "other" word to this word and then takes the modulus of the result with the "mod"
   * before limiting the result to 256 bits.
   *
   * @param other The word to add to this one
   * @param mod The modulus to take after the addition
   * @return The result of the addition mod the modulus and then limited to 256 bits
   */
  Word addMod(final Word other, final Word mod);

  Word multiplyMod(final Word other, final Word mod);

  /**
   * Computes this word raised to the power of the exponent.
   *
   * @param exponent The exponent to raise this word to
   * @return The result of this^exponent % 2^256
   */
  Word modPow(final Word exponent);

  Word and(final Word other);

  Word or(final Word other);

  Word xor(final Word other);

  Word not();

  Word shiftLeft(final int shift);

  String toHexString();

  static Word of(final long value) {
    return new Word63(value);
  }

  static Word of(final BigInteger value) {
    return new Word256(value);
  }

  // Given some byte array, construct the most optional Word (Word63 or Word256)
  static Word of(final byte[] bytes) {
    // Find the first byte which is not zero
    int firstNonZeroByte = -1;
    for (int i = 0; i < bytes.length; i++) {
      if (bytes[i] != 0) {
        firstNonZeroByte = i;
        break;
      }
    }

    // Optimization for zero
    if (firstNonZeroByte == -1) {
      return ZERO;
    }

    // Optimization for 63-bit
    if (firstNonZeroByte >= bytes.length - 8 && (bytes[firstNonZeroByte] & 0x80) == 0) {
      long result = 0;
      for (byte b : bytes) {
        result = (result << 8) | (b & 0xFF);
      }
      return new Word63(result);
    }

    // We have to use a 256-bit word.
    return new Word256(new BigInteger(1, bytes).and(MASK_256_BITS));
  }

  static Word ofByteString(final String byteString) {
    return of(new BigInteger(byteString, 2).toByteArray());
  }

  static Word ofHexString(final String hexString) {
    final var str = hexString.startsWith("0x") ? hexString.substring(2) : hexString;
    if (str.isBlank()) return ZERO;
    return of(new BigInteger(str, 16).toByteArray());
  }
}
