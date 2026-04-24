package fr.recia.restriction_rentree_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class RestrictionRentreeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestrictionRentreeApiApplication.class, args);
	}

}
