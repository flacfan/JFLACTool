package jflactool.gui.action_buttons.convert;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jflactool.gui.GUIFrame;
import jflactool.gui.action_buttons.AbstractProcess;
import jflactool.gui.action_buttons.ActionButtonsPanel;
import jflactool.gui.action_buttons.load.LoadDragDropController;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.gui.action_buttons.settings.SettingsFrame;
import jflactool.gui.album_art.AlbumArtPanel;
import jflactool.gui.album_art.artwork.AlbumArtDragDropController;
import jflactool.gui.album_art.artwork.AlbumArtModel;
import jflactool.gui.tags.TagsPanel;
import jflactool.gui.tags.fields.TagsModel;
import jflactool.misc.MusicFile;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.mp4.Mp4Tag;

public class ConvertButtonController
{
    private Settings settings;
    private TagsModel tagsModel;
    private AlbumArtModel albumArtModel;
    private ConvertModel convertModel;
    private GUIFrame guiFrame;
    private SettingsFrame settingsFrame;
    private ActionButtonsPanel actionButtonsPanel;
    private TagsPanel tagsPanel;
    private AlbumArtPanel albumArtPanel;

    private LoadDragDropController loadDragDropController;
    private AlbumArtDragDropController albumArtDragDropController;

    private ActionListener actionListener;

    public ConvertButtonController(Settings settings, TagsModel tagsModel,
            AlbumArtModel albumArtModel, ConvertModel convertModel,
            GUIFrame guiFrame, SettingsFrame settingsFrame,
            ActionButtonsPanel actionButtonsPanel,
            TagsPanel tagsPanel, AlbumArtPanel albumArtPanel,
            LoadDragDropController loadDragDropController,
            AlbumArtDragDropController albumArtDragDropController)
    {
        this.settings = settings;
        this.tagsModel = tagsModel;
        this.albumArtModel = albumArtModel;
        this.convertModel = convertModel;
        this.guiFrame = guiFrame;
        this.settingsFrame = settingsFrame;
        this.actionButtonsPanel = actionButtonsPanel;
        this.tagsPanel = tagsPanel;
        this.albumArtPanel = albumArtPanel;

        this.loadDragDropController = loadDragDropController;
        this.albumArtDragDropController = albumArtDragDropController;

        connectController();
    }

    private void connectController()
    {
        actionListener = actionEvent ->
        {
            new ConvertProcess(guiFrame, settingsFrame, actionButtonsPanel,
                    tagsPanel, albumArtPanel, loadDragDropController,
                    albumArtDragDropController).execute();
        };

        actionButtonsPanel.getConvertButton().addActionListener(
                actionListener);
    }

    private class ConvertProcess extends AbstractProcess
    {
        public ConvertProcess(GUIFrame guiFrame, SettingsFrame settingsFrame,
                ActionButtonsPanel actionButtonsPanel, TagsPanel tagsPanel,
                AlbumArtPanel albumArtPanel,
                LoadDragDropController loadDragDropController,
                AlbumArtDragDropController albumArtDragDropController)
        {
            super(guiFrame, settingsFrame, actionButtonsPanel, tagsPanel,
                    albumArtPanel, loadDragDropController,
                    albumArtDragDropController);
        }

        @Override
        protected Void doInBackground() throws Exception
        {
            setGUIEnabled(false, "Converting: Started");

            convertModel.updateModel();

            try
            {
                Files.createDirectories(convertModel.getConvertPath());
            }

            catch (IOException ex){}

            if (settings.getSaveAlbumArtToFolderConvert())
            {
                saveAlbumArt();
            }

            convertFiles();

            return null;
        }

        @Override
        protected void done()
        {
            setGUIEnabled(true, "Converting: Finished");
        }

        private void saveAlbumArt()
        {
            if (albumArtModel.getCurrentSmallAlbumArtBytes() != null)
            {
                try
                {
                    Files.write(convertModel.getAlbumArtPath(),
                        albumArtModel.getCurrentSmallAlbumArtBytes());
                }

                catch (IOException ex){}
            }
        }

        private void convertFiles()
        {
            ExecutorService executorService = Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors());

            CountDownLatch countDownLatch = new CountDownLatch(
                    tagsModel.numberOfSelectedFiles());

            if (settings.getConversionCodec().equals("aac") &&
                    settings.getRunningOnMac())
            {
                tagsModel.getMusicFiles().stream()
                        .filter(musicFile -> musicFile.getSelected())
                        .forEach(musicFile -> executorService.submit(() ->
                                {
                                    decodeFLACFile(musicFile);
                                    encodeWAVFileToM4A(musicFile);
                                    deleteWAVFile(musicFile);
                                    tagM4AFile(musicFile);
                                    moveM4AFile(musicFile);

                                    countDownLatch.countDown();
                                }
                        ));
            }

            else
            {
                tagsModel.getMusicFiles().stream()
                        .filter(musicFile -> musicFile.getSelected())
                        .forEach(musicFile -> executorService.submit(() ->
                                {
                                    decodeFLACFile(musicFile);
                                    encodeWAVFileToMP3(musicFile);
                                    deleteWAVFile(musicFile);
                                    tagMP3File(musicFile);
                                    moveMP3File(musicFile);

                                    countDownLatch.countDown();
                                }
                        ));
            }

            executorService.shutdown();

            try
            {
                countDownLatch.await();
            }

            catch (InterruptedException ex){}
        }

        private void decodeFLACFile(MusicFile musicFile)
        {
            try
            {
                Runtime.getRuntime().exec(
                        convertModel.makeSoXCommand(musicFile)).waitFor();
            }

            catch (IOException | InterruptedException ex){}
        }

        private void encodeWAVFileToMP3(MusicFile musicFile)
        {
            try
            {
                Runtime.getRuntime().exec(
                        convertModel.makeLAMECommand(musicFile)).waitFor();
            }

            catch (IOException | InterruptedException ex){}
        }

        private void encodeWAVFileToM4A(MusicFile musicFile)
        {
            try
            {
                Runtime.getRuntime().exec(
                        convertModel.makeAFConvertCommand(musicFile)).waitFor();
            }

            catch (IOException | InterruptedException ex){}
        }

        private void deleteWAVFile(MusicFile musicFile)
        {
            try
            {
                Files.deleteIfExists(musicFile.getTemporaryWAVPath());
            }

            catch (IOException ex){}
        }

        private void tagMP3File(MusicFile musicFile)
        {
            try
            {
                AbstractID3v2Tag id3v2Tag = convertModel.createID3v2Tag();
                id3v2Tag.setField(FieldKey.TRACK, musicFile.getTrackNumber());
                id3v2Tag.setField(FieldKey.TITLE, musicFile.getTitle());

                MP3File mp3File = (MP3File) AudioFileIO.read(
                        musicFile.getTemporaryMP3Path().toFile());
                mp3File.setTag(id3v2Tag);
                mp3File.commit();
            }

            catch (CannotWriteException | KeyNotFoundException |
                    CannotReadException | IOException | TagException |
                    ReadOnlyFileException | InvalidAudioFrameException ex){}
        }

        private void tagM4AFile(MusicFile musicFile)
        {
            try
            {
                Mp4Tag m4aTag = convertModel.createM4ATag();
                m4aTag.setField(FieldKey.TRACK, musicFile.getTrackNumber());
                m4aTag.setField(FieldKey.TITLE, musicFile.getTitle());

                AudioFile m4aFile = AudioFileIO.read(
                        musicFile.getTemporaryM4APath().toFile());
                m4aFile.setTag(m4aTag);
                m4aFile.commit();
            }

            catch (CannotWriteException | KeyNotFoundException |
                    CannotReadException | IOException | TagException |
                    ReadOnlyFileException | InvalidAudioFrameException ex){}
        }

        private void moveMP3File(MusicFile musicFile)
        {
            try
            {
                Files.move(musicFile.getTemporaryMP3Path(),
                        musicFile.getDestinationMP3Path(),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            catch (IOException ex){}
        }

        private void moveM4AFile(MusicFile musicFile)
        {
            try
            {
                Files.move(musicFile.getTemporaryM4APath(),
                        musicFile.getDestinationM4APath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            catch (IOException ex){}
        }
    }
}