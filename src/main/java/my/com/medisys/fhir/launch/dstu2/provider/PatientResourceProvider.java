package my.com.medisys.fhir.launch.dstu2.provider;

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

public class PatientResourceProvider implements IResourceProvider {

    private static final Logger log = LoggerFactory.getLogger(PatientResourceProvider.class);

    private long nextId = 1;
    private Map<Long, Deque<Patient>> patientVersions = new HashMap<Long, Deque<Patient>>();

    public PatientResourceProvider() {
        LinkedList<Patient> list0 = new LinkedList<Patient>();
        LinkedList<Patient> list1 = new LinkedList<Patient>();

        long resourceId = nextId++;
        Patient shahed = new Patient();
        shahed.setId("998915");
        shahed.addIdentifier();
        shahed.getIdentifier().get(0).setSystem(new UriDt("urn:hapitest:mrns"));
        shahed.getIdentifier().get(0).setValue("998915");
        shahed.addName().addFamily("Hossain");
        shahed.getName().get(0).addGiven("Md Shahed");
        shahed.setGender(AdministrativeGenderEnum.MALE);
        list0.add(shahed);
        patientVersions.put(resourceId, list0);

        resourceId = nextId++;
        Patient shohel = new Patient();
        shohel.setId("998916");
        shohel.addIdentifier();
        shohel.getIdentifier().get(0).setSystem(new UriDt("urn:hapitest:mrns"));
        shohel.getIdentifier().get(0).setValue("998916");
        shohel.addName().addFamily("Hossen");
        shohel.getName().get(0).addGiven("Md Shohel");
        shohel.setGender(AdministrativeGenderEnum.MALE);
        list1.add(shohel);
        patientVersions.put(resourceId, list1);
    }

    private void addNewVersion(Patient patient, Long id) {
        InstantDt publishedDate;
        if (!patientVersions.containsKey(id)) {
            patientVersions.put(id, new LinkedList<Patient>());
            publishedDate = InstantDt.withCurrentTime();
        } else {
            Patient currentPatitne = patientVersions.get(id).getLast();
            Map<ResourceMetadataKeyEnum<?>, Object> resourceMetadata = currentPatitne.getResourceMetadata();
            publishedDate = (InstantDt) resourceMetadata.get(ResourceMetadataKeyEnum.PUBLISHED);
        }
        patient.getResourceMetadata().put(ResourceMetadataKeyEnum.PUBLISHED, publishedDate);
        patient.getResourceMetadata().put(ResourceMetadataKeyEnum.UPDATED, InstantDt.withCurrentTime());
        Deque<Patient> existingVersions = patientVersions.get(id);
        String newVersion = Integer.toString(existingVersions.size());
        IdDt newId = new IdDt("Patient", Long.toString(id), newVersion);
        patient.setId(newId);
        existingVersions.add(patient);
    }

    @Create()
    public MethodOutcome createPatient(@ResourceParam Patient thePatient) {
        validateResource(thePatient);
        long id = nextId++;
        addNewVersion(thePatient, id);
        return new MethodOutcome(new IdDt(id));
    }

    @Search()
    public List<Patient> findPatientsByName(@RequiredParam(name = Patient.SP_FAMILY) StringDt familyName, @OptionalParam(name = Patient.SP_GIVEN) StringDt givenName) {
        LinkedList<Patient> patients = new LinkedList<Patient>();
        for (Deque<Patient> nextPatientList : patientVersions.values()) {
            Patient nextPatient = nextPatientList.getLast();
            NAMELOOP: for (HumanNameDt nextName : nextPatient.getName()) {
                for (StringDt nextFamily : nextName.getFamily()) {
                    if (familyName.equals(nextFamily)) {
                        patients.add(nextPatient);
                        break NAMELOOP;
                    }
                }
            }
        }
        log.info("Family Name: {}", familyName.getValueAsString());
        if(givenName != null){
            log.info("Given Name: {}", givenName.getValueAsString());
        }
        return patients;
    }

    @Search
    public List<Patient> findPatientsUsingArbitraryCtriteria() {
        LinkedList<Patient> patients = new LinkedList<Patient>();
        for (Deque<Patient> nextPatientList : patientVersions.values()) {
            Patient nextPatient = nextPatientList.getLast();
            patients.add(nextPatient);
        }
        return patients;
    }

    @Read(version = true)
    public Patient readPatient(@IdParam IdDt resourceId) {
        Deque<Patient> patient;
        try {
            patient = patientVersions.get(resourceId.getIdPartAsLong());
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException(resourceId);
        }
        
        if (resourceId.hasVersionIdPart() == false) {
            return patient.getLast();
        } else {
            for (Patient nextVersion : patient) {
                String nextVersionId = nextVersion.getId().getVersionIdPart();
                if (resourceId.getVersionIdPart().equals(nextVersionId)) {
                    return nextVersion;
                }
            }
            throw new ResourceNotFoundException("Unknown version: " + resourceId.getValue());// HTTP 404
        }
    }

    @Update()
    public MethodOutcome updatePatient(@IdParam IdDt theId, @ResourceParam Patient thePatient) {
        validateResource(thePatient);
        Long id;
        try {
            id = theId.getIdPartAsLong();
        } catch (DataFormatException e) {
            throw new InvalidRequestException("Invalid ID " + theId.getValue() + " - Must be numeric");
        }
        if (!patientVersions.containsKey(id)) {
            throw new ResourceNotFoundException(theId);// HTTP 404
        }
        addNewVersion(thePatient, id);
        return new MethodOutcome();
    }

    private void validateResource(Patient thePatient) {
        if (thePatient.getNameFirstRep().getFamilyFirstRep().isEmpty()) {
            OperationOutcome outcome = new OperationOutcome();
            outcome.addIssue().setSeverity(IssueSeverityEnum.FATAL)
            .setDiagnostics("No family name provided, Patient resources must have at least one family name.");
            throw new UnprocessableEntityException(FhirContext.forDstu2(), outcome);
        }
    }

    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }

}