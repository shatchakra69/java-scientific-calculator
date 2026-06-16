package com.calculator.operations;

/**
 * Percentage ({@code %}) as a postfix operator: it converts the preceding
 * value into a fraction, i.e. {@code x% == x / 100}. So {@code 50%} is 0.5
 * and {@code 200 × 15%} is 30.
 */
public class PercentageOperation extends MathOperation {

    public PercentageOperation() {
        super("%", 1);
    }

    @Override
    public double apply(double... operands) {
        checkArity(operands);
        return operands[0] / 100.0;
    }
}
