package com.calculator.operations;

import com.calculator.exceptions.MathDomainException;

/**
 * Logarithmic functions: base-10 logarithm ({@code log}) and natural
 * logarithm ({@code ln}). Both are only defined for strictly positive
 * inputs, which this class enforces.
 */
public class LogarithmicOperation extends MathOperation {

    public enum Function {
        LOG10("log"),
        LN("ln");

        private final String symbol;

        Function(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }
    }

    private final Function function;

    public LogarithmicOperation(Function function) {
        super(function.symbol(), 1);
        this.function = function;
    }

    @Override
    public double apply(double... operands) {
        checkArity(operands);
        double x = operands[0];
        if (x <= 0.0) {
            throw new MathDomainException(
                    symbol() + " is only defined for positive numbers");
        }
        switch (function) {
            case LOG10:
                return Math.log10(x);
            case LN:
                return Math.log(x);
            default:
                throw new IllegalStateException("Unhandled function: " + function);
        }
    }
}
