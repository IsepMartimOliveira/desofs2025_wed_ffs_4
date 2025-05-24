package com.example.psoft_22_23_project.configuration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut for all public methods in DashboardController
    @Pointcut("execution(public * com.example.psoft_22_23_project.dashboardmanagement.api.DashboardController.*(..))")
    public void dashboardControllerPublicMethods() {}

    @Around("dashboardControllerPublicMethods()")
    public Object logAroundDashboardMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // Log user identifier for non-repudiation (only id, not email)
        String userIdRaw = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "ANONYMOUS";
        String userId = userIdRaw.contains(",") ? userIdRaw.split(",")[0] : userIdRaw;

        logger.info("UserId: [{}], Entering endpoint: [{}], Method: [{}], URI: [{}], Parameters: [{}]",
                userId,
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                request.getRequestURI(),
                Arrays.toString(joinPoint.getArgs()));

        Object result;
        try {
            result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("UserId: [{}], Exiting endpoint: [{}], Method: [{}], URI: [{}], ResultType: [{}], Execution time: [{}ms]",
                    userId,
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    request.getRequestURI(),
                    (result != null ? result.getClass().getSimpleName() : "null"),
                    elapsedTime);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("UserId: [{}], Illegal argument values: [{}] in endpoint: [{}], Method: [{}], URI: [{}]",
                    userId,
                    Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    request.getRequestURI(), e);
            throw e;
        } catch (Exception e) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.error("UserId: [{}], Exception in endpoint: [{}], Method: [{}], URI: [{}], Parameters: [{}], Execution time: [{}ms], Exception: [{}]",
                    userId,
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    request.getRequestURI(),
                    Arrays.toString(joinPoint.getArgs()),
                    elapsedTime,
                    e.getMessage(), e);
            throw e;
        }
    }
}
