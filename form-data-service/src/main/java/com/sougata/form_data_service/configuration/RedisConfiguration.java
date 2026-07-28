package com.sougata.form_data_service.configuration;

import tools.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import io.lettuce.core.api.StatefulConnection;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfiguration implements CachingConfigurer {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.username:}")
    private String redisUsername;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;

    @Value("${spring.data.redis.ssl.enabled:false}")
    private boolean sslEnabled;

    @Value("${spring.data.redis.timeout:2000}")
    private long commandTimeoutMs;

    @Value("${app.cache.default-ttl-minutes:10}")
    private long defaultTtlMinutes;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
        standaloneConfig.setHostName(redisHost);
        standaloneConfig.setPort(redisPort);
        standaloneConfig.setDatabase(redisDatabase);
        if (!redisUsername.isBlank()) {
            standaloneConfig.setUsername(redisUsername);
        }
        if (!redisPassword.isBlank()) {
            standaloneConfig.setPassword(redisPassword);
        }

        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(2);
        poolConfig.setMaxWait(Duration.ofMillis(commandTimeoutMs));
        poolConfig.setTestOnBorrow(true);

        ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(
                        SocketOptions.builder()
                                .connectTimeout(Duration.ofMillis(commandTimeoutMs))
                                .keepAlive(true)
                                .build()
                )
                .timeoutOptions(TimeoutOptions.enabled(Duration.ofMillis(commandTimeoutMs)))
                .autoReconnect(true)
                .build();

        var clientConfigBuilder = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .clientOptions(clientOptions)
                .commandTimeout(Duration.ofMillis(commandTimeoutMs))
                .shutdownTimeout(Duration.ZERO);

        if (sslEnabled) {
            clientConfigBuilder.useSsl();
        }

        return new LettuceConnectionFactory(standaloneConfig, clientConfigBuilder.build());
    }

//    private ObjectMapper redisObjectMapper() {
//
//        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
//                .allowIfSubType("com.example.demo")
//                .allowIfSubType("java.util")
//                .build();
//
//        ObjectMapper mapper = JsonMapper.builder()
//                .addModule(new JavaTimeModule())
//                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//                .activateDefaultTyping(typeValidator,
//                        ObjectMapper.DefaultTyping.NON_FINAL,
//                        JsonTypeInfo.As.PROPERTY)
//                .build();
//
//        return mapper;
//    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        GenericJacksonJsonRedisSerializer jsonSerializer = new GenericJacksonJsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(defaultTtlMinutes))
                .disableCachingNullValues()
                .prefixCacheNameWith("myapp::")
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        Map<String, RedisCacheConfiguration> perCacheConfig = new HashMap<>();
//        perCacheConfig.put(CacheNames.USERS, defaultConfig.entryTtl(Duration.ofMinutes(30)));
//        perCacheConfig.put(CacheNames.PRODUCTS, defaultConfig.entryTtl(Duration.ofHours(2)));
//        perCacheConfig.put(CacheNames.SEARCH_RESULTS, defaultConfig.entryTtl(Duration.ofMinutes(2)));
//        perCacheConfig.put(CacheNames.STATIC_LOOKUPS, defaultConfig.entryTtl(Duration.ofHours(24)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(perCacheConfig)
                .transactionAware() // cache writes only commit if the surrounding @Transactional does
                .build();
    }

//    @Bean
//    @Override
//    public CacheErrorHandler errorHandler() {
//        return new CustomCacheErrorHandler();
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJacksonJsonRedisSerializer(objectMapper));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJacksonJsonRedisSerializer(objectMapper));
        template.afterPropertiesSet();
        return template;
    }
}