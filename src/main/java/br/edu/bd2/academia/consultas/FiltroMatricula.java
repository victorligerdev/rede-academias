package br.edu.bd2.academia.consultas;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.edu.bd2.academia.model.enums.Periodicidade;
import br.edu.bd2.academia.model.enums.StatusMatricula;

public class FiltroMatricula {

    String unidade;
    String categoria;
    Periodicidade periodicidade;
    StatusMatricula status;
    LocalDate dataDe;
    LocalDate dataAte;
    BigDecimal valorMinimo;
    String nomeAluno;

    public FiltroMatricula unidade(String v) { this.unidade = v; return this;
    }
    public FiltroMatricula categoria(String v) { this.categoria = v; return
            this; }
    public FiltroMatricula periodicidade(Periodicidade v) {
        this.periodicidade = v; return this; }
    public FiltroMatricula status(StatusMatricula v) { this.status = v;
        return this; }
    public FiltroMatricula dataDe(LocalDate v) { this.dataDe = v; return
            this; }
    public FiltroMatricula dataAte(LocalDate v) { this.dataAte = v; return
            this; }
    public FiltroMatricula valorMinimo(BigDecimal v) { this.valorMinimo = v;
        return this; }
    public FiltroMatricula nomeAluno(String v) { this.nomeAluno = v; return
            this; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (unidade != null) sb.append("unidade=").append(unidade).append(' ');
        if (categoria != null)
            sb.append("categoria=").append(categoria).append(' ');
        if (periodicidade != null)
            sb.append("periodicidade=").append(periodicidade).append(' ');
        if (status != null) sb.append("status=").append(status).append(' ');
        if (dataDe != null) sb.append("de=").append(dataDe).append(' ');
        if (dataAte != null) sb.append("ate=").append(dataAte).append(' ');
        if (valorMinimo != null)
            sb.append("valor>=").append(valorMinimo).append(' ');
        if (nomeAluno != null) sb.append("aluno~").append(nomeAluno).append(' ');
        return sb.length() == 0 ? "(sem filtros)" : sb.toString().trim();
    }
}