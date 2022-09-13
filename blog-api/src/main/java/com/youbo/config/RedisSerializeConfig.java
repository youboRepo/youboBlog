package com.youbo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Description: Redis序列化配置
 * @Author: Naccl
 * @Date: 2020-09-27
 */
@Configuration
public class RedisSerializeConfig {

	/**
	 * 使用JSON序列化方式
	 *
	 * @param redisConnectionFactory
	 * @return
	 */
/*	@Bean
	public RedisTemplate<Object, Object> jsonRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		template.setDefaultSerializer(serializer);
		return template;
	}*/
	
	@Bean
	@Primary
	@SuppressWarnings("all")
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		
		// JSON序列化
		Jackson2JsonRedisSerializer<Object> objectJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		objectJackson2JsonRedisSerializer.setObjectMapper(om);
		
		// String序列化
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		
		template.setKeySerializer(stringRedisSerializer);
		template.setHashKeySerializer(stringRedisSerializer);
		template.setValueSerializer(objectJackson2JsonRedisSerializer);
		template.setHashValueSerializer(objectJackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		template.setConnectionFactory(redisConnectionFactory);

		return template;
	}
}
