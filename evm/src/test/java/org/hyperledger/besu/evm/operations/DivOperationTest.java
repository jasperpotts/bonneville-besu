package org.hyperledger.besu.evm.operations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.hyperledger.besu.evm.operation.DivOperation;
import org.hyperledger.besu.evm.testutils.TestMessageFrameBuilder;

import java.math.BigInteger;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class DivOperationTest extends BaseNumericTest {

  @ParameterizedTest
  @MethodSource("provideBigIntegerTestCases")
  void testDivOperation(final BigInteger a, final BigInteger b) {
    final BigInteger expected =
        (a.equals(BigInteger.ZERO) || b.equals(BigInteger.ZERO))
            ? BigInteger.ZERO
            : a.divide(b).and(MASK_256_BITS);
    final var frame = new TestMessageFrameBuilder().pushStackItem(b).pushStackItem(a).build();
    DivOperation.staticOperation(frame);
    final var result = frame.stack().popUnsafe();
    assertThat(result)
        .withFailMessage("Expected %d/%d = %d but got %d", a, b, expected, result)
        .isEqualTo(expected);
  }
}
