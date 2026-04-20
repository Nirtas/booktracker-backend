package ru.jerael.booktracker.backend.config

import ch.martinelli.oss.testcontainers.mailpit.MailpitContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistrar
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    
    @Bean
    @ServiceConnection
    fun mailpitContainer(): MailpitContainer = MailpitContainer()
    
    @Bean
    fun minioContainer(): GenericContainer<*> {
        return GenericContainer(DockerImageName.parse("minio/minio:RELEASE.2025-09-07T16-13-09Z"))
            .withEnv("MINIO_ROOT_USER", "testuser")
            .withEnv("MINIO_ROOT_PASSWORD", "testpassword")
            .withCommand("server /data")
            .withExposedPorts(9000)
    }
    
    @Bean
    fun minioPropertyRegistrar(minioContainer: GenericContainer<*>): DynamicPropertyRegistrar {
        return DynamicPropertyRegistrar { registry ->
            registry.add("app.minio.url") {
                "http://" + minioContainer.host + ":" + minioContainer.getMappedPort(9000)
            }
        }
    }
    
    @Bean
    @ServiceConnection(name = "redis")
    fun redisContainer(): GenericContainer<*> {
        return GenericContainer(DockerImageName.parse("redis:8.4.2-alpine3.22"))
            .withExposedPorts(6379)
    }
}