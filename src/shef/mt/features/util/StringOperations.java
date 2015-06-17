/**
 *
 */
package shef.mt.features.util;

import java.util.ArrayList;

/**
 * This is a final class containing some unrelated frequently used functions
 *
 * @author Catalina Hallett
 *
 */
public final class StringOperations {

    public static boolean isNumber(String word) {
        char[] chars = word.toCharArray();
        for (char c : chars) {
            if (!Character.isDigit(c) && (c != Character.DECIMAL_DIGIT_NUMBER)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNoAlpha(String word) {
        char[] chars = word.toCharArray();
        boolean found = false;
        int i = 0;
        while (i < chars.length && !found) {
            if (!Character.isLetter(chars[i])) {
                found = true;
            }
            i++;
        }
        return found;
    }

    public static boolean isAlpha(String word) {
        char[] chars = word.toCharArray();
        boolean found = false;
        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return found;
    }

    public static ArrayList<String> lcs(String[] a, String[] b) {
        int[][] lengths = new int[a.length + 1][b.length + 1];

        // row 0 and column 0 are initialized to 0 already
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (a[i].equals(b[j])) {
                    lengths[i + 1][j + 1] = lengths[i][j] + 1;
                } else {
                    lengths[i + 1][j + 1]
                            = Math.max(lengths[i + 1][j], lengths[i][j + 1]);
                }
            }
        }

        // read the substring out from the matrix
        ArrayList<String> result = new ArrayList<>();
        for (int x = a.length, y = b.length;
                x != 0 && y != 0;) {
            if (lengths[x][y] == lengths[x - 1][y]) {
                x--;
            } else if (lengths[x][y] == lengths[x][y - 1]) {
                y--;
            } else {
                assert a[x - 1].equals(b[y - 1]);
                result.add(a[x-1]);
                x--;
                y--;
            }
        }

        return result;
    }

    public static int editDistance(String s1, String s2) {
        //Levensteihn edit distance between two strings
        String[] words1 = s1.split(" ");
        String[] words2 = s2.split(" ");
        int size1 = words1.length;
        int size2 = words2.length;

        int cost1;
        int cost2;
        int cost3;

        int[][] dpMatrix = new int[size1][size2];
        for (int i = 0; i < size1; i++) {
            dpMatrix[i][0] = i;
        }
        for (int j = 0; j < size2; j++) {
            dpMatrix[0][j] = j;
        }

        for (int i = 1; i < size1; i++) {
            for (int j = 1; j < size2; j++) {
                if (words1[i].equals(words2[j])) {
                    dpMatrix[i][j] = dpMatrix[i - 1][j - 1];
                } else {
                    cost1 = dpMatrix[i - 1][j] + 1;  // a deletion
                    cost2 = dpMatrix[i][j - 1] + 1;  // an insertion
                    cost3 = dpMatrix[i - 1][j - 1] + 1;// a substitution
                    dpMatrix[i][j] = Math.min(cost1, Math.min(cost2, cost3));
                }
            }
        }

        return dpMatrix[size1 - 1][size2 - 1];

    }
}
