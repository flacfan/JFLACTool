package jflactool.gui.album_art.artwork;

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
import java.nio.file.Files;
import java.util.List;
import jflactool.gui.album_art.AlbumArtPanel;

public class AlbumArtDragDropController extends AbstractAlbumArtController
{
    private DropTargetListener dropTargetListener;
    private DropTarget dropTarget;

    public DropTarget getDropTarget() { return dropTarget; }

    public AlbumArtDragDropController(AlbumArtModel albumArtModel,
            AlbumArtPanel albumArtPanel)
    {
        super(albumArtModel, albumArtPanel);
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
                    List<File> files = (List<File>) event.getTransferable().
                            getTransferData(DataFlavor.javaFileListFlavor);

                    if (files.size() == 1)
                    {
                        File albumArtFile = files.get(0);

                        if (Files.isRegularFile(albumArtFile.toPath()))
                        {
                            String extension = getLowerCaseExtension(
                                    albumArtFile.getName());

                            if (isValidExtension(extension))
                            {
                                byte[] albumArtBytes = Files.readAllBytes(
                                        albumArtFile.toPath());

                                updateModel(albumArtBytes, extension);
                                updatePanel(albumArtBytes);
                            }
                        }
                    }
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

        dropTarget = new DropTarget(albumArtPanel.getAlbumArtLabel(),
                dropTargetListener);
        dropTarget.setActive(false);
    }
}