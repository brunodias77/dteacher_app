package com.dias.dteacher;

import org.springframework.boot.SpringApplication;

public class TestDteacherBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(Startup::main).with(TestcontainersConfiguration.class).run(args);
	}

}
