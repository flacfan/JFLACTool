package jflactool;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import jflactool.gui.GUIFrame;
import jflactool.gui.action_buttons.archive.ArchiveButtonController;
import jflactool.gui.action_buttons.archive.ArchiveModel;
import jflactool.gui.action_buttons.convert.ConvertButtonController;
import jflactool.gui.action_buttons.convert.ConvertModel;
import jflactool.gui.action_buttons.load.LoadButtonController;
import jflactool.gui.action_buttons.load.LoadDragDropController;
import jflactool.gui.action_buttons.load.LoadModel;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.gui.action_buttons.settings.SettingsButtonsController;
import jflactool.gui.action_buttons.settings.SettingsFrame;
import jflactool.gui.action_buttons.spectrogram.SpectrogramButtonController;
import jflactool.gui.action_buttons.spectrogram.SpectrogramModel;
import jflactool.gui.album_art.artwork.AlbumArtButtonController;
import jflactool.gui.album_art.artwork.AlbumArtDragDropController;
import jflactool.gui.album_art.artwork.AlbumArtModel;
import jflactool.gui.album_art.artwork.CycleArtworkButtonsController;
import jflactool.gui.album_art.itunes_buttons.iTunesInfoButtonsController;
import jflactool.gui.tags.fields.FieldFocusControllers;
import jflactool.gui.tags.fields.TagsModel;
import jflactool.gui.tags.select_buttons.SelectButtonsController;
import jflactool.gui.tags.table.TrackTableModelController;
import org.jaudiotagger.tag.TagOptionSingleton;

public class JFLACTool
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
            TagOptionSingleton.getInstance().setWriteMp3GenresAsText(true);

            Settings settings = new Settings();

            LoadModel loadModel = new LoadModel();
            TagsModel tagsModel = new TagsModel(settings, loadModel);
            AlbumArtModel albumArtModel = new AlbumArtModel(settings,
                    tagsModel);
            SpectrogramModel spectrogramModel = new SpectrogramModel(settings);
            ArchiveModel archiveModel = new ArchiveModel(settings, tagsModel,
                    albumArtModel);
            ConvertModel convertModel = new ConvertModel(settings, tagsModel,
                    albumArtModel);

            GUIFrame guiFrame = new GUIFrame(settings);
            SettingsFrame settingsFrame = new SettingsFrame();

            AlbumArtDragDropController albumArtDragDropController =
                    new AlbumArtDragDropController(albumArtModel,
                    guiFrame.getTabbedPane().getAlbumArtPanel());

            new LoadButtonController(settings, loadModel, tagsModel,
                    albumArtModel, guiFrame,
                    guiFrame.getTabbedPane().getAlbumArtPanel(),
                    guiFrame.getActionButtonsPanel(),
                    guiFrame.getTabbedPane().getTagsPanel(),
                    albumArtDragDropController);
            LoadDragDropController loadDragDropController =
                    new LoadDragDropController(settings, loadModel, tagsModel,
                    albumArtModel, guiFrame,
                    guiFrame.getTabbedPane().getAlbumArtPanel(),
                    guiFrame.getActionButtonsPanel(),
                    guiFrame.getTabbedPane().getTagsPanel(),
                    albumArtDragDropController);
            new SpectrogramButtonController(settings, tagsModel,
                    spectrogramModel, guiFrame, settingsFrame,
                    guiFrame.getActionButtonsPanel(),
                    guiFrame.getTabbedPane().getTagsPanel(),
                    guiFrame.getTabbedPane().getAlbumArtPanel(),
                    loadDragDropController, albumArtDragDropController);
            new ArchiveButtonController(settings, tagsModel, archiveModel,
                    albumArtModel, guiFrame, settingsFrame,
                    guiFrame.getActionButtonsPanel(),
                    guiFrame.getTabbedPane().getTagsPanel(),
                    guiFrame.getTabbedPane().getAlbumArtPanel(),
                    loadDragDropController, albumArtDragDropController);
            new ConvertButtonController(settings, tagsModel, albumArtModel,
                    convertModel, guiFrame, settingsFrame,
                    guiFrame.getActionButtonsPanel(),
                    guiFrame.getTabbedPane().getTagsPanel(),
                    guiFrame.getTabbedPane().getAlbumArtPanel(),
                    loadDragDropController, albumArtDragDropController);
            new SettingsButtonsController(settings,
                    guiFrame.getActionButtonsPanel(),
                    settingsFrame);

            new TrackTableModelController(tagsModel,
                    guiFrame.getActionButtonsPanel(),
                    guiFrame.getTabbedPane().getTagsPanel());
            new SelectButtonsController(tagsModel,
                    guiFrame.getActionButtonsPanel(),
                    guiFrame.getTabbedPane().getTagsPanel());
            new FieldFocusControllers(tagsModel,
                    guiFrame.getTabbedPane().getTagsPanel());

            new iTunesInfoButtonsController(albumArtModel,
                    guiFrame.getTabbedPane().getAlbumArtPanel());
            new CycleArtworkButtonsController(settings,
                    albumArtModel, guiFrame.getTabbedPane()
                            .getAlbumArtPanel());
            new AlbumArtButtonController(albumArtModel,
                    guiFrame.getTabbedPane().getAlbumArtPanel());
        });
    }
}