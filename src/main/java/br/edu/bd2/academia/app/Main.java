package br.edu.bd2.academia.app;

import br.edu.bd2.academia.carga.CargaDados;
import br.edu.bd2.academia.dao.AlunoDAO;
import br.edu.bd2.academia.dao.PlanoDAO;
import br.edu.bd2.academia.model.Aluno;
import br.edu.bd2.academia.model.CategoriaPlano;
import br.edu.bd2.academia.model.Endereco;
import br.edu.bd2.academia.model.Plano;
import br.edu.bd2.academia.model.enums.Periodicidade;
import br.edu.bd2.academia.model.enums.Sexo;
import br.edu.bd2.academia.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/** Interface de console para CRUD, carga e demonstração das consultas. */
public class Main {
    private static final Scanner IN = new Scanner(System.in);
    private static final DateTimeFormatter BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            if (args.length > 0) {
                modoAutomatico(em, args[0]);
            } else {
                menuPrincipal(em);
            }
        } finally {
            em.close();
            JPAUtil.fechar();
        }
    }

    private static void modoAutomatico(EntityManager em, String comando) {
        DemonstracaoConsultas demo = new DemonstracaoConsultas(em);
        if ("carga".equalsIgnoreCase(comando)) {
            recarregar(em);
        } else if ("consultas".equalsIgnoreCase(comando)) {
            for (int i = 1; i <= 10; i++) demo.executarJPQL(i);
            for (int i = 1; i <= 3; i++) demo.executarCriteria(i);
        } else {
            System.out.println("Comandos: carga | consultas");
        }
    }

    private static void menuPrincipal(EntityManager em) {
        DemonstracaoConsultas demo = new DemonstracaoConsultas(em);
        while (true) {
            System.out.println("\n========== REDE DE ACADEMIAS VÉRTICE ==========");
            System.out.println("1 - Cadastro de Alunos (CRUD)");
            System.out.println("2 - Cadastro de Planos (CRUD)");
            System.out.println("3 - Consultas JPQL (1 a 10)");
            System.out.println("4 - Consultas Criteria (1 a 3)");
            System.out.println("5 - Carga de dados");
            System.out.println("6 - Totais do banco");
            System.out.println("0 - Sair");
            try {
                switch (lerInt("Opção: ")) {
                    case 1: menuAlunos(em); break;
                    case 2: menuPlanos(em); break;
                    case 3:
                        int jpql = lerInt("Consulta (1-10, 0=todas): ");
                        if (jpql == 0) for (int i = 1; i <= 10; i++) demo.executarJPQL(i);
                        else demo.executarJPQL(jpql);
                        break;
                    case 4:
                        int criteria = lerInt("Consulta (1-3, 0=todas): ");
                        if (criteria == 0) for (int i = 1; i <= 3; i++) demo.executarCriteria(i);
                        else demo.executarCriteria(criteria);
                        break;
                    case 5:
                        if (lerTexto("Isso APAGA todos os dados. Confirma (s/n)? ").equalsIgnoreCase("s")) recarregar(em);
                        break;
                    case 6: totais(em); break;
                    case 0: return;
                    default: System.out.println("Opção inválida.");
                }
            } catch (PersistenceException | IllegalArgumentException e) {
                System.out.println("ERRO: " + causaRaiz(e));
            }
        }
    }

    private static void menuAlunos(EntityManager em) {
        AlunoDAO dao = new AlunoDAO(em);
        while (true) {
            System.out.println("\n--- ALUNOS (" + dao.contar() + " cadastrados) ---");
            System.out.println("1 - Listar");
            System.out.println("2 - Buscar por nome");
            System.out.println("3 - Detalhar por id");
            System.out.println("4 - Inserir");
            System.out.println("5 - Atualizar contato/endereço");
            System.out.println("6 - Remover");
            System.out.println("0 - Voltar");
            int op = lerInt("Opção: ");
            em.clear();
            if (op == 0) return;
            switch (op) {
                case 1:
                    int pagina = 0;
                    while (true) {
                        List<Aluno> lista = dao.listarPaginado(pagina, 10);
                        System.out.println("Página " + (pagina + 1));
                        lista.forEach(a -> System.out.println(" " + a));
                        String nav = lerTexto("[p] próxima, [a] anterior, [s] sair: ");
                        if (nav.equalsIgnoreCase("p") && lista.size() == 10) pagina++;
                        else if (nav.equalsIgnoreCase("a") && pagina > 0) pagina--;
                        else if (nav.equalsIgnoreCase("s") || nav.isEmpty()) break;
                    }
                    break;
                case 2:
                    dao.buscarPorNome(lerTexto("Trecho do nome: ")).forEach(a -> System.out.println(" " + a));
                    break;
                case 3:
                    Aluno detalhado = dao.buscarPorId(lerLong("Id: "));
                    if (detalhado == null) System.out.println("Não encontrado.");
                    else {
                        System.out.println(detalhado);
                        System.out.println("E-mail: " + detalhado.getEmail() + " | Telefone: " + detalhado.getTelefone());
                        System.out.println("Endereço: " + detalhado.getEndereco());
                    }
                    break;
                case 4:
                    String cpf = lerTexto("CPF: ");
                    if (dao.buscarPorCpf(cpf) != null) {
                        System.out.println("CPF já cadastrado.");
                        break;
                    }
                    Aluno novo = new Aluno();
                    novo.setNome(lerTexto("Nome: "));
                    novo.setCpf(cpf);
                    novo.setDataNascimento(lerData("Data de nascimento (dd/mm/aaaa): "));
                    novo.setSexo(Sexo.valueOf(lerTexto("Sexo (MASCULINO/FEMININO/OUTRO): ").toUpperCase()));
                    novo.setEmail(lerTexto("E-mail: "));
                    novo.setTelefone(lerTexto("Telefone: "));
                    novo.setEndereco(lerEndereco());
                    novo.setDataCadastro(LocalDate.now());
                    dao.salvar(novo);
                    System.out.println("Aluno salvo com id " + novo.getId());
                    break;
                case 5:
                    Aluno existente = dao.buscarPorId(lerLong("Id: "));
                    if (existente == null) {
                        System.out.println("Não encontrado.");
                        break;
                    }
                    String email = lerTexto("Novo e-mail (enter mantém): ");
                    String telefone = lerTexto("Novo telefone (enter mantém): ");
                    if (!email.isEmpty()) existente.setEmail(email);
                    if (!telefone.isEmpty()) existente.setTelefone(telefone);
                    if (lerTexto("Alterar endereço (s/n)? ").equalsIgnoreCase("s")) existente.setEndereco(lerEndereco());
                    em.detach(existente);
                    dao.atualizar(existente);
                    System.out.println("Aluno atualizado.");
                    break;
                case 6:
                    Long idRemover = lerLong("Id: ");
                    Aluno remover = dao.buscarPorId(idRemover);
                    if (remover == null) System.out.println("Não encontrado.");
                    else if (!remover.getMatriculas().isEmpty()) System.out.println("Aluno possui matrículas e não pode ser removido.");
                    else {
                        dao.remover(idRemover);
                        System.out.println("Aluno removido.");
                    }
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void menuPlanos(EntityManager em) {
        PlanoDAO dao = new PlanoDAO(em);
        while (true) {
            System.out.println("\n--- PLANOS (" + dao.contar() + " cadastrados) ---");
            System.out.println("1 - Listar");
            System.out.println("2 - Inserir");
            System.out.println("3 - Atualizar preço/ativo");
            System.out.println("4 - Remover");
            System.out.println("0 - Voltar");
            int op = lerInt("Opção: ");
            em.clear();
            if (op == 0) return;
            switch (op) {
                case 1:
                    int pagina = 0;
                    while (true) {
                        List<Plano> planos = dao.listarComCategoria(pagina, 10);
                        planos.forEach(p -> System.out.println(" " + p));
                        String nav = lerTexto("[p] próxima, [a] anterior, [s] sair: ");
                        if (nav.equalsIgnoreCase("p") && planos.size() == 10) pagina++;
                        else if (nav.equalsIgnoreCase("a") && pagina > 0) pagina--;
                        else if (nav.equalsIgnoreCase("s") || nav.isEmpty()) break;
                    }
                    break;
                case 2:
                    List<CategoriaPlano> categorias = dao.listarCategorias();
                    if (categorias.isEmpty()) {
                        System.out.println("Rode a carga antes: não há categorias.");
                        break;
                    }
                    Plano novo = new Plano();
                    novo.setCodigo(lerTexto("Código: "));
                    novo.setNome(lerTexto("Nome: "));
                    novo.setPeriodicidade(Periodicidade.valueOf(lerTexto("Periodicidade (MENSAL/TRIMESTRAL/SEMESTRAL/ANUAL): ").toUpperCase()));
                    novo.setValorMensal(new BigDecimal(lerTexto("Valor mensal: ").replace(',', '.')));
                    for (int i = 0; i < categorias.size(); i++) System.out.println(" " + (i + 1) + " - " + categorias.get(i));
                    novo.setCategoria(categorias.get(lerInt("Categoria: ") - 1));
                    dao.salvar(novo);
                    System.out.println("Plano salvo com id " + novo.getId());
                    break;
                case 3:
                    Plano plano = dao.buscarPorId(lerLong("Id: "));
                    if (plano == null) {
                        System.out.println("Não encontrado.");
                        break;
                    }
                    String valor = lerTexto("Novo valor mensal (enter mantém): ");
                    if (!valor.isEmpty()) plano.setValorMensal(new BigDecimal(valor.replace(',', '.')));
                    String ativo = lerTexto("Ativo (s/n, enter mantém): ");
                    if (!ativo.isEmpty()) plano.setAtivo(ativo.equalsIgnoreCase("s"));
                    dao.atualizar(plano);
                    System.out.println("Plano atualizado.");
                    break;
                case 4:
                    Long planoId = lerLong("Id: ");
                    Plano removerPlano = dao.buscarPorId(planoId);
                    if (removerPlano == null) System.out.println("Não encontrado.");
                    else if (dao.possuiMatriculas(planoId)) {
                        removerPlano.setAtivo(false);
                        dao.atualizar(removerPlano);
                        System.out.println("Plano desativado porque possui matrículas.");
                    } else {
                        dao.remover(planoId);
                        System.out.println("Plano removido.");
                    }
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void recarregar(EntityManager em) {
        CargaDados carga = new CargaDados(em);
        System.out.println("Limpando tabelas...");
        carga.limpar();
        System.out.println("Gerando dados...");
        carga.executar();
        totais(em);
    }

    private static void totais(EntityManager em) {
        String[] entidades = {"Unidade", "Funcionario", "Aluno", "FichaMedica", "Modalidade", "CategoriaPlano", "Plano", "ServicoAdicional", "Matricula", "Pagamento"};
        System.out.println("Totais no banco:");
        for (String entidade : entidades) {
            Long total = em.createQuery("SELECT COUNT(x) FROM " + entidade + " x", Long.class).getSingleResult();
            System.out.printf("%-18s %6d%n", entidade, total);
        }
    }

    private static Endereco lerEndereco() {
        return new Endereco(
                lerTexto("Logradouro: "), lerTexto("Número: "), lerTexto("Bairro: "),
                lerTexto("Cidade: "), lerTexto("UF: ").toUpperCase(), lerTexto("CEP: "));
    }

    private static String lerTexto(String rotulo) {
        System.out.print(rotulo);
        return IN.hasNextLine() ? IN.nextLine().trim() : "";
    }

    private static int lerInt(String rotulo) {
        while (true) {
            try { return Integer.parseInt(lerTexto(rotulo)); }
            catch (NumberFormatException e) { System.out.println("Digite um número."); }
        }
    }

    private static Long lerLong(String rotulo) { return (long) lerInt(rotulo); }

    private static LocalDate lerData(String rotulo) {
        while (true) {
            try { return LocalDate.parse(lerTexto(rotulo), BR); }
            catch (RuntimeException e) { System.out.println("Data inválida, use dd/mm/aaaa."); }
        }
    }

    private static String causaRaiz(Throwable t) {
        while (t.getCause() != null) t = t.getCause();
        return t.getMessage();
    }
}
