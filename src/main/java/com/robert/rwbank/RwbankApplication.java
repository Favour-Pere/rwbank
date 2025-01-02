package com.robert.rwbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;



@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(
		title = "RWBANK",
		description = "A Demo Banking application that simulates real world banking activites like debit, credit, transfer and genration of statements",
		version = "v1.0",
		contact = @Contact(
			name = "Robert-Wilson Peremobowei",
			email = "peremoboweirobert@gmail.com",
			url = "localhost:8080.com"
		),
		license = @License(
			name = "Favour-Pere",
			url = "localhost:4343.com"
		)
	), 
	externalDocs = @ExternalDocumentation(
		description = "The Java Academy Bank Application Documentation",
		url = "https://github.com/Musdon/tja_bank_app"
	)
)
public class RwbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(RwbankApplication.class, args);
	}

}
