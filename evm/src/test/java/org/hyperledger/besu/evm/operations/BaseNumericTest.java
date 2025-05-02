package org.hyperledger.besu.evm.operations;

import org.hyperledger.besu.evm.gascalculator.BerlinGasCalculator;
import org.hyperledger.besu.evm.gascalculator.GasCalculator;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.Streams;
import org.hyperledger.besu.evm.word.Word;
import org.hyperledger.besu.evm.word.Word256;
import org.hyperledger.besu.evm.word.Word63;
import org.junit.jupiter.params.provider.Arguments;

/** BaseNumericTest is a base class for testing numeric operations in the EVM. */
public class BaseNumericTest {
  private static final Random RANDOM = new Random(90192749219704971L);
  static final BigInteger MASK_256_BITS = BigInteger.valueOf(2).pow(256).subtract(BigInteger.ONE);
  private static final BigInteger TWO_POW_256 = BigInteger.ONE.shiftLeft(256);
  static final BigInteger MAX_U256 = TWO_POW_256.subtract(BigInteger.ONE);

  final GasCalculator gasCalculator = new BerlinGasCalculator();

  // Provide test cases for BigInteger, pairs of interesting values
  static Stream<Arguments> provideBigIntegerTestCases() {
    return Streams.concat(
        Stream.of(
            // Basic values
            Arguments.of(BigInteger.ZERO, BigInteger.ZERO),
            Arguments.of(BigInteger.ZERO, BigInteger.ONE),
            Arguments.of(BigInteger.ONE, BigInteger.ZERO),
            Arguments.of(BigInteger.ONE, BigInteger.ONE),
            Arguments.of(BigInteger.TEN, BigInteger.TWO),
            Arguments.of(BigInteger.TWO, BigInteger.TEN),
            Arguments.of(BigInteger.valueOf(100), BigInteger.valueOf(200)),

            // Edge cases: max 256-bit value
            Arguments.of(MAX_U256, BigInteger.ZERO),
            Arguments.of(MAX_U256, BigInteger.ONE), // Overflow: 2^256 - 1 + 1 = 0 mod 2^256
            Arguments.of(MAX_U256, BigInteger.TWO), // Overflow: 2^256 - 1 + 2 = 1 mod 2^256
            Arguments.of(BigInteger.ZERO, MAX_U256),
            Arguments.of(BigInteger.ONE, MAX_U256),
            Arguments.of(BigInteger.TWO, MAX_U256),

            // Some large numbers
            Arguments.of(
                new BigInteger("1234567890123456789012345678901234567890"),
                new BigInteger("9876543210987654321098765432109876543210"))),
        IntStream.range(0, 500) // Random large numbers
            .mapToObj(i -> Arguments.of(generateRandomU256(), generateRandomU256())));
  }

  // Provide test cases for Word, pairs of interesting values
  static Stream<Arguments> provideWordTestCases() {
    return Streams.concat(
        Stream.of(
            // Basic values
            Arguments.of(Word.ZERO, Word.ZERO),
            Arguments.of(Word.ZERO, new Word63(1)),
            Arguments.of(new Word63(1), Word.ZERO),
            Arguments.of(new Word63(1), new Word63(1)),
            Arguments.of(new Word63(10), new Word63(2)),
            Arguments.of(new Word63(2), new Word63(10)),
            Arguments.of(new Word63(100), new Word63(200)),

            // Edge cases: max 256-bit value
            Arguments.of(Word.MAX, Word.ZERO),
            Arguments.of(Word.MAX, new Word63(1)), // Overflow: 2^256 - 1 + 1 = 0 mod 2^256
            Arguments.of(Word.MAX, new Word63(2)), // Overflow: 2^256 - 1 + 2 = 1 mod 2^256
            Arguments.of(Word.ZERO, Word.MAX),
            Arguments.of(new Word63(1), Word.MAX),
            Arguments.of(new Word63(2), Word.MAX),

            // Some large numbers
            Arguments.of(
                new Word256(new BigInteger("1234567890123456789012345678901234567890")),
                new Word256(new BigInteger("9876543210987654321098765432109876543210")))),
        IntStream.range(0, 500) // Random large numbers
            .mapToObj(i -> Arguments.of(generateRandomU2562(), generateRandomU2562())));
  }

  // Helper: Generate a random 256-bit number
  private static BigInteger generateRandomU256() {
    byte[] bytes = new byte[32];
    RANDOM.nextBytes(bytes);
    // Ensure non-negative and within 256 bits
    return new BigInteger(1, bytes).and(MASK_256_BITS);
  }

  private static Word generateRandomU2562() {
    byte[] bytes = new byte[32];
    RANDOM.nextBytes(bytes);
    // Ensure non-negative and within 256 bits
    return Word.of(bytes);
  }
}
