/**
 *
 */
package shef.mt.syntree;

import java.io.*;

/**
 * @author Catalina Hallett
 *
 * AlignCheck takes in a folder of Arabic syntactic trees and a folder
 * containing the syntactic trees of their English translations, and checks each
 * pair of files for number of lines If the number of lines is different in the
 * two files, it prints out the file and the respective number of lines
 */
public class AlignCheck {

    String folderAr;
    String folderEn;

    public AlignCheck(String folderAr, String folderEn) {
        this.folderAr = folderAr;
        this.folderEn = folderEn;
    }

    public void run() {
        try {
            File folderArFiles = new File(folderAr);
            File folderEnFiles = new File(folderEn);
            File[] arFiles = folderArFiles.listFiles();
            File enFile;
            File arFile;
            for (int i = 0; i < arFiles.length; i++) {
                arFile = arFiles[i];
                String arFileName = arFiles[i].getName().substring(0, arFiles[i].getName().lastIndexOf(".ar"));
                enFile = new File(folderEnFiles + File.separator + arFileName + ".eng.full");
                if (!enFile.exists()) {
                    System.out.println(enFile.getPath() + " doesn't exist");
                    continue;
                }
                LineNumberReader lnrAr = new LineNumberReader(new InputStreamReader(new FileInputStream(arFile)));
                LineNumberReader lnrEn = new LineNumberReader(new InputStreamReader(new FileInputStream(enFile)));
                lnrAr.skip(Long.MAX_VALUE);
                long arNr = lnrAr.getLineNumber();
                lnrEn.skip(Long.MAX_VALUE);
                long enNr = lnrEn.getLineNumber();
                if (arNr != enNr) {
                    System.out.println(arFile.getName() + "\t" + arNr);
                    System.out.println(enFile.getName() + "\t" + enNr);
                    System.out.println();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AlignCheck ac = new AlignCheck(args[0], args[1]);
        ac.run();
    }
}
