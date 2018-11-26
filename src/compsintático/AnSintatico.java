/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compsintático;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author Gome
 */
public class AnSintatico {

    private HashMap<Integer, HashMap<String, String>> tabelaAcao;
    private HashMap<Integer, HashMap<String, Integer>> tabelaDesvio;
    private List<ProducaoGramatica> gramatica;
    private List<String> variable_declaration;
    private List<String> variable_type;
    private int[] label_counter;

    public AnSintatico() {

        tabelaAcao = new HashMap<Integer, HashMap<String, String>>();
        tabelaDesvio = new HashMap<Integer, HashMap<String, Integer>>();
        gramatica = new ArrayList<ProducaoGramatica>();
        variable_declaration = new ArrayList<>();
        variable_type = new ArrayList<>();
        label_counter = new int[1];
        label_counter[0] = 0;

        setaGramatica();
        setaEstados();
        adicionaAcoes();
        adicionaDesvios();
    }

    //Executa a análise sintática
    public String programaDiretorSintatico(List<Token> tokens) {
        Stack<Token> pilha = new Stack<Token>();
        Stack<Token> pilha_aux = new Stack<Token>();
        Integer s;
        Integer sLinha;

        String FODENDOACAO;
        String A;
        String prod;

        Token a;

        int ip = 0;
        int numeroProducao;
        int beta;

        setaGramatica();

        tokens.add(new Token("$", "$", "$"));
        
        pilha.push(new Token("", "0", ""));
                
        while (true) {

            System.out.println(tokens.get(ip));
            s = Integer.parseInt(pilha.peek().getLexema());
            a = tokens.get(ip);
            FODENDOACAO = tabelaAcao.get(s).get(a.getTipo());
            System.out.println("s = " + s + ", a = " + a.getTipo() + ", x = " + FODENDOACAO);

            if (FODENDOACAO == null) {
                //System.out.println("argghh");
                return tratamentoDeErro(a, s);
            } else if (FODENDOACAO.charAt(0) == 'E') { //Empilhar
                 pilha.push(a);
                pilha.push(new Token("", FODENDOACAO.substring(1, FODENDOACAO.length()), ""));
                ip++;
            } else if (FODENDOACAO.charAt(0) == 'R') { //Reduzir
                numeroProducao = Integer.parseInt(FODENDOACAO.substring(1, FODENDOACAO.length()));

                 for (int i = 0; i < gramatica.size(); i++) {
                    if (gramatica.get(i).getNumero() == numeroProducao) {
                        beta = gramatica.get(i).getLadoDireito().size();
                        A = gramatica.get(i).getLadoEsquerdo();

                        if (!gramatica.get(i).getLadoDireito().get(0).equals("VAZIO")) {
                            //Desempilha 2 * Beta
                            for (int j = 0; j < 2 * beta; j++) {
                                Token p = pilha.pop();
                                if (!p.getTipo().equals("")) {
                                    pilha_aux.push(p);
                                }
                            }
                        }

                        AnSemantico semantic = new AnSemantico(this.variable_declaration, this.variable_type);
                        Token semanticToken;
                        semanticToken = semantic.codeGenerator(Integer.toString(numeroProducao), A, pilha_aux, label_counter);

                        sLinha = Integer.parseInt(pilha.peek().getLexema());
                        
                        //erro
                        if (!semanticToken.getError().equals("")) {
                            System.out.println(semanticToken.getError());
                            return "SUA MAE";
                        }
                        

                        pilha.push(new Token("", A, ""));
                        pilha.push(semanticToken);
                        //Busca o valor no desvio [sLinha, A]
                        System.out.println("SLINHA = " + sLinha + " A = " + A);
                        pilha.push(new Token("",String.valueOf(tabelaDesvio.get(sLinha).get(A)),""));

                        break;
                    }
                }
            } else {
                if (tabelaAcao.get(s).get(a.getTipo()).equals("ACEITA")) {
                    return "Código sem erros sintáticos";
                } else {
                    return tratamentoDeErro(a, s); //Aciona 
                }
            }
        }

    }

    //Verifica a linha em que o ocorreu e monta a mensagem de erro
    private String tratamentoDeErro(Token tokenErro, int linhaTabela) {
        //Verifica quais os caracteres esperados na respectiva linha			
        Iterator it = tabelaAcao.get(linhaTabela).entrySet().iterator();

        String esperado = "Esperado: ";

        //Percorre os elementos esperados da linha
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            esperado += pair.getKey() + ", ";
        }

        return esperado + "na linha " + tokenErro.getLinha();
    }

    //Seta a gramática
    private void setaGramatica() {

        gramatica.add(new ProducaoGramatica(1, "Inicial", (List<String>) Arrays.asList("Var", "Ini")));
        gramatica.add(new ProducaoGramatica(2, "Var", (List<String>) Arrays.asList("var", "{", "Declaracao", "}")));
        gramatica.add(new ProducaoGramatica(3, "Ini", (List<String>) Arrays.asList("ini", "{", "Comando", "}")));
        gramatica.add(new ProducaoGramatica(4, "Declaracao", (List<String>) Arrays.asList("Tipo", "id", "Declaracao2")));
        gramatica.add(new ProducaoGramatica(5, "Declaracao", (List<String>) Arrays.asList("VAZIO")));
        gramatica.add(new ProducaoGramatica(6, "Declaracao2", (List<String>) Arrays.asList("=", "Valor", ";", "Declaracao")));
        gramatica.add(new ProducaoGramatica(7, "Declaracao2", (List<String>) Arrays.asList(";", "Declaracao")));
        gramatica.add(new ProducaoGramatica(8, "Tipo", (List<String>) Arrays.asList("int")));
        gramatica.add(new ProducaoGramatica(9, "Tipo", (List<String>) Arrays.asList("float")));
        gramatica.add(new ProducaoGramatica(10, "Tipo", (List<String>) Arrays.asList("boolean")));
        gramatica.add(new ProducaoGramatica(11, "Tipo", (List<String>) Arrays.asList("char")));
        gramatica.add(new ProducaoGramatica(12, "Comando", (List<String>) Arrays.asList("id", "=", "Atribuicao", ";", "Comando")));
        gramatica.add(new ProducaoGramatica(13, "Comando", (List<String>) Arrays.asList("if", "(", "Expressao_relacional", ")", "{", "Comando", "Break", "}", "Else", "Comando")));
        gramatica.add(new ProducaoGramatica(14, "Comando", (List<String>) Arrays.asList("choice", "(", "id", ")", "{", "Option", "None", "}", "Comando")));
        gramatica.add(new ProducaoGramatica(15, "Comando", (List<String>) Arrays.asList("for", "(", "id", "=", "Atribuicao", ";", "Expressao_relacional", ";", "id", "=", "Expressao_aritmetica", ")", "{", "Comando", "}", "Comando")));
        gramatica.add(new ProducaoGramatica(16, "Comando", (List<String>) Arrays.asList("while", "(", "Expressao_relacional", ")", "{", "Comando", "}", "Comando")));
        gramatica.add(new ProducaoGramatica(17, "Comando", (List<String>) Arrays.asList("print", "(", "Print", ")", ";", "Comando")));
        gramatica.add(new ProducaoGramatica(18, "Comando", (List<String>) Arrays.asList("read", "(", "id", ")", ";", "Comando")));
        gramatica.add(new ProducaoGramatica(19, "Comando", (List<String>) Arrays.asList("VAZIO")));
        gramatica.add(new ProducaoGramatica(20, "Valor", (List<String>) Arrays.asList("valor_booleano")));
        gramatica.add(new ProducaoGramatica(21, "Valor", (List<String>) Arrays.asList("valor_inteiro")));
        gramatica.add(new ProducaoGramatica(22, "Valor", (List<String>) Arrays.asList("valor_float")));
        gramatica.add(new ProducaoGramatica(23, "Valor", (List<String>) Arrays.asList("valor_caractere")));
        gramatica.add(new ProducaoGramatica(24, "Atribuicao", (List<String>) Arrays.asList("valor_booleano", "Aux1")));
        gramatica.add(new ProducaoGramatica(25, "Atribuicao", (List<String>) Arrays.asList("valor_inteiro", "Aux3")));
        gramatica.add(new ProducaoGramatica(26, "Atribuicao", (List<String>) Arrays.asList("valor_float", "Aux3")));
        gramatica.add(new ProducaoGramatica(27, "Atribuicao", (List<String>) Arrays.asList("valor_caractere")));
        gramatica.add(new ProducaoGramatica(28, "Atribuicao", (List<String>) Arrays.asList("id", "Aux5")));
        gramatica.add(new ProducaoGramatica(29, "Atribuicao", (List<String>) Arrays.asList("!", "Aux6")));
        gramatica.add(new ProducaoGramatica(30, "Aux1", (List<String>) Arrays.asList("operador_booleano", "Aux2")));
        gramatica.add(new ProducaoGramatica(31, "Aux1", (List<String>) Arrays.asList("VAZIO")));
        gramatica.add(new ProducaoGramatica(32, "Aux2", (List<String>) Arrays.asList("id")));
        gramatica.add(new ProducaoGramatica(33, "Aux2", (List<String>) Arrays.asList("valor_booleano")));
        gramatica.add(new ProducaoGramatica(34, "Aux3", (List<String>) Arrays.asList("operador_aritmetico", "Aux4")));
        gramatica.add(new ProducaoGramatica(35, "Aux3", (List<String>) Arrays.asList("VAZIO")));
        gramatica.add(new ProducaoGramatica(36, "Aux4", (List<String>) Arrays.asList("id")));
        gramatica.add(new ProducaoGramatica(37, "Aux4", (List<String>) Arrays.asList("valor_inteiro")));
        gramatica.add(new ProducaoGramatica(38, "Aux4", (List<String>) Arrays.asList("valor_float")));
        gramatica.add(new ProducaoGramatica(39, "Aux5", (List<String>) Arrays.asList("operador_aritmetico", "Aux4")));
        gramatica.add(new ProducaoGramatica(40, "Aux5", (List<String>) Arrays.asList("Aux1")));
        gramatica.add(new ProducaoGramatica(41, "Aux6", (List<String>) Arrays.asList("valor_booleano", "Aux1")));
        gramatica.add(new ProducaoGramatica(42, "Aux6", (List<String>) Arrays.asList("id", "Aux1")));
        gramatica.add(new ProducaoGramatica(43, "Expressao_relacional", (List<String>) Arrays.asList("id", "Aux10")));
        gramatica.add(new ProducaoGramatica(44, "Expressao_relacional", (List<String>) Arrays.asList("valor_float", "Aux7")));
        gramatica.add(new ProducaoGramatica(45, "Expressao_relacional", (List<String>) Arrays.asList("valor_inteiro", "Aux7")));
        gramatica.add(new ProducaoGramatica(46, "Expressao_relacional", (List<String>) Arrays.asList("valor_booleano", "Aux8")));
        gramatica.add(new ProducaoGramatica(47, "Expressao_relacional", (List<String>) Arrays.asList("valor_caractere", "Aux9")));
        gramatica.add(new ProducaoGramatica(48, "Aux7", (List<String>) Arrays.asList("operador_relacional", "Aux4")));
        gramatica.add(new ProducaoGramatica(49, "Aux7", (List<String>) Arrays.asList("operador_igualdade", "Aux4")));
        gramatica.add(new ProducaoGramatica(50, "Aux10", (List<String>) Arrays.asList("operador_relacional", "Aux4")));
        gramatica.add(new ProducaoGramatica(51, "Aux10", (List<String>) Arrays.asList("operador_igualdade", "Aux11")));
        gramatica.add(new ProducaoGramatica(52, "Aux11", (List<String>) Arrays.asList("valor_booleano")));
        gramatica.add(new ProducaoGramatica(53, "Aux11", (List<String>) Arrays.asList("Aux4")));
        gramatica.add(new ProducaoGramatica(54, "Aux8", (List<String>) Arrays.asList("operador_igualdade", "Aux2")));
        gramatica.add(new ProducaoGramatica(55, "Aux8", (List<String>) Arrays.asList("VAZIO")));
        gramatica.add(new ProducaoGramatica(56, "Aux9", (List<String>) Arrays.asList("operador_igualdade", "valor_caractere")));
        gramatica.add(new ProducaoGramatica(57, "Print", (List<String>) Arrays.asList("id")));
        gramatica.add(new ProducaoGramatica(58, "Print", (List<String>) Arrays.asList("string")));
        gramatica.add(new ProducaoGramatica(59, "Else", (List<String>) Arrays.asList("else", "{", "Comando", "Break", "}")));
        gramatica.add(new ProducaoGramatica(60, "Else", (List<String>) Arrays.asList("VAZIO")));
        gramatica.add(new ProducaoGramatica(61, "Break", (List<String>) Arrays.asList("break", ";")));
        gramatica.add(new ProducaoGramatica(62, "Break", (List<String>) Arrays.asList("VAZIO")));
        gramatica.add(new ProducaoGramatica(63, "Option", (List<String>) Arrays.asList("option", "(", "Valor", ")", "{", "Comando", "}", "Aux12")));
        gramatica.add(new ProducaoGramatica(64, "Aux12", (List<String>) Arrays.asList("Option")));
        gramatica.add(new ProducaoGramatica(65, "Aux12", (List<String>) Arrays.asList("VAZIO")));
        gramatica.add(new ProducaoGramatica(66, "None", (List<String>) Arrays.asList("none", "{", "Comando", "}")));
        gramatica.add(new ProducaoGramatica(67, "None", (List<String>) Arrays.asList("VAZIO")));
        gramatica.add(new ProducaoGramatica(68, "Expressao_aritmetica", (List<String>) Arrays.asList("id", "operador_aritmetico", "Aux4")));
        gramatica.add(new ProducaoGramatica(69, "Expressao_aritmetica", (List<String>) Arrays.asList("valor_inteiro", "operador_aritmetico", "Aux4")));
        gramatica.add(new ProducaoGramatica(70, "Expressao_aritmetica", (List<String>) Arrays.asList("valor_float", "operador_aritmetico", "Aux4")));
    }

    //Seta as keys da tabela (estado - linhas)
    private void setaEstados() {
        for (int i = 0; i < 342; i++) {
            tabelaAcao.put(i, new HashMap<String, String>());
            tabelaDesvio.put(i, new HashMap<String, Integer>());
        }
    }

    //Adiciona ações
    private void adicionaAcoes() {
        tabelaAcao.get(1).put("$", "ACEITA");
        tabelaAcao.get(0).put("var", "E3");
        tabelaAcao.get(2).put("ini", "E5");
        tabelaAcao.get(3).put("{", "E6");
        tabelaAcao.get(5).put("{", "E7");
        tabelaAcao.get(6).put("int", "E10");
        tabelaAcao.get(6).put("float", new String("E11"));
        tabelaAcao.get(6).put("boolean", new String("E12"));
        tabelaAcao.get(6).put("char", new String("E13"));
        tabelaAcao.get(7).put("id", new String("E15"));
        tabelaAcao.get(7).put("if", new String("E16"));
        tabelaAcao.get(7).put("choice", new String("E17"));
        tabelaAcao.get(7).put("for", new String("E18"));
        tabelaAcao.get(7).put("while", new String("E19"));
        tabelaAcao.get(7).put("print", new String("E20"));
        tabelaAcao.get(7).put("read", new String("E21"));
        tabelaAcao.get(8).put("}", new String("E22"));
        tabelaAcao.get(9).put("id", new String("E23"));
        tabelaAcao.get(14).put("}", new String("E24"));
        tabelaAcao.get(15).put("=", new String("E25"));
        tabelaAcao.get(16).put("(", new String("E26"));
        tabelaAcao.get(17).put("(", new String("E27"));
        tabelaAcao.get(18).put("(", new String("E28"));
        tabelaAcao.get(19).put("(", new String("E29"));
        tabelaAcao.get(20).put("(", new String("E30"));
        tabelaAcao.get(21).put("(", new String("E31"));
        tabelaAcao.get(23).put("=", new String("E33"));
        tabelaAcao.get(23).put(";", new String("E34"));
        tabelaAcao.get(25).put("valor_booleano", new String("E36"));
        tabelaAcao.get(25).put("valor_inteiro", new String("E37"));
        tabelaAcao.get(25).put("valor_float", new String("E38"));
        tabelaAcao.get(25).put("valor_caractere", new String("E39"));
        tabelaAcao.get(25).put("id", new String("E40"));
        tabelaAcao.get(25).put("!", new String("E41"));
        tabelaAcao.get(26).put("id", new String("E43"));
        tabelaAcao.get(26).put("valor_float", new String("E44"));
        tabelaAcao.get(26).put("valor_inteiro", new String("E45"));
        tabelaAcao.get(26).put("valor_booleano", new String("E46"));
        tabelaAcao.get(26).put("valor_caractere", new String("E47"));
        tabelaAcao.get(27).put("id", new String("E48"));
        tabelaAcao.get(28).put("id", new String("E49"));
        tabelaAcao.get(29).put("id", new String("E43"));
        tabelaAcao.get(29).put("valor_float", new String("E44"));
        tabelaAcao.get(29).put("valor_inteiro", new String("E45"));
        tabelaAcao.get(29).put("valor_booleano", new String("E46"));
        tabelaAcao.get(29).put("valor_caractere", new String("E47"));
        tabelaAcao.get(30).put("id", new String("E58"));
        tabelaAcao.get(30).put("string", new String("E59"));
        tabelaAcao.get(31).put("id", new String("E60"));
        tabelaAcao.get(33).put("valor_booleano", new String("E62"));
        tabelaAcao.get(33).put("valor_inteiro", new String("E63"));
        tabelaAcao.get(33).put("valor_float", new String("E64"));
        tabelaAcao.get(33).put("valor_caractere", new String("E65"));
        tabelaAcao.get(34).put("int", new String("E10"));
        tabelaAcao.get(34).put("float", new String("E11"));
        tabelaAcao.get(34).put("boolean", new String("E12"));
        tabelaAcao.get(34).put("char", new String("E13"));
        tabelaAcao.get(35).put(";", new String("E72"));
        tabelaAcao.get(36).put("operador_booleano", new String("E74"));
        tabelaAcao.get(37).put("operador_aritmetico", new String("E76"));
        tabelaAcao.get(40).put("operador_aritmetico", new String("E80"));
        tabelaAcao.get(40).put("operador_booleano", new String("E74"));
        tabelaAcao.get(41).put("valor_booleano", new String("E84"));
        tabelaAcao.get(41).put("id", new String("E85"));
        tabelaAcao.get(42).put(")", new String("E86"));
        tabelaAcao.get(43).put("operador_relacional", new String("E88"));
        tabelaAcao.get(43).put("operador_igualdade", new String("E89"));
        tabelaAcao.get(44).put("operador_relacional", new String("E91"));
        tabelaAcao.get(44).put("operador_igualdade", new String("E92"));
        tabelaAcao.get(45).put("operador_relacional", new String("E94"));
        tabelaAcao.get(45).put("operador_igualdade", new String("E95"));
        tabelaAcao.get(46).put("operador_igualdade", new String("E97"));
        tabelaAcao.get(47).put("operador_igualdade", new String("E99"));
        tabelaAcao.get(48).put(")", new String("E100"));
        tabelaAcao.get(49).put("=", new String("E101"));
        tabelaAcao.get(50).put(")", new String("E102"));
        tabelaAcao.get(57).put(")", new String("E103"));
        tabelaAcao.get(60).put(")", new String("E104"));
        tabelaAcao.get(61).put(";", new String("E105"));
        tabelaAcao.get(67).put("id", new String("E23"));
        tabelaAcao.get(72).put("if", new String("E16"));
        tabelaAcao.get(72).put("choice", new String("E17"));
        tabelaAcao.get(72).put("for", new String("E18"));
        tabelaAcao.get(72).put("while", new String("E19"));
        tabelaAcao.get(72).put("print", new String("E20"));
        tabelaAcao.get(72).put("read", new String("E21"));
        tabelaAcao.get(72).put("id", new String("E15"));
        tabelaAcao.get(74).put("id", new String("E115"));
        tabelaAcao.get(74).put("valor_booleano", new String("E116"));
        tabelaAcao.get(76).put("id", new String("E118"));
        tabelaAcao.get(76).put("valor_inteiro", new String("E119"));
        tabelaAcao.get(76).put("valor_float", "E120");
        tabelaAcao.get(80).put("id", new String("E118"));
        tabelaAcao.get(80).put("valor_inteiro", new String("E119"));
        tabelaAcao.get(80).put("valor_float", new String("E120"));
        tabelaAcao.get(84).put("operador_booleano", new String("E74"));
        tabelaAcao.get(85).put("operador_booleano", new String("E74"));
        tabelaAcao.get(86).put("{", new String("E127"));
        tabelaAcao.get(88).put("id", new String("E118"));
        tabelaAcao.get(88).put("valor_inteiro", new String("E119"));
        tabelaAcao.get(88).put("valor_float", new String("E120"));
        tabelaAcao.get(89).put("id", new String("E118"));
        tabelaAcao.get(89).put("valor_inteiro", new String("E119"));
        tabelaAcao.get(89).put("valor_float", new String("E120"));
        tabelaAcao.get(89).put("valor_booleano", new String("E133"));
        tabelaAcao.get(91).put("id", new String("E118"));
        tabelaAcao.get(91).put("valor_inteiro", new String("E119"));
        tabelaAcao.get(91).put("valor_float", new String("E120"));
        tabelaAcao.get(92).put("id", new String("E118"));
        tabelaAcao.get(92).put("valor_inteiro", new String("E119"));
        tabelaAcao.get(92).put("valor_float", new String("E120"));
        tabelaAcao.get(94).put("id", new String("E118"));
        tabelaAcao.get(94).put("valor_inteiro", new String("E119"));
        tabelaAcao.get(94).put("valor_float", new String("E120"));
        tabelaAcao.get(97).put("id", new String("E115"));
        tabelaAcao.get(97).put("valor_booleano", new String("E116"));
        tabelaAcao.get(99).put("valor_caractere", new String("E153"));
        tabelaAcao.get(100).put("{", new String("E154"));
        tabelaAcao.get(101).put("id", new String("E40"));
        tabelaAcao.get(101).put("!", new String("E41"));
        tabelaAcao.get(101).put("valor_booleano", new String("E36"));
        tabelaAcao.get(101).put("valor_inteiro", new String("E37"));
        tabelaAcao.get(101).put("valor_float", new String("E38"));
        tabelaAcao.get(101).put("valor_caractere", new String("E39"));
        tabelaAcao.get(102).put("{", new String("E162"));
        tabelaAcao.get(103).put(";", new String("E163"));
        tabelaAcao.get(104).put(";", new String("E164"));
        tabelaAcao.get(105).put("int", new String("E10"));
        tabelaAcao.get(105).put("float", new String("E11"));
        tabelaAcao.get(105).put("boolean", new String("E12"));
        tabelaAcao.get(105).put("char", new String("E13"));
        tabelaAcao.get(127).put("id", new String("E15"));
        tabelaAcao.get(127).put("choice", new String("E17"));
        tabelaAcao.get(127).put("for", new String("E18"));
        tabelaAcao.get(127).put("while", new String("E19"));
        tabelaAcao.get(127).put("print", new String("E20"));
        tabelaAcao.get(127).put("read", new String("E21"));
        tabelaAcao.get(127).put("if", new String("E16"));
        tabelaAcao.get(154).put("option", new String("E179"));
        tabelaAcao.get(155).put(";", new String("E180"));
        tabelaAcao.get(162).put("id", new String("E15"));
        tabelaAcao.get(162).put("choice", new String("E17"));
        tabelaAcao.get(162).put("for", new String("E18"));
        tabelaAcao.get(162).put("while", new String("E19"));
        tabelaAcao.get(162).put("print", new String("E20"));
        tabelaAcao.get(162).put("read", new String("E21"));
        tabelaAcao.get(162).put("if", new String("E16"));
        tabelaAcao.get(163).put("id", new String("E15"));
        tabelaAcao.get(163).put("choice", new String("E17"));
        tabelaAcao.get(163).put("for", new String("E18"));
        tabelaAcao.get(163).put("while", new String("E19"));
        tabelaAcao.get(163).put("print", new String("E20"));
        tabelaAcao.get(163).put("read", new String("E21"));
        tabelaAcao.get(163).put("if", new String("E16"));
        tabelaAcao.get(164).put("id", new String("E15"));
        tabelaAcao.get(164).put("choice", new String("E17"));
        tabelaAcao.get(164).put("for", new String("E18"));
        tabelaAcao.get(164).put("while", new String("E19"));
        tabelaAcao.get(164).put("print", new String("E20"));
        tabelaAcao.get(164).put("read", new String("E21"));
        tabelaAcao.get(164).put("if", new String("E16"));
        tabelaAcao.get(171).put("break", new String("E203"));
        tabelaAcao.get(171).put("}", new String("R62"));
        tabelaAcao.get(178).put("none", new String("E205"));
        tabelaAcao.get(179).put("(", new String("E206"));
        tabelaAcao.get(180).put("id", new String("E43"));
        tabelaAcao.get(180).put("valor_float", new String("E44"));
        tabelaAcao.get(180).put("valor_inteiro", new String("E45"));
        tabelaAcao.get(180).put("valor_booleano", new String("E46"));
        tabelaAcao.get(180).put("valor_caractere", new String("E47"));
        tabelaAcao.get(181).put("}", new String("E213"));
        tabelaAcao.get(202).put("}", new String("E214"));
        tabelaAcao.get(203).put(";", new String("E215"));
        tabelaAcao.get(204).put("}", new String("E216"));
        tabelaAcao.get(205).put("{", new String("E217"));
        tabelaAcao.get(206).put("valor_float", new String("E64"));
        tabelaAcao.get(206).put("valor_inteiro", new String("E63"));
        tabelaAcao.get(206).put("valor_caractere", new String("E65"));
        tabelaAcao.get(206).put("valor_booleano", new String("E62"));
        tabelaAcao.get(207).put(";", new String("E223"));
        tabelaAcao.get(213).put("id", new String("E15"));
        tabelaAcao.get(213).put("choice", new String("E17"));
        tabelaAcao.get(213).put("for", new String("E18"));
        tabelaAcao.get(213).put("while", new String("E19"));
        tabelaAcao.get(213).put("print", new String("E20"));
        tabelaAcao.get(213).put("read", new String("E21"));
        tabelaAcao.get(213).put("if", new String("E16"));
        tabelaAcao.get(214).put("else", new String("E232"));
        tabelaAcao.get(215).put("}", new String("R62"));
        tabelaAcao.get(216).put("id", new String("E15"));
        tabelaAcao.get(216).put("choice", new String("E17"));
        tabelaAcao.get(216).put("for", new String("E18"));
        tabelaAcao.get(216).put("while", new String("E19"));
        tabelaAcao.get(216).put("print", new String("E20"));
        tabelaAcao.get(216).put("read", new String("E21"));
        tabelaAcao.get(216).put("if", new String("E16"));
        tabelaAcao.get(217).put("id", new String("E15"));
        tabelaAcao.get(217).put("choice", new String("E17"));
        tabelaAcao.get(217).put("for", new String("E18"));
        tabelaAcao.get(217).put("while", new String("E19"));
        tabelaAcao.get(217).put("print", new String("E20"));
        tabelaAcao.get(217).put("read", new String("E21"));
        tabelaAcao.get(217).put("if", new String("E16"));
        tabelaAcao.get(218).put(")", new String("E241"));
        tabelaAcao.get(223).put("id", new String("E242"));
        tabelaAcao.get(231).put("id", new String("E15"));
        tabelaAcao.get(231).put("choice", new String("E17"));
        tabelaAcao.get(231).put("for", new String("E18"));
        tabelaAcao.get(231).put("while", new String("E19"));
        tabelaAcao.get(231).put("print", new String("E20"));
        tabelaAcao.get(231).put("read", new String("E21"));
        tabelaAcao.get(231).put("if", new String("E16"));
        tabelaAcao.get(232).put("{", new String("E250"));
        tabelaAcao.get(240).put("}", new String("E251"));
        tabelaAcao.get(241).put("{", new String("E252"));
        tabelaAcao.get(242).put("=", new String("E253"));
        tabelaAcao.get(250).put("id", new String("E15"));
        tabelaAcao.get(250).put("choice", new String("E17"));
        tabelaAcao.get(250).put("for", new String("E18"));
        tabelaAcao.get(250).put("while", new String("E19"));
        tabelaAcao.get(250).put("print", new String("E20"));
        tabelaAcao.get(250).put("read", new String("E21"));
        tabelaAcao.get(250).put("if", new String("E16"));
        tabelaAcao.get(252).put("id", new String("E15"));
        tabelaAcao.get(252).put("choice", new String("E17"));
        tabelaAcao.get(252).put("for", new String("E18"));
        tabelaAcao.get(252).put("while", new String("E19"));
        tabelaAcao.get(252).put("print", new String("E20"));
        tabelaAcao.get(252).put("read", new String("E21"));
        tabelaAcao.get(252).put("if", new String("E16"));
        tabelaAcao.get(253).put("id", new String("E270"));
        tabelaAcao.get(253).put("valor_inteiro", new String("E271"));
        tabelaAcao.get(253).put("valor_float", new String("E272"));
        tabelaAcao.get(254).put("break", new String("E274"));
        tabelaAcao.get(261).put("}", new String("E275"));
        tabelaAcao.get(269).put(")", new String("E276"));
        tabelaAcao.get(270).put("operador_aritmetico", new String("E277"));
        tabelaAcao.get(271).put("operador_aritmetico", new String("E278"));
        tabelaAcao.get(272).put("operador_aritmetico", new String("E279"));
        tabelaAcao.get(273).put(";", new String("E280"));
        tabelaAcao.get(273).put("}", new String("E280"));
        tabelaAcao.get(274).put(";", new String("E281"));
        tabelaAcao.get(275).put("option", new String("E284"));
        tabelaAcao.get(276).put("{", new String("E285"));
        tabelaAcao.get(277).put("id", new String("E118"));
        tabelaAcao.get(277).put("valor_inteiro", new String("E119"));
        tabelaAcao.get(277).put("valor_float", new String("E120"));
        tabelaAcao.get(278).put("id", new String("E118"));
        tabelaAcao.get(278).put("valor_inteiro", new String("E119"));
        tabelaAcao.get(278).put("valor_float", new String("E120"));
        tabelaAcao.get(279).put("id", new String("E118"));
        tabelaAcao.get(279).put("valor_inteiro", new String("E119"));
        tabelaAcao.get(279).put("valor_float", new String("E120"));
        tabelaAcao.get(284).put("(", new String("E298"));
        tabelaAcao.get(285).put("id", new String("E15"));
        tabelaAcao.get(285).put("choice", new String("E17"));
        tabelaAcao.get(285).put("for", new String("E18"));
        tabelaAcao.get(285).put("while", new String("E19"));
        tabelaAcao.get(285).put("print", new String("E20"));
        tabelaAcao.get(285).put("read", new String("E21"));
        tabelaAcao.get(285).put("if", new String("E16"));
        tabelaAcao.get(298).put("valor_booleano", new String("E62"));
        tabelaAcao.get(298).put("valor_inteiro", new String("E63"));
        tabelaAcao.get(298).put("valor_caractere", new String("E65"));
        tabelaAcao.get(298).put("valor_float", new String("E64"));
        tabelaAcao.get(299).put("}", new String("E311"));
        tabelaAcao.get(311).put("id", new String("E15"));
        tabelaAcao.get(311).put("choice", new String("E17"));
        tabelaAcao.get(311).put("for", new String("E18"));
        tabelaAcao.get(311).put("while", new String("E19"));
        tabelaAcao.get(311).put("print", new String("E20"));
        tabelaAcao.get(311).put("read", new String("E21"));
        tabelaAcao.get(311).put("if", new String("E16"));

        tabelaAcao.get(4).put("$", new String("R1"));
        tabelaAcao.get(6).put("}", new String("R5"));
        tabelaAcao.get(7).put("}", new String("R19"));
        tabelaAcao.get(7).put("break", new String("R19"));
        tabelaAcao.get(10).put("id", new String("R8"));
        tabelaAcao.get(11).put("id", new String("R9"));
        tabelaAcao.get(12).put("id", new String("R10"));
        tabelaAcao.get(13).put("id", new String("R11"));
        tabelaAcao.get(14).put("id", new String("R12"));
        tabelaAcao.get(22).put("ini", new String("R2"));
        tabelaAcao.get(24).put("$", new String("R3"));
        tabelaAcao.get(32).put("}", new String("R4"));
        tabelaAcao.get(34).put("}", new String("R5"));
        tabelaAcao.get(36).put(";", new String("R31"));
        tabelaAcao.get(37).put(";", new String("R35"));
        tabelaAcao.get(38).put(";", new String("R35"));
        tabelaAcao.get(39).put(";", new String("R27"));
        tabelaAcao.get(40).put(";", new String("R31"));
        tabelaAcao.get(46).put(";", new String("R55"));
        tabelaAcao.get(46).put(")", new String("R55"));
        tabelaAcao.get(58).put(")", new String("R57"));
        tabelaAcao.get(59).put(")", new String("R58"));
        tabelaAcao.get(62).put(";", new String("R20"));
        tabelaAcao.get(62).put(")", new String("R20"));
        tabelaAcao.get(63).put(";", new String("R21"));
        tabelaAcao.get(63).put(")", new String("R21"));
        tabelaAcao.get(64).put(";", new String("R22"));
        tabelaAcao.get(64).put(")", new String("R22"));
        tabelaAcao.get(65).put(";", new String("R23"));
        tabelaAcao.get(65).put(")", new String("R23"));
        tabelaAcao.get(66).put("}", new String("R7"));
        tabelaAcao.get(72).put("}", new String("R19"));
        tabelaAcao.get(72).put("break", new String("R19"));
        tabelaAcao.get(73).put(";", new String("R24"));
        tabelaAcao.get(75).put(";", new String("R25"));
        tabelaAcao.get(77).put(";", new String("R26"));
        tabelaAcao.get(79).put(";", new String("R28"));
        tabelaAcao.get(78).put(";", new String("R34"));
        tabelaAcao.get(81).put(";", new String("R40"));
        tabelaAcao.get(83).put(";", new String("R29"));
        tabelaAcao.get(84).put(";", new String("R31"));
        tabelaAcao.get(85).put(";", new String("R31"));
        tabelaAcao.get(87).put(";", new String("R43"));
        tabelaAcao.get(87).put(")", new String("R43"));
        tabelaAcao.get(90).put(";", new String("R44"));
        tabelaAcao.get(90).put(")", new String("R44"));
        tabelaAcao.get(93).put(";", new String("R45"));
        tabelaAcao.get(93).put(")", new String("R45"));
        tabelaAcao.get(95).put(";", new String("R49"));
        tabelaAcao.get(95).put(")", new String("R49"));
        tabelaAcao.get(96).put(";", new String("R46"));
        tabelaAcao.get(96).put(")", new String("R46"));
        tabelaAcao.get(98).put(";", new String("R47"));
        tabelaAcao.get(98).put(")", new String("R47"));
        tabelaAcao.get(105).put("}", new String("R5"));
        tabelaAcao.get(107).put("}", new String("R12"));
        tabelaAcao.get(107).put("break", new String("R12"));
        tabelaAcao.get(114).put(";", new String("R30"));
        tabelaAcao.get(115).put(";", new String("R32"));
        tabelaAcao.get(115).put(")", new String("R32"));
        tabelaAcao.get(116).put(";", new String("R33"));
        tabelaAcao.get(116).put(")", new String("R33"));
        tabelaAcao.get(118).put(";", new String("R36"));
        tabelaAcao.get(118).put(")", new String("R36"));
        tabelaAcao.get(119).put(";", new String("R37"));
        tabelaAcao.get(119).put(")", new String("R37"));
        tabelaAcao.get(120).put(";", new String("R38"));
        tabelaAcao.get(120).put(")", new String("R38"));
        tabelaAcao.get(121).put(";", new String("R39"));
        tabelaAcao.get(125).put(";", new String("R41"));
        tabelaAcao.get(126).put(";", new String("R42"));
        tabelaAcao.get(127).put("}", new String("R19"));
        tabelaAcao.get(127).put("break", new String("R19"));
        tabelaAcao.get(128).put(";", new String("R50"));
        tabelaAcao.get(128).put(")", new String("R50"));
        tabelaAcao.get(132).put(";", new String("R51"));
        tabelaAcao.get(132).put(")", new String("R51"));
        tabelaAcao.get(133).put(";", new String("R52"));
        tabelaAcao.get(133).put(")", new String("R52"));
        tabelaAcao.get(134).put(";", new String("R53"));
        tabelaAcao.get(134).put(")", new String("R53"));
        tabelaAcao.get(138).put(";", new String("R48"));
        tabelaAcao.get(138).put(")", new String("R48"));
        tabelaAcao.get(150).put(";", new String("R54"));
        tabelaAcao.get(150).put(")", new String("R54"));
        tabelaAcao.get(153).put(";", new String("R56"));
        tabelaAcao.get(153).put(")", new String("R56"));
        tabelaAcao.get(162).put("}", new String("R19"));
        tabelaAcao.get(162).put("break", new String("R19"));
        tabelaAcao.get(163).put("}", new String("R19"));
        tabelaAcao.get(163).put("break", new String("R19"));
        tabelaAcao.get(164).put("}", new String("R19"));
        tabelaAcao.get(164).put("break", new String("R19"));
        tabelaAcao.get(165).put("}", new String("R6"));
        tabelaAcao.get(171).put("}", new String("R62"));
        tabelaAcao.get(178).put("}", new String("R67"));
        tabelaAcao.get(188).put("}", new String("R17"));
        tabelaAcao.get(188).put("break", new String("R17"));
        tabelaAcao.get(195).put("}", new String("R18"));
        tabelaAcao.get(195).put("break", new String("R18"));
        tabelaAcao.get(203).put(";", new String("E215"));
        tabelaAcao.get(213).put("}", new String("R19"));
        tabelaAcao.get(213).put("break", new String("R19"));
        tabelaAcao.get(214).put("id", new String("R60"));
        tabelaAcao.get(214).put("if", new String("R60"));
        tabelaAcao.get(214).put("choice", new String("R60"));
        tabelaAcao.get(214).put("for", new String("R60"));
        tabelaAcao.get(214).put("while", new String("R60"));
        tabelaAcao.get(214).put("print", new String("R60"));
        tabelaAcao.get(214).put("read", new String("R60"));
        tabelaAcao.get(214).put("}", new String("R60"));
        tabelaAcao.get(214).put("break", new String("R60"));
        tabelaAcao.get(215).put("}", new String("R62"));
        tabelaAcao.get(216).put("}", new String("R19"));
        tabelaAcao.get(216).put("break", new String("R19"));
        tabelaAcao.get(217).put("}", new String("R19"));
        tabelaAcao.get(217).put("break", new String("R19"));
        tabelaAcao.get(224).put("}", new String("R16"));
        tabelaAcao.get(224).put("break", new String("R16"));
        tabelaAcao.get(231).put("}", new String("R19"));
        tabelaAcao.get(231).put("break", new String("R19"));
        tabelaAcao.get(233).put("}", new String("R14"));
        tabelaAcao.get(233).put("break", new String("R14"));
        tabelaAcao.get(243).put("}", new String("R13"));
        tabelaAcao.get(243).put("break", new String("R13"));
        tabelaAcao.get(250).put("}", new String("R19"));
        tabelaAcao.get(250).put("break", new String("R19"));
        tabelaAcao.get(251).put("}", new String("R66"));
        tabelaAcao.get(252).put("}", new String("R19"));
        tabelaAcao.get(252).put("break", new String("R19"));
        tabelaAcao.get(254).put("}", new String("R62"));
        tabelaAcao.get(275).put("none", new String("R65"));
        tabelaAcao.get(275).put("}", new String("R65"));
        tabelaAcao.get(280).put("id", new String("R59"));
        tabelaAcao.get(280).put("if", new String("R59"));
        tabelaAcao.get(280).put("choice", new String("R59"));
        tabelaAcao.get(280).put("for", new String("R59"));
        tabelaAcao.get(280).put("while", new String("R59"));
        tabelaAcao.get(280).put("print", new String("R59"));
        tabelaAcao.get(280).put("read", new String("R59"));
        tabelaAcao.get(280).put("}", new String("R59"));
        tabelaAcao.get(280).put("break", new String("R59"));
        tabelaAcao.get(281).put("}", new String("R61"));
        tabelaAcao.get(282).put("none", new String("R63"));
        tabelaAcao.get(282).put("}", new String("R63"));
        tabelaAcao.get(283).put("none", new String("R64"));
        tabelaAcao.get(283).put("}", new String("R64"));
        tabelaAcao.get(285).put("}", new String("R19"));
        tabelaAcao.get(285).put("break", new String("R19"));
        tabelaAcao.get(286).put(")", new String("R68"));
        tabelaAcao.get(290).put(")", new String("R70"));
        tabelaAcao.get(311).put("}", new String("R19"));
        tabelaAcao.get(311).put("break", new String("R19"));
        tabelaAcao.get(312).put("}", new String("R15"));
        tabelaAcao.get(312).put("break", new String("R15"));
    }

    //Adiciona desvios
    private void adicionaDesvios() {
        tabelaDesvio.get(0).put("Inicial", 1);
        tabelaDesvio.get(0).put("Var", 2);
        tabelaDesvio.get(2).put("Ini", 4);
        tabelaDesvio.get(6).put("Declaracao", 8);
        tabelaDesvio.get(6).put("Tipo", 9);
        tabelaDesvio.get(7).put("Comando", 14);
        tabelaDesvio.get(23).put("Declaracao2", 32);
        tabelaDesvio.get(25).put("Atribuicao", 35);
        tabelaDesvio.get(26).put("Expressao_relacional", 42);
        tabelaDesvio.get(29).put("Expressao_relacional", 50);
        tabelaDesvio.get(30).put("Print", 57);
        tabelaDesvio.get(33).put("Valor", 61);
        tabelaDesvio.get(34).put("Declaracao", 66);
        tabelaDesvio.get(34).put("Tipo", 67);
        tabelaDesvio.get(36).put("Aux1", 73);
        tabelaDesvio.get(37).put("Aux3", 75);
        tabelaDesvio.get(38).put("Aux3", 77);
        tabelaDesvio.get(38).put("Aux4", 78);
        tabelaDesvio.get(40).put("Aux5", 79);
        tabelaDesvio.get(40).put("Aux1", 81);
        tabelaDesvio.get(41).put("Aux6", 83);
        tabelaDesvio.get(43).put("Aux10", 87);
        tabelaDesvio.get(44).put("Aux7", 90);
        tabelaDesvio.get(45).put("Aux7", 93);
        tabelaDesvio.get(46).put("Aux8", 96);
        tabelaDesvio.get(47).put("Aux9", 98);
        tabelaDesvio.get(72).put("Comando", 107);
        tabelaDesvio.get(74).put("Aux2", 114);
        tabelaDesvio.get(76).put("Aux4", 78);
        tabelaDesvio.get(80).put("Aux4", 121);
        tabelaDesvio.get(84).put("Aux1", 125);
        tabelaDesvio.get(85).put("Aux1", 126);
        tabelaDesvio.get(88).put("Aux4", 128);
        tabelaDesvio.get(89).put("Aux11", 132);
        tabelaDesvio.get(89).put("Aux4", 134);
        tabelaDesvio.get(91).put("Aux4", 138);
        tabelaDesvio.get(92).put("Aux4", 95);
        tabelaDesvio.get(94).put("Aux4", 138);
        tabelaDesvio.get(97).put("Aux2", 150);
        tabelaDesvio.get(101).put("Atribuicao", 155);
        tabelaDesvio.get(105).put("Declaracao", 165);
        tabelaDesvio.get(105).put("Tipo", 9);
        tabelaDesvio.get(127).put("Comando", 171);
        tabelaDesvio.get(154).put("Option", 178);
        tabelaDesvio.get(162).put("Comando", 181);
        tabelaDesvio.get(163).put("Comando", 188);
        tabelaDesvio.get(164).put("Comando", 195);
        tabelaDesvio.get(171).put("Break", 202);
        tabelaDesvio.get(178).put("None", 204);
        tabelaDesvio.get(180).put("Expressao_relacional", 207);
        tabelaDesvio.get(206).put("Valor", 218);
        tabelaDesvio.get(213).put("Comando", 224);
        tabelaDesvio.get(214).put("Else", 231);
        tabelaDesvio.get(215).put("Break", 202);
        tabelaDesvio.get(216).put("Comando", 233);
        tabelaDesvio.get(217).put("Comando", 240);
        tabelaDesvio.get(231).put("Comando", 243);
        tabelaDesvio.get(250).put("Comando", 254);
        tabelaDesvio.get(252).put("Comando", 261);
        tabelaDesvio.get(253).put("Expressao_aritmetica", 269);
        tabelaDesvio.get(254).put("Break", 273);
        tabelaDesvio.get(275).put("Aux12", 282);
        tabelaDesvio.get(275).put("Option", 283);
        tabelaDesvio.get(277).put("Aux4", 286);
        tabelaDesvio.get(278).put("Aux4", 290);
        tabelaDesvio.get(279).put("Aux4", 294);
        tabelaDesvio.get(285).put("Comando", 299);
        tabelaDesvio.get(298).put("Valor", 218);
        tabelaDesvio.get(311).put("Comando", 312);
    }

}
