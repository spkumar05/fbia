package com.hearst.fbia.frm.config.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@PropertySource(value = "classpath:prop/swagger.properties", ignoreResourceNotFound = true)
public class SwaggerConfig {

	@Autowired
	Environment environment;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build().apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		Contact contact = new Contact(environment.getRequiredProperty("swagger.apiInfo.contact.name"),
				environment.getRequiredProperty("swagger.apiInfo.contact.url"),
				environment.getRequiredProperty("swagger.apiInfo.contact.email"));
		ApiInfo apiInfo = new ApiInfo(environment.getRequiredProperty("swagger.apiInfo.title"),
				environment.getRequiredProperty("swagger.apiInfo.description"),
				environment.getRequiredProperty("swagger.apiInfo.version"),
				environment.getRequiredProperty("swagger.apiInfo.termsOfServiceUrl"), contact,
				environment.getRequiredProperty("swagger.apiInfo.license"),
				environment.getRequiredProperty("swagger.apiInfo.licenseUrl"));
		return apiInfo;
	}

}