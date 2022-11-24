package tw.com.rex.sse.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        // 處理 CORS
        corsRegistry.addMapping("/**").allowedMethods("*");
    }

}
