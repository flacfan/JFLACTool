package jflactool.gui.tags.fields;

import java.io.IOException;
import java.util.ArrayList;
import jflactool.gui.action_buttons.load.LoadModel;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.misc.MusicFile;
import jflactool.misc.StringUtils;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;

public class TagsModel
{
    private Settings settings;
    private LoadModel loadModel;

    private ArrayList<MusicFile> musicFiles;

    public ArrayList<MusicFile> getMusicFiles() { return musicFiles; }
    public String getArtist() { return musicFiles.get(0).getArtist(); }
    public String getAlbum() { return musicFiles.get(0).getAlbum(); }
    public String getYear() { return musicFiles.get(0).getYear(); }
    public String getGenre() { return musicFiles.get(0).getGenre(); }

    public TagsModel(Settings settings, LoadModel loadModel)
    {
        this.settings = settings;
        this.loadModel = loadModel;
    }

    public boolean haveSelectedFiles()
    {
        return musicFiles.stream().anyMatch(
                musicFile -> musicFile.getSelected());
    }

    public int numberOfSelectedFiles()
    {
        return musicFiles.stream()
                .filter(musicFile -> musicFile.getSelected())
                .mapToInt(musicFile -> 1)
                .sum();
    }

    public void loadTags()
    {
        musicFiles = new ArrayList<>(loadModel.getSourceFLACPaths().size());

        loadModel.getSourceFLACPaths().stream().forEach(sourceFLACPath ->
        {
            try
            {
                VorbisCommentTag vorbisCommentTag =
                        ((FlacTag) AudioFileIO.read(sourceFLACPath.toFile())
                                .getTag()).getVorbisCommentTag();

                MusicFile musicFile = new MusicFile(sourceFLACPath);

                setFields(musicFile, vorbisCommentTag);

                isolateTrackNumber(musicFile);

                if (settings.getZeroPadTrackNumbers())
                {
                    zeroPadTrackNumber(musicFile);
                }

                if (settings.getCapitalizeTags())
                {
                    capitalizeTags(musicFile);
                }

                musicFile.setFileName(musicFile.getTrackNumber() + " - " +
                        StringUtils.replaceInvalidChars(musicFile.getTitle()));

                musicFiles.add(musicFile);
            }

            catch (IOException | CannotReadException | TagException |
                    ReadOnlyFileException | InvalidAudioFrameException ex){}
        });
    }

    private void setFields(MusicFile musicFile, VorbisCommentTag tag)
    {
        musicFile.setTrackNumber(tag.getFirst(FieldKey.TRACK).trim());
        musicFile.setTitle(tag.getFirst(FieldKey.TITLE).trim());
        musicFile.setArtist(tag.getFirst(FieldKey.ARTIST).trim());
        musicFile.setAlbum(tag.getFirst(FieldKey.ALBUM).trim());
        musicFile.setYear(tag.getFirst(FieldKey.YEAR).trim());
        musicFile.setGenre(tag.getFirst(FieldKey.GENRE).trim());
    }

    private void isolateTrackNumber(MusicFile musicFile)
    {
        if (musicFile.getTrackNumber().contains("/"))
        {
            musicFile.setTrackNumber(
                    musicFile.getTrackNumber().split("/")[0].trim());
        }

        musicFile.setTrackNumber(musicFile.getTrackNumber().replace(".", ""));
    }

    private void zeroPadTrackNumber(MusicFile musicFile)
    {
        try
        {
            musicFile.setTrackNumber(
                    String.format("%02d",
                            Integer.parseInt(musicFile.getTrackNumber())));
        }

        catch (NumberFormatException ex){}
    }

    private void capitalizeTags(MusicFile musicFile)
    {
        musicFile.setTitle(capitalizeTag(musicFile.getTitle()));
        musicFile.setArtist(capitalizeTag(musicFile.getArtist()));
        musicFile.setAlbum(capitalizeTag(musicFile.getAlbum()));
        musicFile.setGenre(capitalizeTag(musicFile.getGenre()));
    }

    private String capitalizeTag(String tag)
    {
        String capitalizedTag = "";
        boolean foundDigit = false;
        boolean foundFirstLetter = false;

        for (char c : tag.toCharArray())
        {
            if (c == ' ')
            {
                capitalizedTag += c;
                foundDigit = false;
                foundFirstLetter = false;
            }

            else if (Character.isDigit(c))
            {
                capitalizedTag += c;
                foundDigit = true;
            }

            else if (Character.isLetter(c))
            {
                if (!foundDigit && !foundFirstLetter)
                {
                    capitalizedTag += Character.toUpperCase(c);
                    foundFirstLetter = true;
                }

                else
                {
                    capitalizedTag += c;
                }
            }

            else
            {
                capitalizedTag += c;
            }
        }

        return capitalizedTag;
    }
}