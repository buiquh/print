import com.eastbay.performancezone.print.Service.PrintService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bob.gause
 * Date: 5/24/14
 * Time: 11:25 PM
 * SDG
 */
public class PrintServiceRunner {

    public static void main(String[] args) {
        PrintServiceRunner printServiceRunner = new PrintServiceRunner();
        if (args.length != 1) {
            printServiceRunner.usage();
        } else {
            printServiceRunner.runPrintService(args[0]);
        }
    }

    private void runPrintService(String jsonInputFile) {
        try {
            Path path = Paths.get(jsonInputFile);
            List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
            StringBuilder jsonInputBuf = new StringBuilder();
            for (String line : lines) {
                jsonInputBuf.append(line);
                jsonInputBuf.append("\n");
            }
            PrintService printService = new PrintService();
            String jsonOutput = printService.printProducts(jsonInputBuf.toString());
            System.out.println("jsonOutput: " + jsonOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void usage() {
        System.err.println("usage: " + this.getClass().getName() + " <input-json-file>");
    }
}
