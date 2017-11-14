package my.com.medisys.fhir.launch.provider;

import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
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
 * @author    Medical Systems<devs@medisys.com.my>
 * @version   1.0.00-SNAPSHOT
 * @since     1.0.00-SNAPSHOT
 */
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
        shahed.getIdentifier().get(0).setSystem("urn:hapitest:mrns");
        shahed.getIdentifier().get(0).setValue("998915");
        shahed.addName().setFamily("Hossain");
        shahed.getName().get(0).addGiven("Md Shahed");
        shahed.setGender(AdministrativeGender.MALE);
        list0.add(shahed);
        patientVersions.put(resourceId, list0);

        resourceId = nextId++;
        Patient shohel = new Patient();
        shohel.setId("998916");
        shohel.addIdentifier();
        shohel.getIdentifier().get(0).setSystem("urn:hapitest:mrns");
        shohel.getIdentifier().get(0).setValue("998916");
        shohel.addName().setFamily("Hossen");
        shohel.getName().get(0).addGiven("Md Shohel");
        shohel.setGender(AdministrativeGender.MALE);
        list1.add(shohel);
        patientVersions.put(resourceId, list1);
    }

    private void addNewVersion(Patient patient, Long id) {
        // Date publishedDate;
        if (!patientVersions.containsKey(id)) {
            patientVersions.put(id, new LinkedList<Patient>());
            // publishedDate = new Date();
        } else {
            // Patient currentPatitne =
            // myIdToPatientVersions.get(theId).getLast();
            // Meta meta = currentPatitne.getMeta();
            // publishedDate = meta.getLastUpdated();
            // publishedDate = (InstantType)
            // meta.get(ResourceMetadataKeyEnum.PUBLISHED);
        }

        // thePatient.getResourceMetadata().put(ResourceMetadataKeyEnum.PUBLISHED,
        // publishedDate);
        // thePatient.getResourceMetadata().put(ResourceMetadataKeyEnum.UPDATED,
        // InstantDt.withCurrentTime());
        patient.getMeta().setLastUpdated(new Date());
        // ResourceMetadataKeyEnum.PUBLISHED.put(patient, pubDate);
        Deque<Patient> existingVersions = patientVersions.get(id);
        String newVersion = Integer.toString(existingVersions.size());
        IdType newId = new IdType("Patient", Long.toString(id), newVersion);
        patient.setId(newId);
        existingVersions.add(patient);
    }

    @Create()
    public MethodOutcome createPatient(@ResourceParam Patient patient) {
        validateResource(patient);
        long id = nextId++;
        addNewVersion(patient, id);
        return new MethodOutcome(new IdType(id));
    }

    @Search()
    public List<Patient> findPatientsByName(@RequiredParam(name = Patient.SP_FAMILY) StringType familyName, @OptionalParam(name = Patient.SP_GIVEN) StringType givenName) {
        LinkedList<Patient> retVal = new LinkedList<Patient>();
        for (Deque<Patient> nextPatientList : patientVersions.values()) {
            Patient nextPatient = nextPatientList.getLast();
            NAMELOOP: for (HumanName nextName : nextPatient.getName()) {
                if (familyName.equals(nextName.getFamily())) {
                    retVal.add(nextPatient);
                    break NAMELOOP;
                }
            }
        }
        log.info("Family Name: {}", familyName.getValueAsString());
        if (givenName != null) {
            log.info("Given Name: {}", givenName.getValueAsString());
        }
        return retVal;
    }

    @Search
    public List<Patient> findPatientsUsingArbitraryCtriteria() {
        LinkedList<Patient> retVal = new LinkedList<Patient>();
        for (Deque<Patient> nextPatientList : patientVersions.values()) {
            Patient nextPatient = nextPatientList.getLast();
            retVal.add(nextPatient);
        }
        return retVal;
    }

    @Read(version = true)
    public Patient readPatient(@IdParam IdType resourceId) {
        Deque<Patient> retVal;
        try {
            retVal = patientVersions.get(resourceId.getIdPartAsLong());
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException(resourceId);
        }
        if (resourceId.hasVersionIdPart() == false) {
            return retVal.getLast();
        } else {
            for (Patient nextVersion : retVal) {
                String nextVersionId = nextVersion.getId();
                if (resourceId.getVersionIdPart().equals(nextVersionId)) {
                    return nextVersion;
                }
            }
            throw new ResourceNotFoundException("Unknown version: "+ resourceId.getValue());// HTTP 404
        }
    }

    @Update()
    public MethodOutcome updatePatient(@IdParam IdType resourceId, @ResourceParam Patient thePatient) {
        validateResource(thePatient);
        Long id;
        try {
            id = resourceId.getIdPartAsLong();
        } catch (DataFormatException e) {
            throw new InvalidRequestException("Invalid ID " + resourceId.getValue() + " - Must be numeric");
        }
        if (!patientVersions.containsKey(id)) {
            throw new ResourceNotFoundException(resourceId);// HTTP 404
        }
        addNewVersion(thePatient, id);
        return new MethodOutcome();
    }

    private void validateResource(Patient patient) {
        if (patient.getNameFirstRep().getFamilyElement().isEmpty()) {
            OperationOutcome outcome = new OperationOutcome();
            outcome.addIssue().setSeverity(IssueSeverity.FATAL)
            .setDiagnostics("No family name provided, Patient resources must have at least one family name.");
            throw new UnprocessableEntityException(FhirContext.forDstu3(), outcome);
        }
    }

    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }

}
