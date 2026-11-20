package br.edu.bd2.academia.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;

import javax.persistence.Column;

import javax.persistence.Entity;

import javax.persistence.FetchType;

import javax.persistence.JoinColumn;

import javax.persistence.NamedQueries;

import javax.persistence.NamedQuery;

import javax.persistence.OneToMany;

import javax.persistence.OneToOne;

import javax.persistence.PrimaryKeyJoinColumn;

import javax.persistence.Table;

/** Cliente da rede (o "cliente" do fato Matrícula/Pagamento). */
@Entity
@Table(name = "aluno")
@PrimaryKeyJoinColumn(name = "pessoa_id")
@NamedQueries({
    // @NamedQuery 1: alunos que moram em uma cidade (parâmetro nomeado)
    @NamedQuery(name = "Aluno.porCidade",
        query = "SELECT a FROM Aluno a WHERE a.endereco.cidade = :cidade ORDER BY a.nome"),
    // @NamedQuery 2: alunos que nunca fizeram matrícula (subconsulta NOT EXISTS)
    @NamedQuery(name = "Aluno.semMatricula",
        query = "SELECT a FROM Aluno a WHERE NOT EXISTS "
              + "(SELECT m FROM Matricula m WHERE m.aluno = a) ORDER BY a.nome")
})
public class Aluno extends Pessoa {

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    /*
     * @OneToOne unidirecional: o aluno é o lado dono (tem a FK ficha_medica_id).
     * cascade ALL + orphanRemoval: a ficha não existe sem o aluno, então é salva,
     * atualizada e removida junto com ele.
     * LAZY: a ficha só é lida quando alguém precisar dela (não em toda listagem).
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_medica_id", unique = true)
    private FichaMedica fichaMedica;

    /*
     * Lado inverso do @ManyToOne Matricula.aluno. Sem cascade: matrícula tem ciclo
     * de vida próprio (é um fato de negócio, não deve sumir/ser criada por tabela).
     * LAZY é o padrão de @OneToMany; um aluno pode ter dezenas de matrículas.
     */
    @OneToMany(mappedBy = "aluno")
    private List<Matricula> matriculas = new ArrayList<>();

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
    public FichaMedica getFichaMedica() { return fichaMedica; }
    public void setFichaMedica(FichaMedica fichaMedica) { this.fichaMedica = fichaMedica; }
    public List<Matricula> getMatriculas() { return matriculas; }
    @Override
    public String toString() {
        return String.format("Aluno[id=%d, nome=%s, cpf=%s, cidade=%s]", getId(), getNome(), getCpf(),
                getEndereco() != null ? getEndereco().getCidade() : "-");
    }
}
