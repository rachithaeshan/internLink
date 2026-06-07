package com.internlink.internlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InternlinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(InternlinkApplication.class, args);
	}

}
