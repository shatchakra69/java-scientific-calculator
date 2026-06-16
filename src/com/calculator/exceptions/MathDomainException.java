package com.calculator.exceptions;

/**
 * Raised when an operation is given arguments outside its mathematical
 * domain, e.g. the square root of a negative number, the logarithm of a
 * non-positive number, the factorial of a negative or non-integer value,
 * or an inverse-trig argument outside [-1, 1].
 */
public class MathDomainException extends CalculatorException {

    public MathDomainException(String message) {
        super(message);
    }
}
