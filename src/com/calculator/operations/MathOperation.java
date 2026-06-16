package com.calculator.operations;

import com.calculator.exceptions.InvalidInputException;

/**
 * Abstract base class for all concrete operations.
 *
 * <p>It encapsulates the bits every operation shares — its symbol and its
 * arity — behind private fields with public getters, and provides a
 * reusable {@link #checkArity(double[])} guard so each subclass can focus
 * purely on its own maths inside {@link #apply(double...)}.</p>
 *
 * <p>Concrete subclasses ({@link ArithmeticOperation},
 * {@link TrigonometricOperation}, {@link LogarithmicOperation},
 * {@link ExponentialOperation}, {@link PowerOperation},
 * {@link RootOperation}, {@link FactorialOperation},
 * {@link PercentageOperation}) demonstrate inheritance and override
 * {@code apply()} to provide their own behaviour (polymorphism).</p>
 */
public abstract class MathOperation implements Operation {

    private final String symbol;
    private final int arity;

    protected MathOperation(String symbol, int arity) {
        this.symbol = symbol;
        this.arity = arity;
    }

    @Override
    public String symbol() {
        return symbol;
    }

    @Override
    public int arity() {
        return arity;
    }

    /**
     * Validate that the right number of operands was supplied before a
     * subclass touches them. Reused by every subclass.
     */
    protected void checkArity(double[] operands) {
        if (operands == null || operands.length != arity) {
            throw new InvalidInputException(
                    "Operation '" + symbol + "' expects " + arity
                            + " operand(s) but received "
                            + (operands == null ? 0 : operands.length));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + symbol + ")";
    }
}
