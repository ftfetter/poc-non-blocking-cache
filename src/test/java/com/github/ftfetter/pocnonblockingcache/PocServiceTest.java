package com.github.ftfetter.pocnonblockingcache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.blockhound.BlockHound;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import java.time.Duration;

@SpringBootTest
class PocServiceTest {

    private PocService pocService;
    private FakeRepository fakeRepository;

    @BeforeEach
    void setUp() {
        fakeRepository = mock(FakeRepository.class);
        pocService = new PocService(fakeRepository);
    }

    @BeforeAll
    static void beforeAll() {
        BlockHound.install();
    }

    @Test
    void validateIfCacheIsNonBlocking() {
        String defaultName = "DEFAULT";

        when(fakeRepository.findFooByName(defaultName))
                .thenCallRealMethod();

        // First time execution, loads the cache
        Foo foo = pocService.validateNonBlockingCache(defaultName)
                .subscribeOn(Schedulers.parallel())
                .block();

        // Second run, must return before 5 seconds of delay
        Foo cachedFoo = pocService.validateNonBlockingCache(defaultName)
                .subscribeOn(Schedulers.parallel())
                .block();

        assertEquals(foo, cachedFoo);
    }

    @Test
    void validateIfNonBlockingCacheIsWorking() {
        String defaultName = "DEFAULT";

        when(fakeRepository.findFooByName(defaultName))
                .thenCallRealMethod();

        // First time execution, loads the cache
        StepVerifier.create(pocService.validateNonBlockingCache(defaultName))
                .assertNext(mono -> assertEquals(defaultName, mono.getName()))
                .expectComplete()
                .verifyThenAssertThat()
                .tookMoreThan(Duration.ofMillis(500));

        // Second run, must return before 5 seconds of delay
        StepVerifier.create(pocService.validateNonBlockingCache(defaultName))
                .assertNext(mono -> assertEquals(defaultName, mono.getName()))
                .expectComplete()
                .verifyThenAssertThat()
                .tookLessThan(Duration.ofMillis(500));

        // Waits for cache to reset
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Third run, with cache reseted, must waits 5 seconds again
        StepVerifier.create(pocService.validateNonBlockingCache(defaultName))
                .assertNext(mono -> assertEquals(defaultName, mono.getName()))
                .expectComplete()
                .verifyThenAssertThat()
                .tookMoreThan(Duration.ofMillis(500));
    }
}