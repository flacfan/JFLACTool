package jflactool.misc;

import java.nio.file.Path;
import java.util.UUID;

public class MusicFile
{
    private Path sourceFLACPath;
    private Path destinationFLACPath;
    private Path temporaryWAVPath;
    private Path temporaryMP3Path;
    private Path destinationMP3Path;
    private String artist;
    private String album;
    private String year;
    private String genre;
    private String trackNumber;
    private String title;
    private String fileName;
    private String uuid;
    private Boolean selected;

    public Path getSourceFLACPath() { return sourceFLACPath; }
    public Path getDestinationFLACPath() { return destinationFLACPath; }
    public Path getTemporaryWAVPath() { return temporaryWAVPath; }
    public Path getTemporaryMP3Path() { return temporaryMP3Path; }
    public Path getDestinationMP3Path() { return destinationMP3Path; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getYear() { return year; }
    public String getGenre() { return genre; }
    public String getTrackNumber() { return trackNumber; }
    public String getTitle() { return title; }
    public String getFileName() { return fileName; }
    public String getUUID() { return uuid; }
    public Boolean getSelected() { return selected; }

    public MusicFile(Path sourceFLACPath)
    {
        this.sourceFLACPath = sourceFLACPath;
        uuid = UUID.randomUUID().toString().replace("-", "");
        selected = Boolean.TRUE;
    }

    public void setDestinationFLACPath(Path destinationFLACPath)
    {
        this.destinationFLACPath = destinationFLACPath;
    }

    public void setTemporaryWAVPath(Path temporaryWAVPath)
    {
        this.temporaryWAVPath = temporaryWAVPath;
    }

    public void setTemporaryMP3Path(Path temporaryMP3Path)
    {
        this.temporaryMP3Path = temporaryMP3Path;
    }

    public void setDestinationMP3Path(Path destinationMP3Path)
    {
        this.destinationMP3Path = destinationMP3Path;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public void setAlbum(String album)
    {
        this.album = album;
    }

    public void setYear(String year)
    {
        this.year = year;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    public void setTrackNumber(String trackNumber)
    {
        this.trackNumber = trackNumber;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public void setSelected(Boolean selected)
    {
        this.selected = selected;
    }
}