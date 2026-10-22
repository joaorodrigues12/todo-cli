package br.com.todocli.application;

import br.com.todocli.persistence.JsonTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TodoCliTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void integraAdicaoListagemConclusaoEFiltro() {
        TodoCli cli = new TodoCli(new JsonTaskRepository(temporaryDirectory.resolve("tasks.json")));
        String added = run(cli, "add", "Estudar Java 25").out();
        String id = added.substring(added.indexOf(": ") + 2, added.indexOf(" — "));

        assertTrue(run(cli, "list").out().contains("[pending] Estudar Java 25"));
        assertTrue(run(cli, "filter", "--status", "pending").out().contains(id));
        assertEquals(0, cli.run(new String[]{"complete", id}, new PrintStream(new ByteArrayOutputStream()), System.err));
        assertTrue(run(cli, "filter", "--status", "completed").out().contains("[completed] Estudar Java 25"));
        assertTrue(run(cli, "filter", "--status", "pending").out().contains("Nenhuma tarefa"));
    }

    @Test
    void ajudaNaoPrecisaAcessarPersistencia() {
        TodoCli cli = new TodoCli(new JsonTaskRepository(temporaryDirectory.resolve("tasks.json")));
        assertTrue(run(cli, "help").out().contains("todo add"));
        assertFalse(java.nio.file.Files.exists(temporaryDirectory.resolve("tasks.json")));
    }

    @Test
    void erroDeArgumentoRetornaCodigoNaoZero() {
        TodoCli cli = new TodoCli(new JsonTaskRepository(temporaryDirectory.resolve("tasks.json")));
        Result result = run(cli, "filter", "--status", "unknown");
        assertEquals(1, result.exitCode());
        assertTrue(result.err().contains("Erro:"));
    }

    private Result run(TodoCli cli, String... args) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        int code = cli.run(args, new PrintStream(out), new PrintStream(err));
        return new Result(code, out.toString(StandardCharsets.UTF_8), err.toString(StandardCharsets.UTF_8));
    }

    private record Result(int exitCode, String out, String err) { }
}
