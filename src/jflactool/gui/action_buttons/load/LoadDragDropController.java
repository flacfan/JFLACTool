package jflactool.gui.action_buttons.load;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import jflactool.gui.GUIFrame;
import jflactool.gui.action_buttons.ActionButtonsPanel;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.gui.album_art.AlbumArtPanel;
import jflactool.gui.album_art.artwork.AlbumArtDragDropController;
import jflactool.gui.album_art.artwork.AlbumArtModel;
import jflactool.gui.tags.TagsPanel;
import jflactool.gui.tags.fields.TagsModel;

public class LoadDragDropController extends AbstractLoadController
{
    private DropTargetListener dropTargetListener;
    private DropTarget dropTarget;

    public DropTarget getDropTarget() { return dropTarget; }

    public LoadDragDropController(Settings settings, LoadModel loadModel,
            TagsModel tagsModel, AlbumArtModel albumArtModel,
            GUIFrame guiFrame, AlbumArtPanel albumArtPanel,
            ActionButtonsPanel actionButtonsPanel,
            TagsPanel tagsPanel,
            AlbumArtDragDropController albumArtDragDropController)
    {
        super(settings, loadModel, tagsModel, albumArtModel, guiFrame,
                albumArtPanel, actionButtonsPanel, tagsPanel,
                albumArtDragDropController);
        connectController();
    }

    @Override
    final void connectController()
    {
        dropTargetListener = new DropTargetListener()
        {
            @Override
            @SuppressWarnings("unchecked")
            public void drop(DropTargetDropEvent event)
            {
                event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                try
                {
                    findFLACFiles((List<File>) event.getTransferable().
                            getTransferData(DataFlavor.javaFileListFlavor));
                }

                catch (IOException | UnsupportedFlavorException ex){}

                event.dropComplete(true);
            }

            @Override
            public void dragEnter(DropTargetDragEvent event){}
            @Override
            public void dragExit(DropTargetEvent event){}
            @Override
            public void dragOver(DropTargetDragEvent event){}
            @Override
            public void dropActionChanged(DropTargetDragEvent event){}
        };

        dropTarget = new DropTarget(tagsPanel.getTrackTable(),
                dropTargetListener);
    }
}