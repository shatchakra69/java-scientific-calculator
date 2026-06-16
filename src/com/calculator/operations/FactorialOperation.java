package com.calculator.operations;

import com.calculator.exceptions.MathDomainException;

/**
 * Factorial ({@code n!}). Defined only for non-negative integers. Because
 * the result grows extremely quickly, anything above 170! overflows a
 * {@code double} to infinity, so that is treated as a domain error too.
 */
public class FactorialOperation extends MathOperation {

    /** 171! is the first factorial that overflows a IEEE-754 double. */
    private static final int MAX_INPUT = 170;

    public FactorialOperation() {
        super("!", 1);
    }

    @Override
    public double apply(double... operands) {
        checkArity(operands);
        double x = operands[0];

        if (x < 0.0) {
            throw new MathDomainException("Factorial is not defined for negative numbers");
        }
        if (x != Math.floor(x)) {
            throw new MathDomainException("Factorial is only defined for whole numbers");
        }
        if (x > MAX_INPUT) {
            throw new MathDomainException("Value too large for factorial (max " + MAX_INPUT + ")");
        }

        int n = (int) x;
        double result = 1.0;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
