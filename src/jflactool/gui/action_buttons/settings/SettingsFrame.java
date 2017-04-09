package jflactool.gui.action_buttons.settings;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingsFrame
{
    private final String comboBoxItems[] =
    {
        "itunes.search.parameters"
    };

    private JFrame jframe;
    private JComboBox<String> comboBox;
    private JButton updateLoadedSettings;

    private JTextField iTunesSearchParameters;

    public JFrame getFrame() { return jframe; }
    public JComboBox getComboBox() { return comboBox; }
    public JButton getUpdateLoadedSettingsButton() {
        return updateLoadedSettings; }

    public JTextField getiTunesSearchParametersField() {
        return iTunesSearchParameters; }

    public SettingsFrame()
    {
        jframe = new JFrame("Settings");
        jframe.setSize(new Dimension(300, 150));
        jframe.setResizable(false);

        updateLoadedSettings = new JButton("Update Loaded Settings");
        updateLoadedSettings.setFocusable(false);

        JPanel cardsPanel = new JPanel(new CardLayout());
        JPanel comboBoxPanel = createComboBoxPanel(cardsPanel);

        createAndAddSettingsCards(cardsPanel);

        jframe.add(cardsPanel, BorderLayout.CENTER);
        jframe.add(comboBoxPanel, BorderLayout.SOUTH);

        jframe.setLocationRelativeTo(null);
    }

    private JPanel createComboBoxPanel(final JPanel cardsPanel)
    {
        JPanel comboBoxPanel = new JPanel();
        comboBoxPanel.setBackground(Color.DARK_GRAY);

        comboBox = new JComboBox<>(comboBoxItems);
        comboBox.setEditable(false);
        comboBox.setFocusable(false);
        comboBox.addItemListener(itemEvent ->
        {
            CardLayout cardLayout = (CardLayout) cardsPanel.getLayout();
            cardLayout.show(cardsPanel, (String) itemEvent.getItem());
        });

        comboBoxPanel.add(comboBox);

        return comboBoxPanel;
    }

    private void createAndAddSettingsCards(JPanel cardsPanel)
    {
        cardsPanel.add(createiTunesSearchParametersCard(), comboBoxItems[0]);
    }

    private JPanel createiTunesSearchParametersCard()
    {
        iTunesSearchParameters = createSettingsField();
        return createSettingsCard(iTunesSearchParameters);
    }

    private JTextField createSettingsField()
    {
        JTextField settingsField = new JTextField();
        settingsField.setHorizontalAlignment(JTextField.CENTER);
        settingsField.setBackground(Color.LIGHT_GRAY);
        settingsField.setOpaque(true);

        return settingsField;
    }

    private JPanel createSettingsCard(JTextField settingsField)
    {
        JPanel settingsCard = new JPanel(new GridLayout(2, 1));
        settingsCard.setBackground(Color.DARK_GRAY);

        settingsCard.add(settingsField);
        settingsCard.add(updateLoadedSettings);

        return settingsCard;
    }
}