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

package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.Assertions.assertThat;

import org.hyperledger.besu.evm.operation.PushOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PushOperationTest {
  private static final BigInteger TWO_POW_256 = BigInteger.ONE.shiftLeft(256);
  private static final BigInteger MAX_U256 = TWO_POW_256.subtract(BigInteger.ONE);
  private static final Random RANDOM = new Random(4302025L);

  // Test PUSH operation for various byte lengths
  @ParameterizedTest
  @MethodSource("providePushTestCases")
  void testPushOperation(final int byteLength, final byte[] bytes, final BigInteger expected) {
    // Create code: opcode (e.g., 0x60 for PUSH1) followed by bytes
    byte opcode = (byte) (0x60 + byteLength - 1); // PUSH1 = 0x60, PUSH32 = 0x7F
    byte[] code = new byte[1 + byteLength];
    code[0] = opcode;
    System.arraycopy(bytes, 0, code, 1, byteLength);

    final var frame = new TestMessageFrameBuilder().build();
    final var result = PushOperation.staticOperation(frame, code, 0, byteLength);
    final var stackValue = frame.stack().popUnsafe();

    System.out.println("Code: " + Arrays.toString(code));
    System.out.println("Byte length: " + byteLength);
    System.out.println("Bytes: 0x" + toHexString(bytes));
    System.out.println("Expected: 0x" + expected.toString(16));
    System.out.println("Stack value: 0x" + stackValue.toString(16));
    assertThat(stackValue).isEqualTo(expected);
    assertThat(result.getGasCost()).isEqualTo(3);
    assertThat(result.getHaltReason()).isNull();
    assertThat(result.getPcIncrement()).isEqualTo(1);
    assertThat(frame.getPC()).isEqualTo(byteLength);
  }

  // Provide test cases for PUSH operations
  static Stream<Arguments> providePushTestCases() {
    return Stream.of(
            // PUSH1: Single byte
            Arguments.of(1, new byte[] {(byte) 0x00}, BigInteger.ZERO), // PUSH1 0x00
            Arguments.of(1, new byte[] {(byte) 0xFF}, BigInteger.valueOf(0xFF)), // PUSH1 0xFF
            Arguments.of(1, new byte[] {(byte) 0x7F}, BigInteger.valueOf(0x7F)), // PUSH1 0x7F

            // PUSH4: Four bytes
            Arguments.of(
                4,
                new byte[] {(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78},
                new BigInteger("12345678", 16)), // PUSH4 0x12345678
            Arguments.of(
                4,
                new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00},
                BigInteger.ZERO), // PUSH4 0x00000000

            // PUSH32: Full 32 bytes
            Arguments.of(32, new byte[32], BigInteger.ZERO), // PUSH32 0x0000...0000
            Arguments.of(32, createByteArray(32, (byte) 0xFF), MAX_U256), // PUSH32 0xFFFF...FFFF
            Arguments.of(
                32,
                createByteArray(32, (byte) 0xAA),
                new BigInteger(
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                    16)), // PUSH32 0xAAAA...AAAA

            // Random byte lengths and values
            Arguments.of(8, randomByteArray(8), null), // Random 8-byte push
            Arguments.of(16, randomByteArray(16), null), // Random 16-byte push
            Arguments.of(32, randomByteArray(32), null) // Random 32-byte push
            )
        .map(
            args -> {
              // Compute expected for random cases
              if (args.get()[2] == null) {
                int byteLength = (int) args.get()[0];
                byte[] bytes = (byte[]) args.get()[1];
                BigInteger expected = new BigInteger(1, bytes); // Positive, right-aligned
                return Arguments.of(byteLength, bytes, expected);
              }
              return args;
            });
  }

  // Helper: Create a byte array filled with a specific value
  private static byte[] createByteArray(final int length, final byte value) {
    byte[] bytes = new byte[length];
    for (int i = 0; i < length; i++) {
      bytes[i] = value;
    }
    return bytes;
  }

  // Helper: Generate a random byte array
  private static byte[] randomByteArray(final int length) {
    byte[] bytes = new byte[length];
    RANDOM.nextBytes(bytes);
    return bytes;
  }

  // Helper: Convert byte array to hex string for logging
  private static String toHexString(final byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02X", b));
    }
    return sb.toString();
  }
}
