package com.oriol.customermagnet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class CustomermagnetApplication {
	private static final int STRENGTH = 12;
	@Value("${spring.mail.username}")
	private static String username;

	@Value("${spring.mail.password}")
	private static String password;

	public static void main(String[] args) {
		System.out.println("********* USERNAME: "+username);
		System.out.println("********* PASSWORD: "+password);
		SpringApplication.run(CustomermagnetApplication.class, args);

	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(STRENGTH);
	}

}
