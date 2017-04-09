package jflactool.gui.album_art.artwork;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.ImageIcon;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.gui.tags.fields.TagsModel;
import jflactool.misc.ImageUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlbumArtModel
{
    private Settings settings;
    private TagsModel tagsModel;

    private String artistFromTags;
    private String albumFromTags;
    private String iTunesSearchParametersFromSettings;

    private boolean useLocalAlbumArtData;
    private AlbumData localAlbumArtData;

    private ArrayList<AlbumData> albumDataCache;

    private JSONArray searchAPIResults;
    private AlbumIterator albumIterator;

    public boolean getAlbumIteratorHasPrevious() {
        if (albumIterator == null) { return false; }
        return albumIterator.hasPrevious(); }
    public boolean getAlbumIteratorHasNext() {
        if (albumIterator == null) { return true; }
        if (albumIterator.getLength() == 1) { return true; }
        return albumIterator.hasNext(); }

    public String getCurrentAlbumNum() {
        if (useLocalAlbumArtData) { return "Local Album Art Loaded"; }
        if (searchAPIResults != null && albumIterator == null) {
            return "No iTunes Album Art Found"; }
        if (albumIterator == null || albumIterator.getIndex() == -1) {
            return "No Album Art Loaded"; }
        return albumDataCache.get(albumIterator.getIndex()).albumNum; }
    public String getCurrentArtist() {
        if (useLocalAlbumArtData || albumIterator == null ||
                albumIterator.getIndex() == -1) {
            return null; }
        return albumDataCache.get(albumIterator.getIndex()).artist; }
    public String getCurrentAlbum() {
        if (useLocalAlbumArtData || albumIterator == null ||
                albumIterator.getIndex() == -1) {
            return null; }
        return albumDataCache.get(albumIterator.getIndex()).album; }
    public URI getCurrentArtistLink() {
        if (useLocalAlbumArtData || albumIterator == null ||
                albumIterator.getIndex() == -1) {
            return null; }
        return albumDataCache.get(albumIterator.getIndex()).artistLink; }
    public URI getCurrentAlbumLink() {
        if (useLocalAlbumArtData || albumIterator == null ||
                albumIterator.getIndex() == -1) {
            return null; }
        return albumDataCache.get(albumIterator.getIndex()).albumLink; }

    public byte[] getCurrentSmallAlbumArtBytes() {
        if (useLocalAlbumArtData) { return localAlbumArtData.smallAlbumArtBytes; }
        if (albumIterator == null || albumIterator.getIndex() == -1) {
            return null; }
        return albumDataCache.get(albumIterator.getIndex()).smallAlbumArtBytes; }
    public byte[] getCurrentLargeAlbumArtBytes() {
        if (useLocalAlbumArtData) { return localAlbumArtData.largeAlbumArtBytes; }
        if (albumIterator == null || albumIterator.getIndex() == -1) {
            return null; }
        return albumDataCache.get(albumIterator.getIndex()).largeAlbumArtBytes; }
    public ImageIcon getCurrentAlbumArtImageIcon() {
        if (useLocalAlbumArtData) { return localAlbumArtData.albumArtImageIcon; }
        if (albumIterator == null || albumIterator.getIndex() == -1) {
            return null; }
        return albumDataCache.get(albumIterator.getIndex()).albumArtImageIcon; }
    public String getCurrentAlbumArtExtension() {
        if (useLocalAlbumArtData) { return localAlbumArtData.albumArtExtension; }
        if (albumIterator == null || albumIterator.getIndex() == -1) {
            return null; }
        return albumDataCache.get(albumIterator.getIndex()).albumArtExtension; }

    public void loadLocalAlbumArt(byte[] albumArtBytes,
            String albumArtExtension)
    {
        localAlbumArtData.smallAlbumArtBytes = albumArtBytes;
        localAlbumArtData.largeAlbumArtBytes = albumArtBytes;
        localAlbumArtData.albumArtImageIcon = ImageUtils.createScaledImageIcon(
                albumArtBytes);
        localAlbumArtData.albumArtExtension = albumArtExtension;
        useLocalAlbumArtData = true;
    }

    public AlbumArtModel(Settings settings, TagsModel tagsModel)
    {
        this.settings = settings;
        this.tagsModel = tagsModel;

        localAlbumArtData = new AlbumData();
        albumDataCache = new ArrayList<>();
    }

    public void loadAlbumArt()
    {
        artistFromTags = tagsModel.getArtist();
        albumFromTags = tagsModel.getAlbum();
        iTunesSearchParametersFromSettings = settings.
                getiTunesSearchParameters();

        if (useLocalAlbumArtData)
        {
            localAlbumArtData.resetImageData();
            useLocalAlbumArtData = false;
        }

        albumDataCache.clear();
        searchAPIResults = null;
        albumIterator = null;

        try
        {
            URL iTunesAPI = new URL("https", "itunes.apple.com", "/search?term="
                    + tagsModel.getArtist().toLowerCase().replace(" ", "+")
                    + "+" + tagsModel.getAlbum().toLowerCase().replace(" ", "+")
                    + "&media=music&entity=album"
                    + iTunesSearchParametersFromSettings);

            searchAPIResults = new JSONObject(new Scanner(
                    iTunesAPI.openStream(), "UTF-8").useDelimiter("\\A").next())
                    .getJSONArray("results");

            if (searchAPIResults.length() != 0)
            {
                albumIterator = new AlbumIterator(searchAPIResults);
            }
        }

        catch (IOException ex)
        {
            searchAPIResults = new JSONArray();
        }
    }

    public void previousAlbum()
    {
        if (newArtistOrAlbum() || newiTunesSearchParameters())
        {
            nextAlbum();
            return;
        }

        if (albumIterator.hasPrevious())
        {
            albumIterator.previous();
            if (albumDataCache.size() == albumIterator.getIndex())
            {
                albumDataCache.add(createAlbumData());
            }
        }
    }

    public void nextAlbum()
    {
        if (useLocalAlbumArtData)
        {
            localAlbumArtData.resetImageData();
            useLocalAlbumArtData = false;
        }

        if (newArtistOrAlbum() || newiTunesSearchParameters())
        {
            loadAlbumArt();
        }

        if (albumIterator != null && albumIterator.hasNext())
        {
            albumIterator.next();
            if (albumDataCache.size() == albumIterator.getIndex())
            {
                albumDataCache.add(createAlbumData());
            }
        }
    }

    private boolean newArtistOrAlbum()
    {
        return !tagsModel.getArtist().equals(artistFromTags)
                || !tagsModel.getAlbum().equals(albumFromTags);
    }

    private boolean newiTunesSearchParameters()
    {
        return !settings.getiTunesSearchParameters().equals(
                iTunesSearchParametersFromSettings);
    }

    private AlbumData createAlbumData()
    {
        JSONObject albumJSON = albumIterator.getCurrentJSONObject();

        AlbumData albumData = new AlbumData();

        albumData.albumNum =
                (albumIterator.getLength() >= 10
                && (albumIterator.getIndex() + 1) < 10) ?
            "0" + Integer.toString(albumIterator.getIndex() + 1) + " / " +
                albumIterator.getLength()
            : Integer.toString(albumIterator.getIndex() + 1) + " / " +
                albumIterator.getLength();

        albumData.artist = albumJSON.getString("artistName");
        albumData.album = albumJSON.getString("collectionName");

        try
        {
            albumData.artistLink = new URI(albumJSON.getString(
                    "artistViewUrl").split("\\?")[0]);
        }

        catch (JSONException | URISyntaxException ex){}

        try
        {
            albumData.albumLink = new URI(albumJSON.getString(
                    "collectionViewUrl").split("\\?")[0]);
        }

        catch (URISyntaxException ex){}

        try
        {
            try (ByteArrayOutputStream baosSmall = new ByteArrayOutputStream())
            {
                URL currentSmallAlbumArtLink = new URL(
                        albumJSON.getString("artworkUrl100")
                                .replace("100x100", "600x600"));

                int bytesRead;
                byte[] chunk = new byte[1048576];
                InputStream isSmall = currentSmallAlbumArtLink.openStream();

                while ((bytesRead = isSmall.read(chunk)) > 0)
                {
                    baosSmall.write(chunk, 0, bytesRead);
                }

                albumData.smallAlbumArtBytes = baosSmall.toByteArray();
            }

            try (ByteArrayOutputStream baosLarge = new ByteArrayOutputStream())
            {
                URL currentLargeAlbumArtLink = new URL(
                        albumJSON.getString("artworkUrl100")
                                .replace("100x100", "1200x1200"));

                int bytesRead;
                byte[] chunk = new byte[1048576];
                InputStream isLarge = currentLargeAlbumArtLink.openStream();

                while ((bytesRead = isLarge.read(chunk)) > 0)
                {
                    baosLarge.write(chunk, 0, bytesRead);
                }

                albumData.largeAlbumArtBytes = baosLarge.toByteArray();
            }

            albumData.albumArtImageIcon = ImageUtils.createScaledImageIcon(
                    albumData.smallAlbumArtBytes);
            albumData.albumArtExtension = ".jpg";
        }

        catch (Exception ex)
        {
            albumData.resetImageData();
        }

        return albumData;
    }

    private class AlbumData
    {
        public String albumNum;
        public String artist;
        public String album;
        public URI artistLink;
        public URI albumLink;
        public byte[] smallAlbumArtBytes;
        public byte[] largeAlbumArtBytes;
        public ImageIcon albumArtImageIcon;
        public String albumArtExtension;

        public void resetImageData()
        {
            smallAlbumArtBytes = null;
            largeAlbumArtBytes = null;
            albumArtImageIcon = null;
            albumArtExtension = null;
        }
    }

    private class AlbumIterator
    {
        private int index;
        private JSONArray jsonArray;

        public int getIndex() { return index; }
        public int getLength() { return jsonArray.length(); }

        public AlbumIterator(JSONArray jsonArray)
        {
            this.index = -1;
            this.jsonArray = jsonArray;
        }

        public void resetIndex()
        {
            index = -1;
        }

        public boolean hasPrevious()
        {
            return index != 0;
        }

        public boolean hasNext()
        {
            return index != jsonArray.length() - 1;
        }

        public void previous()
        {
            index--;
        }

        public void next()
        {
            index++;
        }

        public JSONObject getCurrentJSONObject()
        {
            return jsonArray.getJSONObject(index);
        }
    }
}