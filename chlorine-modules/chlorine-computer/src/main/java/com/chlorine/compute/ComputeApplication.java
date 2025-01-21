package com.chlorine.compute;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.chlorine"})
public class ComputeApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ComputeApplication.class, args);
    }
}
