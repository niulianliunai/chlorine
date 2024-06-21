package com.chlorine.app.packageSource;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableJpaRepositories
@EnableJpaAuditing
@EnableSwagger2
@EnableEncryptableProperties
@SpringBootApplication(scanBasePackages = {"com.chlorine"})
public class PackageSourceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PackageSourceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        ClassGeneratorUtil.generate("/Users/chenlong/mine/mall/word-system/src/main/java/com/cl/chlorine","com.cl.chlorine","com.cl.chlorine");
    }
}
