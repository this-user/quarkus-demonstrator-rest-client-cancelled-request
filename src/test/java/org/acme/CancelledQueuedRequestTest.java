package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.TimeoutException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class CancelledQueuedRequestTest {

    @RestClient
    SlowClient client;

    @Test
    void cancelledQueuedRequestDoesNotHoldTheConnection() throws Exception {
        CompletableFuture<String> a = client.call("A").subscribeAsCompletionStage();
        Thread.sleep(100);

        assertThrows(TimeoutException.class,
                () -> client.call("B").ifNoItem().after(Duration.ofMillis(200)).fail().await().indefinitely());

        assertEquals("A", a.get());

        long start = System.nanoTime();
        String c = client.call("C").ifNoItem().after(Duration.ofSeconds(5)).recoverWithItem("no answer").await().indefinitely();
        long elapsedMs = Duration.ofNanos(System.nanoTime() - start).toMillis();

        assertTrue(elapsedMs < 1000, "C waited " + elapsedMs + " ms after A finished (gives up at 5000 ms)"
                + "; the cancelled queued request B holds the only connection until read-timeout");
        assertEquals("C", c);
        assertEquals(List.of("A", "C"), SlowResource.RECEIVED, "B is never sent");
    }
}
