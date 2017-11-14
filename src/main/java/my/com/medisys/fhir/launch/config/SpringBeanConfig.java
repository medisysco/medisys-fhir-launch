package my.com.medisys.fhir.launch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author    Medical Systems<devs@medisys.com.my>
 * @version   1.0.00-SNAPSHOT
 * @since     1.0.00-SNAPSHOT
 */
@Configuration
@ComponentScan(basePackages= {"my.com.medisys.fhir.launch.beans", "my.com.medisys.fhir.launch.service"})
public class SpringBeanConfig {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SpringBeanConfig.class);

}
