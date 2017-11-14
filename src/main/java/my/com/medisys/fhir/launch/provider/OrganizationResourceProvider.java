package my.com.medisys.fhir.launch.provider;

import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;

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
    public Organization getResourceById(@IdParam IdType theId) {
        if (!"1".equals(theId.getValue())) {
            throw new ResourceNotFoundException(theId);
        }

        Organization retVal = new Organization();
        retVal.setId("1");
        retVal.addIdentifier().setSystem("urn:example:orgs").setValue("FooOrganization");
        retVal.addAddress().addLine("123 Fake Street").setCity("Toronto");
        retVal.addTelecom(new ContactPoint().setUse(ContactPointUse.WORK).setValue("1-888-123-4567"));
        return retVal;
    }

}
