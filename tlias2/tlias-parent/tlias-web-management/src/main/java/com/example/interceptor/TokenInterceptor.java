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
        String url = request.getRequestURL().toString();
        log.info("拦截到请求: {}", url);

        if (url.contains("/login")) {
            log.info("登录请求，放行");
            return true;
        }

        String token = request.getHeader("token");
        log.info("获取到的令牌: {}", token);

        if (!StringUtils.hasLength(token)) {
            log.warn("请求头中缺少 token，拒绝访问");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        try {
            Claims claims = JwtUtils.parseJwt(token);
            Integer empId = (Integer) claims.get("id");
            CurrentHolder.setCurrentId(empId);
            log.info("令牌校验通过，放行");
            return true;
        } catch (Exception e) {
            log.warn("令牌解析失败，可能是过期或被篡改: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CurrentHolder.remove();
    }
}
