/**
 *
 */
package shef.mt.util;

/**
 * @author Catalina Hallett
 *
 */
import java.util.*;
import java.io.*;

public class StreamGobbler extends Thread {

    InputStream is;
    String type;
    OutputStream os;
    boolean echo;

    public StreamGobbler(InputStream is, String type) {
        this(is, type, null, false);
    }

    public StreamGobbler(InputStream is, String type, boolean echo) {
        this(is, type, null, echo);
    }

    public StreamGobbler(InputStream is, String type, OutputStream redirect) {
        this(is, type, redirect, false);
    }

    public StreamGobbler(InputStream is, String type, OutputStream redirect, boolean echo) {
        this.is = is;
        this.type = type;
        this.os = redirect;
        this.echo = echo;

    }

    public void run() {
        try {
            PrintWriter pw = null;
            if (os != null) {
                pw = new PrintWriter(os);
            }

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (pw != null) {
                    pw.println(line);
                }
                if (echo) {
                    System.out.println(type + ">" + line);
                }
            }
            if (pw != null) {
                pw.flush();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
