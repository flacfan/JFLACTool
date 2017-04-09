package jflactool.gui.action_buttons;

import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.SwingWorker;
import jflactool.gui.GUIFrame;
import jflactool.gui.action_buttons.load.LoadDragDropController;
import jflactool.gui.action_buttons.settings.SettingsFrame;
import jflactool.gui.album_art.AlbumArtPanel;
import jflactool.gui.album_art.artwork.AlbumArtDragDropController;
import jflactool.gui.tags.TagsPanel;
import jflactool.gui.tags.table.TrackTableModel;

public abstract class AbstractProcess extends SwingWorker<Void, Void>
{
    private GUIFrame guiFrame;
    private SettingsFrame settingsFrame;
    private ActionButtonsPanel actionButtonsPanel;
    private TagsPanel tagsPanel;
    private AlbumArtPanel albumArtPanel;

    private LoadDragDropController loadDragDropController;
    private AlbumArtDragDropController albumArtDragDropController;

    private HashMap<JButton, Boolean> buttonStates = new HashMap<>(4);

    public AbstractProcess(GUIFrame guiFrame, SettingsFrame settingsFrame,
            ActionButtonsPanel actionButtonsPanel, TagsPanel tagsPanel,
            AlbumArtPanel albumArtPanel,
            LoadDragDropController loadDragDropController,
            AlbumArtDragDropController albumArtDragDropController)
    {
        this.guiFrame = guiFrame;
        this.settingsFrame = settingsFrame;
        this.actionButtonsPanel = actionButtonsPanel;
        this.tagsPanel = tagsPanel;
        this.albumArtPanel = albumArtPanel;
        this.loadDragDropController = loadDragDropController;
        this.albumArtDragDropController = albumArtDragDropController;
    }

    protected void setGUIEnabled(boolean enabled, String statusMessage)
    {
        if (!enabled)
        {
            buttonStates.put(albumArtPanel.getArtistButton(),
                    albumArtPanel.getArtistButton().isEnabled());
            buttonStates.put(albumArtPanel.getAlbumButton(),
                    albumArtPanel.getAlbumButton().isEnabled());
            buttonStates.put(albumArtPanel.getPreviousButton(),
                    albumArtPanel.getPreviousButton().isEnabled());
            buttonStates.put(albumArtPanel.getNextButton(),
                    albumArtPanel.getNextButton().isEnabled());

            ((TrackTableModel) tagsPanel.getTrackTable().getModel()).
                    stopTrackIfPlaying();
        }

        guiFrame.getFrame().setTitle(statusMessage);

        actionButtonsPanel.getLoadButton().setEnabled(enabled);
        actionButtonsPanel.getSpectrogramButton().setEnabled(enabled);
        actionButtonsPanel.getArchiveButton().setEnabled(enabled);
        actionButtonsPanel.getArchiveComboBox().setEnabled(enabled);
        actionButtonsPanel.getConvertButton().setEnabled(enabled);
        actionButtonsPanel.getSettingsButton().setEnabled(enabled);

        tagsPanel.getTrackTable().setEnabled(enabled);
        tagsPanel.getArtistField().setEnabled(enabled);
        tagsPanel.getAlbumField().setEnabled(enabled);
        tagsPanel.getYearField().setEnabled(enabled);
        tagsPanel.getGenreField().setEnabled(enabled);

        albumArtPanel.getArtistButton().setEnabled(enabled ?
                buttonStates.get(albumArtPanel.getArtistButton()) : false);
        albumArtPanel.getAlbumButton().setEnabled(enabled ?
                buttonStates.get(albumArtPanel.getAlbumButton()) : false);
        albumArtPanel.getPreviousButton().setEnabled(enabled ?
                buttonStates.get(albumArtPanel.getPreviousButton()) : false);
        albumArtPanel.getNextButton().setEnabled(enabled ?
                buttonStates.get(albumArtPanel.getNextButton()) : false);
        albumArtPanel.getSelectAlbumArtButton().setEnabled(enabled);

        settingsFrame.getUpdateLoadedSettingsButton().setEnabled(enabled);
        settingsFrame.getComboBox().setEnabled(enabled);

        settingsFrame.getiTunesSearchParametersField().setEnabled(enabled);

        loadDragDropController.getDropTarget().setActive(enabled);
        albumArtDragDropController.getDropTarget().setActive(enabled);

        for (JButton button : ((TrackTableModel)
                tagsPanel.getTrackTable().getModel()).getPlayButtons())
        {
            button.setEnabled(enabled);
        }
    }
}