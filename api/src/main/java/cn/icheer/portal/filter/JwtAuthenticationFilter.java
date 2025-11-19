package cn.icheer.portal.filter;

import cn.icheer.portal.details.JWTUserDetails;
import cn.icheer.portal.utils.JWTUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author fangweihao
 * @since 2025/7/7 13:32
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 因为重写方法后，security里的放行失效了，所以此处得重新放行
        String uri = request.getRequestURI();
        if (uri.startsWith("/open/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 获取请求头中的token
            String tokenFromHeader = extractTokenFromHeader(request);
            // 验证token
            if (tokenFromHeader != null && JWTUtils.verifyToken(tokenFromHeader)) {
                // 从JWT中获取用户信息
                JWTUserDetails jwtUserDetails = JWTUtils.getUserInfo(tokenFromHeader);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(jwtUserDetails, null, null);
                authentication.setDetails(request.getRemoteAddr());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // token无效 或 已过期
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中获取token
     *
     * @param request HttpServletRequest
     * @return String
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHead = request.getHeader("Authorization");
        if (authHead == null) {
            return null;
        }
        if (authHead.startsWith("Bearer ")) {
            authHead = authHead.substring(7);
            return authHead;
        }
        return authHead;
    }
}
