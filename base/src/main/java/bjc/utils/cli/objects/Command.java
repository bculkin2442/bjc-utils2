package bjc.utils.cli.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A single-line command read from the user.
 *
 * @author Ben Culkin */
public class Command {
    /** Command status values.
     *
     * @author Ben Culkin */
    public static enum CommandStatus {
        /** The command succeeded. */
        SUCCESS,
        /** The command failed non-fatally. */
        FAIL,
        /** The command failed fatally. */
        ERROR,
        /** The command was the last one. */
        FINISH,
    }

    /** The line number of this command. */
    public final int lno;

    /** The full text of this command. */
    public final String full;
    /** The text of this command without its name. */
    public String remn;
    /** The name of this command. */
    public final String name;

    /** The name of the I/O source this command was read from. */
    public final String src;

    /** Create a new command.
     *
     * @param ln
     *              The string to get the command from.
     *
     * @param lno
     *              The number of the line the command came from.
     *
     * @param ioSrc
     *              The name of where the I/O came from. */
    public Command(String ln, int lno, String ioSrc) {
        int idx = ln.indexOf(' ');
        if (idx == -1) idx = ln.length();

        /* Grab command parts. */
        full = ln;
        name = ln.substring(0, idx).trim();
        remn = ln.substring(idx).trim();

        this.lno = lno;

        src = ioSrc;
    }

    /** Removes up until the first occurrence of a particular string for the
     * remaining command, and returns the removed string.
     *
     * By default, both the substring and the remaining text are trimmed
     * (leading/trailing spaces removed).
     *
     * @param delm
     *             The delimiter to stop substringing at.
     *
     * @return The substring, or null if there is no occurrence of the delimiter. */
    public String trimTo(String delm) {
        return trimTo(delm, true);
    }

    /** Removes up until the first occurrence of a particular string for the
     * remaining command, and returns the removed string.
     *
     * @param delm
     *               The delimiter to stop substringing at.
     * @param doTrim
     *               Whether or not to trim the substring and remaining command
     *               (Remove leading/trailing spaces).
     *
     * @return The substring, or null if there is no occurrence of the delimiter. */
    public String trimTo(String delm, boolean doTrim) {
        int idx = remn.indexOf(delm);
        if (idx == -1) return null;

        String tmp = remn.substring(0, idx);
        remn = remn.substring(idx);

        if (doTrim) {
            tmp = tmp.trim();
            remn = remn.trim();
        }

        return tmp;
    }

    /** Removes up until the first occurrence of a particular regex for the
     * remaining
     * command, and returns the removed string.
     *
     * By default, both the substring and the remaining text are trimmed
     * (leading/trailing spaces removed).
     *
     * @param rDelm
     *              The regex to stop substringing at.
     *
     * @return The string, up to the matched pattern. */
    public String trimToRX(String rDelm) {
        return trimToRX(Pattern.compile(rDelm), true);
    }

    /** Removes up until the first occurrence of a particular regex for the
     * remaining
     * command, and returns the removed string.
     *
     * By default, both the substring and the remaining text are trimmed
     * (leading/trailing spaces removed).
     *
     * @param delm
     *             The regex to stop substringing at.
     *
     * @return The string, up to the matched pattern. */
    public String trimToRX(Pattern delm) {
        return trimToRX(delm, true);
    }

    /** Removes up until the first occurrence of a particular regex for the
     * remaining
     * command, and returns the removed string.
     *
     * @param rDelm
     *               The regex to stop substringing at.
     * @param doTrim
     *               Whether or not to trim the substring and remaining command
     *               (Remove leading/trailing spaces).
     *
     * @return The string, up to the matched pattern. */
    public String trimToRX(String rDelm, boolean doTrim) {
        return trimToRX(Pattern.compile(rDelm), doTrim);
    }

    /** Removes up until the first occurrence of a particular regex for the
     * remaining
     * command, and returns the removed string.
     *
     * @param delm
     *               The regex to stop substringing at.
     * @param doTrim
     *               Whether or not to trim the substring and remaining command
     *               (Remove leading/trailing spaces).
     *
     * @return The string, up to the matched pattern. */
    public String trimToRX(Pattern delm, boolean doTrim) {
        Matcher mat = delm.matcher(remn);
        if (!mat.find()) return null;

        String tmp = remn.substring(0, mat.start());
        remn = remn.substring(mat.end());

        if (doTrim) {
            tmp = tmp.trim();
            remn = remn.trim();
        }

        return tmp;
    }

    /** Removes up until the first occurrence of a particular string for the
     * remaining command, and returns the removed string.
     *
     * By default, both the substring and the remaining text are trimmed
     * (leading/trailing spaces removed).
     *
     * @param delm
     *             The delimiter to stop substringing at.
     *
     * @return The substring, or null if there is no occurrence of the delimiter. */
    public String trimTo(char delm) {
        return trimTo(delm, true);
    }

    /** Removes up until the first occurrence of a particular string for the
     * remaining command, and returns the removed string.
     *
     * @param delm
     *               The delimiter to stop substringing at.
     * @param doTrim
     *               Whether or not to trim the substring and remaining command
     *               (Remove leading/trailing spaces).
     *
     * @return The substring, or null if there is no occurrence of the delimiter. */
    public String trimTo(char delm, boolean doTrim) {
        int idx = remn.indexOf(delm);
        if (idx == -1) return null;

        String tmp = remn.substring(0, idx);
        remn = remn.substring(idx);

        if (doTrim) {
            tmp = tmp.trim();
            remn = remn.trim();
        }

        return tmp;
    }

    /** Check if this command has text after its name.
     *
     * @return Whether or not this command has text after its name. */
    public boolean hasRemaining() {
        return !remn.equals("");
    }

    /** Parse a command from a string.
     *
     * The main thing this does is ignore blank lines, as well as comments marked by
     * #'s either at the start of the line or part of the way through the line.
     *
     * @param lne
     *                The string to get the command from.
     *
     * @param lno
     *                The line number of the command.
     *
     * @param srcName
     *                The name of where the I/O came from.
     * 
     * @return The parsed command */
    public static Command fromString(String lne, int lno, String srcName) {
        String ln = lne;

        /* Ignore blank lines and comments. */
        if (ln.equals(""))      return null;
        if (ln.startsWith("#")) return null;

        /* Trim off comments part-way through the line. */
        int idxHash = ln.indexOf('#');
        if (idxHash != -1) ln = ln.substring(0, idxHash).trim();

        return new Command(ln, lno, srcName);
    }

    /** Give an informational message about something in relation to this command.
     *
     * @param info
     *              The informational message.
     *
     * @param parms
     *              The parameters for the informational message.
     * 
     * @return The information message. */
    public String info(String info, Object... parms) {
        String msg = String.format(info, parms);

        return String.format("INFO (%s:%d): %s", src, lno, msg);
    }

    /** Warn about something in relation to this command.
     *
     * @param warning
     *                The warning message.
     *
     * @param parms
     *                The parameters for the warning message.
     *
     * @return The formatted warning. */
    public String warn(String warning, Object... parms) {
        String msg = String.format(warning, parms);

        return String.format("WARNING (%s:%d): %s", src, lno, msg);
    }

    /** Give an error about something in relation to this command.
     *
     * @param err
     *              The error message.
     *
     * @param parms
     *              The parameters for the error message.
     *
     * @return The formatted error */
    public String error(String err, Object... parms) {
        String msg = String.format(err, parms);

        return String.format("ERROR (%s:%d): %s", src, lno, msg);
    }
}