import com.formdev.flatlaf.FlatDarkLaf;
import ui.MainFrame;

import javax.swing.*;

public static void main(String[] args) {
    FlatDarkLaf.setup();

    SwingUtilities.invokeLater(() -> {
        new MainFrame().setVisible(true);
    });
}