import org.apache.commons.lang3.StringUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.stream.Stream;

class Reader {
    private String fileName;
    private Path filePath;
    private float[][] coordinates;
    private int stops;
    private final int NUMBER_OF_COLUMNS = 2;
    private final int HEADER_AND_DEPOT_LINES = 2;

    Reader(String fileName) throws IOException {
        this.fileName = fileName;
        this.filePath = Paths.get("src", "main", "resources", fileName).toAbsolutePath();
        readFile();
    }

    private int countLines() throws IOException {
        int count;
        try (Stream<String> lines = Files.lines(this.filePath, Charset.defaultCharset())) {
            count = (int) lines.count();
        }
        if (count % 2 != 0) {
            throw new IOException("Number of lines in '" + fileName + "' should be even. " +
                    "1 for the header line + 1 for the depot coordinates + " +
                    "Even number of pickup + drop-off coordinates");
        }
        return count;
    }

    int getStops() throws IOException {
        if (stops == 0) {
            stops = countLines() - HEADER_AND_DEPOT_LINES;
        }
        return stops;
    }

    private void readFile() throws IOException {
        try (
                FileReader fileReader = new FileReader(this.filePath.toString());
                LineNumberReader lineNumberReader = new LineNumberReader(fileReader)
        ) {
            StringTokenizer tokens;
            int dim = getStops() + 1;
            coordinates = new float[dim][NUMBER_OF_COLUMNS];
            for(int i = 0; i < dim; i++) {
                String tokenString = lineNumberReader.readLine();
                if (StringUtils.isAlphaSpace(tokenString)) {
                    tokenString = lineNumberReader.readLine();
                }
                tokens = new StringTokenizer(tokenString);
                if (tokens.countTokens() != NUMBER_OF_COLUMNS) {
                    throw new IOException("Line " + lineNumberReader.getLineNumber() +
                            " should contain exactly two columns: the location's coordinate pair");
                }
                while (tokens.hasMoreTokens()) {
                    coordinates[i][0] = Float.parseFloat(tokens.nextToken());
                    coordinates[i][1] = Float.parseFloat(tokens.nextToken());
                }
            }
        }
    }

    float[][] getCoordinates() {
        return coordinates;
    }
}
