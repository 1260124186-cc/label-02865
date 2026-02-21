package com.phonemall.aspect;

import com.phonemall.entity.OperationLog;
import com.phonemall.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;

    @Pointcut("execution(* com.phonemall.controller.*.*(..)) && " +
              "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void logPointcut() {}

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = point.proceed();
        long cost = System.currentTimeMillis() - start;

        try {
            saveLog(point, cost);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
        return result;
    }

    private void saveLog(ProceedingJoinPoint point, long cost) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = point.getSignature().getName();

        OperationLog opLog = new OperationLog();
        Object userId = request.getAttribute("userId");
        if (userId != null) {
            opLog.setUserId((Long) userId);
        }
        Object username = request.getAttribute("username");
        if (username != null) {
            opLog.setUsername((String) username);
        }
        opLog.setModule(className.replace("Controller", ""));
        opLog.setAction(methodName);
        opLog.setDetail(request.getMethod() + " " + request.getRequestURI() + " [" + cost + "ms]");
        opLog.setIp(getIpAddr(request));
        opLog.setCreateTime(LocalDateTime.now());

        operationLogMapper.insert(opLog);
        log.info("操作日志: {} - {} - {} - {}ms", opLog.getUsername(), opLog.getModule(), opLog.getAction(), cost);
    }

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
