package com.xclone.xclone.commons.configuration

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableConfigurationProperties(CorsProps::class)
class WebConfig(private val corsProps: CorsProps) : WebMvcConfigurer {

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/api/**")
                    .allowedOriginPatterns(*corsProps.origins.toTypedArray())
                    .allowedMethods("*")
                    .allowedHeaders("*")
                    .allowCredentials(true)
            }
        }
    }
}