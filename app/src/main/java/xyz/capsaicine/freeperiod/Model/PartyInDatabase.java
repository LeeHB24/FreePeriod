package xyz.capsaicine.freeperiod.Model;

import xyz.capsaicine.freeperiod.app.Utility;

public class PartyInDatabase {
    private int partyId;
    private String partyType;
    private String partyStatus;
    private byte[] partyCover;
    private String partyName;
    private String partyTag;
    private int capacityMax;
    private int capacityCurrent;
    private String partyStartTime;
    private String partyEndTime;
    private String partyCloseTime;

    public PartyInDatabase(Party party){
        partyId = party.getPartyId();
        partyType = party.getPartyType().name();
        partyStatus = party.getPartyStatus().name();
        partyCover = Utility.convertDrawableToByteArray(party.getPartyCover());
        partyName = party.getPartyName();
        partyTag = Utility.getTagsInOneString(party.getPartyTagList(), false);
        capacityMax = party.getCapacityMax();
        capacityCurrent = party.getCapacityCurrent();
        partyStartTime = party.getPartyStartTime().toString();
        partyEndTime = party.getPartyEndTime().toString();
        partyCloseTime = party.getPartyCloseTime().toString();
    }

    public int getPartyId() {
        return partyId;
    }

    public String getPartyType() {
        return partyType;
    }

    public String getPartyStatus() {
        return partyStatus;
    }

    public byte[] getPartyCover() {
        return partyCover;
    }

    public String getPartyName() {
        return partyName;
    }

    public String getPartyTag() {
        return partyTag;
    }

    public int getCapacityMax() {
        return capacityMax;
    }

    public int getCapacityCurrent() {
        return capacityCurrent;
    }

    public String getPartyStartTime() {
        return partyStartTime;
    }

    public String getPartyEndTime() {
        return partyEndTime;
    }

    public String getPartyCloseTime() {
        return partyCloseTime;
    }
}


