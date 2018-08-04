package testCases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by olive on 4/08/2018.
 */
public class Test {

    /**
     * Compares 2 text files
     * @param file1
     * @param file2
     * @return true if they're the same, false if not the same.
     * @throws IOException
     */
    public boolean compareTextFiles(String file1, String file2) throws IOException {
        boolean areEqual = true;

        int lineNum1 = 1;
        int lineNum2 = 2;

        BufferedReader reader1 = new BufferedReader(new FileReader(file1));
        String line1 = reader1.readLine();
        while (line1 != null) {
            //System.out.println("TextA: Reading line " + lineNum1);
            BufferedReader reader2 = new BufferedReader(new FileReader(file2));
            String line2 = reader2.readLine();
            lineNum2 = 1;
            while (line2 != null) {
                //   System.out.println("TextB: Reading line " + lineNum2);
                if(!line1.equals(line2)) {
                    line2 = reader2.readLine();
                    lineNum2++;
                    if(line2 == null) {
                        areEqual = false;
                        System.out.println("Line " + lineNum1 + " does not appear in the second file.");
                        break;
                    }
                } else {
                    System.out.println("Line " + lineNum1 + " appears in line " + lineNum2 + " of TextB.");
                    break;
                }
            }
            reader2.close();
            line1 = reader1.readLine();
            lineNum1++;

        }
        reader1.close();

        if(areEqual) {
            System.out.println("Two files have same content.");
            return true;
        }
        else {
            return false;
        }
    }
}
