package jflactool.gui.action_buttons.settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Settings
{
    private final String settingsFile = "resources/Settings.properties";

    private boolean runningOnMac;
    private Path workingPath;

    private Path lamePath;
    private Path soxPath;
    private Path spectrogramPath;
    private Path archivePath;
    private Path convertPath;

    private boolean disableHints;
    private boolean zeroPadTrackNumbers;
    private boolean capitalizeTags;
    private boolean saveAlbumArtToFolderArchive;
    private boolean saveAlbumArtToFolderConvert;
    private boolean embedAlbumArtConvert;

    private String lameOptions;
    private String id3v2TagVersion;
    private String iTunesSearchParameters;

    public boolean getRunningOnMac() { return runningOnMac; }
    public Path getWorkingPath() { return workingPath; }

    public Path getLAMEPath() { return lamePath; }
    public Path getSoXPath() { return soxPath; }
    public Path getSpectrogramPath() { return spectrogramPath; }
    public Path getArchivePath() { return archivePath; }
    public Path getConvertPath() { return convertPath; }

    public boolean getDisableHints() { return disableHints; }
    public boolean getZeroPadTrackNumbers() { return zeroPadTrackNumbers; }
    public boolean getCapitalizeTags() { return capitalizeTags; }
    public boolean getSaveAlbumArtToFolderArchive() {
        return saveAlbumArtToFolderArchive; }
    public boolean getSaveAlbumArtToFolderConvert() {
        return saveAlbumArtToFolderConvert; }
    public boolean getEmbedAlbumArtConvert() { return embedAlbumArtConvert; }

    public String getLAMEOptions() { return lameOptions; }
    public String getID3v2TagVersion() { return id3v2TagVersion; }
    public String getiTunesSearchParameters() { return iTunesSearchParameters; }

    public void setiTunesSearchParameters(String iTunesSearchParameters)
    {
        this.iTunesSearchParameters = iTunesSearchParameters;
    }

    public Settings()
    {
        loadSettings();
    }

    private void loadSettings()
    {
        runningOnMac = System.getProperty("os.name")
                .toLowerCase().contains("mac");
        workingPath = Paths.get(System.getProperty("java.io.tmpdir"));

        Path jarPath = getJARPath();
        Map<String, String> settings = loadProperties(jarPath);

        setLAMEPath(jarPath, settings);
        setSoXPath(jarPath, settings);
        setSpectrogramPath(jarPath, settings);
        setArchivePath(jarPath, settings);
        setConvertPath(jarPath, settings);

        setNonPathSettings(settings);
    }

    private Path getJARPath()
    {
        Path jarPath = null;

        try
        {
            jarPath = Paths.get(getClass().getProtectionDomain().
                        getCodeSource().getLocation().toURI());

            if (jarPath.endsWith(".jar"))
            {
                jarPath = jarPath.subpath(0, jarPath.getNameCount() - 1);
            }
        }

        catch (URISyntaxException ex){}

        return jarPath;
    }

    private Path getSettingsPath(Path jarPath)
    {
        Path settingsPath = jarPath.resolveSibling(settingsFile);

        if (Files.notExists(settingsPath))
        {
            showErrorAndExit(settingsFile + " could not be found.");
            return null;
        }

        return settingsPath;
    }

    private Map<String, String> loadProperties(Path jarPath)
    {
        try (FileInputStream fileInputStream = new FileInputStream(
                getSettingsPath(jarPath).toFile()))
        {
            Properties properties = new Properties();
            properties.load(fileInputStream);

            HashMap<String, String> settings = new HashMap<>();

            settings.put("treat.as.full.path.lame",
                    properties.getProperty("treat.as.full.path.lame"));
            settings.put("lame.path",
                    properties.getProperty("lame.path"));

            settings.put("treat.as.full.path.sox",
                    properties.getProperty("treat.as.full.path.sox"));
            settings.put("sox.path",
                    properties.getProperty("sox.path"));

            settings.put("treat.as.full.path.spectrogram",
                    properties.getProperty("treat.as.full.path.spectrogram"));
            settings.put("spectrogram.path",
                    properties.getProperty("spectrogram.path"));

            settings.put("treat.as.full.path.archive",
                    properties.getProperty("treat.as.full.path.archive"));
            settings.put("archive.path",
                    properties.getProperty("archive.path"));

            settings.put("treat.as.full.path.convert",
                    properties.getProperty("treat.as.full.path.convert"));
            settings.put("convert.path",
                    properties.getProperty("convert.path"));

            settings.put("disable.hints",
                    properties.getProperty("disable.hints"));
            settings.put("zero.pad.track.numbers",
                    properties.getProperty("zero.pad.track.numbers"));
            settings.put("capitalize.tags",
                    properties.getProperty("capitalize.tags"));
            settings.put("save.album.art.to.folder.archive",
                    properties.getProperty("save.album.art.to.folder.archive"));
            settings.put("save.album.art.to.folder.convert",
                    properties.getProperty("save.album.art.to.folder.convert"));
            settings.put("embed.album.art.convert",
                    properties.getProperty("embed.album.art.convert"));

            settings.put("lame.options",
                    properties.getProperty("lame.options"));
            settings.put("id3v2.tag.version",
                    properties.getProperty("id3v2.tag.version"));
            settings.put("itunes.search.parameters",
                    properties.getProperty("itunes.search.parameters"));

            for (String key : settings.keySet())
            {
                if (settings.get(key) == null)
                {
                    showErrorAndExit(
                            key + " is missing from Settings.properties.");
                }
            }

            return settings;
        }

        catch (IOException ex){}

        return null;
    }

    private void setLAMEPath(Path jarPath, Map<String, String> settings)
    {
        if (Boolean.parseBoolean(settings.get(
                "treat.as.full.path.lame")))
        {
            lamePath = Paths.get(settings.get("lame.path"));
        }

        else
        {
            lamePath = jarPath.resolveSibling(settings.get(
                    "lame.path"));
        }
    }

    private void setSoXPath(Path jarPath, Map<String, String> settings)
    {
        if (Boolean.parseBoolean(settings.get(
                "treat.as.full.path.sox")))
        {
            soxPath = Paths.get(settings.get("sox.path"));
        }

        else
        {
            soxPath = jarPath.resolveSibling(settings.get(
                    "sox.path"));
        }
    }

    private void setSpectrogramPath(Path jarPath, Map<String, String> settings)
    {
        if (Boolean.parseBoolean(settings.get(
                "treat.as.full.path.spectrogram")))
        {
            spectrogramPath = Paths.get(settings.get("spectrogram.path"));
        }

        else
        {
            spectrogramPath = jarPath.resolveSibling(settings.get(
                    "spectrogram.path"));
        }
    }

    private void setArchivePath(Path jarPath, Map<String, String> settings)
    {
        if (Boolean.parseBoolean(settings.get(
                "treat.as.full.path.archive")))
        {
            archivePath = Paths.get(settings.get("archive.path"));
        }

        else
        {
            archivePath = jarPath.resolveSibling(settings.get(
                    "archive.path"));
        }
    }

    private void setConvertPath(Path jarPath, Map<String, String> settings)
    {
        if (Boolean.parseBoolean(settings.get(
                "treat.as.full.path.convert")))
        {
            convertPath = Paths.get(settings.get("convert.path"));
        }

        else
        {
            convertPath = jarPath.resolveSibling(settings.get(
                    "convert.path"));
        }
    }

    private void setNonPathSettings(Map<String, String> settings)
    {
        disableHints = Boolean.parseBoolean(settings.get(
                "disable.hints"));
        zeroPadTrackNumbers = Boolean.parseBoolean(settings.get(
                "zero.pad.track.numbers"));
        capitalizeTags = Boolean.parseBoolean(settings.get(
                "capitalize.tags"));
        saveAlbumArtToFolderArchive = Boolean.parseBoolean(
                settings.get("save.album.art.to.folder.archive"));
        saveAlbumArtToFolderConvert = Boolean.parseBoolean(
                settings.get("save.album.art.to.folder.convert"));
        embedAlbumArtConvert = Boolean.parseBoolean(settings.get(
                "embed.album.art.convert"));

        lameOptions = settings.get("lame.options");
        id3v2TagVersion = settings.get("id3v2.tag.version");
        iTunesSearchParameters = settings.get("itunes.search.parameters");

        if (!id3v2TagVersion.equals("2.3") && !id3v2TagVersion.equals("2.4"))
        {
            showErrorAndExit("Please make sure id3v2.tag.version is set "
                    + "to either 2.3 or 2.4 in Settings.properties.");
        }
    }

    private void showErrorAndExit(String error)
    {
        JOptionPane.showMessageDialog(new JFrame(), error, "JFLACTool",
                JOptionPane.PLAIN_MESSAGE);
        System.exit(0);
    }
}