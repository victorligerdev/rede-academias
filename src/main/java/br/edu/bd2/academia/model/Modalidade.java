package br.edu.bd2.academia.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/** Atividade oferecida (Musculação, Natação, Funcional, Lutas...). */
@Entity
@Table(name = "modalidade")
public class Modalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nome;

    @Column(length = 200)
    private String descricao;

    /* Lado inverso do @ManyToMany Plano.modalidades. */
    @ManyToMany(mappedBy = "modalidades")
    private Set<Plano> planos = new HashSet<>();

    public Modalidade() { }

    public Modalidade(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Set<Plano> getPlanos() { return planos; }

    @Override
    public String toString() { return nome; }
}
