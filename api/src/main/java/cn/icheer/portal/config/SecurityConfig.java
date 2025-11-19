package cn.icheer.portal.config;

import cn.icheer.portal.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

/**
 * @author fangweihao
 * @since 2025/7/7 13:15
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 登录成功失败跳转交给前端
        http
                // 关闭CSRF保护
                .csrf().disable()
                // 拦截配置
                .authorizeRequests()
                // 必须放在最前：放行所有 OPTIONS 浏览器预检请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/open/**").permitAll() // 公开访问
                .antMatchers("/portal/**").authenticated() // 需认证
                .anyRequest().authenticated()
                .and()
                // 跨域配置
                .cors().configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedHeader("*");
                    config.addAllowedMethod("*");
                    config.addAllowedOriginPattern("*");
                    config.setAllowCredentials(true);
                    return config;
                })
                .and()
//                // 登出配置 已在UserController里实现
//                .logout()
//                .logoutUrl("/open/logout")
//                .logoutSuccessUrl("/login")
//                .clearAuthentication(true) // 清除认证信息
//                .invalidateHttpSession(true) // 登出时使session失效
//                .permitAll()
//                .and()
                // 禁用 Session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 禁用 Session 创建
                .and()
                // 添加JWT过滤器
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
