package nl.jonaskoperdraat.ingcsvtognucash;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {

        LOG.info("Application started");
        Application app = new Application();
        app.process(args);
    }

    /**
     * Processes the input file and writes the processed lines to an output file.
     *
     * @param args the program files.
     * @throws IOException
     */
    private void process(String[] args) throws IOException {

        if (args.length != 1) {
            LOG.error("Expecting a single path to a file as argument.");
            System.exit(1);
        }

        // Determine filenames of input and output files.
        String infile = args[0];
        if (!infile.contains(".")) {
            LOG.error("Input filename doesn't contain an extension.");
            System.exit(1);
        }
        String outfile = infile.substring(0, infile.lastIndexOf(".")) + "_converted"
                + infile.substring(infile.lastIndexOf("."));

        LOG.debug("Parsing file {} and writing new file to {}", infile, outfile);

        // Replace superfluous double quotes (") whith a single quote (') as a double quote is used as a delimiter.
        Path path = Paths.get(infile);
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(path), charset);
        String altContent = content.replaceAll("([^$,\n])\"+([^^,\r])", "$1'$2");


        Reader inputFileReader = new BufferedReader(new StringReader(altContent));

        // Create reader
        CSVReader reader = new CSVReader(inputFileReader, ',');

        // Create writer
        CSVWriter writer = new CSVWriter(new FileWriter(outfile), ',', '"', "\n");

        // Process each line.
        String [] line;
        while ((line = reader.readNext()) != null) {
            
            // Add each processed line to the output file.
            String[] newLine = processLine(line);
            writer.writeNext(newLine);

        }

        reader.close();
        writer.close();

    }

    /**
     * Process a single line.
     * @param line The line to convert
     * @return a String[] containing the new line.
     */
    private String[] processLine(String[] line) {
        String[] newLine = new String[8];

        // Check if this is the first 'header' line.
        if ("Datum".equals(line[0])) {
            newLine[0] = "Datum";
            newLine[1] = "Nr";
            newLine[2] = "Omschrijving";
            newLine[3] = "Toelichting";
            newLine[4] = "Rekening";
            newLine[5] = "Storting";
            newLine[6] = "Opname";
            newLine[7] = "Saldo";
        } else {
            newLine[0] = line[0];
            newLine[1] = "";
            newLine[2] = line[1];
            newLine[3] = line[8];
            newLine[4] = line[2];
            // Storting
            newLine[5] = "Bij".equals(line[5]) ? line[6] : "0";
            // Opname
            newLine[6] = "Af".equals(line[5]) ? line[6] : "0";
            newLine[7] = "";
        }

        return newLine;
    }

}
