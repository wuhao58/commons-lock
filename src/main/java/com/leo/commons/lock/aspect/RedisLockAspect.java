package com.leo.commons.lock.aspect;

import com.leo.commons.lock.LockService;
import com.leo.commons.lock.annotations.DistributedLock;
import com.leo.commons.lock.exceptions.LockFailNotifyException;
import com.leo.commons.lock.exceptions.LockFailUnNotifyException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author: LEO
 * @description: 分布式锁
 * @date: 2020-09-09 21:22
 */
@Slf4j
@Aspect
@AllArgsConstructor
public class RedisLockAspect {

    private LockService redisLockService;

    @Pointcut("@annotation(com.leo.commons.lock.annotations.DistributedLock)")
    public void lockPointCut() {

    }

    @Around("lockPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();
        DistributedLock redisLock = getLockAnnotation(method);

        String key = redisLock.key();
        String id = redisLock.id();
        String value = redisLock.value();
        long expire = redisLock.expire();
        long timeout = redisLock.timeout();

        String lockKey = getLockKey(key, id, method, args);
        String lockValue = getLockValue(value);
        boolean lock;
        if (timeout <= 0) {
            lock = redisLockService.lock(lockKey, lockValue, expire);
        } else {
            lock = redisLockService.tryLock(lockKey, lockValue, expire, timeout);
        }
        // 未获取到锁[指定不忽略异常]
        if (!lock) {
            log.error("tryLock_Error: lockKey={}, lockValue={}, expire={}, timeout={}", lockKey, lockValue, expire, timeout);
            if (redisLock.lockFailNotify()) {
                throw new LockFailNotifyException("获取锁失败，操作太频繁！");
            } else {
                throw new LockFailUnNotifyException("获取锁失败，操作太频繁！");
            }
        }
        try {
            return point.proceed();
        } finally {
            // 执行后解锁
            redisLockService.unlock(lockKey, lockValue);
        }
    }

    private DistributedLock getLockAnnotation(Method method) {
        DistributedLock redisLock = method.getAnnotation(DistributedLock.class);
        if (redisLock == null) {
            throw new IllegalStateException("加锁异常，无法从方法获取RedisLock注解！");
        }
        return redisLock;
    }

    private String getLockKey(String key, String id, Method method, Object[] args) {
        String result = String.valueOf(args[0]);
        if (!StringUtils.isEmpty(id)) {
            // EL表达式获取值#
            result = parseSpel(id, method, args);
        }
        if (StringUtils.isEmpty(result)) {
            throw new IllegalStateException(String.format("加锁异常，没有名为%s的参数！", key));
        }
        return key + result;
    }

    private String parseSpel(String key, Method method, Object[] args) {
        // 获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer discoverer =
                new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = discoverer.getParameterNames(method);

        // 使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        // SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 把方法参数放入SPEL上下文中
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        Expression expression = parser.parseExpression(key, new TemplateParserContext());
        return expression.getValue(context, String.class);
    }

    private String getLockValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return UUID.randomUUID().toString();
        }
        return value;
    }

}
