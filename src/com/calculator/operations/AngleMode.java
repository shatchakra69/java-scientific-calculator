package com.calculator.operations;

/**
 * Whether trigonometric functions interpret/produce angles in degrees or
 * radians. The GUI exposes this as a DEG/RAD toggle and the engine passes
 * the current mode into every {@link TrigonometricOperation}.
 */
public enum AngleMode {
    DEGREES,
    RADIANS;

    /** Convert an angle expressed in this mode into radians for java.lang.Math. */
    public double toRadians(double angle) {
        return this == DEGREES ? Math.toRadians(angle) : angle;
    }

    /** Convert a radian result from java.lang.Math back into this mode. */
    public double fromRadians(double radians) {
        return this == DEGREES ? Math.toDegrees(radians) : radians;
    }
}
