package br.edu.bd2.academia.model;

import br.edu.bd2.academia.model.enums.StatusMatricula;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;

import javax.persistence.Column;

import javax.persistence.Entity;

import javax.persistence.EnumType;

import javax.persistence.Enumerated;

import javax.persistence.FetchType;

import javax.persistence.GeneratedValue;

import javax.persistence.GenerationType;

import javax.persistence.Id;

import javax.persistence.JoinColumn;

import javax.persistence.JoinTable;

import javax.persistence.ManyToMany;

import javax.persistence.ManyToOne;

import javax.persistence.NamedQuery;

import javax.persistence.OneToMany;

import javax.persistence.OrderBy;

import javax.persistence.Table;

/**
 * Matrícula de um aluno em um plano, numa unidade, em uma data.
 * É a "venda" do domínio: cliente (aluno) + item (plano) + local (unidade) + data.
 */
@Entity
@Table(name = "matricula")
@NamedQuery(name = "Matricula.faturamentoPorUnidade",
    query = "SELECT m.unidade.nome, COUNT(m), SUM(m.valorLiquido) FROM Matricula m "
          + "WHERE m.status <> :cancelada "
          + "GROUP BY m.unidade.nome HAVING SUM(m.valorLiquido) > :minimo "
          + "ORDER BY SUM(m.valorLiquido) DESC")
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Todos os @ManyToOne são LAZY: o padrão do JPA (EAGER) faria um SELECT extra
       por associação em cada listagem de matrículas (problema N+1). Quando a tela
       precisa dos dados, usamos JOIN FETCH na consulta. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plano_id")
    private Plano plano;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultor_id")
    private Funcionario consultor;

    @Column(name = "data_matricula", nullable = false)
    private LocalDate dataMatricula;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private StatusMatricula status;

    @Column(name = "valor_bruto", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorBruto;

    @Column(precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(name = "valor_liquido", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorLiquido;

    /* @ManyToMany unidirecional: serviços extras contratados na matrícula. */
    @ManyToMany
    @JoinTable(name = "matricula_servico",
        joinColumns = @JoinColumn(name = "matricula_id"),
        inverseJoinColumns = @JoinColumn(name = "servico_id"))
    private Set<ServicoAdicional> servicos = new HashSet<>();

    /*
     * @OneToMany bidirecional (lado inverso de Pagamento.matricula).
     * cascade ALL + orphanRemoval: as parcelas pertencem à matrícula; salvar a
     * matrícula salva as parcelas, e remover uma parcela da lista apaga do banco.
     */
    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numeroParcela")
    private List<Pagamento> pagamentos = new ArrayList<>();

    /** Mantém os dois lados da associação bidirecional sincronizados. */
    public void adicionarPagamento(Pagamento p) {
        pagamentos.add(p);
        p.setMatricula(this);
    }
    public Long getId() { return id; }
    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }
    public Plano getPlano() { return plano; }
    public void setPlano(Plano plano) { this.plano = plano; }
    public Unidade getUnidade() { return unidade; }
    public void setUnidade(Unidade unidade) { this.unidade = unidade; }
    public Funcionario getConsultor() { return consultor; }
    public void setConsultor(Funcionario consultor) { this.consultor = consultor; }
    public LocalDate getDataMatricula() { return dataMatricula; }
    public void setDataMatricula(LocalDate dataMatricula) { this.dataMatricula = dataMatricula; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public StatusMatricula getStatus() { return status; }
    public void setStatus(StatusMatricula status) { this.status = status; }
    public BigDecimal getValorBruto() { return valorBruto; }
    public void setValorBruto(BigDecimal valorBruto) { this.valorBruto = valorBruto; }
    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }
    public BigDecimal getValorLiquido() { return valorLiquido; }
    public void setValorLiquido(BigDecimal valorLiquido) { this.valorLiquido = valorLiquido; }
    public Set<ServicoAdicional> getServicos() { return servicos; }
    public List<Pagamento> getPagamentos() { return pagamentos; }
}
