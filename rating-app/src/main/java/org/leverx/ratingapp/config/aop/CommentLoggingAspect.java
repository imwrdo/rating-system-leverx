package org.leverx.ratingapp.config.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging comment-related operations.
 * Captures method execution in comment services, pending comment services,
 * and comment controllers, then logs relevant information.
 */
@Aspect
@Component
public class CommentLoggingAspect extends BaseLoggingAspect {

    @Override
    protected String getLogPrefix() {
        return "COMMENT";
    }

    /**
     * Pointcut for all methods in comment services.
     */
    @Pointcut("execution(* org.leverx.ratingapp.services.comment.*.*(..))")
    public void commentServiceMethods() {}

    /**
     * Pointcut for all methods in pending comment services.
     */
    @Pointcut("execution(* org.leverx.ratingapp.services.pendingcomment.*.*(..))")
    public void pendingCommentServiceMethods() {}

    /**
     * Pointcut for all methods in comment controllers.
     */
    @Pointcut("execution(* org.leverx.ratingapp.controllers.CommentController.*(..))")
    public void commentControllerMethods() {}

    /**
     * Logs before method execution in comment-related components.
     * @param joinPoint The join point representing the method execution
     */
    @Before("commentServiceMethods() || commentControllerMethods() || pendingCommentServiceMethods()")
    public void logBeforeCommentAction(JoinPoint joinPoint) {
        logMethodEntry(joinPoint);
    }

    /**
     * Logs after successful method execution in comment-related components.
     * @param joinPoint The join point representing the method execution
     * @param result The result returned by the method
     */
    @AfterReturning(pointcut = "commentServiceMethods() || commentControllerMethods() || pendingCommentServiceMethods()", returning = "result")
    public void logAfterCommentAction(JoinPoint joinPoint, Object result) {
        logMethodSuccess(joinPoint, result);
    }

    /**
     * Logs when methods in comment-related components throw exceptions.
     * @param joinPoint The join point representing the method execution
     * @param exception The exception thrown by the method
     */
    @AfterThrowing(pointcut = "commentServiceMethods() || commentControllerMethods() || pendingCommentServiceMethods()", throwing = "exception")
    public void logAfterCommentActionException(JoinPoint joinPoint, Exception exception) {
        logMethodException(joinPoint, exception);
    }

    /**
     * Measures and logs execution time of comment-related methods.
     * @param joinPoint The join point representing the method execution
     * @return The result of the method execution
     * @throws Throwable if the method execution fails
     */
    @Around("commentServiceMethods() || commentControllerMethods() || pendingCommentServiceMethods()")
    public Object logCommentActionExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint);
    }
}