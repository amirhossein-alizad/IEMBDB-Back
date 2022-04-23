package com.iemdb;

import com.iemdb.Entity.User;
import com.iemdb.model.IEMovieDataBase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class IemdbApplication {

    public static void main(String[] args) {
        SpringApplication.run(IemdbApplication.class, args);
    }

//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/login").allowedOrigins("*");
//            }
//        };
//    }

}
