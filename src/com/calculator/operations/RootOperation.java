package com.calculator.operations;

import com.calculator.exceptions.MathDomainException;

/**
 * Square root ({@code √}). Real square roots are undefined for negative
 * numbers, so this class rejects them with a {@link MathDomainException}.
 */
public class RootOperation extends MathOperation {

    public RootOperation() {
        super("√", 1);
    }

    @Override
    public double apply(double... operands) {
        checkArity(operands);
        double x = operands[0];
        if (x < 0.0) {
            throw new MathDomainException(
                    "Cannot take the square root of a negative number");
        }
        return Math.sqrt(x);
    }
}
