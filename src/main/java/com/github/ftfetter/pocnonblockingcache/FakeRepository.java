package com.github.ftfetter.pocnonblockingcache;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.LocalDate;

@Component
public class FakeRepository {

    public Mono<Foo> findFooByName(String name) {
        return Mono.delay(Duration.ofSeconds(5))
                .map(l -> Foo.FooBuilder.aFoo().withName(name).withDate(LocalDate.now()).withValue(0L).build());
    }
}
