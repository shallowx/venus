package org.venus.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;

import static org.venus.cache.MultiLevelCacheConstants.DEFAULT_LISTENER_NAME;

/**
 * Auto-configuration class for Venus Multi-Level Cache.
 * This class sets up the necessary beans and configurations to enable and manage a multi-level caching system that integrates local and remote cache layers.
 */
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MultiLevelCacheProperties.class)
public class MultiLevelCacheAutoConfiguration {

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

    /**
     * Creates and configures a VenusMultiLevelCacheManager bean.
     *
     * @param template the RedisTemplate used for operations on the remote cache
     * @param properties the properties used to configure the multi-level caching system
     * @return an instance of VenusMultiLevelCacheManager configured with the specified properties and template
     */
    @ConditionalOnBean(Initializer.class)
    @DependsOn("venusInitializer")
    @Bean
    public MultiLevelCacheManager venusMultiLevelCacheManager(RedisTemplate<String, CacheWrapper> template, MultiLevelCacheProperties properties) {
        return new MultiLevelCacheManager(properties, template);
    }

    @Configuration(proxyBeanMethods = false)
    static class MessageListenerAutoConfiguration {
        @Bean
        public RedisMessageReceiver redisMessageReceiver(MultiLevelCacheManager manager, RedisTemplate<String, CacheListenerMessage> template) {
            return new RedisMessageReceiver(template, manager);
        }

        /**
         * Creates a MessageListenerAdapter to handle Redis messages for cache synchronization.
         * The adapter is configured to use a specific method in the RedisMessageReceiver class annotated
         * with @CacheMessageListener for handling incoming messages.
         *
         * @param receiver The RedisMessageReceiver instance that contains the method for handling messages.
         * @return A configured MessageListenerAdapter based on the annotated method in the RedisMessageReceiver.
         */
        @Bean
        public MessageListenerAdapter adapter(RedisMessageReceiver receiver) {
            Class<RedisMessageReceiver> clz = RedisMessageReceiver.class;
            Method[] methods = clz.getDeclaredMethods();
            Method defaultListenerMethod = null;
            for (Method method : methods) {
                CacheMessageListener annotation = AnnotationUtils.findAnnotation(method, CacheMessageListener.class);
                if (annotation != null) {
                    defaultListenerMethod = method;
                    break;
                }
            }

            if (defaultListenerMethod == null) {
                return new MessageListenerAdapter();
            }
            String methodName = defaultListenerMethod.getName();
            return new MessageListenerAdapter(receiver, methodName);
        }

        /**
         * Creates and configures a RedisMessageListenerContainer bean.
         *
         * @param listenerAdapter the MessageListenerAdapter used for listening to Redis messages
         * @param redisConnectionFactory the RedisConnectionFactory used to establish connections to the Redis server
         * @return a configured instance of RedisMessageListenerContainer
         */
        @Bean
        public RedisMessageListenerContainer container(MessageListenerAdapter listenerAdapter,
                                                       RedisConnectionFactory redisConnectionFactory) {
            RedisMessageListenerContainer container = new RedisMessageListenerContainer();
            container.setConnectionFactory(redisConnectionFactory);
            container.addMessageListener(listenerAdapter, new PatternTopic(DEFAULT_LISTENER_NAME));
            return container;
        }
    }

    /**
     * Creates a new instance of MultiLevelCacheAspect.
     *
     * @param manager The VenusMultiLevelCacheManager responsible for handling multi-level cache operations.
     * @param callback The Callback instance used to handle multi-level cache events such as updates or evictions.
     * @return A new MultiLevelCacheAspect instance configured with the specified manager and callback.
     */
    @Bean
    public MultiLevelCacheAspect multiLevelCacheAspect(MultiLevelCacheManager manager, @Autowired Callback callback) {
        return new MultiLevelCacheAspect(manager, callback);
    }
}
