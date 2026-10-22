package br.com.todocli.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Task(UUID id, String description, Instant createdAt, TaskStatus status, Instant completedAt) {
    public Task {
        Objects.requireNonNull(id, "id não pode ser nulo");
        Objects.requireNonNull(description, "description não pode ser nula");
        Objects.requireNonNull(createdAt, "createdAt não pode ser nulo");
        Objects.requireNonNull(status, "status não pode ser nulo");
        if (description.isBlank()) {
            throw new IllegalArgumentException("A descrição não pode estar vazia.");
        }
        if ((status == TaskStatus.COMPLETED) != (completedAt != null)) {
            throw new IllegalArgumentException("O status e a data de conclusão da tarefa são inconsistentes.");
        }
        if (completedAt != null && completedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("A tarefa não pode ser concluída antes de sua criação.");
        }
    }

    public static Task create(String description) {
        return new Task(UUID.randomUUID(), description.trim(), Instant.now(), TaskStatus.PENDING, null);
    }

    public Task complete(Instant completedAt) {
        Objects.requireNonNull(completedAt, "completedAt não pode ser nulo");
        if (status == TaskStatus.COMPLETED) {
            return this;
        }
        return new Task(id, description, createdAt, TaskStatus.COMPLETED, completedAt);
    }
}
