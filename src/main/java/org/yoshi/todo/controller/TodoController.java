package org.yoshi.todo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.yoshi.todo.model.Todo;
import org.yoshi.todo.repository.TodoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoRepository repo;

    public TodoController(TodoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Flux<Todo> getAllTodos() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Todo> getTodoById(@PathVariable Long id) {
        return repo.findById(id);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Todo> streamTodos() {
        return Flux.interval(Duration.ofMillis(66))
                .flatMap(tick -> repo.findAll())
                .distinct(Todo::id);
    }

    @GetMapping(value = "/completed", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Todo> streamCompletedTodos() {
        return Flux.interval(Duration.ofSeconds(11))
                .flatMap(tick -> repo.findAll())
                .filter(Todo::done);
    }


    @PostMapping
    public Mono<Todo> create(@RequestBody Todo todo) {
        return repo.save(todo);
    }

    @PatchMapping("/{id}")
    public Mono<Todo> markDone(@PathVariable Long id, @RequestParam boolean done) {
        return repo.update(id, done);
    }

    @GetMapping("/size")
    public Mono<Integer> getSize () {
        return Mono.just(repo.getSize());
    }

    @GetMapping("/stats")
    public Map<Long, Long> getDoneStats () {
        return repo.getDoneStats();
    }
}
