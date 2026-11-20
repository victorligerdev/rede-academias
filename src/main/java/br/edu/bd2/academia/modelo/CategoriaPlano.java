package br.edu.bd2.academia.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/** Categoria comercial do plano (Estudante, Básico, Plus, Premium, Corporativo). */
@Entity
@Table(name = "categoria_plano")
public class CategoriaPlano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String nome;

    @Column(length = 200)
    private String descricao;

    @OneToMany(mappedBy = "categoria")
    private List<Plano> planos = new ArrayList<>();

    public CategoriaPlano() { }

    public CategoriaPlano(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public List<Plano> getPlanos() { return planos; }

    @Override
    public String toString() { return nome; }
}
