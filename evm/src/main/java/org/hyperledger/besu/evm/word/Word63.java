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

// Because Java only supports signed longs, we only use 63 bits in the fast-path, otherwise
// we have to punt to 256 bit long-winded slower arithmetic.
public class Word63 implements Word {
  final long value;

  public Word63(final long value) {
    // value must ONLY be >= 0. For now, I will do the test and throw but probably remove this
    // later.
    if (value < 0) throw new IllegalArgumentException("Negative value: " + value);
    this.value = value;
  }

  @Override
  public boolean isZero() {
    return value == 0;
  }

  @Override
  public boolean isPositive() {
    return value > 0;
  }

  @Override
  public boolean isGreaterThan(final Word other) {
    return other.is63Bit() ? this.value > ((Word63) other).value : as256Bit().isGreaterThan(other);
  }

  @Override
  public boolean isLessThan(final Word other) {
    return other.is63Bit() ? this.value < ((Word63) other).value : as256Bit().isLessThan(other);
  }

  @Override
  public boolean isGreaterThanOrEqualTo(final Word other) {
    return other.is63Bit()
        ? this.value >= ((Word63) other).value
        : as256Bit().isGreaterThanOrEqualTo(other);
  }

  @Override
  public boolean isLessThanOrEqualTo(final Word other) {
    return other.is63Bit()
        ? this.value <= ((Word63) other).value
        : as256Bit().isLessThanOrEqualTo(other);
  }

  @Override
  public boolean isEqualTo(final Word other) {
    return other.is63Bit() ? this.value == ((Word63) other).value : as256Bit().isEqualTo(other);
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
    return Long.hashCode(value);
  }

  @Override
  public boolean isNotEqualTo(final Word other) {
    return other.is63Bit() ? this.value != ((Word63) other).value : as256Bit().isNotEqualTo(other);
  }

  @Override
  public boolean is63Bit() {
    return true;
  }

  @Override
  public Word256 as256Bit() {
    return new Word256(BigInteger.valueOf(value));
  }

  @Override
  public byte[] asByteArray() {
    if (value == 0) {
      return new byte[0];
    }
    int bitLength = Long.SIZE - Long.numberOfLeadingZeros(value);
    int numBytes = (bitLength + 7) / 8;
    byte[] bytes = new byte[numBytes];
    for (int i = 0; i < numBytes; i++) {
      bytes[numBytes - 1 - i] = (byte) ((value >> (i * 8)) & 0xFF);
    }
    return bytes;
  }

  @Override
  public Word add(final Word other) {
    if (other.is63Bit()) {
      final var result = this.value + ((Word63) other).value;
      if (result >= 0) return new Word63(result);
    }

    // If we fell through to here, we need to use 256-bit math
    return as256Bit().add(other);
  }

  @Override
  public Word multiply(final Word other) {
    if (other.is63Bit()) {
      try {
        final var result = this.value * ((Word63) other).value;
        if (result >= 0) return new Word63(result);
      } catch (ArithmeticException ignored) {
        // Overflow, will need to promote to 256-bit. Fall through.
      }
    }
    return as256Bit().multiply(other);
  }

  @Override
  public Word subtract(final Word other) {
    if (other.is63Bit()) {
      final var result = this.value - ((Word63) other).value;
      if (result >= 0) return new Word63(result);
    }
    return as256Bit().subtract(other);
  }

  @Override
  public Word divide(final Word other) {
    if (other.is63Bit()) {
      final var divisor = ((Word63) other).value;
      if (divisor == 0) {
        return Word.ZERO; // EVM Semantics
      }
      final var result = this.value / divisor;
      if (result >= 0) return new Word63(result);
    }
    return as256Bit().divide(other);
  }

  // TODO sdiv

  @Override
  public Word mod(final Word other) {
    if (other.is63Bit()) {
      final var divisor = ((Word63) other).value;
      if (divisor == 0) {
        return Word.ZERO; // EVM Semantics
      }
      final var result = this.value % divisor;
      if (result >= 0) return new Word63(result);
    }
    return as256Bit().mod(other);
  }

  // TODO smod

  @Override
  public Word addMod(final Word other, final Word mod) {
    if (other.is63Bit() && mod.is63Bit()) {
      final var modulus = ((Word63) mod).value;
      if (modulus == 0) return Word.ZERO; // EVM Semantics
      final var sum = this.value + ((Word63) other).value;
      if (sum >= 0) {
        final var result = sum % modulus;
        return new Word63(result);
      }
    }
    return as256Bit().addMod(other, mod);
  }

  @Override
  public Word and(final Word other) {
    if (other.is63Bit()) {
      return new Word63(this.value & ((Word63) other).value);
    }
    return as256Bit().and(other);
  }

  @Override
  public String toString() {
    return Long.toBinaryString(value);
  }
}
