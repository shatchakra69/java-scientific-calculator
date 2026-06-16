package com.calculator.operations;

/**
 * Abstraction shared by every calculator operation.
 *
 * <p>The rest of the program (the expression evaluator in particular) only
 * ever talks to this interface: it does not care whether it is holding an
 * addition, a sine, or a factorial. This is the "program to an interface"
 * idea that makes adding new operations painless and is the basis for the
 * polymorphic {@link #apply(double...)} call.</p>
 */
public interface Operation {

    /**
     * Compute the result of this operation for the given operands.
     *
     * @param operands the inputs, in left-to-right order; the count must
     *                 match {@link #arity()}
     * @return the computed value
     */
    double apply(double... operands);

    /** The textual symbol/name used for this operation (e.g. "+", "sin"). */
    String symbol();

    /** How many operands this operation consumes (1 for unary, 2 for binary). */
    int arity();
}
