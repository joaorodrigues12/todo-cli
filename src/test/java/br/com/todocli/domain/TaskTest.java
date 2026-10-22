package br.com.todocli.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void criaTarefaPendenteComUuidEData() {
        Task task = Task.create("  Estudar Java 25  ");

        assertNotNull(task.id());
        assertEquals("Estudar Java 25", task.description());
        assertNotNull(task.createdAt());
        assertEquals(TaskStatus.PENDING, task.status());
        assertNull(task.completedAt());
    }

    @Test
    void rejeitaDescricaoVazia() {
        assertThrows(IllegalArgumentException.class, () -> Task.create("  "));
    }

    @Test
    void concluirPreservaIdentidadeEDataDeCriacao() {
        Task pending = Task.create("Estudar records");
        Instant completedAt = Instant.parse("2026-10-22T12:00:00Z");

        Task completed = pending.complete(completedAt);

        assertEquals(pending.id(), completed.id());
        assertEquals(pending.description(), completed.description());
        assertEquals(pending.createdAt(), completed.createdAt());
        assertEquals(TaskStatus.COMPLETED, completed.status());
        assertEquals(completedAt, completed.completedAt());
    }

    @Test
    void concluirDuasVezesMantemPrimeiraData() {
        Instant first = Instant.parse("2026-10-22T12:00:00Z");
        Task completed = Task.create("Tarefa").complete(first).complete(first.plusSeconds(60));

        assertEquals(first, completed.completedAt());
    }

    @Test
    void rejeitaEstadoInconsistente() {
        assertThrows(IllegalArgumentException.class, () -> new Task(
                UUID.randomUUID(), "Tarefa", Instant.now(), TaskStatus.COMPLETED, null));
    }

    @Test
    void rejeitaConclusaoAnteriorACriacao() {
        Instant createdAt = Instant.parse("2026-10-22T12:00:00Z");
        assertThrows(IllegalArgumentException.class, () -> new Task(
                UUID.randomUUID(), "Tarefa", createdAt, TaskStatus.COMPLETED, createdAt.minusSeconds(1)));
    }
}
