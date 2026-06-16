package com.calculator.operations;

/**
 * The natural exponential function, {@code e^x} (written {@code exp(x)} in
 * an expression). It is the inverse of the natural logarithm provided by
 * {@link LogarithmicOperation} and is defined for every real input.
 *
 * <p>Base-10 exponentiation ({@code 10^x}) is expressed in the grammar as a
 * literal {@code 10} raised via {@link PowerOperation}, so it needs no
 * dedicated class here.</p>
 */
public class ExponentialOperation extends MathOperation {

    public ExponentialOperation() {
        super("exp", 1);
    }

    @Override
    public double apply(double... operands) {
        checkArity(operands);
        return Math.exp(operands[0]);
    }
}
