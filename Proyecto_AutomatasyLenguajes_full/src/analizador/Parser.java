package analizador;

import modelo.Reporte;
import java.io.IOException;

/**
 * Handwritten parser that follows the grammar implied by the .cup earlier.
 * It consumes tokens from Lexer and builds a Reporte instance.
 */
public class Parser {
    private Lexer lexer;
    private Lexer.Token lookahead;
    private Reporte reporte;

    public Parser(Lexer lexer){
        this.lexer = lexer;
        this.reporte = new Reporte();
    }

    private void next() throws IOException {
        lookahead = lexer.nextToken();
        //System.out.println("TK -> " + lookahead);
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
            // on success, print report
            System.out.println(\"--- REPORTE DE VALIDACIÓN ---\");
            reporte.mostrar();
        } catch (ParseException ex) {
            System.err.println(\"Error sintáctico: \" + ex.getMessage());
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
    }

    private void parametros() throws IOException, ParseException {
        // parameters may be a sequence of IDs or NUM separated by commas, or empty
        if (lookahead.type == Lexer.TokenType.PARAM_C) return; // empty
        int count = 0;
        while (lookahead.type == Lexer.TokenType.ID || lookahead.type == Lexer.TokenType.NUM) {
            count++;
            next();
            if (lookahead.type == Lexer.TokenType.DELIM) { next(); continue; }
            else break;
        }
        reporte.parametrosValidos = count;
    }

    private void codigo() throws IOException, ParseException {
        // code can be empty or list of sentences until CODIGO_C
        int assignCount = 0;
        int ifCount = 0;
        int doCount = 0;
        int condCount = 0;

        while (lookahead.type != Lexer.TokenType.CODIGO_C && lookahead.type != Lexer.TokenType.EOF) {
            if (lookahead.type == Lexer.TokenType.ID) {
                // assignment: ID ASIG NUM DELIM
                next();
                if (lookahead.type == Lexer.TokenType.ASIG) {
                    next();
                    if (lookahead.type == Lexer.TokenType.NUM) {
                        next();
                        if (lookahead.type == Lexer.TokenType.DELIM) {
                            next();
                            assignCount++;
                            continue;
                        } else {
                            throw new ParseException(\"Falta delimitador en asignación en línea \" + lookahead.line);
                        }
                    } else {
                        throw new ParseException(\"Se esperaba número en asignación en línea \" + lookahead.line);
                    }
                } else {
                    throw new ParseException(\"Se esperaba '=' en asignación en línea \" + lookahead.line);
                }
            } else if (lookahead.type == Lexer.TokenType.IF_A) {
                ifCount++;
                next(); // consume IF_A
                expect(Lexer.TokenType.COND_A);
                condCount += condicion();
                expect(Lexer.TokenType.COND_C);
                expect(Lexer.TokenType.CODIGO_A);
                codigo(); // nested code
                expect(Lexer.TokenType.CODIGO_C);
                expect(Lexer.TokenType.IF_C);
            } else if (lookahead.type == Lexer.TokenType.DO_A) {
                doCount++;
                next();
                expect(Lexer.TokenType.CODIGO_A);
                codigo();
                expect(Lexer.TokenType.CODIGO_C);
                expect(Lexer.TokenType.COND_A);
                condCount += condicion();
                expect(Lexer.TokenType.COND_C);
                expect(Lexer.TokenType.DO_C);
            } else {
                // unexpected token: try to recover by consuming one token
                // increment syntax error and continue
                reporte.erroresSintacticos++;
                System.err.println(\"Token inesperado \" + lookahead + \", intentando recuperar...\");
                next();
            }
        }

        reporte.asignacionesValidas = assignCount;
        reporte.ifValidos = ifCount;
        reporte.doValidos = doCount;
        reporte.condicionesValidas = condCount;
    }

    // returns number of simple conditions parsed (supports OP_LOG chaining)
    private int condicion() throws IOException, ParseException {
        int count = 0;
        // format: ID OP_REL NUM (OP_LOG condicion)?
        if (lookahead.type == Lexer.TokenType.ID) {
            next();
            if (lookahead.type == Lexer.TokenType.OP_REL) {
                next();
                if (lookahead.type == Lexer.TokenType.NUM || lookahead.type==Lexer.TokenType.ID) {
                    next();
                    count++;
                    if (lookahead.type == Lexer.TokenType.OP_LOG) {
                        next();
                        count += condicion();
                    }
                    return count;
                } else {
                    throw new ParseException(\"Se esperaba NUM/ID en condición en línea \" + lookahead.line);
                }
            } else {
                throw new ParseException(\"Se esperaba operador relacional en condición en línea \" + lookahead.line);
            }
        } else {
            throw new ParseException(\"Se esperaba ID al inicio de condición en línea \" + lookahead.line);
        }
    }

    // Simple ParseException
    public static class ParseException extends Exception {
        public ParseException(String msg){ super(msg); }
    }
}
