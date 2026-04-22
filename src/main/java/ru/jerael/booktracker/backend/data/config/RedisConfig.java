package ru.jerael.booktracker.backend.data.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.jerael.booktracker.backend.domain.model.Genre;
import ru.jerael.booktracker.backend.domain.model.language.Language;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.*;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties properties;

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory, ObjectMapper objectMapper) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10));

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put("languages", collectionConfig(objectMapper, ArrayList.class, Language.class));
        configs.put("language", singleConfig(objectMapper, Language.class));

        configs.put("genres", collectionConfig(objectMapper, LinkedHashSet.class, Genre.class));
        configs.put("genre", singleConfig(objectMapper, Genre.class));

        return RedisCacheManager.builder(factory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(configs)
            .build();
    }

    private RedisCacheConfiguration singleConfig(ObjectMapper mapper, Class<?> valueType) {
        JavaType type = mapper.getTypeFactory().constructType(valueType);
        return createConfig(mapper, type);
    }

    private RedisCacheConfiguration collectionConfig(
        ObjectMapper mapper,
        Class<? extends Collection> collectionClass,
        Class<?> elementClass
    ) {
        JavaType type = mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
        return createConfig(mapper, type);
    }

    private RedisCacheConfiguration createConfig(ObjectMapper mapper, JavaType type) {
        var serializer = new JacksonJsonRedisSerializer<>(mapper, type);
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(properties.getTtl())
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }
}
