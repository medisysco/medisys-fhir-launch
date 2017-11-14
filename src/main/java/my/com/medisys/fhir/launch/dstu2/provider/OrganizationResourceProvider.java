package my.com.medisys.fhir.launch.dstu2.provider;

import ca.uhn.fhir.model.dstu2.resource.Organization;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointUseEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

/**
 * @author    Medical Systems<devs@medisys.com.my>
 * @version   1.0.00-SNAPSHOT
 * @since     1.0.00-SNAPSHOT
 */
public class OrganizationResourceProvider implements IResourceProvider {

    @Override
    public Class<Organization> getResourceType() {
        return Organization.class;
    }

    @Read()
    public Organization getResourceById(@IdParam IdDt id) {
        if (!"1".equals(id.getValue())) {
            throw new ResourceNotFoundException(id);
        }

        Organization organization = new Organization();
        organization.setId("1");
        organization.addIdentifier().setSystem("urn:medisys:com:my").setValue("Rufaida Medical System");
        organization.addAddress().addLine("JLN 3/50, Off JLN Gombak").setCity("Kuala Lumpur City");
        organization.addTelecom().setUse(ContactPointUseEnum.WORK).setValue("+60112278918");
        return organization;
    }

}