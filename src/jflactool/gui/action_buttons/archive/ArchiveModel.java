package jflactool.gui.action_buttons.archive;

import java.nio.file.Path;
import java.util.Arrays;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.gui.album_art.artwork.AlbumArtModel;
import jflactool.gui.tags.fields.TagsModel;
import jflactool.misc.MusicFile;
import jflactool.misc.StringUtils;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.flac.FlacTag;

public class ArchiveModel
{
    public static enum Source { CD, WEB };

    private Settings settings;
    private TagsModel tagsModel;
    private AlbumArtModel albumArtModel;

    private String[] baseSoXCommand;

    private Path albumArtPath;
    private Path archivePath;

    public Path getAlbumArtPath() { return albumArtPath; }
    public Path getArchivePath() { return archivePath; }

    public ArchiveModel(Settings settings, TagsModel tagsModel,
            AlbumArtModel albumArtModel)
    {
        this.settings = settings;
        this.tagsModel = tagsModel;
        this.albumArtModel = albumArtModel;
        makeBaseSoXCommand();
    }

    private void makeBaseSoXCommand()
    {
        baseSoXCommand = new String[8];
        baseSoXCommand[0] = settings.getSoXPath().toString();
        baseSoXCommand[1] = "-G";
        baseSoXCommand[3] = "-b";
        baseSoXCommand[4] = "16";
        baseSoXCommand[5] = "-r";
        baseSoXCommand[6] = "44100";
    }

    public String[] makeSoXCommand(MusicFile musicFile)
    {
        String[] command = Arrays.copyOf(baseSoXCommand, baseSoXCommand.length);
        command[2] = musicFile.getSourceFLACPath().toString();
        command[7] = musicFile.getDestinationFLACPath().toString();
        return command;
    }

    public String[] makeSoXCommand(MusicFile musicFile, int volume)
    {
        String[] command = Arrays.copyOf(baseSoXCommand, baseSoXCommand.length + 2);
        for (int i = command.length - 1; i >= 5; i--) command[i] = command[i - 2];
        command[2] = "-v";
        command[3] = "0." + volume;
        command[4] = musicFile.getSourceFLACPath().toString();
        command[9] = musicFile.getDestinationFLACPath().toString();
        return command;
    }

    public FlacTag createFLACTag()
    {
        FlacTag flacTag = null;

        try
        {
            flacTag = new FlacTag();

            flacTag.setField(FieldKey.ARTIST, tagsModel.getArtist());
            flacTag.setField(FieldKey.ALBUM, tagsModel.getAlbum());
            flacTag.setField(FieldKey.YEAR, tagsModel.getYear());

            if (!tagsModel.getGenre().isEmpty())
            {
                flacTag.setField(FieldKey.GENRE, tagsModel.getGenre());
            }
        }

        catch (FieldDataInvalidException ex){}

        return flacTag;
    }

    public void updateModel(String source)
    {
        determineArchivePath(source);
        setAlbumArtPath();
        setMusicFilePaths();
    }

    private void determineArchivePath(String source)
    {
        archivePath = settings.getArchivePath().resolve(
                StringUtils.replaceInvalidChars(tagsModel.getArtist())
                + " [" + tagsModel.getYear() + "] "
                + StringUtils.replaceInvalidChars(tagsModel.getAlbum())
                + " [FLAC] "
                + "[" + source + "]");
    }

    private void setAlbumArtPath()
    {
        albumArtPath = archivePath.resolve(
                        StringUtils.replaceInvalidChars(tagsModel.getAlbum()) +
                                albumArtModel.getCurrentAlbumArtExtension());
    }

    private void setMusicFilePaths()
    {
        tagsModel.getMusicFiles().forEach(musicFile ->
        {
            musicFile.setDestinationFLACPath(archivePath.resolve(
                    musicFile.getFileName() + ".flac"));
        });
    }
}