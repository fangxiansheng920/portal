package cn.icheer.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author fangweihao
 * @since 2025/11/19 22:15
 */
@Configuration
public class UploadConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatar/**") // 虚拟访问路径
                .addResourceLocations("file:C:/Users/28117/Desktop/作业/pictures/avatar/");     // 虚拟路径对应磁盘路径

        registry.addResourceHandler("/website/**") // 虚拟访问路径
                .addResourceLocations("file:C:/Users/28117/Desktop/作业/pictures/website/");     // 虚拟路径对应磁盘路径
    }
}
