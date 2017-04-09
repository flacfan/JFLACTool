package jflactool.gui.action_buttons.load;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;

public class LoadModel
{
    private ArrayList<Path> sourceFLACPaths;
    private ArrayList<Path> temporaryFLACPaths;

    public ArrayList<Path> getSourceFLACPaths() { return sourceFLACPaths; }

    public LoadModel()
    {
        sourceFLACPaths = new ArrayList<>();
        temporaryFLACPaths = new ArrayList<>();
    }

    public void loadFLACFiles(Path musicFileOrFolder)
    {
        EnumSet<FileVisitOption> opts = EnumSet.of(
                FileVisitOption.FOLLOW_LINKS);
        try
        {
            Files.walkFileTree(musicFileOrFolder, opts, Integer.MAX_VALUE,
                    new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path path,
                        BasicFileAttributes attrs) throws IOException
                {
                    if (attrs.isRegularFile()
                            && path.getFileName().toString().toLowerCase().
                                    endsWith(".flac"))
                    {
                        temporaryFLACPaths.add(path);
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        }

        catch (IOException ex){}
    }

    public boolean flacFilesFound()
    {
       return !temporaryFLACPaths.isEmpty();
    }

    public void loadTempPaths()
    {
        sourceFLACPaths.clear();
        sourceFLACPaths.addAll(temporaryFLACPaths);
        temporaryFLACPaths.clear();
    }
}