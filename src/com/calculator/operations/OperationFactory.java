package com.calculator.operations;

import com.calculator.exceptions.InvalidInputException;

/**
 * Translates a canonical token (produced by the expression evaluator) into
 * the concrete {@link Operation} that implements it.
 *
 * <p>This is the single place where token strings meet classes. Everywhere
 * else in the program works against the {@link Operation} abstraction, so
 * adding a new operation only means adding a new class and one line here.</p>
 */
public final class OperationFactory {

    private OperationFactory() {
        // Utility class: no instances.
    }

    /**
     * @param token the canonical operator/function symbol
     * @param mode  the active angle mode, used by trigonometric operations
     * @return an {@link Operation} ready to evaluate, never {@code null}
     * @throws InvalidInputException if the token is not a known operation
     */
    public static Operation forToken(String token, AngleMode mode) {
        switch (token) {
            // --- binary arithmetic ---
            case "+": return new ArithmeticOperation(ArithmeticOperation.Operator.ADD);
            case "-": return new ArithmeticOperation(ArithmeticOperation.Operator.SUBTRACT);
            case "×": return new ArithmeticOperation(ArithmeticOperation.Operator.MULTIPLY);
            case "÷": return new ArithmeticOperation(ArithmeticOperation.Operator.DIVIDE);
            case "^": return new PowerOperation();

            // --- trigonometry (mode-aware) ---
            case "sin":  return new TrigonometricOperation(TrigonometricOperation.Function.SIN, mode);
            case "cos":  return new TrigonometricOperation(TrigonometricOperation.Function.COS, mode);
            case "tan":  return new TrigonometricOperation(TrigonometricOperation.Function.TAN, mode);
            case "asin": return new TrigonometricOperation(TrigonometricOperation.Function.ASIN, mode);
            case "acos": return new TrigonometricOperation(TrigonometricOperation.Function.ACOS, mode);
            case "atan": return new TrigonometricOperation(TrigonometricOperation.Function.ATAN, mode);

            // --- hyperbolic (mode-independent) ---
            case "sinh":  return new HyperbolicOperation(HyperbolicOperation.Function.SINH);
            case "cosh":  return new HyperbolicOperation(HyperbolicOperation.Function.COSH);
            case "tanh":  return new HyperbolicOperation(HyperbolicOperation.Function.TANH);
            case "asinh": return new HyperbolicOperation(HyperbolicOperation.Function.ASINH);
            case "acosh": return new HyperbolicOperation(HyperbolicOperation.Function.ACOSH);
            case "atanh": return new HyperbolicOperation(HyperbolicOperation.Function.ATANH);

            // --- logarithms & exponential ---
            case "log": return new LogarithmicOperation(LogarithmicOperation.Function.LOG10);
            case "ln":  return new LogarithmicOperation(LogarithmicOperation.Function.LN);
            case "exp": return new ExponentialOperation();

            // --- other unary functions ---
            case "sqrt": return new RootOperation();
            case "cbrt": return new CubeRootOperation();
            case "!":    return new FactorialOperation();
            case "%":    return new PercentageOperation();

            default:
                throw new InvalidInputException("Unknown operation: " + token);
        }
    }

    /** Whether {@code name} is a recognised prefix function such as sin or log. */
    public static boolean isFunction(String name) {
        switch (name) {
            case "sin": case "cos": case "tan":
            case "asin": case "acos": case "atan":
            case "sinh": case "cosh": case "tanh":
            case "asinh": case "acosh": case "atanh":
            case "log": case "ln": case "exp":
            case "sqrt": case "cbrt":
                return true;
            default:
                return false;
        }
    }
}
