package com.github.verhage.mursyabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class MursyabotApplication {
	public static void main(String[] args) {
		SpringApplication.run(MursyabotApplication.class, args);
	}
}
