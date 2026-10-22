package br.com.todocli.persistence;

import br.com.todocli.domain.Task;
import br.com.todocli.domain.TaskRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

public final class JsonTaskRepository implements TaskRepository {
    private static final TypeReference<List<Task>> TASK_LIST = new TypeReference<>() { };
    private final Path file;
    private final ObjectMapper mapper;

    public JsonTaskRepository(Path file) {
        this.file = Objects.requireNonNull(file, "file não pode ser nulo").toAbsolutePath().normalize();
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public List<Task> findAll() throws IOException {
        if (Files.notExists(file)) {
            return List.of();
        }
        try {
            List<Task> tasks = mapper.readValue(file.toFile(), TASK_LIST);
            if (tasks == null) {
                throw new IOException("O arquivo JSON de tarefas contém null: " + file);
            }
            return List.copyOf(tasks);
        } catch (IOException | RuntimeException exception) {
            throw new IOException("Não foi possível ler o arquivo de tarefas '" + file + "'. O arquivo foi preservado.", exception);
        }
    }

    @Override
    public void saveAll(List<Task> tasks) throws IOException {
        Path parent = file.getParent();
        Files.createDirectories(parent);
        String prefix = file.getFileName().toString();
        if (prefix.length() < 3) {
            prefix = (prefix + "___").substring(0, 3);
        }
        Path temporary = Files.createTempFile(parent, prefix, ".tmp");
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(temporary.toFile(), List.copyOf(tasks));
            try {
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
