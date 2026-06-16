package com.calculator.operations;

/**
 * Cube root ({@code ∛}). Unlike the square root this is defined for every
 * real number — including negatives, since {@code ∛(-8) = -2} — so it uses
 * {@link Math#cbrt(double)} and needs no domain guard.
 */
public class CubeRootOperation extends MathOperation {

    public CubeRootOperation() {
        super("∛", 1);
    }

    @Override
    public double apply(double... operands) {
        checkArity(operands);
        return Math.cbrt(operands[0]);
    }
}
