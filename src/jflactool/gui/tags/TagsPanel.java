package jflactool.gui.tags;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.gui.tags.table.TrackTableModel;

public class TagsPanel
{
    private Settings settings;

    private JPanel jpanel;
    private JTable trackTable;
    private JButton selectAll;
    private JButton selectNone;
    private JTextField artist;
    private JTextField album;
    private JTextField year;
    private JTextField genre;

    public JPanel getPanel() { return jpanel; }
    public JTable getTrackTable() { return trackTable; }
    public JButton getSelectAll() { return selectAll; }
    public JButton getSelectNone() { return selectNone; }
    public JTextField getArtistField() { return artist; }
    public JTextField getAlbumField() { return album; }
    public JTextField getYearField() { return year; }
    public JTextField getGenreField() { return genre; }

    public TagsPanel(Settings settings, Dimension PANEL_SIZE)
    {
        this.settings = settings;

        jpanel = new JPanel(new GridBagLayout());
        jpanel.setPreferredSize(PANEL_SIZE);
        jpanel.setBackground(Color.DARK_GRAY);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;

        gridBagConstraints.insets.bottom = 5;
        jpanel.add(createTrackTableScrollPane(), gridBagConstraints);

        gridBagConstraints.insets.bottom = 0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.gridy = 1;
        jpanel.add(createSelectButtonsPanel(), gridBagConstraints);

        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridy = 2;
        jpanel.add(createAlbumInfoPanel(), gridBagConstraints);

        trackTable.getTableHeader().setEnabled(false);
        trackTable.setEnabled(false);
        selectAll.setEnabled(false);
        selectNone.setEnabled(false);
        artist.setEnabled(false);
        album.setEnabled(false);
        year.setEnabled(false);
        genre.setEnabled(false);
    }

    private JScrollPane createTrackTableScrollPane()
    {
        DefaultTableModel trackTableModel = new DefaultTableModel(
                TrackTableModel.COLUMN_NAMES, 0);

        trackTable = new JTable(trackTableModel)
        {
            @Override
            public String getToolTipText(MouseEvent me)
            {
                if (settings.getDisableHints())
                {
                    return null;
                }

                Point point = me.getPoint();
                int rowIndex = rowAtPoint(point);
                int columnIndex = columnAtPoint(point);
                String toolTip = null;

                try
                {
                    if (rowIndex > -1)
                    {
                        if (columnIndex == 0)
                        {
                            toolTip = "Uncheck this box to not convert this "
                                    + "track.";
                        }

                        else if (columnIndex == 3)
                        {
                            toolTip = "Click to \u25B6 listen to this track. "
                                    + "Click Stop to stop playing this track.";
                        }
                    }
                }

                catch (RuntimeException ex){}

                return toolTip;
            }
        };
        trackTable.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent mouseEvent)
            {
                int rowIndex = trackTable.rowAtPoint(mouseEvent.getPoint());
                int columnIndex = trackTable.columnAtPoint(
                        mouseEvent.getPoint());

                if (rowIndex > -1 && columnIndex == 3)
                {
                    Object value = trackTable.getValueAt(rowIndex, columnIndex);

                    if (value instanceof JButton)
                    {
                        ((JButton) value).doClick();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent me){}
            @Override
            public void mouseReleased(MouseEvent me){}
            @Override
            public void mouseEntered(MouseEvent me){}
            @Override
            public void mouseExited(MouseEvent me){}
        });
        trackTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trackTable.setGridColor(Color.DARK_GRAY);
        trackTable.setBackground(Color.LIGHT_GRAY);
        trackTable.setSelectionForeground(Color.BLACK);
        trackTable.setSelectionBackground(Color.WHITE);
        trackTable.setShowGrid(true);
        trackTable.setFillsViewportHeight(true);
        trackTable.setRowHeight(25);
        trackTable.getColumnModel().getColumn(0).setMinWidth(100);
        trackTable.getColumnModel().getColumn(0).setMaxWidth(100);
        trackTable.getColumnModel().getColumn(1).setMinWidth(60);
        trackTable.getColumnModel().getColumn(1).setMaxWidth(60);
        trackTable.getColumnModel().getColumn(3).setMinWidth(60);
        trackTable.getColumnModel().getColumn(3).setMaxWidth(60);
        trackTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        trackTable.getTableHeader().setReorderingAllowed(false);
        trackTable.getTableHeader().setFocusable(false);
        trackTable.getTableHeader().addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent me)
            {
                if (trackTable.isEditing())
                {
                    trackTable.getCellEditor().stopCellEditing();
                }
            }

            @Override
            public void mousePressed(MouseEvent me){}
            @Override
            public void mouseReleased(MouseEvent me){}
            @Override
            public void mouseEntered(MouseEvent me){}
            @Override
            public void mouseExited(MouseEvent me){}
        });

        TableCellRenderer headerRenderer = trackTable.getTableHeader().
                getDefaultRenderer();
        JLabel headerLabel = (JLabel) headerRenderer;
        headerLabel.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane trackTableScrollPane = new JScrollPane(trackTable);
        trackTableScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return trackTableScrollPane;
    }

    private JPanel createSelectButtonsPanel()
    {
        selectAll = new JButton("Select All");
        selectNone = new JButton("Select None");

        selectAll.setFocusable(false);
        selectNone.setFocusable(false);

        if (!settings.getDisableHints())
        {
            selectAll.setToolTipText("Check the Selected box for all tracks "
                    + "in the table above.");
            selectNone.setToolTipText("Uncheck the Selected box for all tracks "
                    + "in the table above.");
        }

        JPanel selectAllNoneButtonPanel = new JPanel(
                new GridLayout(1, 2, 0, 0));
        selectAllNoneButtonPanel.setBackground(Color.DARK_GRAY);

        selectAllNoneButtonPanel.add(selectAll);
        selectAllNoneButtonPanel.add(selectNone);

        return selectAllNoneButtonPanel;
    }

    private JPanel createAlbumInfoPanel()
    {
        artist = createField("Artist");
        album = createField("Album");
        year = createField("Year");
        genre = createField("Genre");


        JPanel albumInfoPanel = new JPanel(new GridLayout(4, 1, 0, 2));
        albumInfoPanel.setBackground(Color.DARK_GRAY);

        albumInfoPanel.add(artist);
        albumInfoPanel.add(album);
        albumInfoPanel.add(year);
        albumInfoPanel.add(genre);

        return albumInfoPanel;
    }

    private JTextField createField(String fieldName)
    {
        JTextField field = new JTextField(fieldName);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBackground(Color.LIGHT_GRAY);

        if (!settings.getDisableHints())
        {
            field.setToolTipText(fieldName + " Tag");
        }

        return field;
    }
}