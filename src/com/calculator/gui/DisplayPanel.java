package com.calculator.gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

/**
 * The top display: a status row (angle mode + memory indicator), the
 * editable expression field, and a secondary line that shows a live result
 * preview, the committed result, or an error message.
 *
 * <p>It is a self-contained GUI component (modularity) and hides how the
 * text field is manipulated behind methods like {@link #insert(String)} and
 * {@link #backspace()} (encapsulation).</p>
 */
public class DisplayPanel extends JPanel {

    private final JLabel modeLabel = new JLabel("DEG");
    private final JLabel memoryLabel = new JLabel(" ");
    private final JTextField expressionField = new JTextField();
    private final JLabel secondaryLabel = new JLabel(" ");

    private final Color normalColor = new Color(0x33, 0x33, 0x33);
    private final Color errorColor = new Color(0xC6, 0x28, 0x28);

    public DisplayPanel() {
        super(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        setBackground(Color.WHITE);

        JPanel statusRow = new JPanel(new BorderLayout());
        statusRow.setBackground(Color.WHITE);
        modeLabel.setForeground(new Color(0x15, 0x65, 0xC0));
        modeLabel.setFont(modeLabel.getFont().deriveFont(Font.BOLD, 13f));
        memoryLabel.setForeground(new Color(0x15, 0x65, 0xC0));
        memoryLabel.setFont(memoryLabel.getFont().deriveFont(Font.BOLD, 13f));
        memoryLabel.setHorizontalAlignment(JLabel.RIGHT);
        statusRow.add(modeLabel, BorderLayout.WEST);
        statusRow.add(memoryLabel, BorderLayout.EAST);

        expressionField.setHorizontalAlignment(JTextField.RIGHT);
        expressionField.setFont(new Font("SansSerif", Font.PLAIN, 30));
        expressionField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        expressionField.setForeground(normalColor);

        secondaryLabel.setHorizontalAlignment(JLabel.RIGHT);
        secondaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        secondaryLabel.setForeground(Color.GRAY);

        add(statusRow, BorderLayout.NORTH);
        add(expressionField, BorderLayout.CENTER);
        add(secondaryLabel, BorderLayout.SOUTH);
    }

    /** Exposed so the frame can attach key/Enter listeners and focus it. */
    public JTextField getExpressionField() {
        return expressionField;
    }

    public String getExpression() {
        return expressionField.getText();
    }

    public void setExpression(String text) {
        expressionField.setText(text);
        expressionField.setCaretPosition(text.length());
    }

    /** Insert text at the caret (replacing any selection), then refocus. */
    public void insert(String text) {
        expressionField.replaceSelection(text);
        expressionField.requestFocusInWindow();
    }

    public void clear() {
        expressionField.setText("");
        setSecondaryText(" ", false);
        expressionField.requestFocusInWindow();
    }

    /** Delete the selection, or the single character before the caret. */
    public void backspace() {
        int start = expressionField.getSelectionStart();
        int end = expressionField.getSelectionEnd();
        try {
            if (start != end) {
                expressionField.getDocument().remove(start, end - start);
            } else {
                int caret = expressionField.getCaretPosition();
                if (caret > 0) {
                    expressionField.getDocument().remove(caret - 1, 1);
                }
            }
        } catch (BadLocationException ignored) {
            // Caret is always within bounds here; nothing to do.
        }
        expressionField.requestFocusInWindow();
    }

    /** Toggle the sign of the number at the end of the expression. */
    public void toggleSign() {
        String t = getExpression();
        if (t.isEmpty()) {
            insert("-");
            return;
        }
        int numStart = t.length();
        while (numStart > 0
                && (Character.isDigit(t.charAt(numStart - 1)) || t.charAt(numStart - 1) == '.')) {
            numStart--;
        }
        if (numStart == t.length()) {
            // No trailing number to negate.
            insert("-");
            return;
        }
        if (numStart > 0 && t.charAt(numStart - 1) == '-'
                && (numStart - 1 == 0 || isOperatorOrOpen(t.charAt(numStart - 2)))) {
            setExpression(t.substring(0, numStart - 1) + t.substring(numStart));
        } else {
            setExpression(t.substring(0, numStart) + "-" + t.substring(numStart));
        }
        expressionField.requestFocusInWindow();
    }

    private boolean isOperatorOrOpen(char c) {
        return c == '+' || c == '-' || c == '×' || c == '÷'
                || c == '*' || c == '/' || c == '^' || c == '(';
    }

    public void setModeLabel(String text) {
        modeLabel.setText(text);
    }

    public void setMemoryIndicator(boolean set) {
        memoryLabel.setText(set ? "M" : " ");
    }

    /** Show a faint live-preview result while typing. */
    public void setPreview(String text) {
        if (text == null || text.isEmpty()) {
            setSecondaryText(" ", false);
        } else {
            setSecondaryText("= " + text, false);
        }
    }

    /** Show the committed expression and its result. */
    public void setResult(String expression, String result) {
        setSecondaryText(expression + " = " + result, false);
    }

    /** Show an error message in red. */
    public void setError(String message) {
        setSecondaryText(message, true);
    }

    private void setSecondaryText(String text, boolean error) {
        secondaryLabel.setText(text);
        secondaryLabel.setForeground(error ? errorColor : Color.GRAY);
    }
}
