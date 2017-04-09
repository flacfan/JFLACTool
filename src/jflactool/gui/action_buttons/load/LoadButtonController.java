package jflactool.gui.action_buttons.load;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import jflactool.gui.GUIFrame;
import jflactool.gui.action_buttons.ActionButtonsPanel;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.gui.album_art.AlbumArtPanel;
import jflactool.gui.album_art.artwork.AlbumArtDragDropController;
import jflactool.gui.album_art.artwork.AlbumArtModel;
import jflactool.gui.tags.TagsPanel;
import jflactool.gui.tags.fields.TagsModel;

public class LoadButtonController extends AbstractLoadController
{
    private ActionListener actionListener;

    public LoadButtonController(Settings settings, LoadModel loadModel,
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
        actionListener = actionEvent ->
        {
            JFileChooser jfc = new JFileChooser();
            jfc.setPreferredSize(new Dimension(600, 400));
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            jfc.setMultiSelectionEnabled(true);
            jfc.setDialogTitle("Select FLAC files and/or folders with FLAC "
                    + "files.");
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
                findFLACFiles(Arrays.asList(jfc.getSelectedFiles()));
            }
        };

        actionButtonsPanel.getLoadButton().addActionListener(
                actionListener);
    }
}