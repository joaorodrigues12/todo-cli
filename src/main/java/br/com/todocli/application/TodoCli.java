package br.com.todocli.application;

import br.com.todocli.cli.Command;
import br.com.todocli.cli.CommandParser;
import br.com.todocli.domain.Task;
import br.com.todocli.domain.TaskRepository;

import java.io.PrintStream;
import java.io.IOException;
import java.util.List;

public final class TodoCli {
    private final TaskService service;
    private final CommandParser parser = new CommandParser();

    public TodoCli(TaskRepository repository) {
        this.service = new TaskService(repository);
    }

    public int run(String[] args, PrintStream out, PrintStream err) {
        try {
            Command command = parser.parse(args);
            switch (command) {
                case Command.Add add -> {
                    Task task = service.add(add.description());
                    out.println("Tarefa adicionada: " + task.id() + " — " + task.description());
                }
                case Command.ListAll ignored -> printTasks(service.list(), out);
                case Command.Complete complete -> {
                    Task task = service.complete(complete.id());
                    out.println("Tarefa concluída: " + task.id() + " — " + task.description());
                }
                case Command.Filter filter -> printTasks(service.filter(filter.status()), out);
                case Command.Help ignored -> printHelp(out);
            }
            return 0;
        } catch (IllegalArgumentException | IOException exception) {
            err.println("Erro: " + exception.getMessage());
            return 1;
        }
    }

    private void printTasks(List<Task> tasks, PrintStream out) {
        if (tasks.isEmpty()) {
            out.println("Nenhuma tarefa encontrada.");
            return;
        }
        tasks.forEach(task -> out.printf("%s [%s] %s (criada em %s)%n",
                task.id(), task.status().label(), task.description(), task.createdAt()));
    }

    private void printHelp(PrintStream out) {
        out.println("Uso: todo <comando>");
        out.println("  add \"descrição\"           Adiciona uma tarefa");
        out.println("  list                      Lista todas as tarefas");
        out.println("  complete <id>             Conclui uma tarefa pelo UUID");
        out.println("  filter --status <status>  Filtra por pending ou completed");
        out.println("  help                      Mostra esta ajuda");
        out.println("Dados: " + System.getProperty("todo.file", ".todo-cli/tasks.json"));
    }
}
