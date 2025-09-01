package org.yoshi.todo.controller;


import org.yoshi.todo.dto.TodoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final Map<Long, TodoDto> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong();

    // Create a new todo
    @PostMapping
    public ResponseEntity<TodoDto> create(@Valid @RequestBody TodoDto dto) {
        long id = idGen.incrementAndGet();
        TodoDto saved = new TodoDto(id, dto.task(), dto.done());
        store.put(id, saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Get all todos with optional filtering
    @GetMapping
    public List<TodoDto> getAll(@RequestParam(required = false) Boolean done, @RequestHeader(value = "X-Client-Id", required = false) String userId) {
        System.out.println(userId == null ? "anonymous" : userId);
        return store.values().stream()
                .filter(t -> done == null || t.done() == done)
                .toList();
    }

    // Get single todo by id
    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getById(@PathVariable Long id) {
        TodoDto todo = store.get(id);
        return todo != null ? ResponseEntity.ok(todo)
                : ResponseEntity.notFound().build();
    }

    // Update entire todo
    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> update(@PathVariable Long id,
                                          @Valid @RequestBody TodoDto dto) {
        if (!store.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        TodoDto updated = new TodoDto(id, dto.task(), dto.done());
        store.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    // Partially update (only "done" flag for demo)
    @PatchMapping("/{id}")
    public ResponseEntity<TodoDto> markDone(@PathVariable Long id,
                                            @RequestParam boolean done) {
        TodoDto existing = store.get(id);
        if (existing == null) return ResponseEntity.notFound().build();
        TodoDto updated = new TodoDto(id, existing.task(), done);
        store.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    // Delete todo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return store.remove(id) != null
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

