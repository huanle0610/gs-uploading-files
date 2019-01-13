package hello

import hello.storage.StorageProperties
import hello.storage.StorageService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties::class)
class Application {
    @Bean
    fun init(storageService: StorageService) = CommandLineRunner {
        storageService.deleteAll()
        storageService.init()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
