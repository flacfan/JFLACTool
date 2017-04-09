package jflactool.gui.action_buttons.archive;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
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
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.flac.FlacTag;

public class ArchiveButtonController
{
    private Settings settings;
    private TagsModel tagsModel;
    private ArchiveModel archiveModel;
    private AlbumArtModel albumArtModel;
    private GUIFrame guiFrame;
    private SettingsFrame settingsFrame;
    private ActionButtonsPanel actionButtonsPanel;
    private TagsPanel tagsPanel;
    private AlbumArtPanel albumArtPanel;

    private LoadDragDropController loadDragDropController;
    private AlbumArtDragDropController albumArtDragDropController;

    private ActionListener actionListener;

    public ArchiveButtonController(Settings settings, TagsModel tagsModel,
            ArchiveModel archiveModel, AlbumArtModel albumArtModel,
            GUIFrame guiFrame, SettingsFrame settingsFrame,
            ActionButtonsPanel actionButtonsPanel, TagsPanel tagsPanel,
            AlbumArtPanel albumArtPanel,
            LoadDragDropController loadDragDropController,
            AlbumArtDragDropController albumArtDragDropController)
    {
        this.settings = settings;
        this.tagsModel = tagsModel;
        this.archiveModel = archiveModel;
        this.albumArtModel = albumArtModel;
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
            new ArchiveButtonController.ArchiveProcess(
                    guiFrame, settingsFrame, actionButtonsPanel,
                    tagsPanel, albumArtPanel, loadDragDropController,
                    albumArtDragDropController).execute();
        };

        actionButtonsPanel.getArchiveButton().addActionListener(
                actionListener);
    }

    private class ArchiveProcess extends AbstractProcess
    {
        public ArchiveProcess(GUIFrame guiFrame,
                SettingsFrame settingsFrame,
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
            setGUIEnabled(false, "Archiving: Started");

            archiveModel.updateModel(
                    (String) actionButtonsPanel.getArchiveComboBox()
                            .getSelectedItem());

            try
            {
                Files.createDirectories(archiveModel.getArchivePath());
            }

            catch (IOException ex){}

            if (settings.getSaveAlbumArtToFolderArchive())
            {
                saveAlbumArt();
            }

            archiveFiles();

            return null;
        }

        @Override
        protected void done()
        {
            setGUIEnabled(true, "Archiving: Finished");
        }

        private void saveAlbumArt()
        {
            if (albumArtModel.getCurrentLargeAlbumArtBytes() != null)
            {
                try
                {
                    Files.write(archiveModel.getAlbumArtPath(),
                            albumArtModel.getCurrentLargeAlbumArtBytes());
                }

                catch (IOException ex){}
            }
        }

        private void archiveFiles()
        {
            ExecutorService executorService = Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors());

            CountDownLatch countDownLatch = new CountDownLatch(
                    tagsModel.numberOfSelectedFiles());

            tagsModel.getMusicFiles().stream()
                    .filter(musicFile -> musicFile.getSelected())
                    .forEach(musicFile -> executorService.submit(() ->
                    {
                        archiveFile(musicFile);
                        tagFile(musicFile);

                        countDownLatch.countDown();
                    }
            ));

            executorService.shutdown();

            try
            {
                countDownLatch.await();
            }

            catch (InterruptedException ex){}
        }

        private void archiveFile(MusicFile musicFile)
        {
            try
            {
                boolean success = false;
                int attempt = 1;
                int volume = 100;

                outerloop:
                while (!success)
                {
                    Process process;

                    if (volume == 100)
                    {
                        process = Runtime.getRuntime().exec(
                                archiveModel.makeSoXCommand(musicFile));
                    }

                    else
                    {
                        process = Runtime.getRuntime().exec(
                                archiveModel.makeSoXCommand(musicFile, volume));
                    }

                    process.waitFor();

                    String line;
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(process.getErrorStream()));
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        if (line.contains("clipped"))
                        {
                            attempt++;
                            if (attempt % 3 == 1) volume--;
                            continue outerloop;
                        }
                    }

                    success = true;
                }
            }

            catch (IOException | InterruptedException ex){}
        }

        private void tagFile(MusicFile musicFile)
        {
            try
            {
                AudioFile flacFile = AudioFileIO.read(
                        musicFile.getDestinationFLACPath().toFile());
                FlacTag flacTag = archiveModel.createFLACTag();
                flacTag.setField(FieldKey.TRACK, musicFile.getTrackNumber());
                flacTag.setField(FieldKey.TITLE, musicFile.getTitle());
                flacTag.setField(FieldKey.ENCODER,
                        flacFile.getTag().getFirst(FieldKey.ENCODER));
                flacFile.setTag(flacTag);
                flacFile.commit();
            }

            catch (CannotWriteException | KeyNotFoundException |
                    CannotReadException | IOException | TagException |
                    ReadOnlyFileException | InvalidAudioFrameException ex){}
        }
    }
}