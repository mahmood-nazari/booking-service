package com.example.booking.config;

import java.time.Duration;
import java.util.Map;

import com.example.booking.api.dto.SlotsPageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class CacheConfig {

	@Primary
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory cf) {
		ObjectMapper mapper = new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		Jackson2JsonRedisSerializer<SlotsPageDto> slotsSerializer =
				new Jackson2JsonRedisSerializer<>(SlotsPageDto.class);
		slotsSerializer.setObjectMapper(mapper);

		RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(5))
				.disableCachingNullValues();

		RedisCacheConfiguration freeSlotsCfg = base.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(slotsSerializer)
		);

		return RedisCacheManager.builder(cf)
				.cacheDefaults(base)
				.withInitialCacheConfigurations(Map.of(
						"freeSlots:v3", freeSlotsCfg
				))
				.build();
	}
}