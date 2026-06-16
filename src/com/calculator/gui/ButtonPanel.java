package com.calculator.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * The calculator keypad. The main view stays uncluttered: it shows the
 * numeric keys, the basic operators, parentheses, and two category buttons —
 * <b>Trig</b> and <b>Math</b> — that open pop-up panels holding the scientific
 * functions. This keeps the advanced functions one click away instead of
 * filling the main window.
 *
 * <p>Every key is an {@link AnimatedButton}. Clicking one routes through
 * {@link #dispatch(String)}, which maps the visible label to the right
 * {@link CalculatorActions} call (the label and the inserted text often
 * differ, e.g. "eˣ" inserts {@code "exp("} and "1/x" inserts {@code "^(-1)"}).</p>
 */
public class ButtonPanel extends JPanel {

    private static final Color DIGIT_BG    = new Color(0xFA, 0xFA, 0xFA);
    private static final Color FUNCTION_BG = new Color(0xEC, 0xEF, 0xF1);
    private static final Color OPERATOR_BG = new Color(0xE3, 0xF2, 0xFD);
    private static final Color EQUALS_BG   = new Color(0x15, 0x65, 0xC0);

    private final CalculatorActions actions;
    private final JPopupMenu trigPopup;
    private final JPopupMenu mathPopup;

    public ButtonPanel(CalculatorActions actions) {
        super(new BorderLayout(8, 8));
        this.actions = actions;
        setBorder(BorderFactory.createEmptyBorder(4, 12, 12, 12));
        setBackground(Color.WHITE);

        trigPopup = buildTrigPopup();
        mathPopup = buildMathPopup();

        add(buildCategoryRow(), BorderLayout.NORTH);
        add(buildBasicGrid(), BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------
    // Main view: category row + basic keypad
    // ------------------------------------------------------------------

    private JPanel buildCategoryRow() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 6, 6));
        grid.setBackground(Color.WHITE);
        grid.add(categoryButton("Trig ▾", trigPopup));
        grid.add(categoryButton("Math ▾", mathPopup));
        grid.add(makeButton("(", FUNCTION_BG, Color.BLACK, 16f));
        grid.add(makeButton(")", FUNCTION_BG, Color.BLACK, 16f));
        return grid;
    }

    private JPanel buildBasicGrid() {
        JPanel grid = new JPanel(new GridLayout(5, 4, 6, 6));
        grid.setBackground(Color.WHITE);
        String[] keys = {
                "C", "⌫", "±", "÷",
                "7", "8", "9", "×",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "Ans", "="
        };
        for (String key : keys) {
            grid.add(makeButton(key, backgroundFor(key), foregroundFor(key), 18f));
        }
        return grid;
    }

    // ------------------------------------------------------------------
    // Function pop-ups
    // ------------------------------------------------------------------

    private JPopupMenu buildTrigPopup() {
        JPopupMenu popup = new JPopupMenu();
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        panel.add(popupButton("Deg/Rad", popup, OPERATOR_BG), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(4, 3, 6, 6));
        grid.setBackground(Color.WHITE);
        String[] keys = {
                "sin",   "cos",   "tan",
                "asin",  "acos",  "atan",
                "sinh",  "cosh",  "tanh",
                "asinh", "acosh", "atanh"
        };
        for (String key : keys) {
            grid.add(popupButton(key, popup, FUNCTION_BG));
        }
        panel.add(grid, BorderLayout.CENTER);

        popup.add(panel);
        return popup;
    }

    private JPopupMenu buildMathPopup() {
        JPopupMenu popup = new JPopupMenu();
        JPanel grid = new JPanel(new GridLayout(0, 3, 6, 6));
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        String[] keys = {
                "x²",  "x³",  "xʸ",
                "√",   "∛",   "ʸ√x",
                "1/x", "n!",  "%",
                "log", "ln",  "EE",
                "eˣ",  "10ˣ", "Rand",
                "π",   "e"
        };
        for (String key : keys) {
            grid.add(popupButton(key, popup, FUNCTION_BG));
        }
        popup.add(grid);
        return popup;
    }

    // ------------------------------------------------------------------
    // Button construction
    // ------------------------------------------------------------------

    /** A main-row button that opens a function pop-up beneath itself. */
    private AnimatedButton categoryButton(String label, JPopupMenu popup) {
        AnimatedButton button = new AnimatedButton(label, OPERATOR_BG, Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.addActionListener(e -> popup.show(button, 0, button.getHeight()));
        return button;
    }

    /** A button inside a pop-up: inserts its function, then closes the pop-up. */
    private AnimatedButton popupButton(String label, JPopupMenu popup, Color background) {
        AnimatedButton button = new AnimatedButton(label, background, Color.BLACK);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(70, 38));
        button.addActionListener(e -> {
            dispatch(label);
            popup.setVisible(false);
        });
        return button;
    }

    private Color backgroundFor(String key) {
        switch (key) {
            case "=":
                return EQUALS_BG;
            case "÷": case "×": case "-": case "+":
                return OPERATOR_BG;
            case "C": case "⌫": case "±": case "Ans":
                return FUNCTION_BG;
            default:
                return DIGIT_BG;
        }
    }

    private Color foregroundFor(String key) {
        return key.equals("=") ? Color.WHITE : Color.BLACK;
    }

    private JButton makeButton(String label, Color background, Color foreground, float fontSize) {
        AnimatedButton button = new AnimatedButton(label, background, foreground);
        button.setFont(new Font("SansSerif", Font.PLAIN, Math.round(fontSize)));
        button.addActionListener(e -> dispatch(label));
        return button;
    }

    // ------------------------------------------------------------------
    // Label → action
    // ------------------------------------------------------------------

    /** Map a button label to the corresponding action. */
    private void dispatch(String label) {
        switch (label) {
            // Special, non-insert actions.
            case "Deg/Rad": actions.toggleAngleMode(); break;
            case "C":       actions.clearAll();        break;
            case "⌫":       actions.backspace();        break;
            case "±":       actions.toggleSign();      break;
            case "=":       actions.evaluate();        break;
            case "Ans":     actions.insertAnswer();    break;
            case "Rand":    actions.insert(randomNumber()); break;

            // Trigonometric functions.
            case "sin":  actions.insert("sin(");  break;
            case "cos":  actions.insert("cos(");  break;
            case "tan":  actions.insert("tan(");  break;
            case "asin": actions.insert("asin("); break;
            case "acos": actions.insert("acos("); break;
            case "atan": actions.insert("atan("); break;

            // Hyperbolic functions.
            case "sinh":  actions.insert("sinh(");  break;
            case "cosh":  actions.insert("cosh(");  break;
            case "tanh":  actions.insert("tanh(");  break;
            case "asinh": actions.insert("asinh("); break;
            case "acosh": actions.insert("acosh("); break;
            case "atanh": actions.insert("atanh("); break;

            // Logarithms / exponentials.
            case "log": actions.insert("log(");  break;
            case "ln":  actions.insert("ln(");   break;
            case "eˣ":  actions.insert("exp(");  break;
            case "10ˣ": actions.insert("10^(");  break;

            // Powers and roots.
            case "√":    actions.insert("√(");    break;
            case "∛":    actions.insert("cbrt("); break;
            case "ʸ√x":  actions.insert("^(1/");  break; // base ^ (1/root)
            case "x²":   actions.insert("^2");    break;
            case "x³":   actions.insert("^3");    break;
            case "xʸ":   actions.insert("^");     break;
            case "1/x":  actions.insert("^(-1)"); break;

            // Other functions.
            case "n!": actions.insert("!"); break;
            case "EE": actions.insert("E"); break; // scientific-notation exponent

            // Everything else inserts its own label verbatim
            // (digits, ".", "(", ")", "π", "e", "+", "-", "×", "÷").
            default: actions.insert(label); break;
        }
    }

    private String randomNumber() {
        return String.format("%.6f", Math.random());
    }
}
