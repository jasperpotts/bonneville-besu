package org.hyperledger.besu.evm.operation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hyperledger.besu.evm.frame.MessageFrame;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.SplittableRandom;
import java.util.stream.Stream;

import org.apache.tuweni.bytes.Bytes;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AddModOperationTest {
  @ParameterizedTest
  @MethodSource("randomTestCases")
  void compareOriginal(final AddModTestCase testCase) {
    final Bytes operand1 = testCase.operand1;
    final Bytes operand2 = testCase.operand2;
    final Bytes modulus = testCase.modulus;

    if (modulus.isZero()) {
      return;
    }

    final var expected = originalAddMod(operand1, operand2, modulus);

    final var actual =
        AddModOperation.biAddMod(
            new BigInteger(1, operand1.toArrayUnsafe()),
            new BigInteger(1, operand2.toArrayUnsafe()),
            new BigInteger(1, modulus.toArrayUnsafe()));

    assertEquals(expected, MessageFrame.toBytes(actual));
  }

  private static Stream<AddModTestCase> randomTestCases() {
    SplittableRandom random = new SplittableRandom();
    return Stream.generate(
            () -> {
              // Generate random operand1, operand2, and modulus (up to 32 bytes)
              Bytes operand1 = generateRandomBytes(random);
              Bytes operand2 = generateRandomBytes(random);
              Bytes modulus = generateRandomBytes(random);

              // Return a test case object
              return new AddModTestCase(operand1, operand2, modulus);
            })
        .limit(10_000);
  }

  private static Bytes generateRandomBytes(final SplittableRandom random) {
    final int len = random.nextInt(33);
    byte[] bytes = new byte[len];
    random.nextBytes(bytes);
    return Bytes.wrap(bytes);
  }

  // A helper class to store test case parameters
  static class AddModTestCase {
    final Bytes operand1;
    final Bytes operand2;
    final Bytes modulus;

    AddModTestCase(final Bytes operand1, final Bytes operand2, final Bytes modulus) {
      this.operand1 = operand1;
      this.operand2 = operand2;
      this.modulus = modulus;
    }

    @Override
    public String toString() {
      return "AddModTestCase{"
          + "operand1="
          + operand1
          + ", operand2="
          + operand2
          + ", modulus="
          + modulus
          + '}';
    }
  }

  /**
   * The original Besu implementation of the ADDMOD operation.
   *
   * @param operand1 the first operand
   * @param operand2 the second operand
   * @param modulus the modulus
   * @return the result of the addition modulo the modulus
   */
  public static Bytes originalAddMod(
      @NonNull final Bytes operand1, @NonNull final Bytes operand2, @NonNull final Bytes modulus) {
    if (modulus.isZero()) {
      return AbstractOperation.FAILURE_STACK_ITEM;
    } else {
      BigInteger b0 = new BigInteger(1, operand1.toArrayUnsafe());
      BigInteger b1 = new BigInteger(1, operand2.toArrayUnsafe());
      BigInteger b2 = new BigInteger(1, modulus.toArrayUnsafe());
      BigInteger result = b0.add(b1).mod(b2);
      Bytes resultBytes = Bytes.wrap(result.toByteArray());
      if (resultBytes.size() > 32) {
        resultBytes = resultBytes.slice(resultBytes.size() - 32, 32);
      }
      final byte[] padding = new byte[32 - resultBytes.size()];
      Arrays.fill(padding, result.signum() < 0 ? (byte) 0xFF : 0x00);
      return Bytes.concatenate(Bytes.wrap(padding), resultBytes);
    }
  }
}
