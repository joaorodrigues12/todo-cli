package br.com.todocli.persistence;

import br.com.todocli.domain.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JsonTaskRepositoryTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void salvaECarregaTarefas() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.json");
        JsonTaskRepository repository = new JsonTaskRepository(file);
        Task task = Task.create("Persistir em JSON");

        repository.saveAll(List.of(task));

        assertEquals(List.of(task), repository.findAll());
        assertTrue(Files.readString(file).contains("Persistir em JSON"));
        try (Stream<Path> files = Files.list(temporaryDirectory)) {
            assertTrue(files.noneMatch(path -> path.getFileName().toString().endsWith(".tmp")));
        }
    }

    @Test
    void arquivoAusenteRepresentaListaVazia() throws IOException {
        assertTrue(new JsonTaskRepository(temporaryDirectory.resolve("ausente.json")).findAll().isEmpty());
    }

    @Test
    void jsonCorrompidoGeraErroEPermaneceIntacto() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.json");
        String corrupted = "{ JSON inválido";
        Files.writeString(file, corrupted);
        JsonTaskRepository repository = new JsonTaskRepository(file);

        assertThrows(IOException.class, repository::findAll);
        assertEquals(corrupted, Files.readString(file));
    }
}
