package jflactool.gui.album_art.artwork;

import jflactool.gui.album_art.AlbumArtPanel;
import jflactool.misc.ImageUtils;

public abstract class AbstractAlbumArtController
{
    AlbumArtModel albumArtModel;
    AlbumArtPanel albumArtPanel;

    AbstractAlbumArtController(AlbumArtModel albumArtModel,
            AlbumArtPanel albumArtPanel)
    {
        this.albumArtModel = albumArtModel;
        this.albumArtPanel = albumArtPanel;
    }

    abstract void connectController();

    final String getLowerCaseExtension(String filename)
    {
        int dot = filename.lastIndexOf(".");
        return filename.substring(dot).toLowerCase();
    }

    final boolean isValidExtension(String extension)
    {
        return extension.equals(".jpg") || extension.equals(".jpeg")
                || extension.equals(".png");
    }

    final void updateModel(byte[] albumArtBytes, String extension)
    {
        albumArtModel.loadLocalAlbumArt(albumArtBytes, extension);
    }

    final void updatePanel(byte[] albumArtBytes)
    {
        albumArtPanel.getAlbumNumLabel().setText(
                albumArtModel.getCurrentAlbumNum());
        albumArtPanel.getArtistButton().setText(
                albumArtModel.getCurrentArtist());
        albumArtPanel.getAlbumButton().setText(
                albumArtModel.getCurrentAlbum());
        albumArtPanel.getAlbumArtLabel().setIcon(
                ImageUtils.createScaledImageIcon(albumArtBytes));
        albumArtPanel.getAlbumArtLabel().setToolTipText(null);

        albumArtPanel.getArtistButton().setEnabled(false);
        albumArtPanel.getAlbumButton().setEnabled(false);
        albumArtPanel.getPreviousButton().setEnabled(false);
        albumArtPanel.getNextButton().setEnabled(true);
    }
}