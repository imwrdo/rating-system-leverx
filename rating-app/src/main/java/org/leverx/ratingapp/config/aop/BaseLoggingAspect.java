package org.leverx.ratingapp.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Base abstract aspect that provides common logging functionality for all aspects.
 * Contains shared methods for logging method entries, successful executions, exceptions,
 * and execution time measurements.
 */
@Aspect
@Slf4j
@Component
public abstract class BaseLoggingAspect {
    /**
     * Returns the prefix used in log messages to identify the specific aspect.
     * @return String identifier for the log messages
     */
    protected abstract String getLogPrefix();

    /**
     * Extracts method information from a join point for consistent logging.
     * @param joinPoint The join point representing the method execution
     * @return Formatted string with class name and method name
     */
    protected String getMethodInfo(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        return className + " method " + methodName;
    }

    /**
     * Logs information when a method is entered.
     * @param joinPoint The join point representing the method execution
     */
    protected void logMethodEntry(JoinPoint joinPoint) {
        String methodInfo = getMethodInfo(joinPoint);
        Object[] args = joinPoint.getArgs();
        log.info("[{}] {} is being executed with parameters:\n {}",
                getLogPrefix(), methodInfo, Arrays.toString(args));
    }

    /**
     * Logs information when a method completes successfully.
     * @param joinPoint The join point representing the method execution
     * @param result The result returned by the method
     */
    protected void logMethodSuccess(JoinPoint joinPoint, Object result) {
        String methodInfo = getMethodInfo(joinPoint);
        log.info("[{}] {} executed successfully with result:\n {}",
                getLogPrefix(), methodInfo, result);
    }

    /**
     * Logs information when a method throws an exception.
     * @param joinPoint The join point representing the method execution
     * @param exception The exception thrown by the method
     */
    protected void logMethodException(JoinPoint joinPoint, Exception exception) {
        String methodInfo = getMethodInfo(joinPoint);
        log.error("[{}] {} failed with exception: {} \n- Message: {}",
                getLogPrefix(), methodInfo, exception.getClass().getSimpleName(), exception.getMessage());
    }

    /**
     * Measures and logs the execution time of a method.
     * @param joinPoint The join point representing the method execution
     * @return The result of the method execution
     * @throws Throwable if the method execution fails
     */
    protected Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodInfo = getMethodInfo(joinPoint);
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        log.info("[{}] {} execution time: {} ms",
                getLogPrefix(), methodInfo, executionTime);

        return result;
    }
}
