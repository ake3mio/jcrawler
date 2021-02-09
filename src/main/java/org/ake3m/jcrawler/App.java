package org.ake3m.jcrawler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("""
                    Pass the url to start crawling from.

                    Expected usage
                    --------------
                    java -jar jcrawler.jar http://url-to-crawl.com
                    """);
            return;
        }
    }
}
