package org.yoshi.todo.repository;

import org.springframework.stereotype.Repository;
import org.yoshi.todo.model.Todo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TodoRepository {
    private final Map<Long, Todo> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(0);
    private final Sinks.Many<Todo> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final Map<Long, Long> frequencies = new ConcurrentHashMap<>();

    public Mono<Todo> save(Todo todo) {
        long id = idGen.incrementAndGet();
        Todo saved = new Todo(id, todo.title(), todo.done());
        storage.put(id, saved);
        sink.tryEmitNext(saved);
        return Mono.just(saved);
    }

    public Flux<Todo> streamTodos() {
        return sink.asFlux();
    }

    public Flux<Todo> findAll() {
        return Flux.fromIterable(storage.values());
    }

    public Mono<Todo> findById(Long id) {
        return Mono.justOrEmpty(storage.get(id));
    }


    public Mono<Todo> update(Long id, boolean done) {
        Todo existing = storage.get(id);
        if (existing == null) return Mono.empty();
        if (existing.done()) {
            System.out.println("Entry with id : " + id + " is already COMPLETED");
        }
        frequencies.merge(id, 1L, Long::sum);
        Todo updated = new Todo(id, existing.title(), done);
        storage.put(id, updated);
        return Mono.just(updated);
    }


    public Mono<Void> deleteById(Long id) {
        storage.remove(id);
        return Mono.empty();
    }

    public Mono<Void> deleteAll() {
        storage.clear();
        frequencies.clear();
        return Mono.empty();
    }

    public int getSize() {
        return storage.size();
    }

    public Map<Long, Long> getDoneStats() {
        return frequencies;
    }
}

