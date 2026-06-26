package com.edueasy.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    public OpenAPIConfig() {
    }

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return (new OpenAPI()).info((new Info()).title("User Service API").description("API for managing users (Teachers, Students, Admins, Support Agents, Support Staff)").version("1.0.0").contact((new Contact()).name("EduEasy Team").email("support@edueasy.com").url("https://edueasy.com")).license((new License()).name("Apache 2.0").url("http://springdoc.org"))).addServersItem((new Server()).url("/api").description("User Service Server")).addSecurityItem((new SecurityRequirement()).addList("Bearer Authentication")).components((new Components()).addSecuritySchemes("Bearer Authentication", (new SecurityScheme()).name("Bearer Authentication").type(Type.HTTP).scheme("bearer").bearerFormat("JWT").in(In.HEADER).name("Authorization"))).addTagsItem((new Tag()).name("Admin Management").description("APIs for managing administrators")).addTagsItem((new Tag()).name("Teacher Management").description("APIs for managing teachers")).addTagsItem((new Tag()).name("Student Management").description("APIs for managing students")).addTagsItem((new Tag()).name("Support Agent Management").description("APIs for managing support agents")).addTagsItem((new Tag()).name("Support Staff Management").description("APIs for managing support staff"));
    }
}
