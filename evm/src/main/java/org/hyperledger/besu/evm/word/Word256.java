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

public class Word256 implements Word {
  final BigInteger value;

  public Word256(final BigInteger b) {
    this.value = b.and(MASK_256_BITS);
  }

  @Override
  public boolean isZero() {
    return value.signum() == 0;
  }

  @Override
  public boolean isPositive() {
    return value.signum() > 0;
  }

  @Override
  public boolean isGreaterThan(final Word other) {
    return value.compareTo(other.as256Bit().value) > 0;
  }

  @Override
  public boolean isLessThan(final Word other) {
    return value.compareTo(other.as256Bit().value) < 0;
  }

  @Override
  public boolean isGreaterThanOrEqualTo(final Word other) {
    return value.compareTo(other.as256Bit().value) >= 0;
  }

  @Override
  public boolean isLessThanOrEqualTo(final Word other) {
    return value.compareTo(other.as256Bit().value) <= 0;
  }

  @Override
  public boolean isEqualTo(final Word other) {
    return value.compareTo(other.as256Bit().value) == 0;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Word other) {
      return this.isEqualTo(other);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean isNotEqualTo(final Word other) {
    return value.compareTo(other.as256Bit().value) != 0;
  }

  @Override
  public boolean is63Bit() {
    return false;
  }

  @Override
  public int numBytes() {
    return (value.bitLength() + 7) / 8;
  }

  @Override
  public Word256 as256Bit() {
    return this;
  }

  @Override
  public int asInteger() {
    return value.intValue();
  }

  @Override
  public BigInteger asBigInteger() {
    return value;
  }

  @Override
  public byte[] asByteArray() {
    if (value.equals(BigInteger.ZERO)) {
      return new byte[0];
    }
    int bitLength = value.bitLength(); // Number of significant bits
    int numBytes = (bitLength + 7) / 8; // Ceiling of bitLength / 8
    byte[] bytes = new byte[numBytes];
    for (int i = 0; i < numBytes; i++) {
      bytes[numBytes - 1 - i] = value.shiftRight(i * 8).byteValue();
    }
    return bytes;
  }

  @Override
  public Word add(final Word other) {
    return new Word256(value.add(other.as256Bit().value));
  }

  @Override
  public Word subtract(final Word other) {
    return new Word256(value.subtract(other.as256Bit().value));
  }

  @Override
  public Word multiply(final Word other) {
    return new Word256(value.multiply(other.as256Bit().value));
  }

  @Override
  public Word divide(final Word other) {
    if (isZero() || other.isZero()) return Word.ZERO; // EVM semantics
    return new Word256(value.divide(other.as256Bit().value));
  }

  @Override
  public Word signedDivide(final Word other) {
    if (isZero() || other.isZero()) return Word.ZERO; // EVM semantics

    // None of the BigDecimals stored within Word256 is negative. But since this us a signed
    // division operation, we need to possibly treat one or the other as negative. We can
    // know if it is negative if there are 256 bits, and if the sign bit is set. If neither
    // are negative, we'll just do normal division. Otherwise, each signed value must be
    // treated as signed for math purposes.
    final var otherValue = other.as256Bit().value;
    final var dividend = value.testBit(255) ? toSigned256(value) : value;
    final var divisor = otherValue.testBit(255) ? toSigned256(otherValue) : otherValue;
    return new Word256(toUnsigned256(dividend.divide(divisor)));
  }

  @Override
  public Word mod(final Word other) {
    if (other.isZero()) return Word.ZERO; // EVM Semantics
    return new Word256(value.mod(other.as256Bit().value));
  }

  @Override
  public Word signedMod(final Word other) {
    // If either is zero, return ZERO. Mod by zero is zero as per the Ethereum Yellow Paper
    if (isZero() || other.isZero()) return Word.ZERO;

    // SMOD(x, y) = sign(x) * (abs(x) mod abs(y)), if y ≠ 0, otherwise 0.
    final var signed = value.testBit(255);
    final var dividend = toSigned256(value);
    final var divisor = toSigned256(other.as256Bit().value);
    final var modResult = dividend.abs().mod(divisor.abs());
    return new Word256(signed ? toUnsigned256(modResult.negate()) : modResult);
  }

  @Override
  public Word addMod(final Word other, final Word mod) {
    if (mod.isZero()) return Word.ZERO; // EVM semantics
    return new Word256(value.add(other.as256Bit().value).mod(mod.as256Bit().value));
  }

  @Override
  public Word multiplyMod(final Word other, final Word mod) {
    if (mod.isZero()) return Word.ZERO; // EVM semantics
    return new Word256(value.multiply(other.as256Bit().value).mod(mod.as256Bit().value));
  }

  @Override
  public Word modPow(final Word exponent) {
    return new Word256(value.modPow(exponent.as256Bit().value, TWO_TO_THE_256));
  }

  @Override
  public Word and(final Word other) {
    return new Word256(value.and(other.as256Bit().value));
  }

  @Override
  public Word or(final Word other) {
    return new Word256(value.or(other.as256Bit().value));
  }

  @Override
  public Word xor(final Word other) {
    return new Word256(value.xor(other.as256Bit().value));
  }

  @Override
  public Word not() {
    return new Word256(value.not());
  }

  @Override
  public Word shiftLeft(final int shift) {
    return new Word256(value.shiftLeft(shift));
  }

  @Override
  public String toHexString() {
    return value.toString(16);
  }

  @Override
  public String toString() {
    return value.toString(2);
  }

  private static BigInteger toSigned256(final BigInteger value) {
    return value.testBit(255) ? value.subtract(TWO_TO_THE_256) : value;
  }

  private static BigInteger toUnsigned256(final BigInteger value) {
    return value.testBit(255) ? value.add(TWO_TO_THE_256) : value;
  }
}
