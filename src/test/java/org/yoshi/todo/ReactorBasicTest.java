package org.yoshi.todo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class ReactorBasicTest {

    @Test
    void simpleFluxTest() {
        Flux<Integer> numbers = Flux.just(1, 2, 3);

        StepVerifier.create(numbers)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .verifyComplete();
    }
}

