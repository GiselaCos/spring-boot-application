package com.toggle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;

@SpringBootApplication
public class ToggleApplication {

    public static void main(String[] args) throws Exception {

        SpringApplication.run(ToggleApplication.class, args);
    }

}