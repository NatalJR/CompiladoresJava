/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compsintático;

/**
 *
 * @author Gome
 */
public class Token {

    private String token;
    private String lexema;
    private String tipo;
    private int linha;
    
    private String local = "";
    private String operator = "";
    private String code = "";
    private String true_label = "";
    private String false_label = "";
    private String type = "";
    private String begin = "";
    private String end = "";
    private String error = "";
    
    public Token (String token, String lexema, String tipo) {
        super();
        this.token = token;
        this.lexema = lexema;
        this.tipo = tipo;
    }

    public Token (String token, String lexema, String tipo, int linha) {
        super();
        this.token = token;
        this.lexema = lexema;
        this.tipo = tipo;
        this.linha = linha;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTrue_label() {
        return true_label;
    }

    public void setTrue_label(String true_label) {
        this.true_label = true_label;
    }

    public String getFalse_label() {
        return false_label;
    }

    public void setFalse_label(String false_label) {
        this.false_label = false_label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    @Override
    public String toString() {
		return String.format("token= %-50s lexema= %-50s tipo= %-50s\n", token, lexema, tipo);
    }
}
