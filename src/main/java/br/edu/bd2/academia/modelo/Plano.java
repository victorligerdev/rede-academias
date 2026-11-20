package br.edu.bd2.academia.modelo;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.Table;

/** Plano vendido: o "item" do fato Matrícula. */
@Entity
@Table(name = "plano")
public class Plano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private Periodicidade periodicidade;

    @Column(name = "valor_mensal", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMensal;

    @Column(nullable = false)
    private boolean ativo = true;

    /* Muitos planos para uma categoria. LAZY: na maioria das telas só precisamos do plano. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id")
    private CategoriaPlano categoria;

    /*
     * @ManyToMany: um plano dá acesso a várias modalidades e uma modalidade está
     * em vários planos. O JPA cria a tabela associativa plano_modalidade.
     * Sem cascade: modalidades são cadastros independentes, não devem ser
     * criadas/apagadas junto com um plano.
     */
    @ManyToMany
    @JoinTable(name = "plano_modalidade",
            joinColumns = @JoinColumn(name = "plano_id"),
            inverseJoinColumns = @JoinColumn(name = "modalidade_id"))
    private Set<Modalidade> modalidades = new HashSet<>();

    /** Valor do plano inteiro (mensalidade x meses da periodicidade). */
    public BigDecimal getValorTotal() {
        return valorMensal.multiply(BigDecimal.valueOf(periodicidade.getMeses()));
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Periodicidade getPeriodicidade() { return periodicidade; }
    public void setPeriodicidade(Periodicidade periodicidade) {
        this.periodicidade = periodicidade;
    }
    public BigDecimal getValorMensal() { return valorMensal; }
    public void setValorMensal(BigDecimal valorMensal) { this.valorMensal = valorMensal; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public CategoriaPlano getCategoria() { return categoria; }
    public void setCategoria(CategoriaPlano categoria) { this.categoria = categoria; }
    public Set<Modalidade> getModalidades() { return modalidades; }
    public void adicionarModalidade(Modalidade m) {
        modalidades.add(m);
        m.getPlanos().add(this);
    }

    @Override
    public String toString() {
        return String.format("Plano[id=%d, %s, %s, %s, R$ %s/mês]",
                id, codigo, nome, periodicidade, valorMensal);
    }
}
