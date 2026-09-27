package com.example.springbootlearning.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.example.springbootlearning.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    
    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN
        );

        response.setContentType(
                "application/json;charset=UTF-8"
        );

        // response.getWriter().write(
        //         """
        //         {
        //             "code": 403,
        //             "message": "没有权限访问该资源",
        //             "data": null
        //         }
        //         """
        // );
        Result<Void> result =
                Result.error(
                        403,
                        "没有权限访问该资源"
                );

        response.getWriter().write(
                objectMapper.writeValueAsString(result)
        );
    }
}
