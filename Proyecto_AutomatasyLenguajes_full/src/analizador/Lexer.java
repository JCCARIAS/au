package analizador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    public static class Token {
        public final TokenType type;
        public final String lexeme;
        public final int line;
        public Token(TokenType type, String lexeme, int line){
            this.type = type;
            this.lexeme = lexeme;
            this.line = line;
        }
        public String toString(){ return type + (lexeme==null? "": "("+lexeme+")"); }
    }

    public enum TokenType {
        FUNCION_A, FUNCION_C, PARAM_A, PARAM_C, CODIGO_A, CODIGO_C,
        IF_A, IF_C, DO_A, DO_C, COND_A, COND_C,
        ID, NUM, OP_REL, OP_LOG, ASIG, DELIM,
        EOF, ERROR
    }

    private BufferedReader reader;
    private int line = 1;
    private String remaining = "";
    private int pos = 0;

    private static final Pattern TAG_PATTERN = Pattern.compile("^\\s*<(\\/)?([a-zA-Z]+)>");
    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*");
    private static final Pattern NUM_PATTERN = Pattern.compile("^[0-9]+");
    private static final Pattern OP_LOG_PATTERN = Pattern.compile("^(\\&\\&|\\|\\|)");
    private static final Pattern OP_REL_PATTERN = Pattern.compile("^(>=|<=|==|!=|>|<)");
    private static final Pattern ASIG_PATTERN = Pattern.compile("^=");
    private static final Pattern DELIM_PATTERN = Pattern.compile("^[,;]");
    private static final Pattern SKIP_PATTERN = Pattern.compile("^\\s+");

    public Lexer(Reader r){
        this.reader = new BufferedReader(r);
    }

    private void fill() throws IOException {
        String l;
        while ((l = reader.readLine()) != null) {
            remaining += l + "\n";
        }
    }

    public Token nextToken() throws IOException {
        if (remaining.isEmpty()) fill();
        while (pos < remaining.length() && Character.isWhitespace(remaining.charAt(pos))) {
            if (remaining.charAt(pos)=='\n') line++;
            pos++;
        }
        if (pos >= remaining.length()) return new Token(TokenType.EOF, null, line);

        String sub = remaining.substring(pos);

        Matcher mtag = TAG_PATTERN.matcher(sub);
        if (mtag.find()) {
            String closing = mtag.group(1);
            String name = mtag.group(2).toLowerCase();
            pos += mtag.end();
            switch(name) {
                case "funcion": return closing==null ? new Token(TokenType.FUNCION_A,"<funcion>",line) : new Token(TokenType.FUNCION_C,"</funcion>",line);
                case "parametros": return closing==null ? new Token(TokenType.PARAM_A,"<parametros>",line) : new Token(TokenType.PARAM_C,"</parametros>",line);
                case "codigo": return closing==null ? new Token(TokenType.CODIGO_A,"<codigo>",line) : new Token(TokenType.CODIGO_C,"</codigo>",line);
                case "if": return closing==null ? new Token(TokenType.IF_A,"<if>",line) : new Token(TokenType.IF_C,"</if>",line);
                case "do": return closing==null ? new Token(TokenType.DO_A,"<do>",line) : new Token(TokenType.DO_C,"</do>",line);
                case "condicion": return closing==null ? new Token(TokenType.COND_A,"<condicion>",line) : new Token(TokenType.COND_C,"</condicion>",line);
                default: return new Token(TokenType.ERROR, "<"+(closing==null?"":" /")+name+">", line);
            }
        }

        Matcher m;
        m = OP_LOG_PATTERN.matcher(sub);
        if (m.find()) { pos += m.end(); return new Token(TokenType.OP_LOG, m.group(1), line); }
        m = OP_REL_PATTERN.matcher(sub);
        if (m.find()) { pos += m.end(); return new Token(TokenType.OP_REL, m.group(1), line); }
        m = ASIG_PATTERN.matcher(sub);
        if (m.find()) { pos += m.end(); return new Token(TokenType.ASIG, "=", line); }
        m = DELIM_PATTERN.matcher(sub);
        if (m.find()) { pos += m.end(); return new Token(TokenType.DELIM, m.group(0), line); }
        m = NUM_PATTERN.matcher(sub);
        if (m.find()) { pos += m.end(); return new Token(TokenType.NUM, m.group(0), line); }
        m = ID_PATTERN.matcher(sub);
        if (m.find()) { pos += m.end(); return new Token(TokenType.ID, m.group(0), line); }

        String bad = sub.substring(0,1);
        pos += 1;
        return new Token(TokenType.ERROR, bad, line);
    }
}
