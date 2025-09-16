package com.eguisse.demospring;

import io.micrometer.core.annotation.Timed;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Hashtable;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class DemoSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringApplication.class, args);
    }

    // setup Logger
    private static final Logger logger = LoggerFactory.getLogger(DemoSpringApplication.class);


    /**
     *  Disable the Spring Security default login form
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }



    /**
     *  Just a test endpoint to verify the application is running
     *   Accessible at http://localhost:8080/api/hello
     *  Return JSON format.
     * @param message: message to return, default is "Hello, World!"
     * @return message

     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping(path="/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed(value = "api.param.time", description = "Time taken to return param")
    public String hello(@RequestParam(name = "message", required = false, defaultValue = "Hello, World!") String message) {
        logger.debug("hello begin");

        return message;
    }

}
