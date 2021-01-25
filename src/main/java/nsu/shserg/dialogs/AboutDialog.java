package nsu.shserg.dialogs;

import javax.swing.*;
import java.awt.*;

public class AboutDialog extends JDialog {
    public AboutDialog(JFrame owner) {
        super(owner, "About My Isolines", true);
        add(new JLabel("<html><h1><i>  My Isolines</i></h1>"
                        + "<hr>By Shniakin Sergei, FIT NSU 17204<br>"
                        + "Build on April 14, 2020</html>"),
                BorderLayout.CENTER);

        JButton ok = new JButton("ok");
        ok.addActionListener(e -> setVisible(false));

        JPanel panel = new JPanel();
        panel.add(ok);
        add(panel, BorderLayout.SOUTH);
        setSize(550, 650);
    }
}