/*
 * Copyright ConsenSys AG.
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
package org.hyperledger.besu.evm.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigInteger;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.units.bigints.UInt256;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestStackTest {

  private static final class TestStack extends FlexStack<Bytes> {
    TestStack(final int maxSize) {
      super(maxSize, Bytes.class);
    }
  }

  @Test
  void construction() {
    final TestStack stack = new TestStack(1);
    assertThat(stack.size()).isZero();
  }

  @Test
  void construction_NegativeMaximumSize() {
    assertThatThrownBy(() -> new TestStack(-1)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void push_StackOverflow() {
    final TestStack stack = new TestStack(1);
    stack.push(UInt256.fromHexString("0x01"));
    final UInt256 operand = UInt256.fromHexString("0x02");
    assertThatThrownBy(() -> stack.push(operand)).isInstanceOf(OverflowException.class);
  }

  @Test
  void pop_StackUnderflow() {
    final TestStack stack = new TestStack(1);
    assertThatThrownBy(stack::pop).isInstanceOf(UnderflowException.class);
  }

  @Test
  void pushPop() {
    final TestStack stack = new TestStack(1);
    stack.push(UInt256.fromHexString("0x01"));
    assertThat(stack.size()).isEqualTo(1);
    assertThat(stack.pop()).isEqualTo(Bytes32.fromHexString("0x01"));
  }

  @Test
  void get_NegativeOffset() {
    final TestStack stack = new TestStack(1);
    assertThatThrownBy(() -> stack.get(-1)).isInstanceOf(UnderflowException.class);
  }

  @Test
  void get_IndexGreaterThanSize() {
    final TestStack stack = new TestStack(1);
    stack.push(UInt256.fromHexString("0x01"));
    assertThatThrownBy(() -> stack.get(2)).isInstanceOf(UnderflowException.class);
  }

  @Test
  void get() {
    final TestStack stack = new TestStack(3);
    stack.push(UInt256.fromHexString("0x01"));
    stack.push(UInt256.fromHexString("0x02"));
    stack.push(UInt256.fromHexString("0x03"));
    assertThat(stack.size()).isEqualTo(3);
    assertThat(stack.get(0)).isEqualTo(Bytes32.fromHexString("0x03"));
    assertThat(stack.get(1)).isEqualTo(Bytes32.fromHexString("0x02"));
    assertThat(stack.get(2)).isEqualTo(Bytes32.fromHexString("0x01"));
  }

  @Test
  void set_NegativeOffset() {
    final TestStack stack = new TestStack(1);
    final Bytes32 operand = Bytes32.fromHexString("0x01");
    assertThatThrownBy(() -> stack.set(-1, operand)).isInstanceOf(UnderflowException.class);
  }

  @Test
  void set_IndexGreaterThanSize() {
    final TestStack stack = new TestStack(1);
    stack.push(UInt256.fromHexString("0x01"));
    final Bytes32 operand = Bytes32.fromHexString("0x01");
    assertThatThrownBy(() -> stack.set(2, operand)).isInstanceOf(OverflowException.class);
  }

  @Test
  void set_IndexGreaterThanCurrentSize() {
    final TestStack stack = new TestStack(1024);
    stack.push(UInt256.fromHexString("0x01"));
    final Bytes32 operand = Bytes32.fromHexString("0x01");
    assertThatThrownBy(() -> stack.set(2, operand)).isInstanceOf(OverflowException.class);
  }

  @Test
  void set() {
    final TestStack stack = new TestStack(3);
    stack.push(UInt256.fromHexString("0x01"));
    stack.push(UInt256.fromHexString("0x02"));
    stack.push(UInt256.fromHexString("0x03"));
    stack.set(2, UInt256.fromHexString("0x04"));
    assertThat(stack.size()).isEqualTo(3);
    assertThat(stack.get(0)).isEqualTo(Bytes32.fromHexString("0x03"));
    assertThat(stack.get(1)).isEqualTo(Bytes32.fromHexString("0x02"));
    assertThat(stack.get(2)).isEqualTo(Bytes32.fromHexString("0x04"));
  }

  @Test
  void bulkPop() {
    final TestStack stack = new TestStack(8);
    stack.push(UInt256.fromHexString("0x01"));
    stack.push(UInt256.fromHexString("0x02"));
    stack.push(UInt256.fromHexString("0x03"));
    stack.push(UInt256.fromHexString("0x04"));
    stack.push(UInt256.fromHexString("0x05"));
    stack.push(UInt256.fromHexString("0x06"));
    stack.push(UInt256.fromHexString("0x07"));
    stack.push(UInt256.fromHexString("0x08"));
    assertThat(stack.size()).isEqualTo(8);
    stack.bulkPop(2);
    assertThat(stack.get(0)).isEqualTo(Bytes32.fromHexString("0x06"));
    stack.bulkPop(6);
    assertThat(stack.isEmpty()).isTrue();
  }

  @Test
  void preserveTop() {
    final TestStack stack = new TestStack(8);
    stack.push(UInt256.fromHexString("0x01"));
    stack.push(UInt256.fromHexString("0x02"));
    stack.push(UInt256.fromHexString("0x03"));
    stack.push(UInt256.fromHexString("0x04"));
    stack.push(UInt256.fromHexString("0x05"));
    stack.push(UInt256.fromHexString("0x06"));
    stack.push(UInt256.fromHexString("0x07"));
    stack.push(UInt256.fromHexString("0x08"));
    assertThat(stack.size()).isEqualTo(8);
    stack.preserveTop(6, 1);
    assertThat(stack.get(0)).isEqualTo(Bytes32.fromHexString("0x08"));
    assertThat(stack.get(1)).isEqualTo(Bytes32.fromHexString("0x06"));
    assertThat(stack.size()).isEqualTo(7);
    stack.preserveTop(1, 3);
    assertThat(stack.get(0)).isEqualTo(Bytes32.fromHexString("0x08"));
    assertThat(stack.get(1)).isEqualTo(Bytes32.fromHexString("0x06"));
    assertThat(stack.get(2)).isEqualTo(Bytes32.fromHexString("0x05"));
    assertThat(stack.get(3)).isEqualTo(Bytes32.fromHexString("0x01"));
    assertThat(stack.size()).isEqualTo(4);

    stack.preserveTop(4, 0);
    assertThat(stack.size()).isEqualTo(4);
    assertThatThrownBy(() -> stack.preserveTop(4, 2)).isInstanceOf(UnderflowException.class);
    stack.preserveTop(2, 2);
    assertThat(stack.size()).isEqualTo(4);
    stack.preserveTop(0, 2);
    assertThat(stack.get(0)).isEqualTo(Bytes32.fromHexString("0x08"));
    assertThat(stack.get(1)).isEqualTo(Bytes32.fromHexString("0x06"));

    assertThatThrownBy(() -> stack.preserveTop(5, 1)).isInstanceOf(UnderflowException.class);
    assertThatThrownBy(() -> stack.preserveTop(1, 5)).isInstanceOf(UnderflowException.class);
  }

  @Test
  void testCheckStackForPop() {
    // Push 4 elements onto the stack
    final OperandStack stack = new OperandStack(8);
    stack.pushUnsafe(BigInteger.valueOf(1));
    stack.pushUnsafe(BigInteger.valueOf(2));
    stack.pushUnsafe(BigInteger.valueOf(3));
    stack.pushUnsafe(BigInteger.valueOf(4));
    // I can pop 0, 1, 2, 3, 4. But I cannot pop 5, because I don't have 5.
    stack.checkStackForPop(0);
    stack.checkStackForPop(1);
    stack.checkStackForPop(2);
    stack.checkStackForPop(3);
    stack.checkStackForPop(4);
    assertThatThrownBy(() -> stack.checkStackForPop(5)).isInstanceOf(UnderflowException.class);

    // Pop 1. I have 3 elements on the stack.
    stack.popUnsafe();
    // I can pop 0, 1, 2, 3. But I cannot pop 4, because I don't have 4.
    stack.checkStackForPop(0);
    stack.checkStackForPop(1);
    stack.checkStackForPop(2);
    stack.checkStackForPop(3);
    assertThatThrownBy(() -> stack.checkStackForPop(4)).isInstanceOf(UnderflowException.class);

    // Pop 2. I have 1 element on the stack
    stack.popUnsafe();
    stack.popUnsafe();
    // I can pop 0, 1. But I cannot pop 2, because I don't have 2.
    assertThatThrownBy(() -> stack.checkStackForPop(2)).isInstanceOf(UnderflowException.class);

    // Pop the last element. I have no elements on the stack.
    stack.popUnsafe();
    // But I can also pop 0. But I cannot pop 1, because I don't have 1.
    stack.checkStackForPop(0);
    assertThatThrownBy(() -> stack.checkStackForPop(1)).isInstanceOf(UnderflowException.class);
  }

  @Test
  void testCheckStackForPush() {
    final OperandStack stack = new OperandStack(4);
    // I can push 4 elements
    stack.checkStackForPush(0);
    stack.checkStackForPush(1);
    stack.checkStackForPush(2);
    stack.checkStackForPush(3);
    stack.checkStackForPush(4);
    // I cannot push 5, because I have no space for it.
    assertThatThrownBy(() -> stack.checkStackForPush(5)).isInstanceOf(OverflowException.class);

    // I push one. Now I can only push 3 elements.
    stack.pushUnsafe(BigInteger.valueOf(1));
    stack.checkStackForPush(0);
    stack.checkStackForPush(1);
    stack.checkStackForPush(2);
    stack.checkStackForPush(3);
    assertThatThrownBy(() -> stack.checkStackForPush(4)).isInstanceOf(OverflowException.class);

    // I push 3 more items. The stack is full.
    stack.pushUnsafe(BigInteger.valueOf(2));
    stack.pushUnsafe(BigInteger.valueOf(3));
    stack.pushUnsafe(BigInteger.valueOf(4));
    // I can put 0, but not 1.
    stack.checkStackForPush(0);
    assertThatThrownBy(() -> stack.checkStackForPush(1)).isInstanceOf(OverflowException.class);
  }

  @ParameterizedTest
  @ValueSource(ints = {5, 31, 32, 33, 1023, 1024, 1025})
  void largeOverflows(final int n) {
    final TestStack stack = new TestStack(n);
    for (int i = 0; i < n; i++) {
      stack.push(UInt256.ONE);
    }
    assertThatThrownBy(() -> stack.push(UInt256.ONE)).isInstanceOf(OverflowException.class);
  }
}
