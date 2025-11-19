package cn.icheer.portal.details;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * @author fangweihao
 * @since 2025/7/15 8:56
 */
@Data
public class JWTUserDetails implements UserDetails {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户手机号
     */
    private String phoneNumber;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户创建时间
     */
    private LocalDateTime createTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
