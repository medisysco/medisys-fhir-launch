package my.com.medisys.fhir.launch.provider;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.ResourceMetadataKeyEnum;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.IssueSeverityEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

/**
 * This is a resource provider which stores Patient resources in memory using a HashMap. This is obviously not a production-ready solution for many reasons, 
 * but it is useful to help illustrate how to build a fully-functional server.
 */
public class PatientResourceProvider implements IResourceProvider {
    
    private static final Logger log = LoggerFactory.getLogger(PatientResourceProvider.class);
     
    /**
     * This map has a resource ID as a key, and each key maps to a Deque list containing all versions of the resource with that ID.
     */
    private Map<Long, Deque<Patient>> myIdToPatientVersions = new HashMap<Long, Deque<Patient>>();
    
    /**
     * This is used to generate new IDs
     */
    private long myNextId = 1;
    
    /**
     * Constructor, which pre-populates the provider with one resource instance.
     */
    public PatientResourceProvider() {
        LinkedList<Patient> list0 = new LinkedList<Patient>();
        LinkedList<Patient> list1 = new LinkedList<Patient>();
        
        long resourceId = myNextId++;
        
        Patient shahed = new Patient();
        shahed.setId("998915");
        shahed.addIdentifier();
        shahed.getIdentifier().get(0).setSystem(new UriDt("urn:hapitest:mrns"));
        shahed.getIdentifier().get(0).setValue("998915");
        shahed.addName().addFamily("Hossain");
        shahed.getName().get(0).addGiven("Md Shahed");
        shahed.setGender(AdministrativeGenderEnum.MALE);
        list0.add(shahed);
        myIdToPatientVersions.put(resourceId, list0);
        
        resourceId = myNextId++;
        Patient shohel = new Patient();
        shohel.setId("998916");
        shohel.addIdentifier();
        shohel.getIdentifier().get(0).setSystem(new UriDt("urn:hapitest:mrns"));
        shohel.getIdentifier().get(0).setValue("998916");
        shohel.addName().addFamily("Hossen");
        shohel.getName().get(0).addGiven("Md Shohel");
        shohel.setGender(AdministrativeGenderEnum.MALE);
        list1.add(shohel);
        myIdToPatientVersions.put(resourceId, list1);
        
    }
    
    /**
     * Stores a new version of the patient in memory so that it can be retrieved later.
     * 
     * @param thePatient
     *            The patient resource to store
     * @param theId
     *            The ID of the patient to retrieve
     */
    private void addNewVersion(Patient thePatient, Long theId) {
        InstantDt publishedDate;
        if (!myIdToPatientVersions.containsKey(theId)) {
            myIdToPatientVersions.put(theId, new LinkedList<Patient>());
            publishedDate = InstantDt.withCurrentTime();
        } else {
            Patient currentPatitne = myIdToPatientVersions.get(theId).getLast();
            Map<ResourceMetadataKeyEnum<?>, Object> resourceMetadata = currentPatitne.getResourceMetadata();
            publishedDate = (InstantDt) resourceMetadata.get(ResourceMetadataKeyEnum.PUBLISHED);
        }
        
        /*
         * PUBLISHED time will always be set to the time that the first version was stored. UPDATED time is set to the time that the new version was stored.
         */
        thePatient.getResourceMetadata().put(ResourceMetadataKeyEnum.PUBLISHED, publishedDate);
        thePatient.getResourceMetadata().put(ResourceMetadataKeyEnum.UPDATED, InstantDt.withCurrentTime());
        
        Deque<Patient> existingVersions = myIdToPatientVersions.get(theId);
        
        // We just use the current number of versions as the next version number
        String newVersion = Integer.toString(existingVersions.size());
        
        // Create an ID with the new version and assign it back to the resource
        IdDt newId = new IdDt("Patient", Long.toString(theId), newVersion);
        thePatient.setId(newId);
        
        existingVersions.add(thePatient);
    }
    
    /**
     * The "@Create" annotation indicates that this method implements "create=type", which adds a 
     * new instance of a resource to the server.
     */
    @Create()
    public MethodOutcome createPatient(@ResourceParam Patient thePatient) {
        validateResource(thePatient);
        
        // Here we are just generating IDs sequentially
        long id = myNextId++;
        
        addNewVersion(thePatient, id);
        
        // Let the caller know the ID of the newly created resource
        return new MethodOutcome(new IdDt(id));
    }
    
    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by family name.
     * 
     * @param familyName
     *            This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *            the search criteria. The datatype here is StringDt, but there are other possible parameter types depending on the specific search criteria.
     * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Patient> findPatientsByName(@RequiredParam(name = Patient.SP_FAMILY) StringDt familyName, @OptionalParam(name = Patient.SP_GIVEN) StringDt givenName) {
        LinkedList<Patient> retVal = new LinkedList<Patient>();
        
        /*
         * Look for all patients matching the name
         */
        for (Deque<Patient> nextPatientList : myIdToPatientVersions.values()) {
            Patient nextPatient = nextPatientList.getLast();
            NAMELOOP: for (HumanNameDt nextName : nextPatient.getName()) {
                for (StringDt nextFamily : nextName.getFamily()) {
                    if (familyName.equals(nextFamily)) {
                        retVal.add(nextPatient);
                        break NAMELOOP;
                    }
                }
            }
        }
        log.info("Family Name: {}", familyName.getValueAsString());
        if(givenName != null){
            log.info("Given Name: {}", givenName.getValueAsString());
        }
        
        return retVal;
    }
    
    
    @Search
    public List<Patient> findPatientsUsingArbitraryCtriteria() {
        LinkedList<Patient> retVal = new LinkedList<Patient>();
        
        for (Deque<Patient> nextPatientList : myIdToPatientVersions.values()) {
            Patient nextPatient = nextPatientList.getLast();
            retVal.add(nextPatient);
        }
    
        return retVal;
    }
    
    
    /**
     * The getResourceType method comes from IResourceProvider, and must be overridden to indicate what type of resource this provider supplies.
     */
    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }
    
    /**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     * 
     * @param theId
     *            The read operation takes one parameter, which must be of type IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     */
    @Read(version = true)
    public Patient readPatient(@IdParam IdDt theId) {
        Deque<Patient> retVal;
        try {
            retVal = myIdToPatientVersions.get(theId.getIdPartAsLong());
        } catch (NumberFormatException e) {
            /*
             * If we can't parse the ID as a long, it's not valid so this is an unknown resource
             */
            throw new ResourceNotFoundException(theId);
        }
        
        if (theId.hasVersionIdPart() == false) {
            return retVal.getLast();
        } else {
            for (Patient nextVersion : retVal) {
                String nextVersionId = nextVersion.getId().getVersionIdPart();
                if (theId.getVersionIdPart().equals(nextVersionId)) {
                    return nextVersion;
                }
            }
            // No matching version
            throw new ResourceNotFoundException("Unknown version: " + theId.getValue());
        }
        
    }
    
    /**
     * The "@Update" annotation indicates that this method supports replacing an existing 
     * resource (by ID) with a new instance of that resource.
     * 
     * @param theId
     *            This is the ID of the patient to update
     * @param thePatient
     *            This is the actual resource to save
     * @return This method returns a "MethodOutcome"
     */
    @Update()
    public MethodOutcome updatePatient(@IdParam IdDt theId, @ResourceParam Patient thePatient) {
        validateResource(thePatient);
        
        Long id;
        try {
            id = theId.getIdPartAsLong();
        } catch (DataFormatException e) {
            throw new InvalidRequestException("Invalid ID " + theId.getValue() + " - Must be numeric");
        }
        
        /*
         * Throw an exception (HTTP 404) if the ID is not known
         */
        if (!myIdToPatientVersions.containsKey(id)) {
            throw new ResourceNotFoundException(theId);
        }
        
        addNewVersion(thePatient, id);
        
        return new MethodOutcome();
    }
    
    /**
     * This method just provides simple business validation for resources we are storing.
     * 
     * @param thePatient
     *            The patient to validate
     */
    private void validateResource(Patient thePatient) {
        /*
         * Our server will have a rule that patients must have a family name or we will reject them
         */
        if (thePatient.getNameFirstRep().getFamilyFirstRep().isEmpty()) {
            OperationOutcome outcome = new OperationOutcome();
            outcome.addIssue().setSeverity(IssueSeverityEnum.FATAL).setDiagnostics("No family name provided, Patient resources must have at least one family name.");
            throw new UnprocessableEntityException(FhirContext.forDstu2(), outcome);
        }
    }
    
}
