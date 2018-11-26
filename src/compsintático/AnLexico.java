/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compsintático;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Gome
 */
public class AnLexico {

    //Percorre o código que será testado, identificando os tokens
    private static List<Token> automato(String conteudoArquivo, HashMap<String, List<String>> tabelaSimbolos) {
        List<Token> tokens = new ArrayList<Token>();

        int stt = 38;
        String bf = "";
        int numeroLinha = 1;

        //Verifica cada caractere do código fonte
        for (int i = 0; i < conteudoArquivo.length(); i++) {
            switch (stt) {
                case 38: {
                    if (conteudoArquivo.charAt(i) == '=') {
                        stt = 6;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '!') {
                        stt = 9;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '>') {
                        stt = 12;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '<') {
                        stt = 15;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '&') {
                        stt = 18;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '|') {
                        stt = 19;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '^') {
                        stt = 20;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '+') {
                        bf += "+";
                        stt = 21;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '-') {
                        bf += "-";
                        stt = 27;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '*') {
                        stt = 30;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '/') {
                        stt = 31;
                        continue;
                    }

                    if (conteudoArquivo.charAt(i) == '\"') {
                        stt = 4;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == ';') {
                        tokens.add(new Token("ponto e virgula", ";", ";", numeroLinha));
                        bf = "";
                        stt = 38;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '(') {
                        tokens.add(new Token("ABRE PARÊNTESES", "(", "(", numeroLinha));
                        bf = "";
                        stt = 38;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == ')') {
                        tokens.add(new Token("FECHA PARÊNTESES", ")", ")", numeroLinha));
                        bf = "";
                        stt = 38;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '{') {
                        tokens.add(new Token("ABRE CHAVES", "{", "{", numeroLinha));
                        bf = "";
                        stt = 38;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '}') {
                        tokens.add(new Token("FECHA CHAVES", "}", "}", numeroLinha));
                        bf = "";
                        stt = 38;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == '\'') {
                        stt = 40;
                        bf = "";
                        continue;
                    }
                    if (Character.isDigit(conteudoArquivo.charAt(i))) {
                        bf += conteudoArquivo.charAt(i);
                        stt = 24;
                        continue;
                    }
                    if (Character.isLetter(conteudoArquivo.charAt(i))) {
                        bf += conteudoArquivo.charAt(i);
                        stt = 2;
                        continue;
                    }
                    if (conteudoArquivo.charAt(i) == ' ' || conteudoArquivo.charAt(i) == '\t' || conteudoArquivo.charAt(i) == '\n') {
                        
                        if (conteudoArquivo.charAt(i) == '\n') {
                            numeroLinha++;
                        }
                        
                        stt = 36;
                        continue;
                    }

                    //Erro: caractere invalido
                    JOptionPane.showMessageDialog(null, "Erro. Caractere não permitido, " + conteudoArquivo.charAt(i) + " linha " + numeroLinhaErro(conteudoArquivo, conteudoArquivo.charAt(i)) + ".");
                    System.exit(1);
                }
                break;
                case 6: {
                    if (conteudoArquivo.charAt(i) == '=') {
                        stt = 8;
                        continue;
                    } else {
                        stt = 7;
                        i--;
                    }
                }
                break;
                case 8: {
                    tokens.add(new Token("IGUALDADE", "==", "operador_igualdade", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 12: {
                    if (conteudoArquivo.charAt(i) == '=') {
                        stt = 14;
                        continue;
                    } else {
                        stt = 13;
                        i--;
                    }
                }
                break;
                case 14: {
                    tokens.add(new Token("MAIOR IGUAL", ">=", "operador_relacional", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 15: {
                    if (conteudoArquivo.charAt(i) == '=') {
                        stt = 17;
                        continue;
                    } else {
                        stt = 16;
                        i--;
                    }
                }
                break;
                case 17: {
                    tokens.add(new Token("MENOR IGUAL", "<=", "operador_relacional", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 9: {
                    if (conteudoArquivo.charAt(i) == '=') {
                        stt = 11;
                        continue;
                    } else {
                        stt = 10;
                        i--;
                    }
                }
                break;
                case 11: {
                    tokens.add(new Token("DIFERENÇA", "!=", "operador_igualdade", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 18: {
                    tokens.add(new Token("AND", "&", "operador_booleano", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 19: {
                    tokens.add(new Token("OR", "|", "operador_booleano", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 20: {
                    tokens.add(new Token("XOR", "^", "operador_booleano", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 21: {
                    if (Character.isDigit(conteudoArquivo.charAt(i))) {
                        bf += conteudoArquivo.charAt(i);
                        stt = 24;
                        continue;
                    } else {
                        stt = 22;
                        i--;
                    }
                }
                break;
                case 27: {

                    if (Character.isDigit(conteudoArquivo.charAt(i))) {
                        bf += conteudoArquivo.charAt(i);
                        stt = 24;
                        continue;
                    } else {
                        stt = 28;
                        i--;
                    }
                }

                break;
                case 30: {
                    tokens.add(new Token("MULTIPLICAÇÃO", "*", "operador_aritmetico", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 31: {
                    if (conteudoArquivo.charAt(i) == '/') {
                        stt = 32;
                        continue;
                    } else {
                        stt = 34;
                        continue;
                    }
                }

                case 7: {
                    tokens.add(new Token("ATRIBUIÇÃO", "=", "=", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 10: {
                    tokens.add(new Token("NEGAÇÃO", "!", "operador_booleano", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 13: {
                    tokens.add(new Token("MAIOR", ">", "operador_relacional", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 16: {
                    tokens.add(new Token("MENOR", "<", "operador_relacional", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 22: {
                    tokens.add(new Token("ADIÇÃO", "+", "operador_aritmetico", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 28: {
                    tokens.add(new Token("SUBTRAÇÃO", "-", "operador_aritmetico", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 36: {
                    if (conteudoArquivo.charAt(i) == ' ' || conteudoArquivo.charAt(i) == '\t'|| conteudoArquivo.charAt(i) == '\n') {
                        stt = 36;
                        if (conteudoArquivo.charAt(i) == '\n') {
                            numeroLinha++;
                        }
                        
                        continue;
                    } else {
                        stt = 38;
                        i--;
                        continue;
                    }
                }
                case 3: {
                    if (Character.isLetter(conteudoArquivo.charAt(i)) || Character.isDigit(conteudoArquivo.charAt(i)) || conteudoArquivo.charAt(i) == '_') {
                        bf += conteudoArquivo.charAt(i);
                        stt = 3;
                        continue;
                    } else {
                        tokens.add(new Token("ID", new String(bf), "id", numeroLinha));
                        bf = "";
                        stt = 38;
                        i--;
                    }
                }
                break;
                case 34: {
                    tokens.add(new Token("DIVISÃO", "/", "operador_aritmetico", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 32: {
                    if (conteudoArquivo.charAt(i) == '\n') {
                        
                        if (conteudoArquivo.charAt(i) == '\n') {
                            numeroLinha++;
                        }
                        
                        bf = "";
                        stt = 33;
                        continue;
                    } else {
                        stt = 32;
                        continue;
                    }
                }
                case 33: {
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 24: {
                    if (Character.isDigit(conteudoArquivo.charAt(i))) {
                        bf += conteudoArquivo.charAt(i);
                        stt = 24;
                        continue;
                    } else {
                        if (conteudoArquivo.charAt(i) == '.') {
                            bf += conteudoArquivo.charAt(i);
                            stt = 25;
                            continue;
                        } else {
                            tokens.add(new Token("NÚMERO", new String(bf), "valor_inteiro", numeroLinha));
                            bf = "";
                            stt = 38;
                            i--;
                        }
                    }
                }
                break;
                case 25: {
                    if (Character.isDigit(conteudoArquivo.charAt(i))) {
                        bf += conteudoArquivo.charAt(i);
                        stt = 26;
                        continue;
                    }
                }
                break;
                case 26: {
                    if (Character.isDigit(conteudoArquivo.charAt(i))) {
                        bf += conteudoArquivo.charAt(i);
                        stt = 26;
                        continue;
                    } else {
                        tokens.add(new Token("NÚMERO", new String(bf), "valor_float", numeroLinha));
                        bf = "";
                        stt = 38;
                        i--;
                    }
                }
                break;
                case 2: {
                    if (Character.isLetter(conteudoArquivo.charAt(i))) {
                        bf += conteudoArquivo.charAt(i);
                        stt = 2;
                        continue;
                    } else {
                        if (Character.isDigit(conteudoArquivo.charAt(i)) || conteudoArquivo.charAt(i) == '_') {
                            bf += conteudoArquivo.charAt(i);
                            stt = 3;
                            continue;
                        } else {
                            if (tabelaSimbolos.get("Palavras Reservadas").contains(bf.toUpperCase())) {
                                tokens.add(new Token(bf.toUpperCase(), new String(bf), new String(bf), numeroLinha));
                            } else if (tabelaSimbolos.get("Tipo").contains(bf.toUpperCase())) {
                                tokens.add(new Token(bf.toUpperCase(), new String(bf), new String(bf), numeroLinha));
                            } else if (tabelaSimbolos.get("Valores Booleanos").contains(bf.toUpperCase())) {
                                tokens.add(new Token(bf.toUpperCase(), new String(bf), "valor_booleano", numeroLinha));
                            } else {
                                tokens.add(new Token("id", new String(bf), "id", numeroLinha));
                            }

                            bf = "";
                            stt = 38;
                            i--;
                        }
                    }
                }
                break;
                case 4: {
                    if (conteudoArquivo.charAt(i) == '\"') {
                        stt = 5;
                        continue;
                    } else {
                        bf += conteudoArquivo.charAt(i);
                        stt = 4;
                        continue;
                    }
                }
                case 5: {
                    tokens.add(new Token("STRING", new String(bf), "string", numeroLinha));
                    bf = "";
                    stt = 38;
                    i--;
                }
                break;
                case 40: {
                    tokens.add(new Token("CARACTERE", Character.toString(conteudoArquivo.charAt(i)), "valor_caractere", numeroLinha));
                    bf = "";
                    stt = 41;
                }
                break;
                case 41: {
                    if (conteudoArquivo.charAt(i) == '\'') {
                        bf = "";
                        stt = 38;
                    }
                }
                break;

            }

        }
        return tokens;
    }

    //Cria a tabela de símbolos
    private static HashMap<String, List<String>> tabeladeSimbolos() {

        HashMap<String, List<String>> tabelaSimbolos = new HashMap<>();
        tabelaSimbolos.put("Palavras Reservadas", Arrays.asList("BREAK", "CHOICE", "ELSE", "FOR", "IF", "NONE",
                "OPTION", "PRINT", "READ", "VAR", "WHILE", "INI"));
        tabelaSimbolos.put("Tipo", Arrays.asList("BOOLEAN", "CHAR", "INT", "FLOAT"));
        tabelaSimbolos.put("Valores Booleanos", Arrays.asList("TRUE", "FALSE"));
        tabelaSimbolos.put("Operadores Lógicos", Arrays.asList("NOT", "AND", "OR", "XOR"));
        tabelaSimbolos.put("Operadores Aritméticos",
                Arrays.asList("ADIÇÃO", "SUBTRAÇÃO", "MULTIPLICAÇÃO", "DIVISÃO"));
        tabelaSimbolos.put("Operadores Relacionais",
                Arrays.asList("IGUALDADE", "DIFERENÇA", "MAIOR", "MENOR", "MAIOR IGUAL", "MENOR IGUAL"));
        tabelaSimbolos.put("Outros Símbolos", Arrays.asList("ATRIBUIÇÃO", "ABRE PARÊNTESES", "FECHA PARÊNTESES",
                "ABRE CHAVES", "FECHA CHAVES", "PONTO E VÍRGULA"));
        return tabelaSimbolos;
    }

    // Abre o arquivo cf, cria a tabela de símbolos e retorna os tokens identificados
    public static List<Token> execute(String nomeArquivo) {
        String conteudoArquivo = leArquivo(nomeArquivo);

        HashMap<String, List<String>> tabelaSimbolos = tabeladeSimbolos();

        return automato(conteudoArquivo, tabelaSimbolos);
    }

    // Le o conteúdo do arquivo cf selecionado
    private static String leArquivo(String nomeArquivo) {
        String conteudoArquivo = "";

        try {
            try (BufferedReader leitor = new BufferedReader(new FileReader(nomeArquivo))) {
                while (leitor.ready()) {
                    conteudoArquivo += leitor.readLine() + '\n';
                }
            }
        } catch (IOException e) {
        }

        return conteudoArquivo;
    }

    // Verifica a linha em que o erro ocorreu (caractere inválido)
    private static int numeroLinhaErro(String conteudoArquivo, Character caractereErro) {
        int contadorLinhas = 1;
        for (int i = 0; i < conteudoArquivo.length(); i++) {
            if (conteudoArquivo.charAt(i) == '\n') {
                contadorLinhas++;
            } else if (conteudoArquivo.charAt(i) == caractereErro) {
                return contadorLinhas;
            }
        }

        return 0;
    }
}
