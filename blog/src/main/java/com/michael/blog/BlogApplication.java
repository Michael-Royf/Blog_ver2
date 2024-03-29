package com.michael.blog;


import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@OpenAPIDefinition(
        info = @Info(
                title = "Spring Boot Blog App REST APIs",
                description = "Spring Boot Blog App REST APIs Documentation",
                version = "v2.0",
                contact = @Contact(
                        name = "Michael Royf",
                        email = "michael.royf87@gmail.com",
                        url = "https://www.linkedin.com/in/michael-royf-13509617a/"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.linkedin.com/in/michael-royf-13509617a/"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Spring Boot Blog App Documentation",
                url = "https://github.com/Michael-Royf/Blog_ver2"
        )
)
@SpringBootApplication
@EnableCaching
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
