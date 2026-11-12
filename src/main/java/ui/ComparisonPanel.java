package ui;

import model.AlgorithmResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ComparisonPanel extends RoundedPanel {

    private ComparisonTableModel tableModel;
    private JTable comparisonTable;

    public ComparisonPanel() {
        super(20);

        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(16, 20, 16, 20));
        setPreferredSize(new Dimension(0, 220));

        JLabel titleLabel = new JLabel("Algorithm Comparison");
        titleLabel.setFont(Theme.SECTION_TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        JButton clearButton = new JButton("Clear Results");

        clearButton.setFont(Theme.SMALL_FONT);
        clearButton.setForeground(Theme.TEXT_SECONDARY);
        clearButton.setBackground(Theme.INPUT_BACKGROUND);
        clearButton.setFocusable(false);

        clearButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        clearButton.setPreferredSize(
                new Dimension(110, 30)
        );

        clearButton.addActionListener(
                e -> clearResults()
        );

        JPanel headerPanel = new JPanel(
                new BorderLayout()
        );

        headerPanel.setOpaque(false);

        headerPanel.add(
                titleLabel,
                BorderLayout.WEST
        );

        headerPanel.add(
                clearButton,
                BorderLayout.EAST
        );

        tableModel = new ComparisonTableModel();

        comparisonTable = new JTable(tableModel);
        configureTable();

        JScrollPane scrollPane =
                new JScrollPane(comparisonTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(Theme.BORDER)
        );

        scrollPane.getViewport().setBackground(
                Theme.CARD_BACKGROUND
        );

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void configureTable() {
        comparisonTable.setFont(Theme.NORMAL_FONT);
        comparisonTable.setForeground(Theme.TEXT_PRIMARY);
        comparisonTable.setBackground(Theme.CARD_BACKGROUND);

        comparisonTable.setRowHeight(32);
        comparisonTable.setGridColor(Theme.BORDER);
        comparisonTable.setShowVerticalLines(false);
        comparisonTable.setShowHorizontalLines(true);

        comparisonTable.setSelectionBackground(
                Theme.INPUT_BACKGROUND
        );

        comparisonTable.setSelectionForeground(
                Theme.TEXT_PRIMARY
        );

        comparisonTable.setFillsViewportHeight(true);
        comparisonTable.setFocusable(false);

        JTableHeader tableHeader =
                comparisonTable.getTableHeader();

        tableHeader.setFont(Theme.NORMAL_FONT);
        tableHeader.setForeground(Theme.TEXT_PRIMARY);
        tableHeader.setBackground(Theme.INPUT_BACKGROUND);
        tableHeader.setReorderingAllowed(false);

        comparisonTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(190);

        comparisonTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(100);

        comparisonTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(120);

        comparisonTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(150);
    }

    public void addResult(AlgorithmResult result) {
        tableModel.addOrUpdateResult(result);
    }

    public void clearResults() {
        tableModel.clearResults();
    }
}