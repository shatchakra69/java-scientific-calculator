package com.calculator.gui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * The memory button row: MS, MR, MC, M+, M-. Each button simply forwards to
 * the matching {@link CalculatorActions} method, keeping all logic in the
 * engine (single responsibility for this panel: lay out and wire memory
 * buttons).
 */
public class MemoryPanel extends JPanel {

    public MemoryPanel(CalculatorActions actions) {
        super(new GridLayout(1, 5, 6, 6));
        setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        setBackground(Color.WHITE);

        add(memoryButton("MS", actions::memoryStore));
        add(memoryButton("MR", actions::memoryRecall));
        add(memoryButton("MC", actions::memoryClear));
        add(memoryButton("M+", actions::memoryAdd));
        add(memoryButton("M-", actions::memorySubtract));
    }

    private AnimatedButton memoryButton(String label, Runnable onClick) {
        AnimatedButton button = new AnimatedButton(label, new Color(0xEC, 0xEF, 0xF1), Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.addActionListener(e -> onClick.run());
        return button;
    }
}
