package com.example.data;

import com.example.data.utils.SM4Util;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication//(scanBasePackages = "com.example.data.*")
public class DataApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DataApplication.class, args);
    }

}
