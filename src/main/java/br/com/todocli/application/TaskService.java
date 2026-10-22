package br.com.todocli.application;

import br.com.todocli.domain.Task;
import br.com.todocli.domain.TaskRepository;
import br.com.todocli.domain.TaskStatus;

import java.io.IOException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository não pode ser nulo");
    }

    public Task add(String description) throws IOException {
        Task task = Task.create(description);
        List<Task> tasks = new java.util.ArrayList<>(repository.findAll());
        tasks.add(task);
        repository.saveAll(List.copyOf(tasks));
        return task;
    }

    public List<Task> list() throws IOException {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Task::createdAt).thenComparing(Task::id))
                .toList();
    }

    public List<Task> filter(TaskStatus status) throws IOException {
        return list().stream().filter(task -> task.status() == status).toList();
    }

    public Task complete(UUID id) throws IOException {
        List<Task> tasks = new java.util.ArrayList<>(repository.findAll());
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            if (task.id().equals(id)) {
                Task completed = task.complete(Instant.now());
                tasks.set(index, completed);
                repository.saveAll(List.copyOf(tasks));
                return completed;
            }
        }
        throw new IllegalArgumentException("Tarefa não encontrada: " + id);
    }
}
