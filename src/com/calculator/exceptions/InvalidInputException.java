package com.calculator.exceptions;

/**
 * Raised when the user's input cannot be parsed into a valid
 * mathematical expression (unbalanced parentheses, stray operators,
 * unknown symbols, empty input, etc.). Surfaced as "Invalid Input".
 */
public class InvalidInputException extends CalculatorException {

    public InvalidInputException(String message) {
        super(message);
    }
}
