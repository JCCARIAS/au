package modelo;

import java.io.FileWriter;
import java.io.IOException;

public class Reporte {
    public int funciones = 0;
    public int parametrosValidos = 0;
    public int parametrosInvalidos = 0;
    public int asignacionesValidas = 0;
    public int asignacionesInvalidas = 0;
    public int ifValidos = 0;
    public int ifInvalidos = 0;
    public int doValidos = 0;
    public int doInvalidos = 0;
    public int condicionesValidas = 0;
    public int condicionesInvalidas = 0;
    public int erroresLexicos = 0;
    public int erroresSintacticos = 0;

    public void mostrar() {
        System.out.println("Funciones: " + funciones);
        System.out.println("Parámetros válidos: " + parametrosValidos);
        System.out.println("Parámetros inválidos: " + parametrosInvalidos);
        System.out.println("Asignaciones válidas: " + asignacionesValidas);
        System.out.println("Asignaciones inválidas: " + asignacionesInvalidas);
        System.out.println("If válidos: " + ifValidos);
        System.out.println("If inválidos: " + ifInvalidos);
        System.out.println("Do válidos: " + doValidos);
        System.out.println("Do inválidos: " + doInvalidos);
        System.out.println("Condiciones válidas: " + condicionesValidas);
        System.out.println("Condiciones inválidas: " + condicionesInvalidas);
        System.out.println("Errores léxicos: " + erroresLexicos);
        System.out.println("Errores sintácticos: " + erroresSintacticos);
        System.out.println("--------------------------------");
    }

    public void escribirArchivo(String path) throws IOException {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write("--- REPORTE DE VALIDACIÓN ---\n");
            fw.write("Funciones: " + funciones + "\n");
            fw.write("Parámetros válidos: " + parametrosValidos + "\n");
            fw.write("Parámetros inválidos: " + parametrosInvalidos + "\n");
            fw.write("Asignaciones válidas: " + asignacionesValidas + "\n");
            fw.write("Asignaciones inválidas: " + asignacionesInvalidas + "\n");
            fw.write("If válidos: " + ifValidos + "\n");
            fw.write("If inválidos: " + ifInvalidos + "\n");
            fw.write("Do válidos: " + doValidos + "\n");
            fw.write("Do inválidos: " + doInvalidos + "\n");
            fw.write("Condiciones válidas: " + condicionesValidas + "\n");
            fw.write("Condiciones inválidas: " + condicionesInvalidas + "\n");
            fw.write("Errores léxicos: " + erroresLexicos + "\n");
            fw.write("Errores sintácticos: " + erroresSintacticos + "\n");
            fw.write("--------------------------------\n");
        }
    }
}
