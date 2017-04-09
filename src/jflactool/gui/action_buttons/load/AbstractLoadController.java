package jflactool.gui.action_buttons.load;

import java.io.File;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import jflactool.gui.GUIFrame;
import jflactool.gui.action_buttons.ActionButtonsPanel;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.gui.album_art.AlbumArtPanel;
import jflactool.gui.album_art.artwork.AlbumArtDragDropController;
import jflactool.gui.album_art.artwork.AlbumArtModel;
import jflactool.gui.tags.TagsPanel;
import jflactool.gui.tags.fields.TagsModel;
import jflactool.gui.tags.table.CenterCellEditor;
import jflactool.gui.tags.table.TrackTableModel;

public abstract class AbstractLoadController
{
    private Settings settings;
    private LoadModel loadModel;
    private TagsModel tagsModel;
    private AlbumArtModel albumArtModel;
    private GUIFrame guiFrame;
    private AlbumArtPanel albumArtPanel;

    ActionButtonsPanel actionButtonsPanel;
    TagsPanel tagsPanel;

    private AlbumArtDragDropController albumArtDragDropController;

    private boolean firstLoad;

    AbstractLoadController(Settings settings, LoadModel loadModel,
            TagsModel tagsModel, AlbumArtModel albumArtModel,
            GUIFrame guiFrame, AlbumArtPanel albumArtPanel,
            ActionButtonsPanel actionButtonsPanel,
            TagsPanel tagsPanel,
            AlbumArtDragDropController albumArtDragDropController)
    {
        this.settings = settings;
        this.loadModel = loadModel;
        this.tagsModel = tagsModel;
        this.albumArtModel = albumArtModel;
        this.guiFrame = guiFrame;
        this.albumArtPanel = albumArtPanel;

        this.actionButtonsPanel = actionButtonsPanel;
        this.tagsPanel = tagsPanel;

        this.albumArtDragDropController = albumArtDragDropController;

        this.firstLoad = true;
    }

    abstract void connectController();

    final void findFLACFiles(List<File> musicFilesOrFolders)
    {
        musicFilesOrFolders.stream().forEach(musicFileOrFolder ->
        {
            loadModel.loadFLACFiles(musicFileOrFolder.toPath());
        });

        if (loadModel.flacFilesFound())
        {
            loadModel.loadTempPaths();
            updateModels();
            updatePanels();
        }
    }

    private void updateModels()
    {
        tagsModel.loadTags();
        albumArtModel.loadAlbumArt();
    }

    private void updatePanels()
    {
        updateTrackTable();

        tagsPanel.getArtistField().setText(tagsModel.getArtist());
        tagsPanel.getAlbumField().setText(tagsModel.getAlbum());
        tagsPanel.getYearField().setText(tagsModel.getYear());
        tagsPanel.getGenreField().setText(tagsModel.getGenre());

        albumArtPanel.getAlbumNumLabel().setText(
                albumArtModel.getCurrentAlbumNum());
        albumArtPanel.getArtistButton().setText(
                albumArtModel.getCurrentArtist());
        albumArtPanel.getAlbumButton().setText(albumArtModel.getCurrentAlbum());
        albumArtPanel.getAlbumArtLabel().setIcon(null);

        if (!settings.getDisableHints())
        {
            albumArtPanel.getAlbumArtLabel().setToolTipText(
                    "Drag and drop album art here to load it.");
        }

        if (tagsModel.numberOfSelectedFiles() > 1)
        {
            guiFrame.getFrame().setTitle(
                loadModel.getSourceFLACPaths().size() + " FLAC Files Loaded");
        }

        else
        {
            guiFrame.getFrame().setTitle(
                loadModel.getSourceFLACPaths().size() + " FLAC File Loaded");
        }

        if (firstLoad)
        {
            tagsPanel.getTrackTable().getTableHeader().setEnabled(true);
            tagsPanel.getTrackTable().setEnabled(true);
            tagsPanel.getSelectAll().setEnabled(true);
            tagsPanel.getSelectNone().setEnabled(true);
            tagsPanel.getArtistField().setEnabled(true);
            tagsPanel.getAlbumField().setEnabled(true);
            tagsPanel.getYearField().setEnabled(true);
            tagsPanel.getGenreField().setEnabled(true);

            albumArtPanel.getSelectAlbumArtButton().setEnabled(true);

            albumArtDragDropController.getDropTarget().setActive(true);

            firstLoad = false;
        }

        actionButtonsPanel.getSpectrogramButton().setEnabled(true);
        actionButtonsPanel.getArchiveButton().setEnabled(true);
        actionButtonsPanel.getArchiveComboBox().setEnabled(true);
        actionButtonsPanel.getConvertButton().setEnabled(true);

        albumArtPanel.getArtistButton().setEnabled(false);
        albumArtPanel.getAlbumButton().setEnabled(false);
        albumArtPanel.getPreviousButton().setEnabled(false);
        albumArtPanel.getNextButton().setEnabled(true);
    }

    private void updateTrackTable()
    {
        AbstractTableModel abstractTableModel =
                (AbstractTableModel) tagsPanel.getTrackTable().getModel();
        if (abstractTableModel instanceof TrackTableModel)
        {
            ((TrackTableModel) abstractTableModel).stopTrackIfPlaying();
        }

        TrackTableModel trackTableModel = new TrackTableModel(
                tagsModel.getMusicFiles());
        for (TableModelListener tableModelListener :
                abstractTableModel.getTableModelListeners())
        {
            trackTableModel.addTableModelListener(tableModelListener);
        }
        tagsPanel.getTrackTable().setModel(trackTableModel);

        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tagsPanel.getTrackTable().getColumnModel().getColumn(1).
                setCellRenderer(centerRenderer);

        CenterCellEditor centerCellEditor = new CenterCellEditor();
        centerCellEditor.setClickCountToStart(1);
        tagsPanel.getTrackTable().getColumnModel().getColumn(1).
                setCellEditor(centerCellEditor);

        DefaultCellEditor singleClickCellEditor = new DefaultCellEditor(
                new JTextField());
        singleClickCellEditor.setClickCountToStart(1);
        tagsPanel.getTrackTable().getColumnModel().getColumn(2).setCellEditor(
                singleClickCellEditor);

        tagsPanel.getTrackTable().getColumnModel().getColumn(3).setCellRenderer(
                (JTable table, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) ->
                        (JButton) value);

        TableRowSorter<TrackTableModel> rowSorter = new TableRowSorter<>(
                trackTableModel);
        rowSorter.setComparator(3, (JButton button1, JButton button2) ->
            button1.getText().compareTo(button2.getText()));
        tagsPanel.getTrackTable().setRowSorter(rowSorter);

        tagsPanel.getTrackTable().getColumnModel().getColumn(0).
                setMinWidth(100);
        tagsPanel.getTrackTable().getColumnModel().getColumn(0).
                setMaxWidth(100);
        tagsPanel.getTrackTable().getColumnModel().getColumn(1).
                setMinWidth(60);
        tagsPanel.getTrackTable().getColumnModel().getColumn(1).
                setMaxWidth(60);
        tagsPanel.getTrackTable().getColumnModel().getColumn(3).
                setMinWidth(60);
        tagsPanel.getTrackTable().getColumnModel().getColumn(3).
                setMaxWidth(60);
    }
}