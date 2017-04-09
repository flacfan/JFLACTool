package jflactool.gui.action_buttons.spectrogram;

import java.awt.event.ActionListener;
import java.io.IOException;
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
import jflactool.gui.tags.TagsPanel;
import jflactool.gui.tags.fields.TagsModel;
import jflactool.misc.MusicFile;

public class SpectrogramButtonController
{
    private Settings settings;
    private TagsModel tagsModel;
    private SpectrogramModel spectrogramModel;
    private GUIFrame guiFrame;
    private SettingsFrame settingsFrame;
    private ActionButtonsPanel actionButtonsPanel;
    private TagsPanel tagsPanel;
    private AlbumArtPanel albumArtPanel;

    private LoadDragDropController loadDragDropController;
    private AlbumArtDragDropController albumArtDragDropController;

    private ActionListener actionListener;

    public SpectrogramButtonController(Settings settings, TagsModel tagsModel,
            SpectrogramModel spectrogramModel,
            GUIFrame guiFrame, SettingsFrame settingsFrame,
            ActionButtonsPanel actionButtonsPanel, TagsPanel tagsPanel,
            AlbumArtPanel albumArtPanel,
            LoadDragDropController loadDragDropController,
            AlbumArtDragDropController albumArtDragDropController)
    {
        this.settings = settings;
        this.tagsModel = tagsModel;
        this.spectrogramModel = spectrogramModel;
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
            new SpectrogramButtonController.SpectrogramProcess(
                    guiFrame, settingsFrame, actionButtonsPanel,
                    tagsPanel, albumArtPanel, loadDragDropController,
                    albumArtDragDropController).execute();
        };

        actionButtonsPanel.getSpectrogramButton().addActionListener(
                actionListener);
    }

    private class SpectrogramProcess extends AbstractProcess
    {
        public SpectrogramProcess(GUIFrame guiFrame,
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
            setGUIEnabled(false, "Spectrograms: Started");

            try
            {
                Files.createDirectories(settings.getSpectrogramPath());
            }

            catch (IOException ex){}

            generateSpectrograms();

            return null;
        }

        @Override
        protected void done()
        {
            setGUIEnabled(true, "Spectrograms: Finished");
        }

        private void generateSpectrograms()
        {
            ExecutorService executorService = Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors());

            CountDownLatch countDownLatch = new CountDownLatch(
                    tagsModel.numberOfSelectedFiles());

            tagsModel.getMusicFiles().stream()
                    .filter(musicFile -> musicFile.getSelected())
                    .forEach(musicFile -> executorService.submit(() ->
                    {
                        generateSpectrogram(musicFile);

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

        private void generateSpectrogram(MusicFile musicFile)
        {
            try
            {
                Runtime.getRuntime().exec(
                        spectrogramModel.makeSoXCommand(musicFile)).waitFor();
            }

            catch (IOException | InterruptedException ex){}
        }
    }
}