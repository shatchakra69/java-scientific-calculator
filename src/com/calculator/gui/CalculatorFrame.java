package com.calculator.gui;

import com.calculator.core.CalculationRecord;
import com.calculator.core.CalculatorEngine;
import com.calculator.exceptions.CalculatorException;
import com.calculator.operations.AngleMode;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

/**
 * The main window. It assembles the {@link DisplayPanel}, {@link ButtonPanel},
 * {@link MemoryPanel} and {@link HistoryPanel}, owns the
 * {@link CalculatorEngine}, and implements {@link CalculatorActions} so the
 * panels can drive it.
 *
 * <p>This is the GUI module's coordinator: it converts user actions into
 * engine calls and reflects the engine's answers back onto the display,
 * catching every {@link CalculatorException} so a bad input shows a message
 * instead of crashing the program.</p>
 */
public class CalculatorFrame extends JFrame implements CalculatorActions {

    private final CalculatorEngine engine = new CalculatorEngine();
    private final DisplayPanel display = new DisplayPanel();
    private final ButtonPanel buttons = new ButtonPanel(this);
    private final MemoryPanel memory = new MemoryPanel(this);
    private final HistoryPanel history = new HistoryPanel(this);

    private final JPanel content = new JPanel(new BorderLayout());
    private final AnimatedButton historyToggle =
            new AnimatedButton("History", new Color(0xEC, 0xEF, 0xF1), Color.BLACK);
    private boolean historyVisible = false;

    public CalculatorFrame() {
        super("Scientific Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.add(buildToolbar(), BorderLayout.NORTH);
        header.add(display, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.add(header, BorderLayout.NORTH);
        top.add(memory, BorderLayout.SOUTH);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(Color.WHITE);
        left.add(top, BorderLayout.NORTH);
        left.add(buttons, BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(340, 440));

        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder());
        content.add(left, BorderLayout.CENTER);
        // History starts hidden — it appears when the History button is clicked.
        setContentPane(content);

        wireKeyboard();
        wireLivePreview();
        display.setModeLabel("DEG");

        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    /** Top toolbar holding the show/hide History toggle. */
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createEmptyBorder(8, 12, 0, 12));
        historyToggle.setFont(new Font("SansSerif", Font.BOLD, 13));
        historyToggle.setPreferredSize(new Dimension(120, 28));
        historyToggle.addActionListener(e -> toggleHistory());
        bar.add(historyToggle);
        return bar;
    }

    /** Show or hide the history panel, resizing the window to fit. */
    private void toggleHistory() {
        historyVisible = !historyVisible;
        if (historyVisible) {
            content.add(history, BorderLayout.EAST);
            history.refresh(engine.getHistory());
            historyToggle.setText("Hide History");
        } else {
            content.remove(history);
            historyToggle.setText("History");
        }
        content.revalidate();
        content.repaint();
        pack();
    }

    /** Give the expression field focus so the keyboard works immediately. */
    public void focusInput() {
        display.getExpressionField().requestFocusInWindow();
    }

    // ------------------------------------------------------------------
    // Wiring
    // ------------------------------------------------------------------

    private void wireKeyboard() {
        JTextField field = display.getExpressionField();
        // Enter evaluates. Typed digits/operators/letters land in the field
        // natively, which is what gives us full keyboard support.
        field.addActionListener(e -> evaluate());
        // Escape clears.
        field.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "clearAll");
        field.getActionMap().put("clearAll", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
    }

    private void wireLivePreview() {
        display.getExpressionField().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { updatePreview(); }
            @Override public void removeUpdate(DocumentEvent e)  { updatePreview(); }
            @Override public void changedUpdate(DocumentEvent e) { updatePreview(); }
        });
    }

    private void updatePreview() {
        String expression = display.getExpression();
        if (expression.trim().isEmpty()) {
            display.setPreview("");
        } else {
            display.setPreview(engine.preview(expression));
        }
    }

    // ------------------------------------------------------------------
    // CalculatorActions
    // ------------------------------------------------------------------

    @Override
    public void insert(String text) {
        display.insert(text);
    }

    @Override
    public void insertAnswer() {
        display.insert(CalculatorEngine.format(engine.getLastValue()));
    }

    @Override
    public void evaluate() {
        String expression = display.getExpression();
        try {
            String result = engine.evaluate(expression);
            display.setExpression(result);      // allow chaining from the result
            display.setResult(expression.trim(), result);
            history.refresh(engine.getHistory());
        } catch (CalculatorException ex) {
            display.setError(ex.getMessage());
        }
    }

    @Override
    public void clearAll() {
        display.clear();
    }

    @Override
    public void backspace() {
        display.backspace();
    }

    @Override
    public void toggleSign() {
        display.toggleSign();
    }

    @Override
    public void toggleAngleMode() {
        AngleMode mode = engine.toggleAngleMode();
        display.setModeLabel(mode == AngleMode.DEGREES ? "DEG" : "RAD");
        updatePreview();
    }

    @Override
    public void memoryStore() {
        withCurrentValue(engine::memoryStore);
    }

    @Override
    public void memoryAdd() {
        withCurrentValue(engine::memoryAdd);
    }

    @Override
    public void memorySubtract() {
        withCurrentValue(engine::memorySubtract);
    }

    @Override
    public void memoryRecall() {
        display.insert(CalculatorEngine.format(engine.memoryRecall()));
    }

    @Override
    public void memoryClear() {
        engine.memoryClear();
        display.setMemoryIndicator(false);
    }

    @Override
    public void deleteHistory(CalculationRecord record) {
        engine.removeHistory(record);
        history.refresh(engine.getHistory());
    }

    @Override
    public void clearHistory() {
        engine.clearHistory();
        history.refresh(engine.getHistory());
    }

    /**
     * Evaluate the current expression to a number and hand it to a memory
     * operation, surfacing any error on the display instead of crashing.
     */
    private void withCurrentValue(java.util.function.DoubleConsumer memoryOp) {
        try {
            double value = engine.evaluateToNumber(display.getExpression());
            memoryOp.accept(value);
            display.setMemoryIndicator(engine.isMemorySet());
        } catch (CalculatorException ex) {
            display.setError(ex.getMessage());
        }
    }
}
