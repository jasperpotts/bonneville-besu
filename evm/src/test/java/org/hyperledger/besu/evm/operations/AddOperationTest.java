package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.Stream;
import org.hyperledger.besu.evm.EVM;
import org.hyperledger.besu.evm.gascalculator.BerlinGasCalculator;
import org.hyperledger.besu.evm.gascalculator.GasCalculator;
import org.hyperledger.besu.evm.operation.AddOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AddOperationTest {
    private final GasCalculator gasCalculator = new BerlinGasCalculator();
    private static final BigInteger TWO_POW_256 = BigInteger.ONE.shiftLeft(256);
    private static final BigInteger MAX_U256 = TWO_POW_256.subtract(BigInteger.ONE);
    private static final Random RANDOM = new Random(4302025L);

    // Test BigInteger add function
    @ParameterizedTest
    @MethodSource("provideBigIntegerTestCases")
    void testAddBigInteger(final BigInteger a, final BigInteger b, final BigInteger expected) {
        final var frame = new TestMessageFrameBuilder()
                .pushStackItem(a)
                .pushStackItem(b)
                .build();
        final var op = new AddOperation(gasCalculator);
        final var result = op.executeFixedCostOperation(frame, mock(EVM.class));
        final var sum = frame.stack().popUnsafe();

        System.out.println("A: " + a.toString(16));
        System.out.println("B: " + b.toString(16));
        System.out.println("Expected: " + expected.toString(16));
        System.out.println("Sum: " + sum.toString(16));
        assertThat(sum).isEqualTo(expected);
        assertThat(result.getGasCost()).isEqualTo(3);
        assertThat(result.getHaltReason()).isNull();
        assertThat(result.getPcIncrement()).isEqualTo(1);
    }

    // Provide test cases for BigInteger add
    static Stream<Arguments> provideBigIntegerTestCases() {
        return Stream.of(
                // Basic addition
                Arguments.of(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO),
                Arguments.of(BigInteger.ONE, BigInteger.ONE, BigInteger.valueOf(2)),
                Arguments.of(BigInteger.valueOf(100), BigInteger.valueOf(200), BigInteger.valueOf(300)),

                // Edge cases: max 256-bit value
                Arguments.of(MAX_U256, BigInteger.ZERO, MAX_U256),
                Arguments.of(MAX_U256, BigInteger.ONE, BigInteger.ZERO), // Overflow: 2^256 - 1 + 1 = 0 mod 2^256
                Arguments.of(MAX_U256, BigInteger.valueOf(2), BigInteger.ONE), // Overflow: 2^256 - 1 + 2 = 1 mod 2^256

                // Large numbers
                Arguments.of(new BigInteger("1234567890123456789012345678901234567890"),
                        new BigInteger("9876543210987654321098765432109876543210"),
                        new BigInteger("11111111101111111110111111111011111111100").mod(TWO_POW_256)),

                // Random large numbers
                Arguments.of(generateRandomU256(), generateRandomU256(), null) // Expected computed in test
        ).map(args -> {
            // Compute expected for random cases
            if (args.get()[2] == null) {
                BigInteger a = (BigInteger) args.get()[0];
                BigInteger b = (BigInteger) args.get()[1];
                BigInteger expected = a.add(b).mod(TWO_POW_256);
                return Arguments.of(a, b, expected);
            }
            return args;
        });
    }

    // Helper: Generate a random 256-bit number
    private static BigInteger generateRandomU256() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        // Ensure non-negative and within 256 bits
        return new BigInteger(1, bytes).mod(TWO_POW_256);
    }
}
