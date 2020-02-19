package com.github.ftfetter.pocnonblockingcache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PocService {

    private Cache<String, Foo> fooCache;
    private FakeRepository fakeRepository;

    public PocService(FakeRepository fakeRepository) {
        this.fooCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build();
        this.fakeRepository = fakeRepository;
    }

    public Mono<Foo> validateNonBlockingCache(String name) {
        return CacheMono.lookup(key -> Mono.justOrEmpty(fooCache.getIfPresent(key)).map(Signal::next), name)
                .onCacheMissResume(fakeRepository.findFooByName(name))
                .andWriteWith((key, signal) -> Mono.fromRunnable(() ->
                        Optional.ofNullable(signal.get())
                                .ifPresent(value -> fooCache.put(key, value))));
    }
}
