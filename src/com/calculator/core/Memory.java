package com.calculator.core;

/**
 * The calculator's single memory register, backing the MS / MR / MC / M+ /
 * M- buttons.
 *
 * <p>Encapsulation: the stored value and the "is anything stored?" flag are
 * private. Callers can only change them through the well-defined memory
 * operations, which keeps the two fields consistent (storing or adding sets
 * the flag; clearing resets it).</p>
 */
public class Memory {

    private double value;
    private boolean set;

    /** MS — replace the stored value. */
    public void store(double newValue) {
        this.value = newValue;
        this.set = true;
    }

    /** MR — read the stored value (0 if nothing has been stored). */
    public double recall() {
        return value;
    }

    /** MC — forget the stored value. */
    public void clear() {
        this.value = 0.0;
        this.set = false;
    }

    /** M+ — add to the stored value. */
    public void add(double amount) {
        this.value += amount;
        this.set = true;
    }

    /** M- — subtract from the stored value. */
    public void subtract(double amount) {
        this.value -= amount;
        this.set = true;
    }

    /** Whether a value is currently held (used to show the "M" indicator). */
    public boolean isSet() {
        return set;
    }
}
