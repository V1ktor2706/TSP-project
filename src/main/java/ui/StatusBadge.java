package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatusBadge extends JLabel {

    private Color backgroundColor;

    public StatusBadge(String status) {
        setFont(Theme.NORMAL_FONT);
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(new EmptyBorder(7, 13, 7, 13));
        setOpaque(false);

        setStatus(status);
    }

    public void setStatus(String status) {
        setText(status);

        switch (status) {
            case "Ready":
            case "Finished":
                setForeground(Theme.START_GREEN);
                backgroundColor = new Color(20, 83, 45);
                break;

            case "Generating...":
            case "Calculating...":
            case "Animating...":
                setForeground(Theme.ROUTE_BLUE);
                backgroundColor = new Color(30, 58, 95);
                break;

            case "Error":
            case "No route found":
                setForeground(new Color(248, 113, 113));
                backgroundColor = new Color(90, 30, 35);
                break;

            default:
                setForeground(Theme.TEXT_PRIMARY);
                backgroundColor = Theme.BORDER;
                break;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(backgroundColor);

        g2.fillRoundRect(
                0,
                0,
                getWidth(),
                getHeight(),
                getHeight(),
                getHeight()
        );

        g2.dispose();

        super.paintComponent(graphics);
    }
}