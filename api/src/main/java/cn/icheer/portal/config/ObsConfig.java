package cn.icheer.portal.config;

import cn.icheer.portal.utils.obs.OBSProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObsConfig {

    @Bean
    @ConfigurationProperties(prefix = "obs")
    public OBSProperties obsProperties() {
        return new OBSProperties();
    }
}
