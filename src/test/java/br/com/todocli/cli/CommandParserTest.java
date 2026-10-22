package br.com.todocli.cli;

import br.com.todocli.domain.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {
    private final CommandParser parser = new CommandParser();

    @Test
    void interpretaComandos() {
        UUID id = UUID.randomUUID();
        assertEquals(new Command.Add("Estudar Java"), parser.parse(new String[]{"add", "Estudar Java"}));
        assertEquals(new Command.ListAll(), parser.parse(new String[]{"list"}));
        assertEquals(new Command.Complete(id), parser.parse(new String[]{"complete", id.toString()}));
        assertEquals(new Command.Filter(TaskStatus.PENDING), parser.parse(new String[]{"filter", "--status", "pending"}));
        assertEquals(new Command.Filter(TaskStatus.COMPLETED), parser.parse(new String[]{"filter", "--status", "completed"}));
        assertEquals(new Command.Help(), parser.parse(new String[]{"help"}));
    }

    @Test
    void rejeitaArgumentosInvalidosComAjudaDeUso() {
        assertTrue(assertThrows(IllegalArgumentException.class, () -> parser.parse(new String[]{})).getMessage().contains("todo help"));
        assertTrue(assertThrows(IllegalArgumentException.class, () -> parser.parse(new String[]{"add"})).getMessage().contains("Uso:"));
        assertTrue(assertThrows(IllegalArgumentException.class, () -> parser.parse(new String[]{"complete", "abc"})).getMessage().contains("UUID"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(new String[]{"filter", "--status", "waiting"}));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(new String[]{"outro"}));
    }
}
