import com.formdev.flatlaf.FlatDarkLaf;
import ui.MainFrame;

import javax.swing.*;

public static void main(String[] args) {
    FlatDarkLaf.setup();


    UIManager.put("Button.arc", 12);
    UIManager.put("Component.arc", 12);
    UIManager.put("TextComponent.arc", 10);
    UIManager.put("ComboBox.arc", 10);
    UIManager.put("ProgressBar.arc", 10);
    UIManager.put("ScrollBar.width", 10);

    SwingUtilities.invokeLater(MainFrame::new);
}