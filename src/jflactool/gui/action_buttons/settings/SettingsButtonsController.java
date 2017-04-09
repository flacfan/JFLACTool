package jflactool.gui.action_buttons.settings;

import java.awt.event.ActionListener;
import jflactool.gui.action_buttons.ActionButtonsPanel;

public class SettingsButtonsController
{
    private Settings settings;
    private ActionButtonsPanel actionButtonsPanel;
    private SettingsFrame settingsFrame;

    private ActionListener actionListener;

    public SettingsButtonsController(Settings settings,
            ActionButtonsPanel actionButtonsPanel,
            SettingsFrame settingsFrame)
    {
        this.settings = settings;
        this.actionButtonsPanel = actionButtonsPanel;
        this.settingsFrame = settingsFrame;
        connectControllers();
    }

    private void connectControllers()
    {
        actionListener = actionEvent ->
        {
            if (actionEvent.getSource() ==
                    actionButtonsPanel.getSettingsButton())
            {
                loadSettings();

                if (settingsFrame.getFrame().isVisible())
                {
                    settingsFrame.getFrame().setVisible(false);
                }

                else
                {
                    settingsFrame.getFrame().setVisible(true);
                }
            }

            else if (actionEvent.getSource() ==
                    settingsFrame.getUpdateLoadedSettingsButton())
            {
                updateSettings();
            }
        };

        actionButtonsPanel.getSettingsButton().addActionListener(
                actionListener);
        settingsFrame.getUpdateLoadedSettingsButton().addActionListener(
                actionListener);
    }

    private void loadSettings()
    {
        settingsFrame.getiTunesSearchParametersField().setText(
                settings.getiTunesSearchParameters());
    }

    private void updateSettings()
    {
        settings.setiTunesSearchParameters(settingsFrame.
                getiTunesSearchParametersField().getText().trim());
    }
}