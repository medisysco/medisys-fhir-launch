package my.com.medisys.fhir.launch.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import my.com.medisys.fhir.launch.dstu3.provider.OrganizationResourceProvider;
import my.com.medisys.fhir.launch.dstu3.provider.PatientResourceProvider;
import my.com.medisys.fhir.launch.dstu3.provider.SlotResourceProvider;

import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.narrative.INarrativeGenerator;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;

/**
 * @author    Medical Systems<devs@medisys.com.my>
 * @version   1.0.00-SNAPSHOT
 * @since     1.0.00-SNAPSHOT
 */
@Component
public class FhirDstu3RestServlet extends RestfulServer {

    private static final long serialVersionUID = 1L;

    public FhirDstu3RestServlet() {
        super(FhirContext.forDstu3());
    }

    @Override
    public void initialize() {
        INarrativeGenerator narrativeGen = new DefaultThymeleafNarrativeGenerator();
        getFhirContext().setNarrativeGenerator(narrativeGen);

        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("Accept");
        config.addAllowedHeader("Content-Type");
        
        config.addExposedHeader("Location");
        config.addExposedHeader("Content-Location");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        registerInterceptor(new ResponseHighlighterInterceptor());
        CorsInterceptor corsInterceptor = new CorsInterceptor(config);
        registerInterceptor(corsInterceptor);
        setDefaultPrettyPrint(true);
        initResourceProviders();
    }

    private void initResourceProviders() {
        List<IResourceProvider> providers = new ArrayList<IResourceProvider>();
        providers.add(new SlotResourceProvider());
        providers.add(new PatientResourceProvider());
        providers.add(new OrganizationResourceProvider());
        setResourceProviders(providers);
    }

}
