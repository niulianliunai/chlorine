package com.chlorine.base.auth;

import com.chlorine.base.mvc.util.ClassGeneratorUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableJpaRepositories
@EnableJpaAuditing
@EnableSwagger2
@SpringBootApplication
public class MallApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MallApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
