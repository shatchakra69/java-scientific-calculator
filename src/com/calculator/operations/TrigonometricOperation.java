package com.calculator.operations;

import com.calculator.exceptions.MathDomainException;

/**
 * Trigonometric and inverse-trigonometric functions. Honours the active
 * {@link AngleMode} so that, for example, {@code sin(30)} in DEGREES mode
 * returns 0.5. Inverse functions return their result in the active mode and
 * validate their domain ([-1, 1] for asin/acos).
 */
public class TrigonometricOperation extends MathOperation {

    public enum Function {
        SIN("sin"),
        COS("cos"),
        TAN("tan"),
        ASIN("asin"),
        ACOS("acos"),
        ATAN("atan");

        private final String symbol;

        Function(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }
    }

    private final Function function;
    private final AngleMode mode;

    public TrigonometricOperation(Function function, AngleMode mode) {
        super(function.symbol(), 1);
        this.function = function;
        this.mode = mode;
    }

    @Override
    public double apply(double... operands) {
        checkArity(operands);
        double x = operands[0];
        switch (function) {
            case SIN:
                return Math.sin(mode.toRadians(x));
            case COS:
                return Math.cos(mode.toRadians(x));
            case TAN:
                return Math.tan(mode.toRadians(x));
            case ASIN:
                requireInRange(x);
                return mode.fromRadians(Math.asin(x));
            case ACOS:
                requireInRange(x);
                return mode.fromRadians(Math.acos(x));
            case ATAN:
                return mode.fromRadians(Math.atan(x));
            default:
                throw new IllegalStateException("Unhandled function: " + function);
        }
    }

    private void requireInRange(double x) {
        if (x < -1.0 || x > 1.0) {
            throw new MathDomainException(
                    symbol() + " is only defined for inputs in [-1, 1]");
        }
    }
}
