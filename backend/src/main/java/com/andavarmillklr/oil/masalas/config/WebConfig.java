package com.andavarmillklr.oil.masalas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/product-images/**")
                .addResourceLocations("file:./product-images/", "file:./backend/product-images/", "file:../product-images/");
    }
}
