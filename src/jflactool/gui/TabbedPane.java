package jflactool.gui;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.gui.album_art.AlbumArtPanel;
import jflactool.gui.tags.TagsPanel;

public class TabbedPane
{
    private JTabbedPane tabbedPane;
    private TagsPanel tagsPanel;
    private AlbumArtPanel albumArtPanel;

    public JTabbedPane getTabbedPane() { return tabbedPane; }
    public TagsPanel getTagsPanel() { return tagsPanel; }
    public AlbumArtPanel getAlbumArtPanel() { return albumArtPanel; }

    private final Dimension PANEL_SIZE;
    private final Dimension LABEL_SIZE;

    public TabbedPane(Settings settings)
    {
        if (UIManager.getLookAndFeel().getName().equals("Nimbus"))
        {
            PANEL_SIZE = new Dimension(750, 790);
        }

        else
        {
            PANEL_SIZE = new Dimension(750, 775);
        }

        tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);

        tagsPanel = new TagsPanel(settings, PANEL_SIZE);
        albumArtPanel = new AlbumArtPanel(settings, PANEL_SIZE);

        tabbedPane.addTab("Tags", tagsPanel.getPanel());
        tabbedPane.addTab("Album Art", albumArtPanel.getPanel());

        JLabel tagsLabel = new JLabel("Tags", SwingConstants.CENTER);
        JLabel albumArtLabel = new JLabel("Album Art", SwingConstants.CENTER);

        LABEL_SIZE = new Dimension(343, 35);

        tagsLabel.setPreferredSize(LABEL_SIZE);
        albumArtLabel.setPreferredSize(LABEL_SIZE);

        tagsLabel.setFocusable(false);
        albumArtLabel.setFocusable(false);

        tabbedPane.setTabComponentAt(0, tagsLabel);
        tabbedPane.setTabComponentAt(1, albumArtLabel);
    }
}