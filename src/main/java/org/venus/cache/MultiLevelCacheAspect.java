package org.venus.cache;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.TreeMap;

import static org.venus.cache.VenusMultiLevelCacheConstants.DEFAULT_LISTENER_NAME;
import static org.venus.cache.VenusMultiLevelCacheConstants.VENUS_REDIRECT_CACHE_NAME;

/**
 * Aspect for handling multi-level caching functionality using VenusMultiLevelCache annotation.
 * This aspect intercepts methods annotated with @VenusMultiLevelCache and manages cache operations
 * such as PUT, EVICT, and fetching cache entries.
 *
 * This class is annotated with @Slf4j for logging and @AllArgsConstructor for automatic generation
 * of a constructor with all required arguments.
 */
@Slf4j
@Aspect
@AllArgsConstructor
public class MultiLevelCacheAspect {

    /**
     * The VenusMultiLevelCacheManager is responsible for handling multi-level cache operations.
     * It integrates both local and remote caching systems and facilitates cache management tasks
     * such as retrieving caches based on the configured properties.
     */
    private VenusMultiLevelCacheManager manager;
    /**
     * The Callback instance used to handle multi-level cache events.
     * This variable will manage the custom actions that should be performed
     * when cache-related operations such as updates or evictions occur.
     */
    private Callback callback;

    /**
     * Pointcut that matches methods annotated with @VenusMultiLevelCache.
     * This pointcut is used to intercept these annotated methods for handling
     * multi-level caching operations, which include actions such as
     * putting, evicting, and fetching cache entries.
     */
    @Pointcut("@annotation(org.venus.cache.VenusMultiLevelCache)")
    public void cacheAspect() {
    }

    /**
     * Handles multi-level caching logic around methods annotated with @VenusMultiLevelCache.
     * This method intercepts the annotated method, performs cache operations (PUT, EVICT, ALL),
     * and either updates, evicts, or retrieves from the cache based on the annotation configuration.
     *
     * @param point ProceedingJoinPoint representing the join point for advice.
     *              It provides reflective access to both the state available at a join point and static information about it.
     * @return The result of the method execution, either from the cache or the method itself.
     * @throws Throwable If any error occurs during method execution.
     */
    @SuppressWarnings("ConstantConditions")
    @Around("cacheAspect()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        String[] paramNames = signature.getParameterNames();
        Object[] args = point.getArgs();
        TreeMap<String, Object> treeMap = new TreeMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            treeMap.put(paramNames[i], args[i]);
        }

        VenusMultiLevelCache annotation = method.getAnnotation(VenusMultiLevelCache.class);
        String elResult = parse(annotation.key(), treeMap);
        Cache cache = manager.getCache(VENUS_REDIRECT_CACHE_NAME);

        if (annotation.type() == MultiLevelCacheType.PUT) {
            Object object = point.proceed();
            cache.put(elResult, object);
            callback.callback(elResult, object, "update");
            return object;
        }

        if (annotation.type() == MultiLevelCacheType.EVICT) {
            cache.evict(elResult);
            callback.callback(elResult, null, "evict");
            return point.proceed();
        }

        // MultiLevelCacheType: ALL
        return cache.get(elResult, () -> {
            try {
                return point.proceed();
            } catch (Throwable e) {
                if (log.isErrorEnabled()) {
                    log.error("Multi-level cache handle failure", e);
                }
                return null;
            }
        });
    }

    /**
     * Parses the given string as a SpEL (Spring Expression Language) expression using the provided map
     * to populate the context variables. The method uses the TemplateParserContext to evaluate the expression.
     *
     * @param elString The string to be parsed as a SpEL expression.
     * @param map A map containing variable names and their corresponding values to be used in
     *  the expression context.
     * @return The string result of evaluating the expression with the provided context.
     */
    public static String parse(String elString, TreeMap<String, Object> map) {
        elString = String.format("#{%s}", elString);
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        map.forEach(context::setVariable);
        Expression expression = parser.parseExpression(elString, new TemplateParserContext());
        return expression.getValue(context, String.class);
    }
}
