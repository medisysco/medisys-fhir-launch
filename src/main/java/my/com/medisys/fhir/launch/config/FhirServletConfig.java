package my.com.medisys.fhir.launch.config;

//import my.com.medisys.fhir.launch.servlet.FhirDstu2RestServlet;
import my.com.medisys.fhir.launch.servlet.FhirDstu3RestServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author    Medical Systems<devs@medisys.com.my>
 * @version   1.0.00-SNAPSHOT
 * @since     1.0.00-SNAPSHOT
 */
@Configuration
@ComponentScan(basePackages= {"my.com.medisys.fhir.launch.beans", "my.com.medisys.fhir.launch.servlet",
        "my.com.medisys.fhir.launch.controller", "my.com.medisys.fhir.launch.service"})
public class FhirServletConfig extends WebMvcConfigurerAdapter {
    
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(FhirServletConfig.class);
    
    //@Autowired
    //FhirDstu2RestServlet fhirDstu2Servlet;
    
    @Autowired
    FhirDstu3RestServlet fhirDstu3Servlet;
    
    @Bean
    //http://localhost:8080/fhir/dstu2/Patient
    //http://localhost:8080/fhir/dstu2/Patient?family=Hossen
    //http://localhost:8080/fhir/dstu2/Patient?family=Hossain
    //http://localhost:8080/fhir/dstu2/Patient?family=Hossain&given=Shahed
    public ServletRegistrationBean servletRegistrationBean(ApplicationContext context) {
        //return new ServletRegistrationBean(fhirDstu2Servlet, "/dstu2/*");
        return new ServletRegistrationBean(fhirDstu3Servlet, "/dstu3/*");
    }
}