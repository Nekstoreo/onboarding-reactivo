package co.com.bancolombia.onboarding.config;

import org.reactivecommons.utils.ObjectMapper;
import org.reactivecommons.utils.ObjectMapperImp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapperImp();
    }

    @Bean
    public com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper() {
        return new com.fasterxml.jackson.databind.ObjectMapper();
    }

}
