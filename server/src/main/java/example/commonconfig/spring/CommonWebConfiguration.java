package example.commonconfig.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * This would be a common configuration for all WARs to reference to bootstrap common Spring settings
 */
@Configuration
public class CommonWebConfiguration extends WebMvcConfigurationSupport {
}
