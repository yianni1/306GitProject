package testCases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Written by Ray on 4/08/2018.
 */
public class CompareOutput {

    /**
     * Compares 2 text files to see if they are the same.
     * Used to compare the outputs files to make unit tests more effective.
     * @param file1 the filepath of the first file
     * @param file2 the filepath of the second file
     * @return true if they're the same, false if not the same.
     * @throws IOException
     */
    public boolean compareTextFiles(String file1, String file2) throws IOException {
        boolean areEqual = true;

        int lineNum1 = 1;
        int lineNum2 = 1;

        BufferedReader reader1 = new BufferedReader(new FileReader(file1));
        String line1 = reader1.readLine();
        while (line1 != null) {
            BufferedReader reader2 = new BufferedReader(new FileReader(file2));
            String line2 = reader2.readLine();
            lineNum2 = 1;
            while (line2 != null) {
                if(!line1.equals(line2)) {
                    line2 = reader2.readLine();
                    lineNum2++;
                    if(line2 == null) {
                        areEqual = false;
                        //System.out.println("Line " + lineNum1 + " does not appear in the second file.");
                        break;
                    }
                } else {
                    //System.out.println("Line " + lineNum1 + " appears in line " + lineNum2 + " of the second file.");
                    break;
                }
            }
            reader2.close();
            line1 = reader1.readLine();
            lineNum1++;

        }
        reader1.close();

        if(areEqual) {
            //System.out.println("Two files have same content.");
            return true;
        }
        else {
            //System.out.println("Two files do not have same content.");
            return false;
        }
    }
}
