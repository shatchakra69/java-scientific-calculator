package com.calculator.core;

/**
 * An immutable record of one completed calculation: the expression the user
 * entered and the formatted result it produced. Instances are created by the
 * {@link CalculatorEngine} and displayed by the history panel.
 *
 * <p>Encapsulation: both fields are private and final and exposed only
 * through getters, so a history entry can never be altered after the fact.</p>
 */
public class CalculationRecord {

    private final String expression;
    private final String result;

    public CalculationRecord(String expression, String result) {
        this.expression = expression;
        this.result = result;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return expression + " = " + result;
    }
}
