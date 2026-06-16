package com.calculator;

import com.calculator.gui.CalculatorFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Application entry point. Launches the {@link CalculatorFrame} on the Swing
 * Event Dispatch Thread, as required for thread-safe GUI construction.
 */
public final class Main {

    private Main() {
        // No instances.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to the default look and feel.
            }
            CalculatorFrame frame = new CalculatorFrame();
            frame.setVisible(true);
            frame.focusInput();
        });
    }
}
