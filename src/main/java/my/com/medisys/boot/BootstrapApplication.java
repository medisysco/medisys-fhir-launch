package my.com.medisys.boot;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author    Medical Systems<devs@medisys.com.my>
 * @version   1.0.00-SNAPSHOT
 * @since     1.0.00-SNAPSHOT
 */
@SpringBootApplication
@ComponentScan(basePackages= {"my.com.medisys.fhir.launch.config"})
public class BootstrapApplication {

    private static Log logger = LogFactory.getLog(BootstrapApplication.class);

    @Bean
    protected ServletContextListener listener() {
        return new ServletContextListener() {
            
            @Override
            public void contextInitialized(ServletContextEvent sce) {
                logger.info("ServletContext initialized");
            }
            
            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                logger.info("ServletContext destroyed");
            }
            
        };
    }

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(BootstrapApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}
