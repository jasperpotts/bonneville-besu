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

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.stream.Stream;
import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class WordTest {
    private static final Word TWO = new Word63(2L);
    private static final Word MAX_63 = new Word63(Long.MAX_VALUE);
    private static final Word MIN_64 = MAX_63.add(Word.ONE); // The smallest 64-bit number

    /*************************************************************************
     * Tests for ZERO sized words
     ************************************************************************/

    @Test
    void testZeroIsZero() {
        Word word = Word.ZERO;
        assertThat(word.isZero()).isTrue();
    }

    @Test
    void testBigZeroIsZero() {
        Word word = new Word256(BigInteger.ZERO);
        assertThat(word.isZero()).isTrue();
    }

    @Test
    void testZerosAreEqual() {
        Word word = new Word256(BigInteger.ZERO);
        assertThat(word.isEqualTo(Word.ZERO)).isTrue();
        assertThat(Word.ZERO.isEqualTo(word)).isTrue();
        assertThat(Word.ZERO.isEqualTo(Word.ZERO.as256Bit())).isTrue();
    }

    @Test
    void testZeroIsSmall() {
        assertThat(Word.ZERO.is63Bit()).isTrue();
    }

    /*************************************************************************
     * Tests for POSITIVE words
     * (Everything is either positive or zero)
     ************************************************************************/

    @Test
    void testZeroIsNotPositive() {
        Word word = Word.ZERO;
        assertThat(word.isPositive()).isFalse();
    }

    @Test
    void testMaxUnsignedIsPositive() {
        Word word = Word.MAX;
        assertThat(word.isPositive()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0x1",
            "0x000000000000000A",
            "0x1230000000000000",
            "0xF0000000000000000",
            "0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"})
    void testNonZeroWordsArePositive(final String hex) {
        Word word = Word.ofHexString(hex);
        assertThat(word.isPositive()).isTrue();
    }

    /*************************************************************************
     * Tests for GREATER THAN comparisons.
     ************************************************************************/

    @ParameterizedTest
    @ValueSource(longs = {1L, 3L, Long.MAX_VALUE})
    void test63And256BitIdenticalWordsAreNotGreaterThan(final long value) {
        Word word1 = new Word63(value);
        Word word2 = new Word256(BigInteger.valueOf(value));
        assertThat(word1.isGreaterThan(word2)).isFalse();
        assertThat(word2.isGreaterThan(word1)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("differentValues")
    void testGreaterThanTwo_DifferentValues(final Word smaller, final Word greater) {
        assertThat(greater.isGreaterThan(smaller)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("differentValues")
    void testGreaterThanTwo_DifferentValues_NotTrue(final Word smaller, final Word greater) {
        assertThat(smaller.isGreaterThan(greater)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("sameValues")
    void testGreaterThanTwo_SameValues_NotTrue(final Word a, final Word b) {
        assertThat(a.isGreaterThan(b)).isFalse();
    }

    /*************************************************************************
     * Tests for LESS THAN comparisons.
     ************************************************************************/

    @ParameterizedTest
    @MethodSource("differentValues")
    void testLessThanTwo_DifferentValues(final Word smaller, final Word greater) {
        assertThat(smaller.isLessThan(greater)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("differentValues")
    void testLessThanTwo_DifferentValues_NotTrue(final Word smaller, final Word greater) {
        assertThat(greater.isLessThan(smaller)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("sameValues")
    void testLessThanTwo_SameValues_NotTrue(final Word a, final Word b) {
        assertThat(a.isLessThan(b)).isFalse();
    }

    /*************************************************************************
     * Tests for GREATER THAN or EQUAL TO comparisons.
     ************************************************************************/

    @ParameterizedTest
    @MethodSource("differentValues")
    void testGreaterThanOrEqualTwo_DifferentValues(final Word smaller, final Word greater) {
        assertThat(greater.isGreaterThanOrEqualTo(smaller)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("sameValues")
    void testGreaterThanOrEqualTwo_SameValues(final Word a, final Word b) {
        assertThat(a.isGreaterThanOrEqualTo(b)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("differentValues")
    void testGreaterThanOrEqualTwo_DifferentValues_NotTrue(final Word smaller, final Word greater) {
        assertThat(smaller.isGreaterThanOrEqualTo(greater)).isFalse();
    }

    /*************************************************************************
     * Tests for LESS THAN or EQUAL TO comparisons.
     ************************************************************************/

    @ParameterizedTest
    @MethodSource("differentValues")
    void testLessThanOrEqualTwo_DifferentValues(final Word smaller, final Word greater) {
        assertThat(smaller.isLessThanOrEqualTo(greater)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("sameValues")
    void testLessThanOrEqualTwo_SameValues(final Word a, final Word b) {
        assertThat(a.isLessThanOrEqualTo(b)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("differentValues")
    void testLessThanOrEqualTwo_DifferentValues_NotTrue(final Word smaller, final Word greater) {
        assertThat(greater.isLessThanOrEqualTo(smaller)).isFalse();
    }

    /*************************************************************************
     * Tests for EQUAL TO comparisons.
     ************************************************************************/

    @ParameterizedTest
    @MethodSource("sameValues")
    void testEqualTwo_SameValues(final Word a, final Word b) {
        assertThat(a.isEqualTo(b)).isTrue();
        assertThat(a).isEqualTo(b);
    }

    @ParameterizedTest
    @MethodSource("differentValues")
    void testEqualTwo_DifferentValues(final Word a, final Word b) {
        assertThat(a.isEqualTo(b)).isFalse();
        assertThat(a).isNotEqualTo(b);
    }

    /*************************************************************************
     * Tests for NOT EQUAL TO comparisons.
     ************************************************************************/

    @ParameterizedTest
    @MethodSource("sameValues")
    void testNotEqualTwo_SameValues(final Word a, final Word b) {
        assertThat(a.isNotEqualTo(b)).isFalse();
        assertThat(a).isEqualTo(b);
    }

    @ParameterizedTest
    @MethodSource("differentValues")
    void testNotEqualTwo_DifferentValues(final Word a, final Word b) {
        assertThat(a.isNotEqualTo(b)).isTrue();
        assertThat(a).isNotEqualTo(b);
    }

    /*************************************************************************
     * Tests for is63Bit
     ************************************************************************/

    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 3L, Long.MAX_VALUE})
    void test63Bit(final long value) {
        Word word = new Word63(value);
        assertThat(word.is63Bit()).isTrue();

        word = new Word256(BigInteger.valueOf(value));
        assertThat(word.is63Bit()).isFalse();
    }

    @Test
    void test63BitWellKnownTypes() {
        assertThat(Word.ZERO.is63Bit()).isTrue();
        assertThat(Word.ONE.is63Bit()).isTrue();
        assertThat(Word.MAX.is63Bit()).isFalse();
    }

    /*************************************************************************
     * Tests for as256Bit
     ************************************************************************/

    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 3L, Long.MAX_VALUE})
    void testAs256Bit(final long value) {
        Word word = new Word63(value);
        assertThat(word.as256Bit()).isEqualTo(new Word256(BigInteger.valueOf(value)));

        word = new Word256(BigInteger.valueOf(value));
        assertThat(word.as256Bit()).isEqualTo(new Word256(BigInteger.valueOf(value)));
    }

    @Test
    void testAs256BitWellKnownTypes() {
        assertThat(Word.ZERO.as256Bit()).isEqualTo(new Word256(BigInteger.ZERO));
        assertThat(Word.ONE.as256Bit()).isEqualTo(new Word256(BigInteger.ONE));
        assertThat(Word.MAX.as256Bit()).isEqualTo(Word.MAX);
    }

    /*************************************************************************
     * Tests for asByteArray
     * The important thing here is to test round tripping.
     ************************************************************************/

    @Test
    void testAsByteArray() {
        Word word = new Word63(0x1234567890ABCDEFL);
        byte[] bytes = word.asByteArray();
        assertThat(bytes).hasSize(8);
        assertThat(bytes[0]).isEqualTo((byte) 0x12);
        assertThat(bytes[1]).isEqualTo((byte) 0x34);
        assertThat(bytes[2]).isEqualTo((byte) 0x56);
        assertThat(bytes[3]).isEqualTo((byte) 0x78);
        assertThat(bytes[4]).isEqualTo((byte) 0x90);
        assertThat(bytes[5]).isEqualTo((byte) 0xAB);
        assertThat(bytes[6]).isEqualTo((byte) 0xCD);
        assertThat(bytes[7]).isEqualTo((byte) 0xEF);

        Word word2 = Word.of(bytes);
        assertThat(word2).isEqualTo(word);
    }

    @Test
    void testAsByteArray_ZERO() {
        Word word = Word.ZERO;
        byte[] bytes = word.asByteArray();
        assertThat(bytes).isEmpty();

        Word word2 = Word.of(bytes);
        assertThat(word2).isEqualTo(word);
    }

    @Test
    void testAsByteArray_OneByte() {
        Word word = new Word63(0x12);
        byte[] bytes = word.asByteArray();
        assertThat(bytes).hasSize(1);
        assertThat(bytes[0]).isEqualTo((byte) 0x12);

        Word word2 = Word.of(bytes);
        assertThat(word2).isEqualTo(word);
    }

    @Test
    void testAsByteArray_TwoBytes() {
        Word word = new Word63(0x1234);
        byte[] bytes = word.asByteArray();
        assertThat(bytes).hasSize(2);
        assertThat(bytes[0]).isEqualTo((byte) 0x12);
        assertThat(bytes[1]).isEqualTo((byte) 0x34);

        Word word2 = Word.of(bytes);
        assertThat(word2).isEqualTo(word);
    }

    @Test
    void testAsByteArray_EightBytes() {
        Word word = MIN_64;
        System.out.println(word);
        byte[] bytes = word.asByteArray();
        assertThat(bytes).hasSize(8);
        assertThat(bytes[0]).isEqualTo((byte) 0x80);
        assertThat(bytes[1]).isEqualTo((byte) 0x00);
        assertThat(bytes[2]).isEqualTo((byte) 0x00);
        assertThat(bytes[3]).isEqualTo((byte) 0x00);
        assertThat(bytes[4]).isEqualTo((byte) 0x00);
        assertThat(bytes[5]).isEqualTo((byte) 0x00);
        assertThat(bytes[6]).isEqualTo((byte) 0x00);
        assertThat(bytes[7]).isEqualTo((byte) 0x00);

        Word word2 = Word.of(bytes);
        assertThat(word2).isEqualTo(word);
    }

    @Test
    void testAsByteArray_NineBytes() {
        Word word = Word.ofHexString("0xABCD1234567890ABCD");
        byte[] bytes = word.asByteArray();
        assertThat(bytes).hasSize(9);
        assertThat(bytes[0]).isEqualTo((byte) 0xAB);
        assertThat(bytes[1]).isEqualTo((byte) 0xCD);
        assertThat(bytes[2]).isEqualTo((byte) 0x12);
        assertThat(bytes[3]).isEqualTo((byte) 0x34);
        assertThat(bytes[4]).isEqualTo((byte) 0x56);
        assertThat(bytes[5]).isEqualTo((byte) 0x78);
        assertThat(bytes[6]).isEqualTo((byte) 0x90);
        assertThat(bytes[7]).isEqualTo((byte) 0xAB);
        assertThat(bytes[8]).isEqualTo((byte) 0xCD);

        Word word2 = Word.of(bytes);
        assertThat(word2).isEqualTo(word);
    }

    @Test
    void testAsByteArray_Max() {
        Word word = Word.MAX;
        byte[] bytes = word.asByteArray();
        assertThat(bytes).hasSize(32);
        for (int i = 0; i < 32; i++) {
            assertThat(bytes[i]).isEqualTo((byte) 0xFF);
        }

        Word word2 = Word.of(bytes);
        assertThat(word2).isEqualTo(word);
    }

    /*************************************************************************
     * Tests for ADDING
     ************************************************************************/

    @Test
    void addingZeroResultsInNoChange() {
        Word word = Word.ZERO;
        assertThat(word.add(Word.ZERO)).isEqualTo(Word.ZERO);
        assertThat(word.add(Word.ONE)).isEqualTo(Word.ONE);
        assertThat(word.add(MAX_63)).isEqualTo(MAX_63);
        assertThat(word.add(MIN_64)).isEqualTo(MIN_64);
        assertThat(word.add(Word.MAX)).isEqualTo(Word.MAX);
    }

    @Test
    void addingOne() {
        assertThat(Word.ZERO.add(Word.ONE)).isEqualTo(Word.ONE);
        assertThat(Word.ONE.add(Word.ONE)).isEqualTo(TWO);
        // The following line will promote from 63-bit to 256-bit
        assertThat(Word.ONE.add(MAX_63)).isEqualTo(MIN_64);
        // This line will just add one at the 256-bit level
        assertThat(Word.ofHexString("0xFFFFFFFFFFFFFFFF").add(Word.ONE))
                .isEqualTo(Word.ofHexString("0x010000000000000000"));
    }

    @Test
    void addOneToMaxIsZero() {
        assertThat(Word.MAX.add(Word.ONE)).isEqualTo(Word.ZERO);
    }

    @Test
    void addTwoToMaxIsOne() {
        assertThat(Word.MAX.add(TWO)).isEqualTo(Word.ONE);
    }

    /*************************************************************************
     * Tests for SUBTRACTING
     ************************************************************************/

    @ParameterizedTest
    @MethodSource("values")
    void subtractingZeroResultsInNoChange(final Word word) {
        assertThat(word.subtract(Word.ZERO)).isEqualTo(word);
    }

    @ParameterizedTest
    @MethodSource("values")
    void subtractingSameValuesResultsInZero(final Word word) {
        assertThat(word.subtract(word)).isEqualTo(Word.ZERO);
    }

    @Test
    void subtractingValues() {
        assertThat(MIN_64.subtract(Word.ONE)).isEqualTo(MAX_63);
    }

    @Test
    void subtractingOneFromZeroIsMax() {
        assertThat(Word.ZERO.subtract(Word.ONE)).isEqualTo(Word.MAX);
    }

    @Test
    void subtractingTwoFromZeroIsMaxMinusOne() {
        final var expected = Word.ofHexString("0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFE");
        assertThat(Word.ZERO.subtract(TWO)).isEqualTo(expected);
    }

    /*************************************************************************
     * Tests for MULTIPLYING
     ************************************************************************/

    @ParameterizedTest
    @MethodSource("values")
    void multiplyingByZeroResultsInZero(final Word word) {
        assertThat(word.multiply(Word.ZERO)).isEqualTo(Word.ZERO);
    }

    @ParameterizedTest
    @MethodSource("values")
    void multiplyingByOneResultsInNoChange(final Word word) {
        assertThat(word.multiply(Word.ONE)).isEqualTo(word);
    }

    @Test
    void simpleMultiplication() {
        // Tests that multiplying 63-bit numbers works
        assertThat(new Word63(10).multiply(TWO))
                .isEqualTo(new Word63(20));

        // Tests that multiplying at the boundary between 63-bit and 256-bit works
        assertThat(Word.ofHexString("0x7FFFFFFFFFFFFFFF").multiply(TWO))
                .isEqualTo(Word.ofHexString("0xFFFFFFFFFFFFFFFE"));

        // And multiplying 256-bit works
        assertThat(Word.ofHexString("0xFFFFFFFFFFFFFFFF").multiply(TWO))
                .isEqualTo(Word.ofHexString("0x1FFFFFFFFFFFFFFFE"));
    }

    @Test
    void multiplyOverflow() {
        // Tests that if we multiply two numbers that overflow 32 bytes then
        // the result wraps back around past zero
        assertThat(Word.ofHexString("0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")
                .multiply(TWO)).isEqualTo(Word.ofHexString("0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFE"));
    }

    /*************************************************************************
     * Tests for DIVIDING
     ************************************************************************/

    @ParameterizedTest
    @MethodSource("values")
    void dividingByZero(final Word word) {
        assertThat(word.divide(Word.ZERO)).isEqualTo(Word.ZERO);
    }

    @ParameterizedTest
    @MethodSource("values")
    void dividingZero(final Word word) {
        assertThat(Word.ZERO.divide(word)).isEqualTo(Word.ZERO);
    }

    @ParameterizedTest
    @MethodSource("values")
    void divideBySelfIsOne(final Word word) {
        Assumptions.assumeThat(word).isNotEqualTo(Word.ZERO);
        assertThat(word.divide(word)).isEqualTo(Word.ONE);
    }

    @Test
    void divideByTwo() {
        assertThat(new Word63(10).divide(TWO)).isEqualTo(new Word63(5));
        assertThat(Word.MAX.divide(TWO)).isEqualTo(Word.ofHexString("0x7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"));
    }

    @Test
    void divideByTwoWithRemainder() {
        assertThat(new Word63(11).divide(TWO)).isEqualTo(new Word63(5));
        assertThat(Word.ONE.divide(TWO)).isEqualTo(Word.ZERO);
    }

    /*************************************************************************
     * Tests for MODDING
     ************************************************************************/

    @ParameterizedTest
    @MethodSource("values")
    void modByZero(final Word word) {
        assertThat(word.mod(Word.ZERO)).isEqualTo(Word.ZERO);
    }

    @ParameterizedTest
    @MethodSource("values")
    void modZero(final Word word) {
        assertThat(Word.ZERO.mod(word)).isEqualTo(Word.ZERO);
    }

    @Test
    void testMod() {
        assertThat(new Word63(10).mod(TWO)).isEqualTo(Word.ZERO);
        assertThat(new Word63(10).mod(new Word63(3L))).isEqualTo(Word.ONE);
        assertThat(new Word63(17).mod(new Word63(5L))).isEqualTo(TWO);
        assertThat(new Word63(11).mod(TWO)).isEqualTo(Word.ONE);
        assertThat(Word.MAX.mod(TWO)).isEqualTo(Word.ONE);
    }

    /*************************************************************************
     * Tests for ANDing
     ************************************************************************/

    @ParameterizedTest
    @MethodSource("values")
    void andWithZero(final Word word) {
        assertThat(word.and(Word.ZERO)).isEqualTo(Word.ZERO);
    }

    @ParameterizedTest
    @MethodSource("values")
    void andWithSelf(final Word word) {
        assertThat(word.and(word)).isEqualTo(word);
    }

    // TODO Need more AND tests.

    private static Stream<Arguments> differentValues() {
        return Stream.of(
                Arguments.of(Word.ZERO, Word.ONE),
                Arguments.of(Word.ONE, TWO),
                Arguments.of(Word.ONE, new Word256(BigInteger.valueOf(2L))),
                Arguments.of(Word.ONE, Word.MAX),
                Arguments.of(MAX_63, MIN_64),
                Arguments.of(MAX_63, MAX_63.as256Bit().add(Word.ONE)));
    }

    private static Stream<Arguments> sameValues() {
        return Stream.of(
                Arguments.of(Word.ZERO, Word.ZERO),
                Arguments.of(Word.ONE, Word.ONE),
                Arguments.of(Word.ONE, new Word256(BigInteger.valueOf(1L))),
                Arguments.of(MAX_63, MAX_63),
                Arguments.of(MAX_63, MAX_63.as256Bit()),
                Arguments.of(MAX_63.as256Bit(), MAX_63.as256Bit()),
                Arguments.of(Word.MAX, Word.MAX));
    }

    private static Stream<Arguments> values() {
        return Stream.of(
                Arguments.of(Word.ZERO),
                Arguments.of(Word.ONE),
                Arguments.of(Word.ONE),
                Arguments.of(new Word256(BigInteger.valueOf(1L))),
                Arguments.of(MAX_63),
                Arguments.of(MAX_63.as256Bit()),
                Arguments.of(MIN_64),
                Arguments.of(Word.MAX));
    }
}
