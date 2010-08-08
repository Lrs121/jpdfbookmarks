/*
 * JPdfBookmarks.java
 *
 * Copyright (c) 2010 Flaviano Petrocchi <flavianopetrocchi at gmail.com>.
 * All rights reserved.
 *
 * This file is part of JPdfBookmarks.
 *
 * JPdfBookmarks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPdfBookmarks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JPdfBookmarks.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.flavianopetrocchi.jpdfbookmarks.start;

import it.flavianopetrocchi.jpdfbookmarks.prefs.Prefs;
import it.flavianopetrocchi.bookmark.Dumper;
import it.flavianopetrocchi.bookmark.Applier;
import it.flavianopetrocchi.jpdfbookmarks.res.Res;
import it.flavianopetrocchi.bookmark.IBookmarksConverter;
import it.flavianopetrocchi.bookmark.Bookmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * This is the main class of the application. It parses the command line and
 * chooses the appropriate mode to start. The default mode is GUI mode.
 */
public class JPdfBookmarks {

    public static final boolean DEBUG = false;


    private enum Mode {

        DUMP,
        APPLY,
        HELP,
        //GUI,
        VERSION,
        SHOW_ON_OPEN,
    }
    // <editor-fold defaultstate="expanded" desc="Member variables">
    public static final String VERSION = "2.3.0";
    public static final String APP_NAME = "JPdfBookmarks";
    public static final String DOWNLOAD_URL =
            "http://flavianopetrocchi.blogspot.com/2008/07/jpsdbookmarks-download-page.html";
    public static final String BLOG_URL =
            "http://flavianopetrocchi.blogspot.com";
    public static final String ITEXT_URL = "http://www.lowagie.com/iText/";
    public static final String LAST_VERSION_URL =
            "http://jpdfbookmarks.altervista.org/version/lastVersion";
    public static final String LAST_VERSION_PROPERTIES_URL =
            "http://jpdfbookmarks.altervista.org/version/jpdfbookmarks.properties";
    public static final String MANUAL_URL = "http://sourceforge.net/apps/mediawiki/jpdfbookmarks/";
    private Mode mode = Mode.HELP;
    private final Options options;
    private final PrintWriter out;
    private final PrintWriter err;
    private String inputFilePath = null;
    private String outputFilePath = null;
    private String bookmarksFilePath = null;
    private String pageSeparator = "/";
    private String attributesSeparator = ",";
    private String indentationString = "\t";
    //private String firstTargetString = null;
    private boolean silentMode = false;
    private String charset;
    private String showOnOpenArg = null;// </editor-fold>

    //<editor-fold defaultstate="expanded" desc="public methods">
//    public static void main(final String[] args) {
//        JPdfBookmarks app = new JPdfBookmarks(args);
        //app.start(args);
//    }

    public JPdfBookmarks(final String[] commandLineArgs) {
        out = new PrintWriter(System.out, true);
        err = new PrintWriter(System.err, true);
        options = createOptions();
        charset = Charset.defaultCharset().displayName();
        start(commandLineArgs);
    }


    static public File getPath() {
        File f = null;
        try {
            f = new File(JPdfBookmarks.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException ex) {
        }
        return f;
    }

    private IBookmarksConverter getBookmarksConverter() {
        IBookmarksConverter pdf;
        ServiceLoader<IBookmarksConverter> s = ServiceLoader.load(IBookmarksConverter.class);
        Iterator<IBookmarksConverter> i = s.iterator();
        if (i.hasNext()) {
            return i.next();
        }
        return null;
    }

    private void fatalOpenFileError(String filePath) {
        err.println(Res.getString("ERROR_OPENING_FILE" + " " + filePath));
        System.exit(1);
    }

    private void fatalSaveFileError(String filePath) {
        err.println(Res.getString("ERROR_SAVING_FILE" + " (" + filePath + ")"));
        System.exit(1);
    }

    private IBookmarksConverter fatalGetConverterAndOpenPdf(String inputFilePath) {
        IBookmarksConverter pdf = getBookmarksConverter();
        if (pdf != null) {
            try {
                pdf.open(inputFilePath);
            } catch (IOException ex) {
                fatalOpenFileError(inputFilePath);
            }
        } else {
            err.println(Res.getString("ERROR_BOOKMARKS_CONVERTER_NOT_FOUND"));
            System.exit(1);
        }
        return pdf;
    }

    private void apply() {
        IBookmarksConverter pdf = fatalGetConverterAndOpenPdf(inputFilePath);

        Applier applier = new Applier(pdf, indentationString,
                pageSeparator, attributesSeparator);
        try {
            applier.loadBookmarksFile(bookmarksFilePath, charset);
        } catch (Exception ex) {
            fatalOpenFileError(bookmarksFilePath);
        }

        if (outputFilePath == null || outputFilePath.equals(inputFilePath)) {
            if (getYesOrNo(Res.getString(
                    "ERR_INFILE_EQUAL_OUTFILE"))) {
                outputFilePath = inputFilePath;
            }
        } else {
            File f = new File(outputFilePath);
            if (!f.exists()
                    || getYesOrNo(Res.getString("WARNING_OVERWRITE_CMD"))) {
                try {
                    applier.save(outputFilePath);
                    pdf.close();
                } catch (IOException ex) {
                    fatalSaveFileError(outputFilePath);
                }
            }
        }

    }

    private void dump() {
        IBookmarksConverter pdf = fatalGetConverterAndOpenPdf(inputFilePath);
        Dumper dumper = new Dumper(pdf, indentationString,
                pageSeparator, attributesSeparator);

        if (outputFilePath == null) {
            dumper.printBookmarksIterative(new OutputStreamWriter(System.out));
        } else {
            File f = new File(outputFilePath);
            if (!f.exists()
                    || getYesOrNo(Res.getString("WARNING_OVERWRITE_CMD"))) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(outputFilePath);
                    OutputStreamWriter outStream = new OutputStreamWriter(fos, charset);
                    dumper.printBookmarksIterative(outStream);
                    outStream.close();
                    pdf.close();
                } catch (FileNotFoundException ex) {
                    fatalOpenFileError(outputFilePath);
                } catch (UnsupportedEncodingException ex) {
                    //already checked in command line parsing
                } catch (IOException ex) {
                }
            }
        }
    }

    private void showOnOpen() {
        IBookmarksConverter pdf = fatalGetConverterAndOpenPdf(inputFilePath);

        if (showOnOpenArg.equalsIgnoreCase("CHECK") || showOnOpenArg.equalsIgnoreCase("c")) {
            if (pdf.showBookmarksOnOpen()) {
                out.println("YES");
            } else {
                out.println("NO");
            }
        } else {
            if (showOnOpenArg.equalsIgnoreCase("yes") || showOnOpenArg.equalsIgnoreCase("y")) {
                pdf.setShowBookmarksOnOpen(true);
            } else if (showOnOpenArg.equalsIgnoreCase("no") || showOnOpenArg.equalsIgnoreCase("n")) {
                pdf.setShowBookmarksOnOpen(false);
            }
            if (outputFilePath == null || outputFilePath.equals(inputFilePath)) {
                if (getYesOrNo(Res.getString(
                        "ERR_INFILE_EQUAL_OUTFILE"))) {
                    outputFilePath = inputFilePath;
                }
            } else {
                File f = new File(outputFilePath);
                if (!f.exists()
                        || getYesOrNo(Res.getString("WARNING_OVERWRITE_CMD"))) {
                    try {
                        pdf.save(outputFilePath);
                        pdf.close();
                    } catch (IOException ex) {
                        fatalSaveFileError(outputFilePath);
                    }
                }
            }
        }
    }

    /**
     * Start the application in the requested mode.
     *
     * @param args Arguments to select mode and pass files to process. Can be
     *             null.
     */
    public void start(final String[] args) {
        if (args != null && args.length > 0) {
            setModeByCommandLine(args);
        }

        switch (mode) {
            case VERSION:
                out.println(VERSION);
                break;
            case DUMP:
                dump();
                break;
            case SHOW_ON_OPEN:
                showOnOpen();
                break;
            case APPLY:
                apply();
                break;
            case HELP:
            default:
                printHelpMessage();

        }
    }


    public static void printErrorForDebug(Exception e) {
        if (DEBUG) {
            System.err.println("***** printErrorForDebug Start *****");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.err.println("***** printErrorForDebug End *****");
        }
    }

    public void printHelpMessage() {
        HelpFormatter help = new HelpFormatter();
        String header = Res.getString("APP_DESCR");
        String syntax = "jpdfbookmarks <input.pdf> "
                + "[--dump | --apply <bookmarks.txt> | --show-on-open <YES | NO | CHECK> "
                + "| --help | --version] [--out <output.pdf>]";
        int width = 80;
        int leftPad = 1, descPad = 2;
        String footer = Res.getString("BOOKMARKS_DESCR");
        help.printHelp(out, width, syntax, header, options,
                leftPad, descPad, footer);
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="private methods">
    /**
     * Sets the mode by the command line arguments and initializes files to
     * process if passed as arguments.
     *
     * @param args Arguments to process
     */
    private void setModeByCommandLine(final String[] args) {
        final Prefs userPrefs = new Prefs();
        final CommandLineParser parser = new PosixParser();
        try {
            final CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption('h')) {
                mode = Mode.HELP;
            } else if (cmd.hasOption('v')) {
                mode = Mode.VERSION;
            } else if (cmd.hasOption('w')) {
                mode = Mode.SHOW_ON_OPEN;
                showOnOpenArg = cmd.getOptionValue('w');
                if (cmd.hasOption('o')) {
                    outputFilePath = cmd.getOptionValue('o');
                } else {
                    outputFilePath = null;
                }
            } else if (cmd.hasOption('a')) {
                mode = Mode.APPLY;
                bookmarksFilePath = cmd.getOptionValue('a');
                if (bookmarksFilePath == null) {
                    throw new ParseException(
                            Res.getString("ERR_NO_BOOKMARKS_FILE"));
                }
            } else if (cmd.hasOption('d')) {
                mode = Mode.DUMP;
            }


            if (cmd.hasOption('o')) {
                outputFilePath = cmd.getOptionValue('o');
            } else {
                outputFilePath = null;
            }

            String[] leftOverArgs = cmd.getArgs();
            if (leftOverArgs.length > 0) {
                inputFilePath = leftOverArgs[0];
            } else if (mode == Mode.DUMP || mode == Mode.APPLY) {
                throw new ParseException(
                        Res.getString("ERR_NO_INPUT_FILE"));
            }

            if (cmd.hasOption("p")) {
                pageSeparator = cmd.getOptionValue("p");
            } else {
                pageSeparator = userPrefs.getPageSeparator();
            }
            if (cmd.hasOption("i")) {
                indentationString = cmd.getOptionValue("i");
            } else {
                indentationString = userPrefs.getIndentationString();
            }
            if (cmd.hasOption("t")) {
                attributesSeparator = cmd.getOptionValue("t");
            } else {
                attributesSeparator = userPrefs.getAttributesSeparator();
            }
            if (cmd.hasOption("f")) {
                silentMode = true;
            }
            if (cmd.hasOption("e")) {
                charset = cmd.getOptionValue("e");
                if (!Charset.isSupported(charset)) {
                    throw new ParseException(
                            Res.getString("ERR_CHARSET_NOT_SUPPORTED"));
                }
            }

            if (pageSeparator.equals(indentationString)
                    || pageSeparator.equals(attributesSeparator)
                    || indentationString.equals(attributesSeparator)) {
                throw new ParseException(
                        Res.getString("ERR_OPTIONS_CONTRAST"));
            }


        } catch (ParseException ex) {
            err.println(ex.getLocalizedMessage());
            System.exit(1);
        }

    }

    /**
     * Get the user answer yes or no, on the command line. It recognize as a yes
     * y or yes and as a no n or no. Not case sensitive.
     *
     * @param question Question to the user.
     * @return Yes will return true and No will return false.
     */
    private boolean getYesOrNo(String question) {
        if (silentMode) {
            return true;
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));
        PrintWriter cout = new PrintWriter(System.out, true);
        boolean answer = false;
        boolean validInput = false;
        while (!validInput) {
            cout.println(question);
            try {
                String line = in.readLine();
                if (line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes")) {
                    answer = true;
                    validInput = true;
                } else if (line.equalsIgnoreCase("n") || line.equalsIgnoreCase("no")) {
                    answer = false;
                    validInput = true;
                }
            } catch (IOException ex) {
            }
        }
        return answer;
    }

    @SuppressWarnings("static-access")
    private Options createOptions() {
        Options appOptions = new Options();

        appOptions.addOption("f", "force", false,
                Res.getString("FORCE_DESCR"));
        appOptions.addOption("v", "version", false,
                Res.getString("VERSION_DESCR"));
        appOptions.addOption("h", "help", false,
                Res.getString("HELP_DESCR"));
        appOptions.addOption(OptionBuilder.withLongOpt("dump").withDescription(Res.getString("DUMP_DESCR")).create('d'));
        appOptions.addOption(OptionBuilder.withLongOpt("apply").hasArg(true).withArgName("bookmarks.txt").withDescription(Res.getString("APPLY_DESCR")).create('a'));
        appOptions.addOption(OptionBuilder.withLongOpt("out").hasArg(true).withArgName("output.pdf").withDescription(Res.getString("OUT_DESCR")).create('o'));
        appOptions.addOption(OptionBuilder.withLongOpt("encoding").hasArg(true).withArgName("UTF-8").withDescription(Res.getString("ENCODING_DESCR")).create('e'));
//        appOptions.addOption(OptionBuilder.withLongOpt("show-on-open").hasArg(true)
//                .withArgName("YES | NO | CHECK")
//                .withDescription(Res.getString("SHOW_ON_OPEN_DESCR")).create('w'));

        appOptions.addOption("b", "bookmark", true,
                Res.getString("BOOKMARK_ARG_DESCR"));
        appOptions.addOption("p", "page-sep", true,
                Res.getString("PAGE_SEP_DESCR"));
        appOptions.addOption("t", "attributes-sep", true,
                Res.getString("ATTRIBUTES_SEP_DESCR"));
        appOptions.addOption("i", "indentation", true,
                Res.getString("INDENTATION_STRING_DESCR"));
        appOptions.addOption("w", "show-on-open", true,
                Res.getString("SHOW_ON_OPEN_DESCR"));

        return appOptions;
    }
    //</editor-fold>
}
