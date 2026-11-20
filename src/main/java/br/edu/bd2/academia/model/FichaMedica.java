package br.edu.bd2.academia.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;

import javax.persistence.Entity;

import javax.persistence.GeneratedValue;

import javax.persistence.GenerationType;

import javax.persistence.Id;

import javax.persistence.Table;

/** Avaliação física/médica do aluno. Relação 1-1 com Aluno. */
@Entity
@Table(name = "ficha_medica")
public class FichaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "peso_kg", precision = 5, scale = 1)
    private BigDecimal pesoKg;

    @Column(name = "altura_cm")
    private Integer alturaCm;

    @Column(length = 255)
    private String restricoes;

    @Column(name = "apto_atividade", nullable = false)
    private boolean aptoAtividade = true;

    @Column(name = "data_avaliacao")
    private LocalDate dataAvaliacao;

    public FichaMedica() { }
    public FichaMedica(BigDecimal pesoKg, Integer alturaCm, String restricoes, boolean apto, LocalDate dataAvaliacao) {
        this.pesoKg = pesoKg;
        this.alturaCm = alturaCm;
        this.restricoes = restricoes;
        this.aptoAtividade = apto;
        this.dataAvaliacao = dataAvaliacao;
    }
    public Long getId() { return id; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }
    public Integer getAlturaCm() { return alturaCm; }
    public void setAlturaCm(Integer alturaCm) { this.alturaCm = alturaCm; }
    public String getRestricoes() { return restricoes; }
    public void setRestricoes(String restricoes) { this.restricoes = restricoes; }
    public boolean isAptoAtividade() { return aptoAtividade; }
    public void setAptoAtividade(boolean aptoAtividade) { this.aptoAtividade = aptoAtividade; }
    public LocalDate getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDate dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
}
