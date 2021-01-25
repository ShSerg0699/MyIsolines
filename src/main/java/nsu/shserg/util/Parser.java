package nsu.shserg.util;

import java.awt.*;
import java.io.File;
import java.util.Scanner;

public class Parser {

    public static Settings parseFile(File file) throws Exception {
        try (Scanner scanner = new Scanner(file)) {

            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            int a = lineScanner.nextInt();
            int b = lineScanner.nextInt();
            int c = lineScanner.nextInt();
            int d = lineScanner.nextInt();

            line = scanner.nextLine();
            lineScanner = new Scanner(line);
            int n = lineScanner.nextInt();
            int m = lineScanner.nextInt();

            line = scanner.nextLine();
            lineScanner = new Scanner(line);
            int k = lineScanner.nextInt();

            int[] colors = new int[k + 1];
            for (int i = 0; i < k + 1; i++) {
                line = scanner.nextLine();
                colors[i] = readColor(line);
            }

            line = scanner.nextLine();
            int isolineColor = readColor(line);

            return new Settings(a, b, c, d, n, m, k, colors, isolineColor);

        } catch (Exception e) {
            throw e;
        }
    }

    private static int readColor(String line) {
        Scanner lineScanner = new Scanner(line);
        int r = lineScanner.nextInt();
        int g = lineScanner.nextInt();
        int b = lineScanner.nextInt();
        int rgb = (r << 16 | g << 8 | b);
        return rgb;
    }
}
