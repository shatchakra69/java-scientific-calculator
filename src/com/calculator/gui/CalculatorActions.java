package com.calculator.gui;

import com.calculator.core.CalculationRecord;

/**
 * The set of user actions the calculator can perform.
 *
 * <p>The button/memory panels are constructed against this interface, so
 * they fire intentions ("insert this text", "evaluate", "store to memory")
 * without holding a reference to the engine or knowing how the work is done.
 * {@link CalculatorFrame} provides the implementation.</p>
 */
public interface CalculatorActions {

    /** Insert literal text (a digit, operator or function) at the caret. */
    void insert(String text);

    /** Insert the most recent answer at the caret. */
    void insertAnswer();

    /** Evaluate the current expression ("="). */
    void evaluate();

    /** Clear the whole expression ("C"). */
    void clearAll();

    /** Delete the character before the caret ("⌫"). */
    void backspace();

    /** Toggle the sign of the trailing number ("+/-"). */
    void toggleSign();

    /** Switch between degrees and radians. */
    void toggleAngleMode();

    void memoryStore();     // MS
    void memoryRecall();    // MR
    void memoryClear();     // MC
    void memoryAdd();       // M+
    void memorySubtract();  // M-

    /** Remove a single entry from the calculation history. */
    void deleteHistory(CalculationRecord record);

    /** Clear the calculation history. */
    void clearHistory();
}
