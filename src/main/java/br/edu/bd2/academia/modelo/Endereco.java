package br.edu.bd2.academia.modelo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Objeto de valor (@Embeddable): não tem tabela nem identidade própria.
 * Suas colunas são gravadas na tabela de quem o contém (pessoa e unidade).
 * Exemplo do problema de "granularidade" da impedância objeto-relacional.
 */
@Embeddable
public class Endereco {

    @Column(length = 120)
    private String logradouro;

    @Column(length = 10)
    private String numero;

    @Column(length = 60)
    private String bairro;

    @Column(length = 80)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(length = 9)
    private String cep;

    public Endereco() { }

    public Endereco(String logradouro, String numero, String bairro,
                    String cidade, String uf, String cep) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
    }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    @Override
    public String toString() {
        return logradouro + ", " + numero + " - " + bairro + ", " + cidade + "/" + uf;
    }
}
