package org.leverx.ratingapp.config.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging administrative operations.
 * Captures method execution in the AdminController and logs relevant information.
 */
@Aspect
@Component
public class AdminLoggingAspect extends BaseLoggingAspect {

    @Override
    protected String getLogPrefix() {
        return "ADMIN";
    }

    /**
     * Aspect for logging administrative operations.
     * Captures method execution in the AdminController and logs relevant information.
     */
    @Pointcut("execution(* org.leverx.ratingapp.controllers.AdminController.*(..))")
    public void adminControllerMethods() {}

    /**
     * Logs before method execution in admin controllers.
     * @param joinPoint The join point representing the method execution
     */
    @Before("adminControllerMethods()")
    public void logBeforeAdminAction(JoinPoint joinPoint) {
        logMethodEntry(joinPoint);
    }

    /**
     * Logs after successful method execution in admin controllers.
     * @param joinPoint The join point representing the method execution
     * @param result The result returned by the method
     */
    @AfterReturning(pointcut = "adminControllerMethods()", returning = "result")
    public void logAfterAdminAction(JoinPoint joinPoint, Object result) {
        logMethodSuccess(joinPoint, result);
    }

    /**
     * Logs when methods in admin controllers throw exceptions.
     * @param joinPoint The join point representing the method execution
     * @param exception The exception thrown by the method
     */
    @AfterThrowing(pointcut = "adminControllerMethods()", throwing = "exception")
    public void logAfterAdminActionException(JoinPoint joinPoint, Exception exception) {
        logMethodException(joinPoint, exception);
    }

    /**
     * Measures and logs execution time of admin controller methods.
     * @param joinPoint The join point representing the method execution
     * @return The result of the method execution
     * @throws Throwable if the method execution fails
     */
    @Around("adminControllerMethods()")
    public Object logAdminActionExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint);
    }
}