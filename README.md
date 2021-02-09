# jcrawler

A small website crawler using:

- Java 15
- okhttp
- lombok
- jsoup
- maven

This minimal program does not currently contain unit tests.

The program uses a multithreaded approach to crawl through a given host and return a list 
of pages stating their:

- url
- internal links to other pages on the website
- external links on the page
- static asset urls

## installing and building

To build the jar run the following:

```shell

mvn clean package

```

## Running the program

You can run the jar passing a website url as an argument

```shell

# example:
java -jar ./target/jcrawler.jar  http://google.co.uk

```