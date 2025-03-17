package org.leverx.ratingapp.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging game object operations.
 * Captures method execution in GameObjectController and logs relevant information.
 */
@Aspect
@Slf4j
@Component
public class GameObjectLoggingAspect extends BaseLoggingAspect {

    @Override
    protected String getLogPrefix() {
        return "GameObject";
    }

    /**
     * Pointcut for all methods in GameObjectController.
     */
    @Pointcut("execution(* org.leverx.ratingapp.controllers.GameObjectController.*(..))")
    public void gameObjectControllerMethods() {}

    /**
     * Logs before method execution in game object controller.
     * @param joinPoint The join point representing the method execution
     */
    @Before("gameObjectControllerMethods()")
    public void logBeforeGameObjectAction(JoinPoint joinPoint) {
        logMethodEntry(joinPoint);
    }

    /**
     * Logs after successful method execution in game object controller.
     * @param joinPoint The join point representing the method execution
     * @param result The result returned by the method
     */
    @AfterReturning(pointcut = "gameObjectControllerMethods()", returning = "result")
    public void logAfterGameObjectAction(JoinPoint joinPoint, Object result) {
        logMethodSuccess(joinPoint, result);
    }

    /**
     * Logs when methods in game object controller throw exceptions.
     * @param joinPoint The join point representing the method execution
     * @param exception The exception thrown by the method
     */
    @AfterThrowing(pointcut = "gameObjectControllerMethods()", throwing = "exception")
    public void logAfterGameObjectActionException(JoinPoint joinPoint, Exception exception) {
        logMethodException(joinPoint, exception);
    }

    /**
     * Measures and logs execution time of game object controller methods.
     * @param joinPoint The join point representing the method execution
     * @return The result of the method execution
     * @throws Throwable if the method execution fails
     */
    @Around("gameObjectControllerMethods()")
    public Object logGameObjectActionExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint);
    }
}