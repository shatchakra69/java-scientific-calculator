package com.calculator.exceptions;

/**
 * Base type for all errors raised by the calculator's logic layer.
 *
 * <p>Using a dedicated exception hierarchy (instead of throwing generic
 * {@link RuntimeException}s) lets the GUI catch calculator problems
 * specifically and translate them into friendly messages such as
 * "Division by Zero" or "Invalid Input", while letting genuinely
 * unexpected JVM errors propagate normally.</p>
 */
public class CalculatorException extends RuntimeException {

    public CalculatorException(String message) {
        super(message);
    }

    public CalculatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
