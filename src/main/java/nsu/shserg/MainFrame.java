package nsu.shserg;

import nsu.shserg.dialogs.*;
import nsu.shserg.util.FunctionModel;
import nsu.shserg.util.Parser;
import nsu.shserg.util.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;

public class MainFrame extends JFrame {
    private AboutDialog aboutDialog;
    private final JToolBar toolBar = new JToolBar("Toolbar");
    private JPanel statusBar = new JPanel();
    private JLabel statusLabel = new JLabel("My Isolines");
    private JFileChooser fileChooser = new JFileChooser();
    private SettingsDialog settingsDialog = new SettingsDialog(this);
    private Settings settings = settingsDialog.getSettings();
    private BiFunction<Double, Double, Double> function = (Double x, Double y) -> Math.sin(y)*Math.cos(x);
    private BiFunction<Double, Double, Double> legendFunction = (Double x, Double y) -> x;
    private FunctionModel legendModel = new FunctionModel(legendFunction,settings);
    private FunctionModel functionModel = new FunctionModel(function, settings);
    private LegendPanel legend = new LegendPanel(legendModel, functionModel);
    private IsolinesPanel isolinesPanel = new IsolinesPanel(functionModel, statusLabel, legend);
    private JPanel mainPanel;

    public MainFrame() {
        super("My Isolines");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(640, 480));

        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.add(statusBar, BorderLayout.SOUTH);
        statusBar.setPreferredSize(new Dimension(this.getWidth(), 16));
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusBar.add(statusLabel);
        toolBar.setRollover(true);
        this.add(toolBar, BorderLayout.PAGE_START);
        toolBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(null);
            }
        });

        JMenuBar menu = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenu menuView = new JMenu("View");
        JMenu menuHelp = new JMenu("Help");

        JMenuItem itemOpen = createMenuItem("Open...", "Open image", KeyEvent.VK_O, "Open.png", event -> openImage(), false);
        menuFile.add(itemOpen);
        toolBar.add(createToolBarButton(itemOpen, false));
        toolBar.addSeparator();

        JMenuItem itemExit = createMenuItem("Exit", "Exit", -1, "Exit.png", event -> System.exit(0), false);
        menuFile.add(itemExit);

        JMenuItem itemAbout = createMenuItem("About", "Show about program", -1, null, event -> openAboutDialog(), false);
        menuHelp.add(itemAbout);


        JMenuItem itemVisualizationMode = createMenuItem("Visualization mode", "Change of visualization mode", KeyEvent.VK_1, "InterpolateColor.png", event -> changeVisualizationMode(), true);
        menuView.add(itemVisualizationMode);
        AbstractButton toolVisualizationMode = createToolBarButton(itemVisualizationMode, true);
        toolBar.add(toolVisualizationMode);
        itemVisualizationMode.addActionListener(event -> toolVisualizationMode.setSelected(itemVisualizationMode.isSelected()));
        toolVisualizationMode.addActionListener(event -> itemVisualizationMode.setSelected(toolVisualizationMode.isSelected()));

        JMenuItem itemGrid = createMenuItem("Grid", "Show grid", KeyEvent.VK_2, "Grid.png", event -> showGrid(), true);
        menuView.add(itemGrid);
        AbstractButton toolGrid = createToolBarButton(itemGrid, true);
        toolBar.add(toolGrid);
        itemGrid.addActionListener(event -> toolGrid.setSelected(itemGrid.isSelected()));
        toolGrid.addActionListener(event -> itemGrid.setSelected(toolGrid.isSelected()));

        JMenuItem itemIsolines = createMenuItem("Isolines", "Show isolines", KeyEvent.VK_3, "Isoline.png", event -> showIsolines(), true);
        menuView.add(itemIsolines);
        AbstractButton toolIsolines = createToolBarButton(itemIsolines, true);
        toolBar.add(toolIsolines);
        itemIsolines.addActionListener(event -> toolIsolines.setSelected(itemIsolines.isSelected()));
        toolIsolines.addActionListener(event -> itemIsolines.setSelected(toolIsolines.isSelected()));

        createNewMenuElement("Settings", "Change settings", KeyEvent.VK_4, "Settings.png", event -> openSettingsDialog(), false, menuHelp);

        menu.add(menuFile);
        menu.add(menuView);
        menu.add(menuHelp);

        this.setJMenuBar(menu);

        mainPanel = createPanel();

        this.add(mainPanel);
        this.pack();
        this.setVisible(true);
    }

    protected void createNewMenuElement(String title, String tooltip, int mnemonic, String icon, ActionListener listener, boolean isRadioButton, JMenu menu) {
        JMenuItem item = createMenuItem(title, tooltip, mnemonic, icon, listener, isRadioButton);
        menu.add(item);
        AbstractButton tool = createToolBarButton(item, isRadioButton);
        toolBar.add(tool);
    }

    protected JMenuItem createMenuItem(String title, String tooltip, int mnemonic, String icon, ActionListener listener, boolean isRadioButton) {
        JMenuItem item;
        if (isRadioButton) {
            item = new JRadioButtonMenuItem(title);
        } else item = new JMenuItem(title);
        if (mnemonic != -1) {
            item.setMnemonic(mnemonic);
        }
        item.setToolTipText(tooltip);
        item.setActionCommand(title);
        if (icon != null) {
            item.setIcon(new ImageIcon(MainFrame.class.getClassLoader().getResource(icon), title));
        }
        item.addActionListener(listener);
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(item.getToolTipText());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(null);
            }
        });
        return item;
    }

    protected AbstractButton createToolBarButton(JMenuItem item, boolean isToggleButton) {
        AbstractButton button;
        if (isToggleButton) {
            button = new JToggleButton(item.getIcon());
        } else button = new JButton(item.getIcon());
        for (ActionListener listener : item.getActionListeners()) {
            button.addActionListener(listener);
        }
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                statusLabel.setText(item.getToolTipText());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusLabel.setText(null);
            }
        });
        button.setToolTipText(item.getToolTipText());
        button.setActionCommand(item.getText());
        return button;
    }

    private JPanel createPanel(){
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 3;
        constraints.gridwidth = 3;
        constraints.weightx = 100;
        constraints.weighty = 100;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(isolinesPanel, constraints);

        constraints.gridy = 4;
        constraints.gridheight = 1;
        constraints.weighty = 20;
        panel.add(legend, constraints);
        return panel;
    }

    private void openImage() {
        int ret = fileChooser.showDialog(this, "Open...");
        if ((ret == JFileChooser.APPROVE_OPTION) && fileChooser.getSelectedFile() != null) {
            File file = fileChooser.getSelectedFile();
            statusLabel.setText(file.getName());
            Settings newSettings = null;
            try {
                newSettings = Parser.parseFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(newSettings == null){
                JOptionPane.showMessageDialog(this,
                        "<html><h2>Incorrectly file </h2><i>The format of the parameter file(.txt) should be like this: \n" +
                                "a b c d // boundaries of the function definition domain (float)\n" +
                                "N M // grid size in X and Y\n" +
                                "K // the number of contours (respectively, the colors should be K + 1)\n" +
                                "R0 G0 B0 // color C0 ... (in the format red, green, blue ϵ [0, 255])\n" +
                                "...\n" +
                                "RK GK BK // color CK\n" +
                                "RZ GZ BZ // contour rendering color</i>");
                return;
            }
            settingsDialog.setSetting(newSettings);
            isolinesPanel.changeSettings(newSettings);
            legend.changeLegendSettings(newSettings);
        }
    }

    private void openSettingsDialog() {
        int result = settingsDialog.showDialog();
        if (result == JOptionPane.OK_OPTION) {
            Settings newSetting = settingsDialog.getSettings();
            if (newSetting == null) {
                return;
            }
            isolinesPanel.changeSettings(newSetting);
            legend.changeLegendSettings(newSetting);
        }
    }

    private void changeVisualizationMode() {
        legend.interpolateColors();
        isolinesPanel.interpolateColors();
    }

    private void showGrid() {
        isolinesPanel.showGrid();
    }

    private void showIsolines() {
        isolinesPanel.showIsolines();
    }

    private void openAboutDialog() {
        if (aboutDialog == null)
            aboutDialog = new AboutDialog(MainFrame.this);
        aboutDialog.setVisible(true);
    }
}
