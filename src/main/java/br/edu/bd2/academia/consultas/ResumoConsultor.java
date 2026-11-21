package br.edu.bd2.academia.consultas;
import java.math.BigDecimal;

public class ResumoConsultor {

    private final String consultor;
    private final String unidade;
    private final Long quantidade;
    private final BigDecimal valorVendido;
    public ResumoConsultor(String consultor, String unidade, Long quantidade,
                           BigDecimal valorVendido) {
        this.consultor = consultor;
        this.unidade = unidade;
        this.quantidade = quantidade;
        this.valorVendido = valorVendido;
    }
    public String getConsultor() { return consultor; }
    public String getUnidade() { return unidade; }
    public Long getQuantidade() { return quantidade; }
    public BigDecimal getValorVendido() { return valorVendido; }
    @Override
    public String toString() {
        return String.format("%-28s %-22s %5d matrículas   R$ %,12.2f",
                consultor, unidade, quantidade, valorVendido);
    }
}

