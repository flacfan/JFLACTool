JFLACTool
=========

I created JFLACTool to help me quickly and easily build my digital music library while keeping everything organized and consistent. This includes things like standardizing file/folder names and folder structure, properly tagging files and including album art, and ensuring files have the same bit depth, sample rate, and bit rate.

Features
--------

* Spectrogram
    * Generate spectrograms for FLAC files to visually confirm their losslessness.
* Archive
    * Reduce the bit depth and sample rate of higher resolution FLAC files to CD quality (16-bit/44.1kHz).
    * Recompress FLAC files with the latest version of FLAC at compression level 8.
    * Tag FLAC files using the information you enter into JFLACTool.
    * Store archived FLAC files with high resolution album art from iTunes.
* Convert
    * Quickly load FLAC files, correct tags, select album art, then convert to MP3.

Setup
-----

**[Java Runtime Environment](https://www.java.com/download/) 8 or later is required to run JFLACTool.**

For converting FLAC to MP3 on Windows you'll also need to install Visual C++ Redistributable for Visual Studio 2015. Download and install vc_redist.x86.exe from [here](https://www.microsoft.com/en-us/download/details.aspx?id=48145).

Download the [latest JFLACTool.zip](https://github.com/flacfan/JFLACTool/releases/latest) and extract its contents.

**Before running JFLACTool check out the contents of resources/Settings.properties using a text editor.**

You may need to change the **lame.path** and **sox.path** settings based on which operating system you are using.

You also might want to change the Country Code in the **itunes.search.parameters** setting. JFLACTool searches the US iTunes Store for album art by default. You can find the Country Code for the iTunes store you want to search [here](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2).

Usage
-----

1. Find JFLACTool.jar in the JFLACTool folder and double click on it.
    * Hover over elements with your mouse cursor to get more info about what they're for.
2. Load FLAC files.
    * Click the Load button at the bottom or drag and drop files/folders into the table.
    * JFLACTool won't modify your original FLAC files.
3. Review and correct tags.
    * If a word is spelled incorrectly or a tag isn't formatted the way you want it, then you can edit it before moving to the next step.
4. Select album art.
    * Search the iTunes Store for album art using the artist and album tags or load local album art.
5. Process FLAC files.
    * Generate spectrograms.
    * Archive FLAC.
    * Convert FLAC to MP3.

##### iTunes Album Art Tip

* If you can't seem to find iTunes album art for your album, make sure the album tag doesn't have anything extra at the end like (Deluxe Edition). If it does, remove that text from the album tag field then switch to the Album Art tab and click the >> button to search for album art again. If the album art you're looking for shows up you can go back to the Tags tab and change the text in the album tag field back to what it was before if you'd like and continue from there.

Libraries
---------

The source code for each library can be found inside the lib/src folder in the code repository.

* [JAudioTagger](https://bitbucket.org/ijabz/jaudiotagger) for reading/writing FLAC Vorbis comments and MP3 ID3 tags.
* [JSON-java](https://github.com/stleary/JSON-java) for parsing JSON from the iTunes Search API.
* [JustFLAC](https://github.com/drogatkin/JustFLAC) for playing FLAC files using Java.

Binaries
--------

Binaries are included in the [latest JFLACTool.zip](https://github.com/flacfan/JFLACTool/releases/latest) but not the code repository.

* [LAME](https://sourceforge.net/projects/lame/) for converting FLAC to MP3.
* [SoX](https://sourceforge.net/projects/sox/) for FLAC file manipulation and spectrogram generation.

License
-------

See [LICENSE.md](LICENSE.md) (MIT) for details.

Screenshots
-----------

![Screenshot-1](/screenshots/Screenshot-1.png?raw=true)
![Screenshot-2](/screenshots/Screenshot-2.png?raw=true)
