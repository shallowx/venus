package org.venus.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.venus.cache.VenusMultiLevelCacheConstants.DEFAULT_LISTENER_NAME;

@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(VenusMultiLevelCacheProperties.class)
public class VenusMultiLevelCacheAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    static class VenusMultiLevelCacheRedisAutoConfiguration {
        @Bean
        public RedisTemplate<String, CacheWrapper> redisTemplate(RedisConnectionFactory factory) {
            RedisTemplate<String, CacheWrapper> template = new RedisTemplate<>();
            template.setConnectionFactory(factory);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
            Jackson2JsonRedisSerializer<CacheWrapper> serializer = new Jackson2JsonRedisSerializer<>(mapper, CacheWrapper.class);

            RedisSerializer<?> stringSerializer = new StringRedisSerializer();
            template.setKeySerializer(stringSerializer);
            template.setValueSerializer(serializer);
            template.setHashKeySerializer(stringSerializer);
            template.setHashValueSerializer(serializer);
            template.afterPropertiesSet();
            return template;
        }

        @Bean
        public RedisTemplate<String, CacheListenerMessage> cacheListenerMessageRedisTemplate(RedisConnectionFactory factory) {
            RedisTemplate<String, CacheListenerMessage> template = new RedisTemplate<>();
            template.setConnectionFactory(factory);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
            Jackson2JsonRedisSerializer<CacheListenerMessage> serializer = new Jackson2JsonRedisSerializer<>(mapper, CacheListenerMessage.class);

            RedisSerializer<?> stringSerializer = new StringRedisSerializer();
            template.setKeySerializer(stringSerializer);
            template.setValueSerializer(serializer);
            template.setHashKeySerializer(stringSerializer);
            template.setHashValueSerializer(serializer);
            template.afterPropertiesSet();
            return template;
        }
    }

    @ConditionalOnBean(VenusInitializer.class)
    @DependsOn("venusInitializer")
    @Bean
    public VenusMultiLevelCacheManager venusMultiLevelCacheManager(RedisTemplate<String, CacheWrapper> template, VenusMultiLevelCacheProperties properties) {
        return new VenusMultiLevelCacheManager(properties, template);
    }

    @Configuration(proxyBeanMethods = false)
    static class MessageListenerAutoConfiguration {

        @Bean
        public RedisMessageReceiver redisMessageReceiver(VenusMultiLevelCacheManager manager, RedisTemplate<String, CacheListenerMessage> template) {
            return new RedisMessageReceiver(template, manager);
        }

        @Bean
        public MessageListenerAdapter adapter(RedisMessageReceiver receiver){
            return new MessageListenerAdapter(receiver,"receive");
        }

        @Bean
        public RedisMessageListenerContainer container(MessageListenerAdapter listenerAdapter,
                                                RedisConnectionFactory redisConnectionFactory){
            RedisMessageListenerContainer container = new RedisMessageListenerContainer();
            container.setConnectionFactory(redisConnectionFactory);
            container.addMessageListener(listenerAdapter, new PatternTopic(DEFAULT_LISTENER_NAME));
            return container;
        }
    }

    @Bean
    public MultiLevelCacheAspect multiLevelCacheAspect(VenusMultiLevelCacheManager manager, @Autowired Callback callback) {
        return new MultiLevelCacheAspect(manager, callback);
    }
}
