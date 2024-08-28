package org.venus.cache;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
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

@Slf4j
@Aspect
@AllArgsConstructor
public class MultiLevelCacheAspect {

    private VenusMultiLevelCacheManager manager;
    private Callback callback;

    @Pointcut("@annotation(org.venus.cache.VenusMultiLevelCache)")
    public void cacheAspect() {
    }

    @SuppressWarnings("ConstantConditions")
    @Around("cacheAspect()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        String[] paramNames = signature.getParameterNames();
        Object[] args = point.getArgs();
        TreeMap<String, Object> treeMap = new TreeMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            treeMap.put(paramNames[i],args[i]);
        }

        VenusMultiLevelCache annotation = method.getAnnotation(VenusMultiLevelCache.class);
        String elResult = parse(annotation.key(), treeMap);
        String realKey = annotation.cacheName() + ":" + elResult;
        Cache cache = manager.getCache(DEFAULT_LISTENER_NAME);

        if (annotation.type() == MultiLevelCacheType.PUT){
            Object object = point.proceed();
            cache.put(realKey, object);
            callback.callback(realKey, object, "update");
            return object;
        }

        if (annotation.type()== MultiLevelCacheType.EVICT){
            cache.evict(realKey);
            callback.callback(realKey, null, "evict");
            return point.proceed();
        }

        // MultiLevelCacheType: ALL
        return cache.get(realKey, () -> {
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

    public static String parse(String elString, TreeMap<String,Object> map){
        elString=String.format("#{%s}",elString);
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        map.forEach(context::setVariable);
        Expression expression = parser.parseExpression(elString, new TemplateParserContext());
        return expression.getValue(context, String.class);
    }
}