package my.com.medisys.fhir.launch.config;

import my.com.medisys.fhir.launch.servlet.FhirRestServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author    Shahed Hossain<shahed@medisys.com.sa>
 * @version   1.0.00-SNAPSHOT
 * @since     1.0.00-SNAPSHOT
 */
@Configuration
@ComponentScan(basePackages= {"my.com.medisys.fhir.launch.beans",
        "my.com.medisys.fhir.launch.controller", "my.com.medisys.fhir.launch.service"})
public class WebServletConfig extends WebMvcConfigurerAdapter {
    
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(WebServletConfig.class);
    
    @Bean
    public ServletRegistrationBean servletRegistrationBean(ApplicationContext context) {
        return new ServletRegistrationBean(new FhirRestServlet(), "/fhir/dstu2/*");
    }
}
