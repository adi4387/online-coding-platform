package com.ps.tga.problem;

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
@OpenAPIDefinition(info = @Info(title = "Problem API", version = "1.0.0"),
		servers = {
				@Server(url = "http://localhost:8080",
						description = "Problem API - development environment"
				)
		},
		tags = {
				@Tag(name = "Problem API",
						description = "Create and Get Problems")
		})
@SecurityScheme(name = "Bearer token", type = HTTP, scheme = "bearer", description = "User token", in = HEADER)
public class ProblemApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProblemApiApplication.class, args);
	}

}
