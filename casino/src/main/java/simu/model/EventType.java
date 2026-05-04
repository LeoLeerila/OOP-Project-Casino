package simu.model;

import simu.framework.IEventType;

// TODO:
// Event types are defined by the requirements of the simulation model
public enum EventType implements IEventType {
	NPC_ARRIVAL, DEP1, DEP2, DEP3, TIME_ADVANCE;
}
