package com.techmate.techmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.techmate.techmate.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class TechmateApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechmateApplication.class, args);
	}

}
