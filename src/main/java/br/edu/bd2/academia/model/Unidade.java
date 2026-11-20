package br.edu.bd2.academia.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/** Unidade (filial) da rede: o "local" do fato. */
@Entity
@Table(name = "unidade")
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nome;

    @Column(length = 18)
    private String cnpj;

    @Column(name = "capacidade_alunos")
    private Integer capacidadeAlunos;

    @Column(name = "data_inauguracao")
    private LocalDate dataInauguracao;

    @Embedded
    private Endereco endereco;

    /* Lado inverso (mappedBy) do bidirecional com Funcionario.unidade. */
    @OneToMany(mappedBy = "unidade")
    private List<Funcionario> funcionarios = new ArrayList<>();

    public Unidade() { }

    public Unidade(String nome, String cnpj, Integer capacidade,
                   LocalDate inauguracao, Endereco endereco) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.capacidadeAlunos = capacidade;
        this.dataInauguracao = inauguracao;
        this.endereco = endereco;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public Integer getCapacidadeAlunos() { return capacidadeAlunos; }
    public void setCapacidadeAlunos(Integer capacidadeAlunos) {
        this.capacidadeAlunos = capacidadeAlunos;
    }
    public LocalDate getDataInauguracao() { return dataInauguracao; }
    public void setDataInauguracao(LocalDate dataInauguracao) {
        this.dataInauguracao = dataInauguracao;
    }
    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
    public List<Funcionario> getFuncionarios() { return funcionarios; }

    @Override
    public String toString() { return nome; }
}
