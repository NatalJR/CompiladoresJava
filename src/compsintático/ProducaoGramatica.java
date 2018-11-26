/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compsintático;

import java.util.List;

/**
 *
 * @author Gome
 */
public class ProducaoGramatica {

    private int numero;
    private String ladoEsquerdo;
    private List<String> ladoDireito;

    public ProducaoGramatica(int numero, String ladoEsquerdo, List<String> ladoDireito) {
        super();
        this.numero = numero;
        this.ladoEsquerdo = ladoEsquerdo;
        this.ladoDireito = ladoDireito;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getLadoEsquerdo() {
        return ladoEsquerdo;
    }

    public void setLadoEsquerdo(String ladoEsquerdo) {
        this.ladoEsquerdo = ladoEsquerdo;
    }

    public List<String> getLadoDireito() {
        return ladoDireito;
    }

    public void setLadoDireito(List<String> ladoDireito) {
        this.ladoDireito = ladoDireito;
    }

}
