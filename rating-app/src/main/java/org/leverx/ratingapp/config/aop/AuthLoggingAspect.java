package org.leverx.ratingapp.config.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging authentication operations.
 * Captures method execution in auth services and logs relevant information.
 */
@Aspect
@Component
public class AuthLoggingAspect extends BaseLoggingAspect {

    @Override
    protected String getLogPrefix() {
        return "AUTH";
    }

    /**
     * Aspect for logging authentication operations.
     * Captures method execution in auth services and logs relevant information.
     */
    @Pointcut("execution(* org.leverx.ratingapp.services.auth.*.*(..))")
    public void authServiceMethods() {}

    /**
     * Logs before method execution in auth services.
     * @param joinPoint The join point representing the method execution
     */
    @Before("authServiceMethods()")
    public void logBeforeAuthAction(JoinPoint joinPoint) {
        logMethodEntry(joinPoint);
    }

    /**
     * Logs after successful method execution in auth services.
     * @param joinPoint The join point representing the method execution
     * @param result The result returned by the method
     */
    @AfterReturning(pointcut = "authServiceMethods()", returning = "result")
    public void logAfterAuthAction(JoinPoint joinPoint, Object result) {
        logMethodSuccess(joinPoint, result);
    }

    /**
     * Logs when methods in auth services throw exceptions.
     * @param joinPoint The join point representing the method execution
     * @param exception The exception thrown by the method
     */
    @AfterThrowing(pointcut = "authServiceMethods()", throwing = "exception")
    public void logAfterAuthActionException(JoinPoint joinPoint, Exception exception) {
        logMethodException(joinPoint, exception);
    }

    /**
     * Measures and logs execution time of auth service methods.
     * @param joinPoint The join point representing the method execution
     * @return The result of the method execution
     * @throws Throwable if the method execution fails
     */
    @Around("authServiceMethods()")
    public Object logUserActionExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint);
    }
}