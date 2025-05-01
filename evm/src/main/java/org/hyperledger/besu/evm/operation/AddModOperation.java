/*
 * Copyright contributors to Hyperledger Besu
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
package org.hyperledger.besu.evm.operation;

import org.hyperledger.besu.evm.EVM;
import org.hyperledger.besu.evm.frame.MessageFrame;
import org.hyperledger.besu.evm.gascalculator.GasCalculator;

import java.math.BigInteger;

import org.checkerframework.checker.nullness.qual.NonNull;

/** The Add mod operation. */
public class AddModOperation extends AbstractFixedCostOperation {

  private static final OperationResult addModSuccess = new OperationResult(8, null);

  /**
   * Instantiates a new Add mod operation.
   *
   * @param gasCalculator the gas calculator
   */
  public AddModOperation(final GasCalculator gasCalculator) {
    super(0x08, "ADDMOD", 3, 1, gasCalculator, gasCalculator.getMidTierGasCost());
  }

  @Override
  public Operation.OperationResult executeFixedCostOperation(
      final MessageFrame frame, final EVM evm) {
    return staticOperation(frame);
  }

  /**
   * A {@link BigInteger}-based ADDMOD implementation.
   *
   * @param frame the frame
   * @return the operation result
   */
  public static OperationResult staticOperation(final MessageFrame frame) {
    final var stack = frame.stack();
    stack.checkStackForPop(3);
    final var operand1 = stack.popUnsafe();
    final var operand2 = stack.popUnsafe();
    final var modulus = stack.popUnsafe();
    if (modulus.equals(BigInteger.ZERO)) {
      stack.pushUnsafe(BigInteger.ZERO);
    } else {
      stack.pushUnsafe(biAddMod(operand1, operand2, modulus));
    }
    return addModSuccess;
  }

  public static BigInteger biAddMod(
      @NonNull final BigInteger operand1,
      @NonNull final BigInteger operand2,
      @NonNull final BigInteger modulus) {
    var sumMod = operand1.add(operand2).mod(modulus);
    sumMod = sumMod.and(MASK_256_BITS);
    int bitLength = sumMod.bitLength();
    if (bitLength < 256) {
      int shift = 256 - bitLength;
      sumMod = sumMod.shiftLeft(shift);
      sumMod = sumMod.shiftRight(shift);
    }
    return sumMod;
  }
}
