package com.calculator.core;

import com.calculator.exceptions.InvalidInputException;
import com.calculator.exceptions.MathDomainException;
import com.calculator.operations.AngleMode;
import com.calculator.operations.OperationFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Parses and evaluates a full mathematical expression entered as text
 * (e.g. {@code "sin(30) + 2 × √(9)"}).
 *
 * <p>It works in three classic stages:</p>
 * <ol>
 *   <li><b>Tokenize</b> the raw string into numbers, operators, functions
 *       and parentheses (also resolving constants {@code e} and {@code π}
 *       and inserting implicit multiplication, so {@code 2π} means
 *       {@code 2 × π}).</li>
 *   <li><b>Shunting-yard</b> the tokens into Reverse Polish Notation,
 *       respecting operator precedence and associativity.</li>
 *   <li><b>Evaluate</b> the RPN, delegating every actual computation to an
 *       {@link com.calculator.operations.Operation} obtained from
 *       {@link OperationFactory}.</li>
 * </ol>
 *
 * <p>Any malformed input is reported as an {@link InvalidInputException};
 * mathematically impossible results bubble up as the relevant
 * {@link com.calculator.exceptions.CalculatorException} subtype.</p>
 */
public class ExpressionEvaluator {

    private enum Type { NUMBER, OPERATOR, FUNCTION, POSTFIX, LPAREN, RPAREN }

    private static final class Token {
        final Type type;
        final String text;
        final double value;

        Token(Type type, String text) {
            this(type, text, 0.0);
        }

        Token(Type type, String text, double value) {
            this.type = type;
            this.text = text;
            this.value = value;
        }
    }

    /** Evaluate {@code expression} under the given trigonometric angle mode. */
    public double evaluate(String expression, AngleMode mode) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new InvalidInputException("Invalid Input");
        }
        List<Token> tokens = insertImplicitMultiplication(tokenize(expression));
        List<Token> rpn = toReversePolish(tokens);
        return evaluateRpn(rpn, mode);
    }

    // ------------------------------------------------------------------
    // Stage 1: tokenize
    // ------------------------------------------------------------------

    private List<Token> tokenize(String s) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        int n = s.length();
        while (i < n) {
            char c = s.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                int start = i;
                boolean dotSeen = false;
                while (i < n && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) {
                    if (s.charAt(i) == '.') {
                        if (dotSeen) {
                            throw new InvalidInputException("Malformed number near '"
                                    + s.substring(start, i + 1) + "'");
                        }
                        dotSeen = true;
                    }
                    i++;
                }
                // Optional scientific exponent, e.g. 1.2e-3 (also handles the
                // "1.0E10" form that Double formatting can produce).
                if (i < n && (s.charAt(i) == 'e' || s.charAt(i) == 'E')) {
                    int j = i + 1;
                    if (j < n && (s.charAt(j) == '+' || s.charAt(j) == '-')) {
                        j++;
                    }
                    if (j < n && Character.isDigit(s.charAt(j))) {
                        i = j;
                        while (i < n && Character.isDigit(s.charAt(i))) {
                            i++;
                        }
                    }
                }
                String num = s.substring(start, i);
                try {
                    tokens.add(new Token(Type.NUMBER, num, Double.parseDouble(num)));
                } catch (NumberFormatException ex) {
                    throw new InvalidInputException("Malformed number: '" + num + "'");
                }
                continue;
            }

            if (Character.isLetter(c)) {
                int start = i;
                while (i < n && Character.isLetter(s.charAt(i))) {
                    i++;
                }
                String word = s.substring(start, i).toLowerCase();
                switch (word) {
                    case "pi":
                    case "π": // π
                        tokens.add(new Token(Type.NUMBER, word, Math.PI));
                        break;
                    case "e":
                        tokens.add(new Token(Type.NUMBER, word, Math.E));
                        break;
                    default:
                        if (OperationFactory.isFunction(word)) {
                            tokens.add(new Token(Type.FUNCTION, word));
                        } else {
                            throw new InvalidInputException("Unknown symbol: '" + word + "'");
                        }
                }
                continue;
            }

            // Single-character symbols.
            Token prev = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1);
            switch (c) {
                case '(':
                    tokens.add(new Token(Type.LPAREN, "("));
                    break;
                case ')':
                    tokens.add(new Token(Type.RPAREN, ")"));
                    break;
                case '+':
                    tokens.add(new Token(Type.OPERATOR, isUnaryContext(prev) ? "u+" : "+"));
                    break;
                case '-':
                    tokens.add(new Token(Type.OPERATOR, isUnaryContext(prev) ? "u-" : "-"));
                    break;
                case '*':
                case '×': // ×
                    tokens.add(new Token(Type.OPERATOR, "×"));
                    break;
                case '/':
                case '÷': // ÷
                    tokens.add(new Token(Type.OPERATOR, "÷"));
                    break;
                case '^':
                    tokens.add(new Token(Type.OPERATOR, "^"));
                    break;
                case '√': // √
                    tokens.add(new Token(Type.FUNCTION, "sqrt"));
                    break;
                case '!':
                    tokens.add(new Token(Type.POSTFIX, "!"));
                    break;
                case '%':
                    tokens.add(new Token(Type.POSTFIX, "%"));
                    break;
                default:
                    throw new InvalidInputException("Unexpected character: '" + c + "'");
            }
            i++;
        }
        return tokens;
    }

    /** A leading +/- is unary at the start, or right after an operator or '('. */
    private boolean isUnaryContext(Token prev) {
        return prev == null || prev.type == Type.OPERATOR || prev.type == Type.LPAREN;
    }

    /**
     * Insert an explicit multiplication between a value-producing token and a
     * value-starting token, so users can write {@code 2π}, {@code 2(3+1)} or
     * {@code 2sin(0)} naturally.
     */
    private List<Token> insertImplicitMultiplication(List<Token> tokens) {
        List<Token> out = new ArrayList<>(tokens.size());
        for (int k = 0; k < tokens.size(); k++) {
            Token cur = tokens.get(k);
            out.add(cur);
            if (k + 1 >= tokens.size()) {
                continue;
            }
            Token next = tokens.get(k + 1);
            boolean curEndsValue = cur.type == Type.NUMBER
                    || cur.type == Type.RPAREN
                    || cur.type == Type.POSTFIX;
            boolean nextStartsValue = next.type == Type.NUMBER
                    || next.type == Type.LPAREN
                    || next.type == Type.FUNCTION;
            if (curEndsValue && nextStartsValue) {
                out.add(new Token(Type.OPERATOR, "×"));
            }
        }
        return out;
    }

    // ------------------------------------------------------------------
    // Stage 2: shunting-yard -> RPN
    // ------------------------------------------------------------------

    private int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 2;
            case "×":
            case "÷":
                return 3;
            case "u+":
            case "u-":
                return 4;
            case "^":
                return 5;
            default:
                return 0;
        }
    }

    private boolean isRightAssociative(String op) {
        return op.equals("^") || op.equals("u+") || op.equals("u-");
    }

    private List<Token> toReversePolish(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        Deque<Token> ops = new ArrayDeque<>();

        for (Token t : tokens) {
            switch (t.type) {
                case NUMBER:
                    output.add(t);
                    break;
                case FUNCTION:
                    ops.push(t);
                    break;
                case POSTFIX:
                    // A postfix operator acts on the value already emitted.
                    output.add(t);
                    break;
                case OPERATOR:
                    while (!ops.isEmpty() && ops.peek().type == Type.OPERATOR) {
                        Token top = ops.peek();
                        boolean pop = precedence(top.text) > precedence(t.text)
                                || (precedence(top.text) == precedence(t.text)
                                        && !isRightAssociative(t.text));
                        if (!pop) {
                            break;
                        }
                        output.add(ops.pop());
                    }
                    ops.push(t);
                    break;
                case LPAREN:
                    ops.push(t);
                    break;
                case RPAREN:
                    while (!ops.isEmpty() && ops.peek().type != Type.LPAREN) {
                        output.add(ops.pop());
                    }
                    if (ops.isEmpty()) {
                        throw new InvalidInputException("Mismatched parentheses");
                    }
                    ops.pop(); // discard the '('
                    if (!ops.isEmpty() && ops.peek().type == Type.FUNCTION) {
                        output.add(ops.pop());
                    }
                    break;
                default:
                    throw new InvalidInputException("Unexpected token: " + t.text);
            }
        }

        while (!ops.isEmpty()) {
            Token top = ops.pop();
            if (top.type == Type.LPAREN) {
                throw new InvalidInputException("Mismatched parentheses");
            }
            output.add(top);
        }
        return output;
    }

    // ------------------------------------------------------------------
    // Stage 3: evaluate RPN
    // ------------------------------------------------------------------

    private double evaluateRpn(List<Token> rpn, AngleMode mode) {
        Deque<Double> stack = new ArrayDeque<>();
        for (Token t : rpn) {
            switch (t.type) {
                case NUMBER:
                    stack.push(t.value);
                    break;
                case OPERATOR:
                    if (t.text.equals("u-")) {
                        stack.push(-requireOperand(stack));
                    } else if (t.text.equals("u+")) {
                        stack.push(requireOperand(stack));
                    } else {
                        double b = requireOperand(stack);
                        double a = requireOperand(stack);
                        stack.push(OperationFactory.forToken(t.text, mode).apply(a, b));
                    }
                    break;
                case FUNCTION:
                case POSTFIX:
                    double x = requireOperand(stack);
                    stack.push(OperationFactory.forToken(t.text, mode).apply(x));
                    break;
                default:
                    throw new InvalidInputException("Invalid Input");
            }
        }

        if (stack.size() != 1) {
            throw new InvalidInputException("Invalid Input");
        }
        double result = stack.pop();
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new MathDomainException("Result is undefined or out of range");
        }
        return result;
    }

    private double requireOperand(Deque<Double> stack) {
        if (stack.isEmpty()) {
            throw new InvalidInputException("Invalid Input");
        }
        return stack.pop();
    }
}
