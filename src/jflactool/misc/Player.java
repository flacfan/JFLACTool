package jflactool.misc;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.PCMProcessor;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.util.ByteData;

public class Player implements PCMProcessor
{
    private Path flacFile;
    private AudioFormat audioFormat;
    private DataLine.Info dataLineInfo;
    private SourceDataLine sourceDataLine;
    private boolean trackPreviewable;

    public boolean getTrackPreviewable() { return trackPreviewable; }

    public Player(Path flacFile)
    {
        this.flacFile = flacFile;
        trackPreviewable = true;
    }

    public void play()
    {
        try (FileInputStream is = new FileInputStream(flacFile.toString()))
        {
            FLACDecoder decoder = new FLACDecoder(is);
            decoder.addPCMProcessor(this);
            decoder.decode();
        }

        catch (IOException ex){}

        sourceDataLine.drain();
        sourceDataLine.close();
    }

    public void stop()
    {
        sourceDataLine.stop();
    }

    @Override
    public void processStreamInfo(StreamInfo streamInfo)
    {
        audioFormat = getAudioFormat(streamInfo);
        dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat,
                AudioSystem.NOT_SPECIFIED);

        try
        {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat, AudioSystem.NOT_SPECIFIED);
            sourceDataLine.start();
        }

        catch (IllegalArgumentException | LineUnavailableException ex)
        {
            trackPreviewable = false;
        }
    }

    @Override
    public void processPCM(ByteData pcm)
    {
        sourceDataLine.write(pcm.getData(), 0, pcm.getLen());
    }

    private AudioFormat getAudioFormat(StreamInfo streamInfo)
    {
        return new AudioFormat(streamInfo.getSampleRate(),
                streamInfo.getBitsPerSample(), streamInfo.getChannels(),
                streamInfo.getBitsPerSample() > 8, false);
    }
}