package com.calculator.exceptions;

/**
 * Raised when an expression attempts to divide by zero (or take the
 * remainder by zero). Surfaced in the display as "Division by Zero".
 */
public class DivisionByZeroException extends CalculatorException {

    public DivisionByZeroException() {
        super("Division by Zero");
    }

    public DivisionByZeroException(String message) {
        super(message);
    }
}
