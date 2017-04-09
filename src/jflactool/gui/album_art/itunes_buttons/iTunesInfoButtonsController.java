package jflactool.gui.album_art.itunes_buttons;

import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.io.IOException;
import jflactool.gui.album_art.AlbumArtPanel;
import jflactool.gui.album_art.artwork.AlbumArtModel;

public class iTunesInfoButtonsController
{
    private AlbumArtModel albumArtModel;
    private AlbumArtPanel albumArtPanel;

    private ActionListener actionListener;

    public iTunesInfoButtonsController(AlbumArtModel albumArtModel,
            AlbumArtPanel albumArtPanel)
    {
        this.albumArtModel = albumArtModel;
        this.albumArtPanel = albumArtPanel;
        connectControllers();
    }

    private void connectControllers()
    {
        actionListener = actionEvent ->
        {
            if (Desktop.isDesktopSupported())
            {
                try
                {
                    if (actionEvent.getSource() ==
                            albumArtPanel.getArtistButton())
                    {
                        if (albumArtModel.getCurrentArtistLink() != null)
                        {
                            Desktop.getDesktop().browse(
                                    albumArtModel.getCurrentArtistLink());
                        }
                    }

                    else if (actionEvent.getSource() ==
                            albumArtPanel.getAlbumButton())
                    {
                        if (albumArtModel.getCurrentAlbumLink() != null)
                        {
                            Desktop.getDesktop().browse(
                                    albumArtModel.getCurrentAlbumLink());
                        }
                    }
                }

                catch (IOException ex){}
            }
        };

        albumArtPanel.getArtistButton().addActionListener(actionListener);
        albumArtPanel.getAlbumButton().addActionListener(actionListener);
    }
}