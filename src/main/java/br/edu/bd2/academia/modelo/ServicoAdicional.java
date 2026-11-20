package br.edu.bd2.academia.modelo;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/** Serviço contratado junto com a matrícula (personal, armário, nutricionista...). */
@Entity
@Table(name = "servico_adicional")
public class ServicoAdicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nome;

    @Column(name = "valor_mensal", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMensal;

    public ServicoAdicional() { }

    public ServicoAdicional(String nome, BigDecimal valorMensal) {
        this.nome = nome;
        this.valorMensal = valorMensal;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getValorMensal() { return valorMensal; }
    public void setValorMensal(BigDecimal valorMensal) { this.valorMensal = valorMensal; }

    @Override
    public String toString() { return nome; }
}
