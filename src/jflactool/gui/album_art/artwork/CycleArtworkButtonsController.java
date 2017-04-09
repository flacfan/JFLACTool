package jflactool.gui.album_art.artwork;

import java.awt.event.ActionListener;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.gui.album_art.AlbumArtPanel;

public class CycleArtworkButtonsController
{
    private Settings settings;
    private AlbumArtModel albumArtModel;
    private AlbumArtPanel albumArtPanel;

    private ActionListener actionListener;

    public CycleArtworkButtonsController(Settings settings,
            AlbumArtModel albumArtModel, AlbumArtPanel albumArtPanel)
    {
        this.settings = settings;
        this.albumArtModel = albumArtModel;
        this.albumArtPanel = albumArtPanel;
        connectControllers();
    }

    private void connectControllers()
    {
        actionListener = actionEvent ->
        {
            if (actionEvent.getSource() == albumArtPanel.getPreviousButton())
            {
                albumArtModel.previousAlbum();
            }

            else if (actionEvent.getSource() == albumArtPanel.getNextButton())
            {
                albumArtModel.nextAlbum();
            }

            albumArtPanel.getArtistButton().setEnabled(
                    albumArtModel.getCurrentArtist() != null);
            albumArtPanel.getAlbumButton().setEnabled(
                    albumArtModel.getCurrentAlbum() != null);
            albumArtPanel.getPreviousButton().setEnabled(
                    albumArtModel.getAlbumIteratorHasPrevious());
            albumArtPanel.getNextButton().setEnabled(
                    albumArtModel.getAlbumIteratorHasNext());

            albumArtPanel.getAlbumNumLabel().setText(
                    albumArtModel.getCurrentAlbumNum());
            albumArtPanel.getArtistButton().setText(
                    albumArtModel.getCurrentArtist());
            albumArtPanel.getAlbumButton().setText(
                    albumArtModel.getCurrentAlbum());

            if (albumArtModel.getCurrentAlbumArtImageIcon() != null)
            {
                albumArtPanel.getAlbumArtLabel().setIcon(
                        albumArtModel.getCurrentAlbumArtImageIcon());
                albumArtPanel.getAlbumArtLabel().setToolTipText(null);
            }

            else
            {
                albumArtPanel.getAlbumArtLabel().setIcon(null);

                if (!settings.getDisableHints())
                {
                    albumArtPanel.getAlbumArtLabel().setToolTipText(
                            "Drag and drop album art here to load it.");
                }
            }
        };

        albumArtPanel.getPreviousButton().addActionListener(actionListener);
        albumArtPanel.getNextButton().addActionListener(actionListener);
    }
}