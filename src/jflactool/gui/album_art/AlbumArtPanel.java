package jflactool.gui.album_art;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import jflactool.gui.action_buttons.settings.Settings;

public class AlbumArtPanel
{
    private final Dimension ARTWORK_SIZE = new Dimension(600, 600);
    private final Dimension EDGE_COMPONENT_SIZE = new Dimension(70, 70);

    private Settings settings;

    private JPanel jpanel;
    private JLabel albumNum;
    private JButton artist;
    private JButton album;
    private JLabel albumArt;
    private JButton next;
    private JButton previous;
    private JButton selectAlbumArt;

    public JPanel getPanel() { return jpanel; }
    public JLabel getAlbumNumLabel() { return albumNum; }
    public JButton getArtistButton() { return artist; }
    public JButton getAlbumButton() { return album; }
    public JLabel getAlbumArtLabel() { return albumArt; }
    public JButton getNextButton() { return next; }
    public JButton getPreviousButton() { return previous; }
    public JButton getSelectAlbumArtButton() { return selectAlbumArt; }

    public AlbumArtPanel(Settings settings, Dimension PANEL_SIZE)
    {
        this.settings = settings;

        jpanel = new JPanel(new BorderLayout(5, 5));
        jpanel.setPreferredSize(PANEL_SIZE);
        jpanel.setBackground(Color.DARK_GRAY);

        createAlbumArtLabel();

        previous = createButton("<<", "Click here to load the previous iTunes "
                + "album art result.", EDGE_COMPONENT_SIZE);
        next = createButton(">>", "Click here to load the next iTunes "
                + "album art result.", EDGE_COMPONENT_SIZE);
        selectAlbumArt = createButton("Select Album Art", "Click here to "
                + "select local album art.", EDGE_COMPONENT_SIZE);

        jpanel.add(createiTunesInfoPanel(), BorderLayout.NORTH);
        jpanel.add(albumArt, BorderLayout.CENTER);
        jpanel.add(previous, BorderLayout.WEST);
        jpanel.add(next, BorderLayout.EAST);
        jpanel.add(selectAlbumArt, BorderLayout.SOUTH);

        artist.setEnabled(false);
        album.setEnabled(false);
        previous.setEnabled(false);
        next.setEnabled(false);
        selectAlbumArt.setEnabled(false);
    }

    private void createAlbumArtLabel()
    {
        albumArt = new JLabel("", SwingConstants.CENTER);
        albumArt.setFocusable(false);
        albumArt.setBackground(Color.DARK_GRAY);
        albumArt.setOpaque(true);

        if (!settings.getDisableHints())
        {
            albumArt.setToolTipText("Drag and drop album art here to load it.");
        }

        albumArt.setPreferredSize(ARTWORK_SIZE);
    }

    private JPanel createiTunesInfoPanel()
    {
        albumNum = new JLabel("No FLAC Files Loaded", SwingConstants.CENTER);
        albumNum.setFocusable(false);
        albumNum.setBackground(Color.LIGHT_GRAY);
        albumNum.setOpaque(true);

        artist = createButton("", "Click here to open the iTunes artist link "
                + "in a web browser.", null);
        album = createButton("", "Click here to open the iTunes album link "
                + "in a web browser.", null);

        JPanel iTunesInfoPanel = new JPanel(new GridLayout(3, 1));
        iTunesInfoPanel.setPreferredSize(EDGE_COMPONENT_SIZE);
        iTunesInfoPanel.setFocusable(false);
        iTunesInfoPanel.setBackground(Color.LIGHT_GRAY);

        iTunesInfoPanel.add(albumNum);
        iTunesInfoPanel.add(artist);
        iTunesInfoPanel.add(album);

        return iTunesInfoPanel;
    }

    private JButton createButton(String text, String toolTipText,
            Dimension size)
    {
        JButton button = new JButton(text);
        button.setFocusable(false);

        if (!settings.getDisableHints())
        {
            button.setToolTipText(toolTipText);
        }

        if (size != null)
        {
            button.setPreferredSize(size);
        }

        return button;
    }
}