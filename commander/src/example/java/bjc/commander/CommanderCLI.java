package bjc.commander;

import java.io.*;
import java.util.*;

/** Runner for the general command-line interface for Commander.
 * 
 * @author Ben Culkin */
public class CommanderCLI {

    /* :CLIArgsParsing */
    /** Run the command line interface
     *
     * @param args
     *             Ignored CLI args. */
    public static void main(String[] args) {
        /* Create/configure I/O sources. */
        Commander reader = new Commander();
    
        reader.ioReaders.put("stdio", new InputStreamReader(System.in));
    
        Scanner input = new Scanner(System.in);
        reader.run(input, "console", true);
        input.close();
    }

}
