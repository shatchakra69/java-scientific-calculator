package com.calculator;

import com.calculator.core.CalculatorEngine;
import com.calculator.exceptions.DivisionByZeroException;
import com.calculator.exceptions.InvalidInputException;
import com.calculator.exceptions.MathDomainException;
import com.calculator.operations.AngleMode;

/**
 * A lightweight, dependency-free test runner for the calculation engine.
 *
 * <p>It is not part of the application; it exists so the maths can be
 * verified from the command line without opening the GUI. Run it with:</p>
 * <pre>  java -cp out com.calculator.CalculatorEngineTest</pre>
 * The process exits with a non-zero status if any check fails.
 */
public final class CalculatorEngineTest {

    private static int passed = 0;
    private static int failed = 0;
    private static final double EPS = 1e-9;

    public static void main(String[] args) {
        arithmetic();
        precedenceAndParentheses();
        powersAndRoots();
        unaryMinus();
        factorialAndPercent();
        constantsAndImplicitMultiplication();
        trigonometry();
        hyperbolicAndExtras();
        logarithmsAndExponential();
        memory();
        formatting();
        errorHandling();

        System.out.println();
        System.out.println("Passed: " + passed + ", Failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void arithmetic() {
        CalculatorEngine e = new CalculatorEngine();
        approx("2+3", e.evaluateToNumber("2+3"), 5);
        approx("10-4", e.evaluateToNumber("10-4"), 6);
        approx("6×7", e.evaluateToNumber("6×7"), 42);
        approx("6*7", e.evaluateToNumber("6*7"), 42);
        approx("8÷2", e.evaluateToNumber("8÷2"), 4);
        approx("8/2", e.evaluateToNumber("8/2"), 4);
        approx("3.5+1.5", e.evaluateToNumber("3.5+1.5"), 5);
    }

    private static void precedenceAndParentheses() {
        CalculatorEngine e = new CalculatorEngine();
        approx("2+3*4", e.evaluateToNumber("2+3*4"), 14);
        approx("(2+3)*4", e.evaluateToNumber("(2+3)*4"), 20);
        approx("2*(3+4)-5", e.evaluateToNumber("2*(3+4)-5"), 9);
    }

    private static void powersAndRoots() {
        CalculatorEngine e = new CalculatorEngine();
        approx("2^10", e.evaluateToNumber("2^10"), 1024);
        approx("2^3^2 (right assoc)", e.evaluateToNumber("2^3^2"), 512);
        approx("√(9)", e.evaluateToNumber("√(9)"), 3);
        approx("√(2)", e.evaluateToNumber("√(2)"), Math.sqrt(2));
        approx("10^(3)", e.evaluateToNumber("10^(3)"), 1000);
        approx("3^2 via x²", e.evaluateToNumber("3^2"), 9);
    }

    private static void unaryMinus() {
        CalculatorEngine e = new CalculatorEngine();
        approx("-5+2", e.evaluateToNumber("-5+2"), -3);
        approx("-3^2 = -(3^2)", e.evaluateToNumber("-3^2"), -9);
        approx("(-3)^2", e.evaluateToNumber("(-3)^2"), 9);
        approx("--3", e.evaluateToNumber("--3"), 3);
    }

    private static void factorialAndPercent() {
        CalculatorEngine e = new CalculatorEngine();
        approx("5!", e.evaluateToNumber("5!"), 120);
        approx("0!", e.evaluateToNumber("0!"), 1);
        approx("50%", e.evaluateToNumber("50%"), 0.5);
        approx("200×15%", e.evaluateToNumber("200×15%"), 30);
    }

    private static void constantsAndImplicitMultiplication() {
        CalculatorEngine e = new CalculatorEngine();
        approx("π", e.evaluateToNumber("π"), Math.PI);
        approx("e", e.evaluateToNumber("e"), Math.E);
        approx("2π", e.evaluateToNumber("2π"), 2 * Math.PI);
        approx("2(3+1)", e.evaluateToNumber("2(3+1)"), 8);
    }

    private static void trigonometry() {
        CalculatorEngine e = new CalculatorEngine();
        e.setAngleMode(AngleMode.DEGREES);
        approx("sin(30) DEG", e.evaluateToNumber("sin(30)"), 0.5);
        approx("cos(60) DEG", e.evaluateToNumber("cos(60)"), 0.5);
        approx("tan(45) DEG", e.evaluateToNumber("tan(45)"), 1);
        approx("asin(0.5) DEG", e.evaluateToNumber("asin(0.5)"), 30);
        approx("atan(1) DEG", e.evaluateToNumber("atan(1)"), 45);

        e.setAngleMode(AngleMode.RADIANS);
        approx("sin(0) RAD", e.evaluateToNumber("sin(0)"), 0);
        approx("cos(0) RAD", e.evaluateToNumber("cos(0)"), 1);
    }

    private static void hyperbolicAndExtras() {
        CalculatorEngine e = new CalculatorEngine();
        approx("sinh(0)", e.evaluateToNumber("sinh(0)"), 0);
        approx("cosh(0)", e.evaluateToNumber("cosh(0)"), 1);
        approx("tanh(0)", e.evaluateToNumber("tanh(0)"), 0);
        approx("asinh(0)", e.evaluateToNumber("asinh(0)"), 0);
        approx("acosh(1)", e.evaluateToNumber("acosh(1)"), 0);
        approx("atanh(0)", e.evaluateToNumber("atanh(0)"), 0);
        approx("cbrt(-8)", e.evaluateToNumber("cbrt(-8)"), -2);
        approx("8^(1/3) yth root", e.evaluateToNumber("8^(1/3)"), 2);
        approx("2^(-1) reciprocal", e.evaluateToNumber("2^(-1)"), 0.5);
        approx("2^3 (x³)", e.evaluateToNumber("2^3"), 8);
        approx("1.5E3 (EE)", e.evaluateToNumber("1.5E3"), 1500);

        expect("atanh domain", MathDomainException.class, () -> evalNum("atanh(1)"));
        expect("acosh domain", MathDomainException.class, () -> evalNum("acosh(0)"));
    }

    private static void logarithmsAndExponential() {
        CalculatorEngine e = new CalculatorEngine();
        approx("log(1000)", e.evaluateToNumber("log(1000)"), 3);
        approx("ln(e)", e.evaluateToNumber("ln(e)"), 1);
        approx("exp(0)", e.evaluateToNumber("exp(0)"), 1);
        approx("exp(1)", e.evaluateToNumber("exp(1)"), Math.E);
    }

    private static void memory() {
        CalculatorEngine e = new CalculatorEngine();
        check("memory empty initially", !e.isMemorySet());
        e.memoryStore(10);
        check("memory set after store", e.isMemorySet());
        approx("MR after MS 10", e.memoryRecall(), 10);
        e.memoryAdd(5);
        approx("M+ 5 -> 15", e.memoryRecall(), 15);
        e.memorySubtract(3);
        approx("M- 3 -> 12", e.memoryRecall(), 12);
        e.memoryClear();
        check("memory cleared", !e.isMemorySet());
        approx("MR after MC", e.memoryRecall(), 0);
    }

    private static void formatting() {
        equalsStr("format(4.0)", CalculatorEngine.format(4.0), "4");
        equalsStr("format(0.5)", CalculatorEngine.format(0.5), "0.5");
        equalsStr("format(0.0)", CalculatorEngine.format(0.0), "0");
        equalsStr("format(1000000.0)", CalculatorEngine.format(1_000_000.0), "1000000");
        check("format(1/3) starts 0.3333",
                CalculatorEngine.format(1.0 / 3.0).startsWith("0.3333"));
    }

    private static void errorHandling() {
        expect("divide by zero", DivisionByZeroException.class, () -> evalNum("1÷0"));
        expect("sqrt of negative", MathDomainException.class, () -> evalNum("√(-1)"));
        expect("log of zero", MathDomainException.class, () -> evalNum("log(0)"));
        expect("ln of negative", MathDomainException.class, () -> evalNum("ln(-2)"));
        expect("asin out of range", MathDomainException.class, () -> evalNum("asin(2)"));
        expect("factorial of negative", MathDomainException.class, () -> evalNum("(-3)!"));
        expect("factorial of non-integer", MathDomainException.class, () -> evalNum("2.5!"));
        expect("empty input", InvalidInputException.class, () -> evalNum(""));
        expect("trailing operator", InvalidInputException.class, () -> evalNum("2+"));
        expect("mismatched parens", InvalidInputException.class, () -> evalNum("(2+3"));
        expect("unknown symbol", InvalidInputException.class, () -> evalNum("foo(2)"));
    }

    // ------------------------------------------------------------------
    // Tiny assertion helpers
    // ------------------------------------------------------------------

    private static double evalNum(String expr) {
        return new CalculatorEngine().evaluateToNumber(expr);
    }

    private static void approx(String name, double actual, double expected) {
        if (Math.abs(actual - expected) <= EPS) {
            pass(name);
        } else {
            fail(name + " expected " + expected + " but got " + actual);
        }
    }

    private static void equalsStr(String name, String actual, String expected) {
        if (expected.equals(actual)) {
            pass(name);
        } else {
            fail(name + " expected \"" + expected + "\" but got \"" + actual + "\"");
        }
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            pass(name);
        } else {
            fail(name);
        }
    }

    private static void expect(String name, Class<? extends Throwable> type, Runnable action) {
        try {
            action.run();
            fail(name + " expected " + type.getSimpleName() + " but nothing was thrown");
        } catch (Throwable thrown) {
            if (type.isInstance(thrown)) {
                pass(name);
            } else {
                fail(name + " expected " + type.getSimpleName()
                        + " but got " + thrown.getClass().getSimpleName());
            }
        }
    }

    private static void pass(String name) {
        passed++;
        System.out.println("  PASS  " + name);
    }

    private static void fail(String name) {
        failed++;
        System.out.println("  FAIL  " + name);
    }
}
