package com.openai36.aggregation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.logging.LogLevel;
import jakarta.inject.Inject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.ReactorNettyClientRequestFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class WebClientConfig {
    @Inject
    private ObjectMapper objectMapper;

    @Bean @Lazy
    public WebClient webClient() {
        HttpClient httpClient = HttpClient
                .create()
                .wiretap("com.openai36.aggregation.WebClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
        ClientHttpConnector conn = new ReactorClientHttpConnector(httpClient);
        return WebClient
                .builder()
                .clientConnector(conn)
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                    clientCodecConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                })
                .build();
    }

    @Bean @Lazy
    public RestClient restClient() {
        HttpClient httpClient = HttpClient
                .create()
                .wiretap("com.openai36.aggregation.RestClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
        return RestClient.builder()
                .requestFactory(new ReactorNettyClientRequestFactory(httpClient))
                .build();
    }
}
