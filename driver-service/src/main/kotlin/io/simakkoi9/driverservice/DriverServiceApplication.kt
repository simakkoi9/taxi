package io.simakkoi9.driverservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DriverServiceApplication

fun main(args: Array<String>) {
    runApplication<DriverServiceApplication>(*args)
}
