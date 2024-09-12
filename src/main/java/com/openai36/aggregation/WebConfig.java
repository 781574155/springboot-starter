package com.openai36.aggregation;

import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins(
                        "http://127.0.0.1:1002",
                        "http://localhost:1002",
                        "https://www.openai36.com",
                        "https://openai36.com",
                        "http://www.openai36.com",
                        "http://openai36.com");
    }

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(true);
//        filter.setHeaderPredicate(); TODO authorization头应该不打印
        filter.setMaxPayloadLength(10000);
        filter.setBeforeMessagePrefix("~~请求内容(处理前): [");
        filter.setBeforeMessageSuffix("]~~");
        filter.setAfterMessagePrefix("~~请求内容(处理后): [");
        filter.setAfterMessageSuffix("]~~");
        return filter;
    }

    @Bean
    public InMemoryHttpExchangeRepository createTraceRepository() {
        return new InMemoryHttpExchangeRepository();
    }
}
