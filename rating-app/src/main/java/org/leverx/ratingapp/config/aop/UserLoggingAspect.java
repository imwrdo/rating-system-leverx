package org.leverx.ratingapp.config.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging user-related operations.
 * Captures method execution in UserController and logs relevant information.
 */
@Aspect
@Component
public class UserLoggingAspect extends BaseLoggingAspect{
    @Override
    protected String getLogPrefix() {
        return "USER";
    }

    /**
     * Pointcut for all methods in UserController.
     */
    @Pointcut("execution(* org.leverx.ratingapp.controllers.UserController.*(..))")
    public void userControllerMethods() {}

    /**
     * Logs before method execution in user controller.
     * @param joinPoint The join point representing the method execution
     */
    @Before("userControllerMethods()")
    public void logBeforeUserAction(JoinPoint joinPoint) {
        logMethodEntry(joinPoint);
    }

    /**
     * Logs after successful method execution in user controller.
     * @param joinPoint The join point representing the method execution
     * @param result The result returned by the method
     */
    @AfterReturning(pointcut = "userControllerMethods()", returning = "result")
    public void logAfterUserAction(JoinPoint joinPoint, Object result) {
        logMethodSuccess(joinPoint, result);
    }

    /**
     * Logs when methods in user controller throw exceptions.
     * @param joinPoint The join point representing the method execution
     * @param exception The exception thrown by the method
     */
    @AfterThrowing(pointcut = "userControllerMethods()", throwing = "exception")
    public void logAfterUserActionException(JoinPoint joinPoint, Exception exception) {
        logMethodException(joinPoint, exception);
    }

    /**
     * Measures and logs execution time of user controller methods.
     * @param joinPoint The join point representing the method execution
     * @return The result of the method execution
     * @throws Throwable if the method execution fails
     */
    @Around("userControllerMethods()")
    public Object logUserActionExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint);
    }
}
