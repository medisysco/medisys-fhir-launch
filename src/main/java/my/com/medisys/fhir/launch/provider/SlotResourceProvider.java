package my.com.medisys.fhir.launch.provider;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.model.dstu2.resource.Slot;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.server.IResourceProvider;

public class SlotResourceProvider implements IResourceProvider {
    
    private static final Logger log = LoggerFactory.getLogger(SlotResourceProvider.class);

    private Map<Long, Deque<Slot>> slots = new HashMap<Long, Deque<Slot>>();
    private long slotNextId = 1;
    
    public SlotResourceProvider() {
        LinkedList<Slot> slot0 = new LinkedList<Slot>();
        LinkedList<Slot> slot1 = new LinkedList<Slot>();
        
        long slotId = slotNextId++;
        
        Slot apt0 = new Slot();
        apt0.setId("998915");
        apt0.addIdentifier();
        apt0.getIdentifier().get(0).setSystem(new UriDt("urn:hapitest:mrns"));
        apt0.getIdentifier().get(0).setValue("998915");
        slot0.add(apt0);
        slots.put(slotId, slot0);
        
        slotId = slotNextId++;
        Slot apt1 = new Slot();
        apt1.setId("998916");
        apt1.addIdentifier();
        apt1.getIdentifier().get(0).setSystem(new UriDt("urn:hapitest:mrns"));
        apt1.getIdentifier().get(0).setValue("998916");
        slot1.add(apt1);
        slots.put(slotId, slot1);
        
    }
    
    
    /*@Create()
    public MethodOutcome createPatient(@ResourceParam Slot slot) {
        //validateResource(slot);
        long id = slotNextId++;
        //addNewVersion(thePatient, id);
        return new MethodOutcome(new IdDt(id));
    }*/
    
    
    @Search
    public List<Slot> findAllSlots() {
        LinkedList<Slot> slots = new LinkedList<Slot>();
        for (Deque<Slot> nextSlotList : this.slots.values()) {
            Slot nextSlot = nextSlotList.getLast();
            slots.add(nextSlot);
        }
        log.info("Slots found: {}", slots.size());
        return slots;
    }
    
    @Read
    //@Read(version = true)
    public Slot readSlot(@IdParam IdDt id) {
        log.info("Slot ID: {}", id.getIdPartAsLong());
        Deque<Slot> slot = null;
        
        for (Deque<Slot> nextSlotList : this.slots.values()) {
            Slot nextSlot = nextSlotList.getLast();
            if(nextSlot.getId().equals(id)){
                slot = nextSlotList;
                break;
            }
        }
        return slot.getLast();
    }
    
    
    @Override
    public Class<Slot> getResourceType() {
        return Slot.class;
    }
}
