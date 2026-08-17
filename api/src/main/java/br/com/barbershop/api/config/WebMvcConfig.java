package br.com.barbershop.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Configuração do Spring MVC: CORS global para APIs e mapeamento de arquivos estáticos.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapeia os arquivos estáticos do módulo web caso estejam no caminho do workspace
        File webDir = new File("../web");
        if (!webDir.exists()) {
            webDir = new File("web");
        }
        if (webDir.exists()) {
            registry.addResourceHandler("/**")
                    .addResourceLocations("file:" + webDir.getAbsolutePath() + "/");
        } else {
            registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/static/");
        }
    }
}
