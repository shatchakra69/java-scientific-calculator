package com.calculator.gui;

import com.calculator.core.CalculationRecord;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * A side panel listing past calculations. Double-clicking an entry inserts its
 * result back into the expression so it can be reused. The "Clear" button
 * removes the selected entry, or — when nothing is selected — clears the whole
 * history. The Delete key also removes the selected entry.
 */
public class HistoryPanel extends JPanel {

    private final DefaultListModel<CalculationRecord> model = new DefaultListModel<>();
    private final JList<CalculationRecord> list = new JList<>(model);

    public HistoryPanel(CalculatorActions actions) {
        super(new BorderLayout(4, 4));
        setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 12));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(220, 0));

        JLabel title = new JLabel("History");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 0));

        list.setFont(new Font("Monospaced", Font.PLAIN, 13));
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    CalculationRecord selected = list.getSelectedValue();
                    if (selected != null) {
                        actions.insert(selected.getResult());
                    }
                }
            }
        });

        // Delete key removes the selected entry.
        list.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("DELETE"), "deleteEntry");
        list.getActionMap().put("deleteEntry", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CalculationRecord selected = list.getSelectedValue();
                if (selected != null) {
                    actions.deleteHistory(selected);
                }
            }
        });

        JButton clear = new JButton("Clear");
        clear.setFocusable(false);
        clear.setToolTipText("Removes the selected entry, or clears all history if none is selected");
        clear.addActionListener(e -> {
            CalculationRecord selected = list.getSelectedValue();
            if (selected != null) {
                actions.deleteHistory(selected);
            } else {
                actions.clearHistory();
            }
        });

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(clear, BorderLayout.SOUTH);
    }

    /** Rebuild the list from the engine's history (newest entry on top). */
    public void refresh(List<CalculationRecord> history) {
        model.clear();
        for (int i = history.size() - 1; i >= 0; i--) {
            model.addElement(history.get(i));
        }
    }
}
