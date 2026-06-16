package com.calculator.operations;

import com.calculator.exceptions.MathDomainException;

/**
 * Hyperbolic functions and their inverses: sinh, cosh, tanh, asinh, acosh,
 * atanh. Unlike the trigonometric functions these do not depend on the angle
 * mode (their argument is always a plain real number).
 *
 * <p>{@link Math} only ships the forward functions, so the inverses are
 * computed from their logarithmic definitions, with domain checks for acosh
 * (x &ge; 1) and atanh (-1 &lt; x &lt; 1).</p>
 */
public class HyperbolicOperation extends MathOperation {

    public enum Function {
        SINH("sinh"),
        COSH("cosh"),
        TANH("tanh"),
        ASINH("asinh"),
        ACOSH("acosh"),
        ATANH("atanh");

        private final String symbol;

        Function(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }
    }

    private final Function function;

    public HyperbolicOperation(Function function) {
        super(function.symbol(), 1);
        this.function = function;
    }

    @Override
    public double apply(double... operands) {
        checkArity(operands);
        double x = operands[0];
        switch (function) {
            case SINH:
                return Math.sinh(x);
            case COSH:
                return Math.cosh(x);
            case TANH:
                return Math.tanh(x);
            case ASINH:
                // asinh(x) = ln(x + sqrt(x^2 + 1)), defined for all x
                return Math.log(x + Math.sqrt(x * x + 1.0));
            case ACOSH:
                if (x < 1.0) {
                    throw new MathDomainException("acosh is only defined for x ≥ 1");
                }
                return Math.log(x + Math.sqrt(x * x - 1.0));
            case ATANH:
                if (x <= -1.0 || x >= 1.0) {
                    throw new MathDomainException("atanh is only defined for -1 < x < 1");
                }
                return 0.5 * Math.log((1.0 + x) / (1.0 - x));
            default:
                throw new IllegalStateException("Unhandled function: " + function);
        }
    }
}
