package com.calculator.operations;

import com.calculator.exceptions.MathDomainException;

/**
 * Binary exponentiation, {@code x ^ y}. Guards against the cases where
 * {@link Math#pow(double, double)} would silently return {@code NaN} or
 * infinity (a negative base with a fractional exponent, or zero raised to a
 * negative power) and reports them as domain errors instead.
 */
public class PowerOperation extends MathOperation {

    public PowerOperation() {
        super("^", 2);
    }

    @Override
    public double apply(double... operands) {
        checkArity(operands);
        double base = operands[0];
        double exponent = operands[1];

        if (base == 0.0 && exponent < 0.0) {
            throw new MathDomainException("0 cannot be raised to a negative power");
        }
        if (base < 0.0 && exponent != Math.floor(exponent)) {
            throw new MathDomainException(
                    "A negative base requires an integer exponent");
        }

        double result = Math.pow(base, exponent);
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new MathDomainException("Result is undefined or out of range");
        }
        return result;
    }
}
