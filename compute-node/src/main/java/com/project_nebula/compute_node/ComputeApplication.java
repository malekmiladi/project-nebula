package com.project_nebula.compute_node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ComputeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComputeApplication.class, args);
	}

}
