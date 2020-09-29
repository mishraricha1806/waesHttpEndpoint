package com.waes.waesHttpEndpoint;

import com.waes.waesHttpEndpoint.util.DataStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@SpringBootApplication
@EnableConfigurationProperties({
		DataStorageProperties.class
})public class WaesHttpEndpointApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaesHttpEndpointApplication.class, args);
	}

	@Bean
	public LocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
		localeResolver.setDefaultLocale(Locale.US);
		return localeResolver;
	}
}
