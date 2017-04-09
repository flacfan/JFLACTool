package jflactool.gui.tags.table;

import java.nio.file.Path;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import jflactool.misc.MusicFile;
import jflactool.misc.Player;
import jflactool.misc.StringUtils;

public class TrackTableModel extends AbstractTableModel
{
    public static final String[] COLUMN_NAMES =
    {
        "Selected", "Track #", "Title", "Audio"
    };

    private ArrayList<MusicFile> musicFiles;

    private final JButton[] PLAY_BUTTONS;

    private TrackPlayer trackPlayer;
    private JButton playingButton;

    public TrackTableModel(ArrayList<MusicFile> musicFiles)
    {
        this.musicFiles = musicFiles;

        PLAY_BUTTONS = new JButton[musicFiles.size()];

        for (int i = 0; i < PLAY_BUTTONS.length; i++)
        {
            PLAY_BUTTONS[i] = new JButton("\u25B6");
        }
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
        return column != 3;
    }

    @Override
    public int getRowCount() { return musicFiles.size(); }

    @Override
    public int getColumnCount() { return COLUMN_NAMES.length; }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if (columnIndex == 0)
        {
            return Boolean.class;
        }

        else if (columnIndex == 3)
        {
            return JButton.class;
        }

        return String.class;
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        return COLUMN_NAMES[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        MusicFile musicFile = musicFiles.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return musicFile.getSelected();
            case 1:
                return musicFile.getTrackNumber();
            case 2:
                return musicFile.getTitle();
            case 3:
                if (PLAY_BUTTONS[rowIndex].getActionListeners().length == 0)
                {
                    PLAY_BUTTONS[rowIndex].addActionListener(actionEvent ->
                    {
                        JButton clickedButton =
                                (JButton) actionEvent.getSource();

                        if (playingButton == null)
                        {
                            trackPlayer = new TrackPlayer(
                                    musicFile.getSourceFLACPath(),
                                    clickedButton);
                            trackPlayer.execute();
                            playingButton = clickedButton;
                        }

                        else
                        {
                            trackPlayer.cancel(true);

                            if (clickedButton != playingButton)
                            {
                                trackPlayer = new TrackPlayer(
                                        musicFile.getSourceFLACPath(),
                                        clickedButton);
                                trackPlayer.execute();
                                playingButton = clickedButton;
                            }

                            else
                            {
                                trackPlayer = null;
                                playingButton = null;
                            }
                        }

                        for (int i = 0; i < musicFiles.size(); i++)
                        {
                            fireTableCellUpdated(i, 3);
                        }
                    });
                }
                return PLAY_BUTTONS[rowIndex];
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex)
    {
        MusicFile musicFile = musicFiles.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                musicFile.setSelected((Boolean) value);
                break;
            case 1:
                String trackNumber = ((String) value).trim();
                if (trackNumber.isEmpty()) { break; }
                musicFile.setTrackNumber(trackNumber);
                musicFile.setFileName(musicFile.getTrackNumber() + " - " +
                        StringUtils.replaceInvalidChars(musicFile.getTitle()));
                break;
            case 2:
                String title = ((String) value).trim();
                if (title.isEmpty()) { break; }
                musicFile.setTitle(title);
                musicFile.setFileName(musicFile.getTrackNumber() + " - " +
                        StringUtils.replaceInvalidChars(musicFile.getTitle()));
                break;
        }

        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public JButton[] getPlayButtons()
    {
        return PLAY_BUTTONS;
    }

    public void stopTrackIfPlaying()
    {
        if (trackPlayer != null && !trackPlayer.isDone())
        {
            trackPlayer.cancel(true);
            trackPlayer = null;
            playingButton = null;
        }
    }

    private class TrackPlayer extends SwingWorker<Void, Void>
    {
        private Player player;
        private JButton trackButton;

        public TrackPlayer(Path flacFile, JButton trackButton)
        {
            player = new Player(flacFile);
            this.trackButton = trackButton;
        }

        @Override
        protected Void doInBackground() throws Exception
        {
            trackButton.setText("Stop");
            player.play();
            return null;
        }

        @Override
        protected void done()
        {
            if (player.getTrackPreviewable())
            {
                player.stop();
                trackButton.setText("\u25B6");
            }

            else
            {
                trackPlayer = null;
                playingButton = null;
                trackButton.setText("!");
            }

            for (int i = 0; i < musicFiles.size(); i++)
            {
                fireTableCellUpdated(i, 3);
            }
        }
    }
}