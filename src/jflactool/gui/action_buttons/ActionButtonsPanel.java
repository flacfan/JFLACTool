package jflactool.gui.action_buttons;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import jflactool.gui.action_buttons.archive.ArchiveModel.Source;
import jflactool.gui.action_buttons.settings.Settings;

public class ActionButtonsPanel
{
    private Settings settings;

    private JPanel jpanel;
    private JButton loadButton;
    private JButton spectrogramButton;
    private JButton archiveButton;
    private JComboBox<String> archiveComboBox;
    private JButton convertButton;
    private JButton settingsButton;

    public JPanel getPanel() { return jpanel; }
    public JButton getLoadButton() { return loadButton; }
    public JButton getSpectrogramButton() { return spectrogramButton; }
    public JButton getArchiveButton() { return archiveButton; }
    public JComboBox<String> getArchiveComboBox() { return archiveComboBox; }
    public JButton getConvertButton() { return convertButton; }
    public JButton getSettingsButton() { return settingsButton; }

    public ActionButtonsPanel(Settings settings)
    {
        this.settings = settings;

        jpanel = new JPanel(new GridLayout(1, 5, 0, 0));
        jpanel.setBackground(Color.DARK_GRAY);

        loadButton = createButton("Load", "Drag and drop files and/or folders "
                + "into the table above or click this button to select "
                + "files and/or folders.");
        spectrogramButton = createButton("Spectrogram", "Click here to "
                + "generate spectrograms for selected FLAC files. Visually "
                + "analyze them to confirm that the FLAC files are indeed "
                + "lossless.");
        archiveButton = createButton("Archive", "Click here to create "
                + "CD quality (16-bit/44.1kHz) copies of selected FLAC "
                + "files using the latest FLAC version at compression "
                + "level 8.");
        archiveComboBox = createComboBox("Select the source of the selected "
                + "FLAC files. This piece of metadata will be included in "
                + "the folder name that holds the resulting archived files.");
        convertButton = createButton("Convert", "Click here to convert "
                + "selected FLAC files to MP3.");
        settingsButton = createButton("Settings", "Click here to show or "
                + "hide the Settings window.");

        spectrogramButton.setEnabled(false);
        archiveButton.setEnabled(false);
        archiveComboBox.setEnabled(false);
        convertButton.setEnabled(false);

        JPanel archivePanel = new JPanel(new GridBagLayout());
        archivePanel.setBackground(Color.DARK_GRAY);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;

        gridBagConstraints.ipady = 15;
        archivePanel.add(archiveButton, gridBagConstraints);

        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 0;
        archivePanel.add(archiveComboBox, gridBagConstraints);

        jpanel.add(loadButton);
        jpanel.add(spectrogramButton);
        jpanel.add(archivePanel);
        jpanel.add(convertButton);
        jpanel.add(settingsButton);
    }

    private JButton createButton(String text, String toolTipText)
    {
        JButton button = new JButton(text);

        if (!text.equals("Archive") && !text.equals("Convert"))
        {
            button.setFocusable(false);
        }

        if (!settings.getDisableHints())
        {
            button.setToolTipText(toolTipText);
        }

        return button;
    }

    private JComboBox<String> createComboBox(String toolTipText)
    {
        JComboBox<String> comboBox = new JComboBox<>(
                new String[]{ Source.WEB.toString(), Source.CD.toString() });
        comboBox.setFocusable(false);
        comboBox.setRenderer(new DefaultListCellRenderer()
        {
            {
                setHorizontalAlignment(DefaultListCellRenderer.CENTER);
            }
        });

        if (!settings.getDisableHints())
        {
            comboBox.setToolTipText(toolTipText);
        }

        return comboBox;
    }
}