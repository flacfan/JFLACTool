package jflactool.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import jflactool.gui.action_buttons.ActionButtonsPanel;
import jflactool.gui.action_buttons.settings.Settings;

public class GUIFrame
{
    private JFrame jframe;
    private TabbedPane tabbedPane;
    private ActionButtonsPanel actionButtonsPanel;

    public JFrame getFrame() { return jframe; }
    public TabbedPane getTabbedPane() { return tabbedPane; }
    public ActionButtonsPanel getActionButtonsPanel() {
        return actionButtonsPanel; }

    public GUIFrame(Settings settings)
    {
        if (!settings.getRunningOnMac())
        {
            try
            {
                UIManager.setLookAndFeel(
                        "javax.swing.plaf.nimbus.NimbusLookAndFeel");
            }

            catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                    InstantiationException | IllegalAccessException ex){}
        }

        jframe = new JFrame("JFLACTool");
        jframe.getContentPane().setBackground(Color.DARK_GRAY);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setResizable(false);

        tabbedPane = new TabbedPane(settings);
        actionButtonsPanel = new ActionButtonsPanel(settings);

        JPanel jpanel = new JPanel(new GridBagLayout());
        jpanel.setBackground(Color.DARK_GRAY);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        jpanel.add(tabbedPane.getTabbedPane(), gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridy = 1;

        if (settings.getRunningOnMac())
        {
            gridBagConstraints.insets.top = -10;
            gridBagConstraints.insets.left = 6;
            gridBagConstraints.insets.right = 6;
        }

        jpanel.add(actionButtonsPanel.getPanel(), gridBagConstraints);

        jframe.add(jpanel);

        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
    }
}