package com.calculator.operations;

import com.calculator.exceptions.DivisionByZeroException;

/**
 * Binary arithmetic: addition, subtraction, multiplication and division.
 * Demonstrates inheritance from {@link MathOperation} and overrides
 * {@link #apply(double...)} with arithmetic-specific behaviour, including
 * guarding against division by zero.
 */
public class ArithmeticOperation extends MathOperation {

    /** The specific arithmetic operator this instance represents. */
    public enum Operator {
        ADD("+"),
        SUBTRACT("-"),
        MULTIPLY("×"),
        DIVIDE("÷");

        private final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }
    }

    private final Operator operator;

    public ArithmeticOperation(Operator operator) {
        super(operator.symbol(), 2);
        this.operator = operator;
    }

    @Override
    public double apply(double... operands) {
        checkArity(operands);
        double a = operands[0];
        double b = operands[1];
        switch (operator) {
            case ADD:
                return a + b;
            case SUBTRACT:
                return a - b;
            case MULTIPLY:
                return a * b;
            case DIVIDE:
                if (b == 0.0) {
                    throw new DivisionByZeroException();
                }
                return a / b;
            default:
                // Unreachable; every enum constant is handled above.
                throw new IllegalStateException("Unhandled operator: " + operator);
        }
    }
}
