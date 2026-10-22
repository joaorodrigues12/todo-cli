package br.com.todocli.cli;

import br.com.todocli.domain.TaskStatus;

import java.util.UUID;

public sealed interface Command permits Command.Add, Command.ListAll, Command.Complete, Command.Filter, Command.Help {
    record Add(String description) implements Command { }

    record ListAll() implements Command { }

    record Complete(UUID id) implements Command { }

    record Filter(TaskStatus status) implements Command { }

    record Help() implements Command { }
}
