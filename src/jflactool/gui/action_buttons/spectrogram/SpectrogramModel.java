package jflactool.gui.action_buttons.spectrogram;

import java.util.Arrays;
import jflactool.gui.action_buttons.settings.Settings;
import jflactool.misc.MusicFile;

public class SpectrogramModel
{
    private Settings settings;

    private String[] baseSoXCommand;

    public SpectrogramModel(Settings settings)
    {
        this.settings = settings;
        makeBaseSoXCommand();
    }

    private void makeBaseSoXCommand()
    {
        baseSoXCommand = new String[10];
        baseSoXCommand[0] = settings.getSoXPath().toString();
        baseSoXCommand[2] = "-n";
        baseSoXCommand[3] = "spectrogram";
        baseSoXCommand[4] = "-x";
        baseSoXCommand[5] = "1776";
        baseSoXCommand[6] = "-t";
        baseSoXCommand[8] = "-o";
    }

    public String[] makeSoXCommand(MusicFile musicFile)
    {
        String filename = musicFile.getSourceFLACPath().getFileName()
                .toString();
        String[] command = Arrays.copyOf(baseSoXCommand, baseSoXCommand.length);
        command[1] = musicFile.getSourceFLACPath().toString();
        command[7] = filename.replace(".flac", "");
        command[9] = settings.getSpectrogramPath().resolve(filename
                .replace(".flac", ".png")).toString();
        return command;
    }
}