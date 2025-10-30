import analizador.Lexer;
import analizador.Parser;
import modelo.Reporte;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String input = "entrada.txt";
        boolean writeReportFile = true;
        if (args.length > 0) input = args[0];
        if (args.length > 1 && args[1].equalsIgnoreCase("noreport")) writeReportFile = false;

        try (FileReader fr = new FileReader(input)) {
            Lexer lexer = new Lexer(fr);
            Parser parser = new Parser(lexer);
            parser.parse();
            Reporte rep = parser.reporte;
            if (writeReportFile) {
                rep.escribirArchivo("reporte.txt");
                System.out.println("Reporte escrito en reporte.txt");
            }
        } catch (IOException ex) {
            System.err.println("Error IO: " + ex.getMessage());
        }
    }
}
