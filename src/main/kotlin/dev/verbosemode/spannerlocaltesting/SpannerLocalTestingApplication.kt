package dev.verbosemode.spannerlocaltesting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpannerLocalTestingApplication

fun main(args: Array<String>) {
    runApplication<SpannerLocalTestingApplication>(*args)
}
