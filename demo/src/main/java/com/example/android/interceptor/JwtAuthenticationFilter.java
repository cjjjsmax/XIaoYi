package com.example.android.interceptor;

import com.example.android.common.Result;
import com.example.android.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

//JWT认证过滤器
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    //核心过滤方法
    @Override
    protected void doFilterInternal(HttpServletRequest request,//HTTP 请求对象
                                    HttpServletResponse response,//HTTP 响应对象
                                    FilterChain filterChain) throws ServletException, IOException {//过滤器链

        String requestUri = request.getRequestURI();

        //公开接口列表
        String[] publicPaths = {
            "/api/users/login",
            "/api/users/register",
            "/api/products/list",
            "/api/products/search",
            "/api/products/detail",
            "/api/purchase-requests/list",
            "/api/purchase-requests/detail",
            "/images/",
            "/uploads/"
        };

        //检查是否是公开接口
        boolean isPublicPath = false;
        for (String path : publicPaths) {
            if (requestUri.contains(path)) {
                isPublicPath = true;
                break;
            }
        }

        //公开接口直接放行
        if (isPublicPath) {
            filterChain.doFilter(request, response);
            return;
        }

        //检查Authorization头
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorizedResponse(response, "缺少认证令牌");
            return;
        }

        //验证Token
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            writeUnauthorizedResponse(response, "认证令牌无效或已过期");
            return;
        }

        //Token有效，设置SecurityContext
        io.jsonwebtoken.Claims claims = jwtUtil.parseToken(token);
        String username = claims.get("username", String.class);
        if (username == null) {
            username = claims.getSubject();
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //继续执行
        filterChain.doFilter(request, response);
    }

    //写入未授权响应
    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.unauthorized(message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}