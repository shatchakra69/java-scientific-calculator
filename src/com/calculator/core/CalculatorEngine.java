package com.calculator.core;

import com.calculator.exceptions.CalculatorException;
import com.calculator.operations.AngleMode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The calculator's "brain" — the single module the GUI talks to.
 *
 * <p>It owns the {@link ExpressionEvaluator}, the {@link Memory} register,
 * the {@link AngleMode} and the running calculation {@link #getHistory()
 * history}, and exposes simple, well-named methods so the GUI never has to
 * know how any of the maths is actually done (abstraction). Each piece is a
 * separate, single-responsibility class (modularity).</p>
 */
public class CalculatorEngine {

    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();
    private final Memory memory = new Memory();
    private final List<CalculationRecord> history = new ArrayList<>();

    private AngleMode angleMode = AngleMode.DEGREES;
    private double lastValue = 0.0;

    // ------------------------------------------------------------------
    // Evaluation
    // ------------------------------------------------------------------

    /**
     * Evaluate an expression, format the result, append it to the history
     * and return the formatted text. Any problem is thrown as a
     * {@link com.calculator.exceptions.CalculatorException}.
     */
    public String evaluate(String expression) {
        double result = evaluator.evaluate(expression, angleMode);
        lastValue = result;
        String formatted = format(result);
        history.add(new CalculationRecord(expression.trim(), formatted));
        return formatted;
    }

    /**
     * Evaluate an expression to a raw number without recording history.
     * Used for memory operations and the live result preview.
     */
    public double evaluateToNumber(String expression) {
        double result = evaluator.evaluate(expression, angleMode);
        lastValue = result;
        return result;
    }

    /**
     * Best-effort live preview: returns the formatted result of a (possibly
     * incomplete) expression, or {@code null} if it cannot be evaluated yet.
     * Has no side effects — it never records history or changes state.
     */
    public String preview(String expression) {
        try {
            return format(evaluator.evaluate(expression, angleMode));
        } catch (CalculatorException ex) {
            return null;
        }
    }

    /** The numeric value of the most recent successful evaluation. */
    public double getLastValue() {
        return lastValue;
    }

    // ------------------------------------------------------------------
    // Angle mode
    // ------------------------------------------------------------------

    public AngleMode getAngleMode() {
        return angleMode;
    }

    public void setAngleMode(AngleMode mode) {
        this.angleMode = mode;
    }

    /** Flip between degrees and radians; returns the new mode. */
    public AngleMode toggleAngleMode() {
        angleMode = (angleMode == AngleMode.DEGREES) ? AngleMode.RADIANS : AngleMode.DEGREES;
        return angleMode;
    }

    // ------------------------------------------------------------------
    // Memory (delegates to the Memory module)
    // ------------------------------------------------------------------

    public void memoryStore(double value)    { memory.store(value); }
    public double memoryRecall()              { return memory.recall(); }
    public void memoryClear()                 { memory.clear(); }
    public void memoryAdd(double value)       { memory.add(value); }
    public void memorySubtract(double value)  { memory.subtract(value); }
    public boolean isMemorySet()              { return memory.isSet(); }

    // ------------------------------------------------------------------
    // History
    // ------------------------------------------------------------------

    /** Read-only view of the calculation history, oldest first. */
    public List<CalculationRecord> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void clearHistory() {
        history.clear();
    }

    // ------------------------------------------------------------------
    // Formatting
    // ------------------------------------------------------------------

    /**
     * Format a result for display: whole numbers show without a decimal
     * point, other values are rounded to 12 significant digits with trailing
     * zeros stripped, and only genuinely tiny/huge magnitudes fall back to
     * scientific notation.
     */
    public static String format(double value) {
        if (value == 0.0) {
            return "0";
        }
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return Long.toString((long) value);
        }
        BigDecimal rounded = new BigDecimal(value, new MathContext(12)).stripTrailingZeros();
        double magnitude = Math.abs(value);
        if (magnitude < 1e-4 || magnitude >= 1e12) {
            return rounded.toString(); // scientific notation is clearer here
        }
        return rounded.toPlainString();
    }
}
