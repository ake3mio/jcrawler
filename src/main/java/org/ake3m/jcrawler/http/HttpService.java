package org.ake3m.jcrawler.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static java.util.Objects.nonNull;

@Slf4j
public class HttpService {
    private final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .build();

    public CompletableFuture<String> get(URI url, int retries) throws InterruptedException {
        return get(url).exceptionallyComposeAsync(throwable -> {
            try {
                if (retries > 0) {
                    sleep(1000);
                    return get(url, retries - 1);
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            return CompletableFuture.completedFuture(null);
        });
    }

    public CompletableFuture<String> get(URI url) {
        Request request = new Request.Builder().url(url.toString()).build();
        return CompletableFuture.supplyAsync(() -> {
            try (var response = client.newCall(request).execute()) {
                var body = response.body();
                if (response.isSuccessful() && nonNull(body)) {
                    return body.string();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            return null;
        });
    }
}
