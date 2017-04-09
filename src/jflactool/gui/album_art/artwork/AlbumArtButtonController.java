package jflactool.gui.album_art.artwork;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import jflactool.gui.album_art.AlbumArtPanel;

public class AlbumArtButtonController extends AbstractAlbumArtController
{
    private ActionListener actionListener;

    public AlbumArtButtonController(AlbumArtModel albumArtModel,
            AlbumArtPanel albumArtPanel)
    {
        super(albumArtModel, albumArtPanel);
        connectController();
    }

    @Override
    final void connectController()
    {
        actionListener = actionEvent ->
        {
            JFileChooser jfc = new JFileChooser();
            jfc.setPreferredSize(new Dimension(600, 400));
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setDialogTitle("Select album art.");
            jfc.setApproveButtonText("Select");

            if (UIManager.getLookAndFeel().toString().equals(
                    "[Nimbus Look and Feel - "
                            + "javax.swing.plaf.nimbus.NimbusLookAndFeel]"))
            {
                Action details = jfc.getActionMap().get("viewTypeDetails");
                details.actionPerformed(null);
            }

            int option = jfc.showOpenDialog(new JFrame());

            if (option == JFileChooser.APPROVE_OPTION)
            {
                File albumArtFile = jfc.getSelectedFile();
                String extension = getLowerCaseExtension(
                        albumArtFile.getName());

                if (isValidExtension(extension))
                {
                    try
                    {
                        byte[] albumArtBytes = Files.readAllBytes(
                                albumArtFile.toPath());

                        updateModel(albumArtBytes, extension);
                        updatePanel(albumArtBytes);
                    }

                    catch (IOException ex){}
                }
            }
        };

        albumArtPanel.getSelectAlbumArtButton().addActionListener(
                actionListener);
    }
}