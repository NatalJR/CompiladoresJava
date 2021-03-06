/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compsintático;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author junio_6hmetbq
 */
public class AnSemantico {
    private String code;
    private Integer temp_counter;
    private List<String> variable_declaration;
    private List<String> variable_type;

   AnSemantico(List<String> variable_declaration, List<String> variable_type) {
        this.code = "";
        this.temp_counter = 0;
        this.variable_declaration = variable_declaration;
        this.variable_type = variable_type;
    }

    Token codeGenerator(String s, String prod, Stack<Token> pilha, int[] label_counter) {
        Token t_prod = new Token("", prod, "nao_terminal");
        Token token;
        switch (Integer.valueOf(s)) {
            case 1:
                pilha.pop();
                pilha.pop();
                pilha.pop();
                pilha.pop();
                token = pilha.pop();
                while (this.variable_type.contains("later")) {
                    int index = this.variable_type.indexOf("later");
                    this.variable_declaration.remove(index);
                    this.variable_type.remove(index);
                }
                if (!this.variable_declaration.isEmpty()) {
                    t_prod.setError("Variavel(s) não declaradas: " + this.variable_declaration);
                } else {
                    System.out.println("Codigo intermediario gerado com sucesso\n" + token.getCode());
                }
                break;
            case 2:
                Token token_type, token_expr;
                token_type = pilha.pop();
                token = pilha.pop();
                token_expr = pilha.pop();
                if (this.variable_declaration.contains(token.getLexema())) {
                    int index = this.variable_declaration.indexOf(token.getLexema());
                    if (this.variable_type.get(index).equals("later")) {
                        t_prod.setError("Variavel '" + token.getLexema() + "' possui declaração duplicada");
                    } else if (this.variable_type.get(index).equals(token_type.getType())) {
                        t_prod.setCode(token_type.getCode() + " " + token.getLexema() + "\n" + token_expr.getCode());
                    } else if (token_type.getType().equals("float") && this.variable_type.get(index).equals("int")) {
                        t_prod.setCode(token_type.getCode() + " " + token.getLexema() + "\n" + token_expr.getCode());
                    } else {
                        t_prod.setError("Uma variavel do tipo " + token_type.getType() + " não pode receber um tipo " + this.variable_type.get(index));
                    }
                    this.variable_declaration.remove(index);
                    this.variable_type.remove(index);
                } else {
                    this.variable_declaration.add(token.getLexema());
                    this.variable_type.add("later");
                    if (!token_expr.getCode().equals("")){
                        t_prod.setCode(token_expr.getCode());
                    }
                }
                break;
            case 3:
            case 4:
            case 5:
            case 6:
                token = pilha.pop();
                t_prod.setCode(token.getCode());
                t_prod.setType(token.getType());
                break;
            case 7: // attributions
            case 8: // attributions
                Token token_attr, last_operation = null;
                token_attr = pilha.pop();
                pilha.pop();
                token = pilha.pop();
                if (!pilha.isEmpty()) {
                    last_operation = pilha.pop();
                }
                code = token_attr.getLexema() + " = " + token.getCode();
                List<String> assembly_expression = Arrays.asList(code.split(" "));
                if (assembly_expression.size() > 3) {
                    ArrayList<String> expression = new ArrayList<>(assembly_expression);
                    expression.remove(0);
                    expression.remove(0);
                    String tac = "";
                    List<String> formattedTac = format3End(expression, tac, token_attr.getLexema());
                    code = String.join("\n ", formattedTac);
                }
                t_prod.setType(token.getType());
                if (!this.variable_declaration.contains(token_attr.getLexema())) {
                    this.variable_declaration.add(token_attr.getLexema());
                }
                int index = this.variable_declaration.indexOf(token_attr.getLexema());
                if (this.variable_type.size() > index) {
                    if (this.variable_type.get(index).matches("int|float") || token.getType().matches("int|float")) {
                        if (this.variable_type.get(index).equals("int") && token.getType().equals("int")) {
                            this.variable_type.set(index, "int");
                        } else {
                            this.variable_type.set(index, "float");
                        }
                    } else if (!this.variable_type.get(index).equals(token.getType())) {
                        t_prod.setError("A variável '" + token_attr.getLexema() + "' está sendo atribuída a tipos diferentes");
                    } else {
                        this.variable_type.set(index, token.getType());
                    }
                } else {
                    this.variable_type.add(token.getType());
                }
                if (last_operation == null) {
                    t_prod.setCode(code);
                } else {
                    t_prod.setCode(code + "\n" + last_operation.getCode());
                }
                break;
            case 9: // Reserved word yoint
                typeDeclaration(pilha, t_prod, "int");
                break;
            case 10: // Reserved word yofloat
                typeDeclaration(pilha, t_prod, "float");
                break;
            case 11: // Reserved word yochar
                typeDeclaration(pilha, t_prod, "char");
                break;
            case 12: // Reserved word yobool
                typeDeclaration(pilha, t_prod, "bool");
                break;
            case 13: // Integer value
                value_declaration(pilha, t_prod, "int");
                break;
            case 14: // Float value
                value_declaration(pilha, t_prod, "float");
                break;
            case 15: // Char value
                value_declaration(pilha, t_prod, "char");
                break;
            case 16: // True value
                value_declaration(pilha, t_prod, "bool");
                break;
            case 17: // False value
                value_declaration(pilha, t_prod, "bool");
                break;
            case 18:
            case 19:
                token = pilha.pop();
                t_prod.setCode(token.getCode());
                break;
            case 20: // SCANF
            case 21: // PRINTF
            case 22: // SCANF
            case 23: // PRINTF
                inOutFormat(pilha, t_prod);
                break;
            case 24: // OUTPUTVALUE == id
            case 25: // OUTPUTVALUE == string
            case 26: // OUTPUTVALUE == TIPO
                token = pilha.pop();
                t_prod.setCode(token.getLexema());
                break;
            case 27: // IF
            case 28: // IF
                formatIf(pilha, t_prod, label_counter);
                break;
            case 29: // IF ELSE
            case 30: // IF ELSE
                formatIfElse(pilha, t_prod, label_counter);
                break;
            case 31: // WHILE
            case 32: // WHILE
                formatWhile(pilha, t_prod, label_counter);
                break;
            case 33: // FOR
            case 34: // FOR
                formatFor(pilha, t_prod, label_counter);
                break;
            case 35: // SWITCH
            case 36: // SWITCH
            case 37: // CASE
            case 38: // CASE
            case 39: // CASE
            case 40: // CASE
                break;
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
                passValueAndType(pilha, t_prod);
                break;
            case 46:
                passValueAndTypeID(pilha, t_prod);
                break;
            case 47:
                pilha.pop();
                token = pilha.pop();
                t_prod.setCode("(" + token.getCode() + ")");
                t_prod.setType(token.getType());
                break;
            case 48:
            case 49:
            case 50:
            case 51:
                formatExpression(pilha, t_prod);
                break;
            case 52:
                Token negative;
                negative = pilha.pop();
                token = pilha.pop();
                t_prod.setCode(negative.getLexema() + token.getCode());
                break;
            case 53:
            case 54:
                formatExpression(pilha, t_prod);
                break;
            case 55:
                passValueAndTypeID(pilha, t_prod);
                break;
            case 56:
                passValueAndType(pilha, t_prod);
                break;
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
                formatOperation(pilha, t_prod, "bool");
                break;
            case 63:
            case 64:
            case 65:
            case 66:
                formatOperation(pilha, t_prod, "int");
                break;
            case 67:
            case 68:
                formatOperation(pilha, t_prod, "bool");
                break;
            case 69:
            case 70:
                token = pilha.pop();
                code += this.getTempCounter() + " = " + token.getLexema() + " + 1\n";
                t_prod.setCode(code + token.getLexema() + " = " + this.getSpecialTempCounter());
                break;
            case 71:
                t_prod.setCode(pilha.pop().getCode());
                break;
            case 72:
                t_prod.setCode(pilha.pop().getCode());
                break;
        }
        return t_prod;
    }

    private void typeDeclaration(Stack<Token> pilha, Token t_prod, String type) {
        Token token;
        token = pilha.pop();
        t_prod.setCode(token.getLexema());
        t_prod.setType(type);
    }

    private void passValueAndTypeID(Stack<Token> pilha, Token t_prod) {
        Token token;
        token = pilha.pop();
        t_prod.setCode(token.getLexema());
        if (!this.variable_declaration.contains(token.getLexema())) {
            this.variable_declaration.add(token.getLexema());
            this.variable_type.add("later");
        }
    }

    private void passValueAndType(Stack<Token> pilha, Token t_prod) {
        Token token;
        token = pilha.pop();
        t_prod.setCode(token.getCode());
        if (token.getType().isEmpty()) {
            String[] split_declaration = token.getCode().split(" ");
            if (!this.variable_declaration.contains(split_declaration[0]) && split_declaration.length <= 1) {
                this.variable_declaration.add(token.getLexema());
                this.variable_type.add("later");
            }
        }
        t_prod.setType(token.getType());
    }

    private List<String> format3End(List<String> assembly_expression, String expr, String attribution) {
        if (assembly_expression.size() > 2) {
            String temp = this.getTempCounter();
            expr += temp + " = ";
            expr += assembly_expression.get(0) + " ";
            assembly_expression.remove(0);
            expr += assembly_expression.get(0) + " ";
            assembly_expression.remove(0);
            expr += assembly_expression.get(0) + " ";
            assembly_expression.remove(0);
            assembly_expression.add(0, temp);
            expr += "\n";
            return format3End(assembly_expression, expr, attribution);
        }
        expr += attribution + " = " + this.getSpecialTempCounter();
        return Collections.singletonList(expr);
    }

    private void formatExpression(Stack<Token> pilha, Token t_prod) {
        Token first_value, expression, second_value;
        first_value = pilha.pop();
        expression = pilha.pop();
        second_value = pilha.pop();
        String fv_type = first_value.getType();
        String sv_type = second_value.getType();

        if (!fv_type.equals("bool") && !sv_type.equals("bool") && !expression.getType().equals("bool")) {
            if (fv_type.equals(sv_type) || (!fv_type.equals("char") && !sv_type.equals("char"))) {
                if (fv_type.equals("char") && sv_type.equals("char")) {
                    t_prod.setType("char");
                } else if (fv_type.equals("int") && sv_type.equals("int")) {
                    t_prod.setType("int");
                } else if (fv_type.equals("") && !sv_type.equals("")) {
                    t_prod.setType(sv_type);
                    if (!this.variable_declaration.contains(first_value.getCode())) {
                        this.variable_declaration.add(first_value.getCode());
                    }
                    int index = this.variable_declaration.indexOf(first_value.getCode());
                    this.variable_type.set(index, sv_type);
                } else if (sv_type.equals("") && !fv_type.equals("")) {
                    t_prod.setType(fv_type);
                    if (!this.variable_declaration.contains(second_value.getCode())) {
                        this.variable_declaration.add(second_value.getCode());
                    }
                    int index = this.variable_declaration.indexOf(second_value.getCode());
                    this.variable_type.set(index, fv_type);
                } else {
                    t_prod.setType("float");
                }
                t_prod.setCode(first_value.getCode() + " " + expression.getCode() + " " + second_value.getCode());
            } else {
                t_prod.setError("Uma variável do tipo " + fv_type + " não pode operar com uma do tipo " + sv_type);
            }
        } else if (fv_type.equals("bool") && sv_type.equals("bool") && !expression.getType().equals("bool")) {
            t_prod.setError("Operador '" + expression.getLexema() + "' não é booleano");
        } else if ((fv_type.equals("bool") && sv_type.equals("bool") || fv_type.equals("") || sv_type.equals("")) && expression.getType().equals("bool")) {
            if (fv_type.equals("") && !sv_type.equals("")) {
                t_prod.setType(sv_type);
                if (!this.variable_declaration.contains(first_value.getCode())) {
                    this.variable_declaration.add(first_value.getCode());
                }
                int index = this.variable_declaration.indexOf(first_value.getCode());
                this.variable_type.set(index, sv_type);
            } else if (sv_type.equals("") && !fv_type.equals("")) {
                t_prod.setType(fv_type);
                if (!this.variable_declaration.contains(second_value.getCode())) {
                    this.variable_declaration.add(second_value.getCode());
                }
                int index = this.variable_declaration.indexOf(second_value.getCode());
                this.variable_type.set(index, fv_type);
            } else {
                t_prod.setType(expression.getType());
            }
            t_prod.setCode(first_value.getCode() + " " + expression.getCode() + " " + second_value.getCode());
        } else if (!fv_type.equals("bool") && !sv_type.equals("bool") && expression.getType().equals("bool")) {
            t_prod.setError("Operador booleano '" + expression.getCode() + "' suporta apenas operadores booleanos");
        }
    }

    private void formatWhile(Stack<Token> pilha, Token t_prod, int[] label_counter) {
        Token token_statement, token_expr;
        pilha.pop();
        pilha.pop();
        token_statement = pilha.pop();
        pilha.pop();
        pilha.pop();
        token_expr = pilha.pop();
        pilha.pop();
        t_prod.setBegin(this.getLabelCounter(label_counter));
        t_prod.setEnd(this.getLabelCounter(label_counter));
        code = t_prod.getBegin() + " : \n";
        code += "if " + token_statement.getCode() + " = false goto " + t_prod.getEnd() + "\n";
        code += token_expr.getCode() + "\n" + "goto " + t_prod.getBegin() + "\n" + t_prod.getEnd() + " : \n";
        if (!pilha.isEmpty())
            code += pilha.pop().getCode();
        t_prod.setCode(code);
    }

    private void formatOperation(Stack<Token> pilha, Token t_prod, String type) {
        Token t_op;
        t_op = pilha.pop();
        t_prod.setCode(t_op.getLexema());
        t_prod.setType(type);
        t_prod.setOperator(t_op.getLexema());
    }

    private void formatFor(Stack<Token> pilha, Token t_prod, int[] label_counter) {
        Token iter_var, op_relational, stop_value, unary_value, expr;
        pilha.pop();
        pilha.pop();
        iter_var = pilha.pop();
        pilha.pop();
        pilha.pop();
        pilha.pop();
        pilha.pop();
        op_relational = pilha.pop();
        stop_value = pilha.pop();
        pilha.pop();
        unary_value = pilha.pop();
        pilha.pop();
        pilha.pop();
        expr = pilha.pop();
        pilha.pop();
        String stop_condition = iter_var.getLexema() + " " + op_relational.getCode() + " " + stop_value.getCode();
        t_prod.setBegin(this.getLabelCounter(label_counter));
        t_prod.setEnd(this.getLabelCounter(label_counter));
        code = "";
        code += t_prod.getBegin() + " : \n";
        code += expr.getCode() + "\n";
        code += "if " + stop_condition + " = false goto " + t_prod.getEnd() + "\n";
        code += unary_value.getCode() + "\n" + "goto " + t_prod.getBegin() + "\n" + t_prod.getEnd() + " : \n";
        if (!pilha.isEmpty())
            code += pilha.pop().getCode();
        t_prod.setCode(code);
    }

    private void value_declaration(Stack<Token> pilha, Token t_prod, String type) {
        Token token;
        token = pilha.pop();
        t_prod.setType(type);
        t_prod.setCode(token.getLexema());
    }

    private void inOutFormat(Stack<Token> pilha, Token t_prod) {
        Token token_in_out, last_operation = null;
        Token token;
        token_in_out = pilha.pop();
        pilha.pop();
        token = pilha.pop();
        pilha.pop();
        if (!pilha.isEmpty())
            last_operation = pilha.pop();
        code = "";
        if (token_in_out.getLexema().equals("printfi"))
            code = "print " + token.getCode();
        else
            code = "read " + token.getLexema();
        if (last_operation == null) {
            t_prod.setCode(code);
        } else {
            t_prod.setCode(code + "\n" + last_operation.getCode());
        }
    }

    private void formatIf(Stack<Token> pilha, Token t_prod, int[] label_counter) {
        Token token_statement, token_expr;
        pilha.pop();
        pilha.pop();
        token_statement = pilha.pop();
        pilha.pop();
        pilha.pop();
        token_expr = pilha.pop();
        pilha.pop();
        t_prod.setFalse_label(this.getLabelCounter(label_counter));
        code += "if " + token_statement.getCode() + " = false goto " + t_prod.getFalse_label() + "\n";
        code += token_expr.getCode() + "\n";
        code += t_prod.getFalse_label() + " : \n";
        if (!pilha.isEmpty())
            code += pilha.pop().getCode();
        t_prod.setCode(code);
    }

    private void formatIfElse(Stack<Token> pilha, Token t_prod, int[] label_counter) {
        Token token_statement;
        Token token_expr;
        Token token_else_expr;
        pilha.pop();
        pilha.pop();
        token_statement = pilha.pop();
        pilha.pop();
        pilha.pop();
        token_expr = pilha.pop();
        pilha.pop();
        pilha.pop();
        pilha.pop();
        token_else_expr = pilha.pop();
        pilha.pop();
        t_prod.setTrue_label(this.getLabelCounter(label_counter));
        t_prod.setFalse_label(this.getLabelCounter(label_counter));
        String str_aux = this.getLabelCounter(label_counter);
        code += "if " + token_statement.getCode() + " = false goto " + t_prod.getFalse_label() + "\n";
        code += token_expr.getCode() + "\n";
        code += "goto " + str_aux + "\n";
        code += t_prod.getFalse_label() + " : \n";
        code += token_else_expr.getCode() + "\n";
        code += str_aux + " : \n";
        if (!pilha.isEmpty())
            code += pilha.pop().getCode();
        t_prod.setCode(code);
    }

    private String getLabelCounter(int[] label_counter) {
        label_counter[0]++;
        return "Label " + label_counter[0];
    }

    private String getTempCounter() {
        temp_counter++;
        return "T" + this.temp_counter;
    }

    private String getSpecialTempCounter() {
        return "T" + this.temp_counter;
    }
}
