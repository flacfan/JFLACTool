package jflactool.misc;

public class StringUtils
{
    public static String replaceInvalidChars(String string)
    {
        return string.replace("\"", "\'\'")
                .replace("\\", "-")
                .replace("/", ",")
                .replace(":", ";")
                .replace("*", "x")
                .replace("?", "\u0000")
                .replace("<", "[")
                .replace(">", "]")
                .replace("|", "!");
    }
}