package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigInteger;
import java.util.Random;
import org.hyperledger.besu.evm.operation.MulOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;
import org.junit.jupiter.api.Test;

public class MulOperationTest {

    @Test
    public void testMulOperation() {
        final var frame = new TestMessageFrameBuilder()
                .pushStackItem(BigInteger.TWO)
                .pushStackItem(BigInteger.TEN)
                .build();
        MulOperation.staticOperation(frame);
        final var result = frame.stack().popUnsafe();
        assertThat(result).isEqualTo(BigInteger.valueOf(20));
    }

    @Test
    public void testMoreMulOperations() {
        final Random rand = new Random(129828978189L);
        for(int i = 0; i < 100; i++) {
            final long a = rand.nextLong(0, Long.MAX_VALUE/10_000);
            final long b = rand.nextLong(0, 10_000);
            final long expected = a * b;
            final var frame = new TestMessageFrameBuilder()
                    .pushStackItem(BigInteger.valueOf(a))
                    .pushStackItem(BigInteger.valueOf(b))
                    .build();
            MulOperation.staticOperation(frame);
            final var result = frame.stack().popUnsafe();
            assertThat(result).isEqualTo(BigInteger.valueOf(expected));
        }
    }
}
