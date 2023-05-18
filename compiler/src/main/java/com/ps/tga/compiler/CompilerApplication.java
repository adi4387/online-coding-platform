package com.ps.tga.compiler;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Compiler Service", version = "1.0.0"),
		servers = {
				@Server(url = "http://localhost:8080",
						description = "Compiler Service - development environment"
				)
		},
		tags = {
				@Tag(name = "Compiler Service",
						description = "Compile and run source code against test cases")
		})
@SecurityScheme(name = "Bearer token", type = HTTP, scheme = "bearer", description = "User token", in = HEADER)
public class CompilerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompilerApplication.class, args);
	}

}
