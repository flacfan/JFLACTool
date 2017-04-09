package jflactool.gui.action_buttons.convert;

import java.nio.file.Path;
import java.util.Arrays;
import javax.swing.ImageIcon;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.gui.album_art.artwork.AlbumArtModel;
import jflactool.gui.tags.fields.TagsModel;
import jflactool.misc.MusicFile;
import jflactool.misc.StringUtils;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.tag.images.StandardArtwork;
import org.jaudiotagger.tag.reference.PictureTypes;

public class ConvertModel
{
    private Settings settings;
    private TagsModel tagsModel;
    private AlbumArtModel albumArtModel;

    private String[] baseSoXCommand;
    private String[] baseLAMECommand;

    private Path albumArtPath;
    private Path convertPath;

    public Path getAlbumArtPath() { return albumArtPath; }
    public Path getConvertPath() { return convertPath; }

    public ConvertModel(Settings settings, TagsModel tagsModel,
            AlbumArtModel albumArtModel)
    {
        this.settings = settings;
        this.tagsModel = tagsModel;
        this.albumArtModel = albumArtModel;
        makeBaseSoXCommand();
        makeBaseLAMECommand();
    }

    private void makeBaseSoXCommand()
    {
        baseSoXCommand = new String[1];
        baseSoXCommand[0] = settings.getSoXPath().toString();
    }

    private void makeBaseLAMECommand()
    {
        String[] options = settings.getLAMEOptions().split(" ");
        baseLAMECommand = new String[1 + options.length];

        baseLAMECommand[0] = settings.getLAMEPath().toString();

        int i = 1;

        for (String flag : options)
        {
            baseLAMECommand[i] = flag;
            i++;
        }
    }

    public String[] makeSoXCommand(MusicFile musicFile)
    {
        String[] command = Arrays.copyOf(baseSoXCommand,
                baseSoXCommand.length + 2);
        command[1] = musicFile.getSourceFLACPath().toString();
        command[2] = musicFile.getTemporaryWAVPath().toString();
        return command;
    }

    public String[] makeLAMECommand(MusicFile musicFile)
    {
        String[] command = Arrays.copyOf(baseLAMECommand,
                baseLAMECommand.length + 2);
        command[command.length - 2] = musicFile.getTemporaryWAVPath()
                .toString();
        command[command.length - 1] = musicFile.getTemporaryMP3Path()
                .toString();
        return command;
    }

    public AbstractID3v2Tag createID3v2Tag()
    {
        AbstractID3v2Tag id3v2Tag = null;

        try
        {
            switch (settings.getID3v2TagVersion())
            {
                case "2.3":
                    id3v2Tag = new ID3v23Tag();
                    break;
                case "2.4":
                    id3v2Tag = new ID3v24Tag();
                    break;
            }

            id3v2Tag.setField(FieldKey.ARTIST, tagsModel.getArtist());
            id3v2Tag.setField(FieldKey.ALBUM, tagsModel.getAlbum());
            id3v2Tag.setField(FieldKey.YEAR, tagsModel.getYear());

            if (!tagsModel.getGenre().isEmpty())
            {
                id3v2Tag.setField(FieldKey.GENRE, tagsModel.getGenre());
            }

            if (settings.getEmbedAlbumArtConvert() &&
                    albumArtModel.getCurrentSmallAlbumArtBytes() != null)
            {
                StandardArtwork standardArtwork = new StandardArtwork();
                standardArtwork.setBinaryData(
                        albumArtModel.getCurrentSmallAlbumArtBytes());
                standardArtwork.setMimeType(
                        ImageFormats.getMimeTypeForBinarySignature(
                                standardArtwork.getBinaryData()));
                standardArtwork.setDescription("Front Cover");
                standardArtwork.setPictureType(PictureTypes.DEFAULT_ID);
                standardArtwork.setWidth(new ImageIcon(
                        standardArtwork.getBinaryData()).getIconWidth());
                standardArtwork.setHeight(new ImageIcon(
                        standardArtwork.getBinaryData()).getIconHeight());
                id3v2Tag.setField(standardArtwork);
            }
        }

        catch (FieldDataInvalidException ex){}

        return id3v2Tag;
    }

    public void updateModel()
    {
        determineConvertPath();
        setAlbumArtPath();
        setMusicFilePaths();
    }

    private void determineConvertPath()
    {
        convertPath = settings.getConvertPath().resolve(
                StringUtils.replaceInvalidChars(tagsModel.getArtist()) + "/" +
                        StringUtils.replaceInvalidChars(tagsModel.getAlbum()));
    }

    private void setAlbumArtPath()
    {
        albumArtPath = convertPath.resolve(
                        StringUtils.replaceInvalidChars(tagsModel.getAlbum()) +
                                albumArtModel.getCurrentAlbumArtExtension());
    }

    private void setMusicFilePaths()
    {
        tagsModel.getMusicFiles().forEach(musicFile ->
        {
            musicFile.setTemporaryWAVPath(settings.getWorkingPath().resolve(
                    musicFile.getUUID() + ".wav"));
            musicFile.setTemporaryMP3Path(settings.getWorkingPath().resolve(
                    musicFile.getUUID() + ".mp3"));
            musicFile.setDestinationMP3Path(convertPath.resolve(
                    musicFile.getFileName() + ".mp3"));
        });
    }
}