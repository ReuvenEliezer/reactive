package com.example.reactive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.*;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class RsocketConfig {

    @Bean
    public RSocketRequester rSocketRequester(
            RSocketStrategies rSocketStrategies,
            @Value("${spring.rsocket.server.port}") int port) {
        return RSocketRequester.builder()
                .rsocketStrategies(rSocketStrategies)
                .rsocketConnector(
                        rSocketConnector -> rSocketConnector
                                .reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2)))
                                .keepAlive(Duration.ofSeconds(5), Duration.ofSeconds(30))
                )
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .tcp("localhost", port)
//                .websocket(URI.create("http://localhost:" + port))
                ;
    }

}
