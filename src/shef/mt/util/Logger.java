/**
 *
 */
package shef.mt.util;

import java.util.*;
import java.io.*;

/**
 * @author Catalina Hallett
 *
 */
public class Logger {

    private static BufferedWriter bw;

    public Logger(String path) {
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            bw.write(dateFormat.format(date) + "\r\n");
        } catch (Exception e) {
            System.out.println("Could not initialise logger. See error tracking information.");
            e.printStackTrace();
        }
    }

    public static void log(String msg) {
        try {
            bw.write(msg + "\r\n");
        } catch (Exception e) {
        }
    }

    public static void close() {
        try {
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
