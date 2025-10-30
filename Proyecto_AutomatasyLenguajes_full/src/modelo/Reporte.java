package modelo;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Reporte simple que muestra estadísticas del análisis y además puede escribir reporte.txt
 */
public class Reporte {
    public int funciones = 1;
    public int parametrosValidos = 0;
    public int asignacionesValidas = 0;
    public int ifValidos = 0;
    public int doValidos = 0;
    public int condicionesValidas = 0;
    public int erroresLexicos = 0;
    public int erroresSintacticos = 0;

    public void mostrar() {
        System.out.println("Funciones: " + funciones);
        System.out.println("Parámetros válidos: " + parametrosValidos);
        System.out.println("Asignaciones válidas: " + asignacionesValidas);
        System.out.println("If válidos: " + ifValidos);
        System.out.println("Do válidos: " + doValidos);
        System.out.println("Condiciones válidas: " + condicionesValidas);
        System.out.println("Errores léxicos: " + erroresLexicos);
        System.out.println("Errores sintácticos: " + erroresSintacticos);
    }

    public void escribirArchivo(String path) throws IOException {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(\"--- REPORTE DE VALIDACIÓN ---\\n\");
            fw.write(\"Funciones: \" + funciones + \"\\n\");
            fw.write(\"Parámetros válidos: \" + parametrosValidos + \"\\n\");
            fw.write(\"Asignaciones válidas: \" + asignacionesValidas + \"\\n\");
            fw.write(\"If válidos: \" + ifValidos + \"\\n\");
            fw.write(\"Do válidos: \" + doValidos + \"\\n\");
            fw.write(\"Condiciones válidas: \" + condicionesValidas + \"\\n\");
            fw.write(\"Errores léxicos: \" + erroresLexicos + \"\\n\");
            fw.write(\"Errores sintácticos: \" + erroresSintacticos + \"\\n\");
        }
    }
}
