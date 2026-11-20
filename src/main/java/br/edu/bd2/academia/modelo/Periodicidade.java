package br.edu.bd2.academia.modelo;

/** Duração do plano. O número de meses é usado para calcular valor total e data de fim. */
public enum Periodicidade {
    MENSAL(1), TRIMESTRAL(3), SEMESTRAL(6), ANUAL(12);

    private final int meses;

    Periodicidade(int meses) { this.meses = meses; }

    public int getMeses() { return meses; }
}
