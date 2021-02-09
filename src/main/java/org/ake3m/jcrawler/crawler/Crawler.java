package org.ake3m.jcrawler.crawler;

import lombok.extern.slf4j.Slf4j;
import org.ake3m.jcrawler.common.URLUtils;
import org.ake3m.jcrawler.crawler.model.Page;
import org.ake3m.jcrawler.http.HttpService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
public class Crawler {

    private final Map<String, Page> pages = new ConcurrentHashMap<>();
    private final HttpService httpService;
    private volatile URL rootURL = null;

    public static Map<String, Page> crawl(String root) {
        var crawler = new Crawler();
        try {
            crawler.run(root).get();
        } catch (InterruptedException | ExecutionException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
        return crawler.getPages();
    }

    private Crawler() {
        this.httpService = new HttpService();
    }

    private Map<String, Page> getPages() {
        return pages;
    }

    private CompletableFuture<Void> run(String root) throws InterruptedException, URISyntaxException {

        log.info("Crawling url: " + root);

        var currentRootURL = URLUtils.createURL(root, null);

        if (isNull(rootURL)) {
            synchronized (this) {
                rootURL = currentRootURL;
            }
        }

        if (shouldSkipURL(currentRootURL)) {
            log.info("Skipping url: " + root);
            return CompletableFuture.completedFuture(null);
        }

        var childFutures =
                httpService
                        .get(currentRootURL.toURI(), 5)
                        .thenApply(Jsoup::parse)
                        .thenComposeAsync(document -> transverseDocument(document, currentRootURL));

        return CompletableFuture.allOf(childFutures);
    }

    private CompletableFuture<Void> transverseDocument(Document document, URL rootURL) {
        if (pages.containsKey(rootURL.toString())) {
            return CompletableFuture.completedFuture(null);
        }
        var page = createPage(document, rootURL);
        pages.put(rootURL.toString(), page);
        return getPageInternalLinks(page);
    }

    private Page createPage(Document document, URL rootURL) {

        var links = findLinks(document);
        var internalLinks = new TreeSet<String>();
        var externalLinks = new TreeSet<String>();

        links.forEach(src -> {
            if (URLUtils.hasEqualHost(src, rootURL)) {
                internalLinks.add(src.toString());
            } else {
                externalLinks.add(src.toString());
            }
        });

        return new Page(
                rootURL.toString(),
                internalLinks,
                externalLinks,
                findStaticURIs(document));
    }

    private CompletableFuture<Void> getPageInternalLinks(Page page) {
        var futures = page
                .getInternalLinks()
                .stream()
                .map(childRoot -> {
                    try {
                        return run(childRoot);
                    } catch (InterruptedException | URISyntaxException e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures);
    }

    private boolean shouldSkipURL(URL url) {
        return isNull(url) || isNull(url.getHost()) || pages.containsKey(url.toString());
    }

    private Set<URL> findLinks(Document document) {
        return document
                .select("a")
                .stream()
                .map(e -> e.attr("href"))
                .map(s -> URLUtils.createURL(s, rootURL))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<String> findStaticURIs(Document document) {
        return document
                .select("[src]")
                .stream()
                .map(e -> e.attr("src"))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
