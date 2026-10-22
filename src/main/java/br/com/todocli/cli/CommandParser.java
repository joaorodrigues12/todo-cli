package br.com.todocli.cli;

import br.com.todocli.domain.TaskStatus;

import java.util.UUID;

public final class CommandParser {
    public Command parse(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Informe um comando. Use 'todo help'.");
        }
        return switch (args[0]) {
            case "add" -> parseAdd(args);
            case "list" -> requireLength(args, 1, new Command.ListAll(), "Uso: todo list");
            case "complete" -> parseComplete(args);
            case "filter" -> parseFilter(args);
            case "help" -> requireLength(args, 1, new Command.Help(), "Uso: todo help");
            default -> throw new IllegalArgumentException("Comando desconhecido: " + args[0] + ". Use 'todo help'.");
        };
    }

    private Command parseAdd(String[] args) {
        if (args.length != 2 || args[1].isBlank()) {
            throw new IllegalArgumentException("Uso: todo add \"descrição da tarefa\"");
        }
        return new Command.Add(args[1]);
    }

    private Command parseComplete(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Uso: todo complete <id>");
        }
        try {
            return new Command.Complete(UUID.fromString(args[1]));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("ID inválido. Informe um UUID.", exception);
        }
    }

    private Command parseFilter(String[] args) {
        if (args.length != 3 || !"--status".equals(args[1])) {
            throw new IllegalArgumentException("Uso: todo filter --status <pending|completed>");
        }
        return new Command.Filter(TaskStatus.fromArgument(args[2]));
    }

    private Command requireLength(String[] args, int length, Command command, String usage) {
        if (args.length != length) {
            throw new IllegalArgumentException(usage);
        }
        return command;
    }
}
