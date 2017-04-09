package jflactool.gui.tags.fields;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import jflactool.gui.tags.TagsPanel;

public class FieldFocusControllers
{
    private TagsModel tagsModel;
    private TagsPanel tagsPanel;

    private FocusListener artistFocusListener;
    private FocusListener albumFocusListener;
    private FocusListener yearFocusListener;
    private FocusListener genreFocusListener;

    public FieldFocusControllers(TagsModel tagsModel, TagsPanel tagsPanel)
    {
        this.tagsModel = tagsModel;
        this.tagsPanel = tagsPanel;
        connectControllers();
    }

    private void connectControllers()
    {
        artistFocusListener = new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent fe)
            {
                saveArtistToModel();
            }

            @Override
            public void focusGained(FocusEvent fe){}
        };

        albumFocusListener = new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent fe)
            {
                saveAlbumToModel();
            }

            @Override
            public void focusGained(FocusEvent fe){}
        };

        yearFocusListener = new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent fe)
            {
                saveYearToModel();
            }

            @Override
            public void focusGained(FocusEvent fe){}
        };

        genreFocusListener = new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent fe)
            {
                saveGenreToModel();
            }

            @Override
            public void focusGained(FocusEvent fe){}
        };

        tagsPanel.getArtistField().addFocusListener(artistFocusListener);
        tagsPanel.getAlbumField().addFocusListener(albumFocusListener);
        tagsPanel.getYearField().addFocusListener(yearFocusListener);
        tagsPanel.getGenreField().addFocusListener(genreFocusListener);
    }

    private void saveArtistToModel()
    {
        String artist = tagsPanel.getArtistField().getText().trim();

        if (artist.isEmpty())
        {
            tagsPanel.getArtistField().setText(tagsModel.getArtist());
        }

        else
        {
            tagsPanel.getArtistField().setText(artist);

            tagsModel.getMusicFiles().stream().forEach(musicFile ->
            {
                musicFile.setArtist(artist);
            });
        }
    }

    private void saveAlbumToModel()
    {
        String album = tagsPanel.getAlbumField().getText().trim();

        if (album.isEmpty())
        {
            tagsPanel.getAlbumField().setText(tagsModel.getAlbum());
        }

        else
        {
            tagsPanel.getAlbumField().setText(album);

            tagsModel.getMusicFiles().stream().forEach(musicFile ->
            {
                musicFile.setAlbum(album);
            });
        }
    }

    private void saveYearToModel()
    {
        String year = tagsPanel.getYearField().getText().trim();

        if (year.isEmpty())
        {
            tagsPanel.getYearField().setText(tagsModel.getYear());
        }

        else
        {
            tagsPanel.getYearField().setText(year);

            tagsModel.getMusicFiles().stream().forEach(musicFile ->
            {
                musicFile.setYear(year);
            });
        }
    }

    private void saveGenreToModel()
    {
        tagsPanel.getGenreField().setText(
                tagsPanel.getGenreField().getText().trim());

        tagsModel.getMusicFiles().stream().forEach(musicFile ->
        {
            musicFile.setGenre(tagsPanel.getGenreField().getText());
        });
    }
}