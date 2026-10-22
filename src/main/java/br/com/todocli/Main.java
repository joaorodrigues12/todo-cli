package br.com.todocli;

import br.com.todocli.application.TodoCli;
import br.com.todocli.persistence.JsonTaskRepository;

import java.io.PrintStream;
import java.nio.file.Path;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Path dataFile = Path.of(System.getProperty("todo.file", ".todo-cli/tasks.json"));
        int exitCode = new TodoCli(new JsonTaskRepository(dataFile)).run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
