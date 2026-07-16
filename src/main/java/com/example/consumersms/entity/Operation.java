package com.example.consumersms.entity;

public enum Operation {
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    READ("READ");

    private final String value;

    Operation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Operation fromString(String value) {
        for (Operation op : Operation.values()) {
            if (op.value.equalsIgnoreCase(value)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Invalid operation: " + value);
    }
}