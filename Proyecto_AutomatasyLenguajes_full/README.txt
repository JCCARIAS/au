Proyecto_AutomatasyLenguajes (completo y ejecutable)
====================================================

Este proyecto está diseñado para abrirse en NetBeans y ejecutarse directamente sin necesidad de JFlex ni CUP.
He incluido una implementación manual del lexer y parser para que puedas ejecutar inmediatamente.

Estructura:
- src/
  - analizador/
    - Lexer.java   (lexer hecho a mano)
    - Parser.java  (parser hecho a mano)
  - modelo/
    - Reporte.java
  - Main.java
- entrada.txt     (ejemplo de entrada)
- reporte.txt     (generado al ejecutar)
- lib/            (vacío; debes colocar aquí JFlex.jar y java-cup-11b.jar si deseas usarlos)

Ejecución:
1) Abre NetBeans -> New Project -> Java with Existing Sources, o crea un proyecto y copia la carpeta src.
2) Ejecuta la clase Main. Por defecto lee "entrada.txt" y genera "reporte.txt".
   Puedes pasar otro archivo como argumento: java Main archivo.txt
   Para no generar el archivo reporte.txt, pasa segundo argumento "noreport":
   java Main entrada.txt noreport

Notas:
- Si prefieres regenerar con JFlex/CUP reales, coloca los .flex/.cup en src/analizador y usa las herramientas.
- El lexer/parser manual está optimizado para el formato de ejemplo (etiquetas tipo <funcion>, <parametros>, <codigo>, <if>, <condicion>, <do>),
  y maneja asignaciones simples (ID = NUM;), estructuras if y do con condiciónes combinadas con &&/||.
