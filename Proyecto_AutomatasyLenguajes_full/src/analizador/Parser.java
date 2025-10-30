package analizador;

import modelo.Reporte;
import java.io.IOException;

public class Parser {
    private Lexer lexer;
    private Lexer.Token lookahead;
    public Reporte reporte;

    public Parser(Lexer lexer){
        this.lexer = lexer;
        this.reporte = new Reporte();
    }

    private void next() throws IOException {
        lookahead = lexer.nextToken();
    }

    private boolean accept(Lexer.TokenType t) throws IOException {
        if (lookahead.type == t) { next(); return true; }
        return false;
    }

    private void expect(Lexer.TokenType t) throws IOException, ParseException {
        if (lookahead.type == t) { next(); return; }
        throw new ParseException("Se esperaba " + t + " pero vino " + lookahead.type + " en línea " + lookahead.line);
    }

    public void parse() throws IOException {
        next();
        try {
            funcion();
            System.out.println("--- REPORTE DE VALIDACIÓN ---");
            reporte.mostrar();
        } catch (ParseException ex) {
            System.err.println("Error sintáctico: " + ex.getMessage());
            reporte.erroresSintacticos++;
            reporte.mostrar();
        }
    }

    private void funcion() throws IOException, ParseException {
        expect(Lexer.TokenType.FUNCION_A);
        expect(Lexer.TokenType.PARAM_A);
        parametros();
        expect(Lexer.TokenType.PARAM_C);
        expect(Lexer.TokenType.CODIGO_A);
        codigo();
        expect(Lexer.TokenType.CODIGO_C);
        expect(Lexer.TokenType.FUNCION_C);
        reporte.funciones++;
    }

    private void parametros() throws IOException, ParseException {
        if (lookahead.type == Lexer.TokenType.PARAM_C) return;
        int valid = 0;
        int invalid = 0;
        while (lookahead.type == Lexer.TokenType.ID || lookahead.type == Lexer.TokenType.NUM) {
            if (lookahead.type == Lexer.TokenType.ID || lookahead.type == Lexer.TokenType.NUM) valid++;
            else invalid++;
            next();
            if (lookahead.type == Lexer.TokenType.DELIM) next();
            else break;
        }
        reporte.parametrosValidos = valid;
        reporte.parametrosInvalidos = invalid;
    }

    private void codigo() throws IOException, ParseException {
        int assignValid = 0;
        int assignInvalid = 0;
        int ifValid = 0;
        int ifInvalid = 0;
        int doValid = 0;
        int doInvalid = 0;
        int condValid = 0;
        int condInvalid = 0;

        while (lookahead.type != Lexer.TokenType.CODIGO_C && lookahead.type != Lexer.TokenType.EOF) {
            try {
                if (lookahead.type == Lexer.TokenType.ID) {
                    if (asignacion()) assignValid++;
                    else assignInvalid++;
                } else if (lookahead.type == Lexer.TokenType.IF_A) {
                    next();
                    expect(Lexer.TokenType.COND_A);
                    int c = condicion();
                    condValid += c;
                    expect(Lexer.TokenType.COND_C);
                    expect(Lexer.TokenType.CODIGO_A);
                    codigo();
                    expect(Lexer.TokenType.CODIGO_C);
                    expect(Lexer.TokenType.IF_C);
                    ifValid++;
                } else if (lookahead.type == Lexer.TokenType.DO_A) {
                    next();
                    expect(Lexer.TokenType.CODIGO_A);
                    codigo();
                    expect(Lexer.TokenType.CODIGO_C);
                    expect(Lexer.TokenType.COND_A);
                    condValid += condicion();
                    expect(Lexer.TokenType.COND_C);
                    expect(Lexer.TokenType.DO_C);
                    doValid++;
                } else {
                    reporte.erroresSintacticos++;
                    next();
                }
            } catch (ParseException ex) {
                reporte.erroresSintacticos++;
                next();
            }
        }

        reporte.asignacionesValidas += assignValid;
        reporte.asignacionesInvalidas += assignInvalid;
        reporte.ifValidos += ifValid;
        reporte.ifInvalidos += ifInvalid;
        reporte.doValidos += doValid;
        reporte.doInvalidos += doInvalid;
        reporte.condicionesValidas += condValid;
        reporte.condicionesInvalidas += condInvalid;
    }

    private boolean asignacion() throws IOException, ParseException {
        // formato simplificado: id = id/num (+/- id/num)* ;
        next(); // id
        if (!accept(Lexer.TokenType.ASIG)) return false;
        boolean valid = false;
        if (lookahead.type == Lexer.TokenType.ID || lookahead.type == Lexer.TokenType.NUM) {
            valid = true;
            next();
            while (lookahead.type == Lexer.TokenType.ID || lookahead.type == Lexer.TokenType.NUM || lookahead.lexeme.equals("+") || lookahead.lexeme.equals("-")) {
                next();
            }
        }
        if (!accept(Lexer.TokenType.DELIM)) valid = false;
        return valid;
    }

    private int condicion() throws IOException, ParseException {
        int count = 0;
        if (lookahead.type == Lexer.TokenType.ID || lookahead.type == Lexer.TokenType.NUM) {
            next();
            if (lookahead.type == Lexer.TokenType.OP_REL) {
                next();
                if (lookahead.type == Lexer.TokenType.ID || lookahead.type == Lexer.TokenType.NUM) {
                    next();
                    count++;
                    if (lookahead.type == Lexer.TokenType.OP_LOG) {
                        next();
                        count += condicion();
                    }
                    return count;
                }
            }
        }
        return count;
    }

    public static class ParseException extends Exception {
        public ParseException(String msg){ super(msg); }
    }
}
