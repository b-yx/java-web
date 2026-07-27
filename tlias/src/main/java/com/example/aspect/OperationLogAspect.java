package com.example.aspect;

import com.example.anno.LogOperation;
import com.example.mapper.OperateLogMapper;
import com.example.pojo.OperateLog;
import com.example.utils.CurrentHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect        // 标识这是一个切面类
@Component     // 交给 Spring 管理
public class OperationLogAspect {

    @Autowired
    private OperateLogMapper operateLogMapper;

    /**
     * 环绕通知：拦截所有加了 @LogOperation 注解的方法
     */
    @Around("@annotation(logOperation)")
    public Object recordLog(ProceedingJoinPoint joinPoint, LogOperation logOperation) throws Throwable {
        // 1. 记录开始时间
        long startTime = System.currentTimeMillis();

        // 2. 执行原始方法（调用 Controller 的业务方法）
        Object result = joinPoint.proceed();

        // 3. 记录结束时间，计算耗时
        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;

        // 4. 从 ThreadLocal 中获取当前登录员工 ID
        Integer currentEmpId = CurrentHolder.getCurrentId();

        // 5. 构建日志对象
        OperateLog operateLog = new OperateLog();
        operateLog.setOperateEmpId(currentEmpId);
        operateLog.setOperateTime(LocalDateTime.now());
        operateLog.setClassName(joinPoint.getTarget().getClass().getName());  // 类名
        operateLog.setMethodName(joinPoint.getSignature().getName());        // 方法名
        operateLog.setMethodParams(Arrays.toString(joinPoint.getArgs()));    // 参数
        operateLog.setReturnValue(result != null ? result.toString() : null); // 返回值
        operateLog.setCostTime(costTime);

        // 6. 保存日志到数据库
        operateLogMapper.insert(operateLog);

        // 7. 返回原始方法的执行结果
        return result;
    }
}
