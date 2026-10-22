package br.com.todocli.domain;

import java.io.IOException;
import java.util.List;

public interface TaskRepository {
    List<Task> findAll() throws IOException;

    void saveAll(List<Task> tasks) throws IOException;
}
