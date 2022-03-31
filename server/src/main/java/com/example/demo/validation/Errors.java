package com.example.demo.validation;

import lombok.Getter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Errors {

    @Getter
    private final Set<String> nulls = new TreeSet<>();
    @Getter
    private final Set<String> otherErrors = new TreeSet<>();

    private final Deque<String> context = new LinkedList<>();

    private String getPath(final String fieldName) {
        List<String> path = new ArrayList<>(context.size() + 1);
        path.addAll(context);
        path.add(fieldName);
        return String.join(".", path);
    }

    public Errors checkNotNull(
            final String fieldName,
            final Object obj
    ) {
        return checkNotNull(fieldName, obj, null);
    }

    public <T> Errors checkNotNull(
            final String fieldName,
            final T obj,
            final Consumer<T> consumer
    ) {
        if (obj == null) {
            nulls.add(getPath(fieldName));
        } else if (consumer != null) {
            pushContext(fieldName);
            try {
                consumer.accept(obj);
            } finally {
                popContext();
            }
        }
        return this;
    }

    public Errors otherCheck(
            final String message,
            final Supplier<Boolean> checkFunction
    ) {
        if (!checkFunction.get()) {
            otherErrors.add(getPath(message));
        }
        return this;
    }

    public Errors pushContext(final String a) {
        context.addLast(a);
        return this;
    }

    public Errors popContext() {
        context.removeLast();
        return this;
    }

    @Override
    public String toString() {
        String result = null;
        if (!nulls.isEmpty()) {
            result = "Fields must not be null: " + String.join(", ", nulls);
        }
        if (!otherErrors.isEmpty()) {
            result = String.join(result == null ? "" : "\n", "Other errors: " + String.join(", ", otherErrors));
        }
        return result;
    }

    public boolean isNotEmpty() {
        return !nulls.isEmpty() || !otherErrors.isEmpty();
    }
}