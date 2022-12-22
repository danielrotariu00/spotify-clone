package com.spotify.songcollection.config;

import com.spotify.songcollection.business.service.IdmClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class IdmConfiguration {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.spotify.idm");
        return marshaller;
    }

    @Bean
    public IdmClient idmClient(Jaxb2Marshaller marshaller) {
        IdmClient client = new IdmClient();
        client.setDefaultUri("http://localhost:8080/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

}