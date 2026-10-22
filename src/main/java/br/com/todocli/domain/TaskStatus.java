package br.com.todocli.domain;

import java.util.Locale;

public enum TaskStatus {
    PENDING,
    COMPLETED;

    public static TaskStatus fromArgument(String value) {
        if (value == null) {
            throw new IllegalArgumentException("O status é obrigatório.");
        }
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "pending" -> PENDING;
            case "completed" -> COMPLETED;
            default -> throw new IllegalArgumentException("Status inválido: " + value + ". Use pending ou completed.");
        };
    }

    public String label() {
        return name().toLowerCase(Locale.ROOT);
    }
}
