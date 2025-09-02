package org.yoshi.todo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.yoshi.todo.model.Todo;
import org.yoshi.todo.repository.TodoRepository;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TodoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    TodoRepository todoRepository;

    @BeforeEach
    void setUp () {
        todoRepository.deleteAll();
        todoRepository.save(new Todo(null, "Wake up", false));
        todoRepository.save(new Todo(null, "Go to the lou", true));
        todoRepository.save(new Todo(null, "Wash your paws", false));
        todoRepository.save(new Todo(null, "Brush your teeth", true));
        todoRepository.save(new Todo(null, "Pet Mr Cowboy", false));
        todoRepository.save(new Todo(null, "Let Him out", true));
        todoRepository.save(new Todo(null, "Drink some water", false));
        todoRepository.save(new Todo(null, "Dress up ... a bit", false));
        todoRepository.save(new Todo(null, "Go for a walk", true));
        todoRepository.save(new Todo(null, "Make and drink coffee", false));
        todoRepository.save(new Todo(null, "Clean the kitchen", true));
        todoRepository.save(new Todo(null, "Work, work ...", false));
    }

    @Test
    void testAddTodo() {
        Todo newTodo = new Todo(null, "Write tests", false);

        webTestClient.post()
                .uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newTodo)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Write tests")
                .jsonPath("$.done").isEqualTo(false);
    }

    @Test
    void testStreamTodos() {
        Flux<Todo> result = webTestClient.get()
                .uri("/api/todos/stream")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Todo.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNextCount(2) // now we know 2 exist
                .thenCancel()
                .verify();
    }
}
