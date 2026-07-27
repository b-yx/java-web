package com.example.interceptor;

import com.example.utils.CurrentHolder;
import com.example.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取请求路径
        String url = request.getRequestURL().toString();
        log.info("拦截到请求: {}", url);

        // 2. 如果是登录请求，直接放行（不拦截登录接口）
        if (url.contains("/login")) {
            log.info("登录请求，放行");
            return true;
        }

        // 3. 从请求头中获取 token（前端会把令牌放在名为 "token" 的请求头里）
        String token = request.getHeader("token");
        log.info("获取到的令牌: {}", token);

        // 4. 判断 token 是否存在
        if (!StringUtils.hasLength(token)) {
            log.warn("请求头中缺少 token，拒绝访问");
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 返回 401 状态码
            return false; // 不放行
        }

        // 5. 尝试解析 token
        try {
//            JwtUtils.parseJwt(token);
//            log.info("令牌校验通过，放行");
//            return true; // 解析成功，放行
            Claims claims = JwtUtils.parseJwt(token);
            Integer empId = (Integer) claims.get("id");  // 从 JWT 中取出 id

            // ======== 新增：存入 ThreadLocal ========
            CurrentHolder.setCurrentId(empId);
            // ======================================

            log.info("令牌校验通过，放行");
            return true;
        } catch (Exception e) {
            log.warn("令牌解析失败，可能是过期或被篡改: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false; // 不放行
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求处理完毕，清除 ThreadLocal
        CurrentHolder.remove();
    }
}
