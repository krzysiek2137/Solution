package se.visionmate.solution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import se.visionmate.solution.security.CustomBasicAuthenticationFilter;

@SpringBootApplication
@EnableJpaRepositories("se.visionmate.solution.repository")
public class SolutionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolutionApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean someFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(theActualFilter());
		registration.addUrlPatterns("/user", "/role", "/user/*", "/role/*");
		registration.setName("basicFilter");
		registration.setOrder(1);
		return registration;
	}

	@Bean
	public CustomBasicAuthenticationFilter theActualFilter() {
		return new CustomBasicAuthenticationFilter();
	}
}
