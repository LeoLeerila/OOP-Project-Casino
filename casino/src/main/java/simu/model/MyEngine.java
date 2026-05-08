package simu.model;

import controller.IControllerMtoV;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.*;
import simu.model.game.GameAbstract;

public class MyEngine extends Engine {
	private ArrivalProcess arrivalProcess;
	private final Object pauseLock = new Object();

	public MyEngine(IControllerMtoV controller){ //Apua
		super(controller);
		Trace.setTraceLevel(Trace.Level.INFO);
		servicePoints = new ServicePoint[0];

		//servicePoints = new GameAbstract[0]; for all games so each are their own thread

	}
	/**
	 * setPaused() and setReset() are methods that allow the controller to manage the simulation's state.
	 * */
	@Override
	public void setPaused(boolean paused){
		synchronized (pauseLock) {
			this.isPaused = paused;
			if (!isPaused) {
				pauseLock.notify();
			}
		}
	}
	@Override
	public void setReset(boolean reset){
		this.isReset = reset;
	}
	/**
	 * Handles the bulk spawn of NPC's
	 * */
	@Override
	protected void initialization() {
		double arrivalInterval = 3.0;
		double next = arrivalInterval;
		while(next <= simulationTime) {
			for(int i = 0; i < (Math.random()* 5); i++) //now people come in bulk (wow, that's crazy)
				eventList.add(new Event(EventType.NPC_ARRIVAL, next));
			next += arrivalInterval;
		}
		for(double t = 1; t<= simulationTime; t++){
			eventList.add(new Event(EventType.TIME_ADVANCE, t));
		}
	}
	//overwrite Engine.java run method to be able to pause and reset simu (was not fun)
	/**
	 * run() method takes care of simulation running, and managing states like pause and reset.
	 * */
	@Override
	public void run() {
		initialization();
		//check to make sure simulation is actually running
		while(Clock.getInstance().getTime() < simulationTime && !isReset) {
			//improved pause handling
			synchronized (pauseLock){
				while (isPaused && !isReset) {
					try {
						pauseLock.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
			if (isReset) break;

			//Log time and delay for debugging purposes
			Trace.out(Trace.Level.INFO, "Time is: " + Clock.getInstance().getTime());
			Trace.out(Trace.Level.INFO, "Delay " + getDelay());
			try {
				Thread.sleep(getDelay());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}

			//Updated: Process all events (events and continuous) at the next time point
			try {
				double nextEventTime = eventList.getNextTime();
				Clock.getInstance().setTime(nextEventTime);
				while (eventList.getNextTime() == Clock.getInstance().getTime()){
					Event event = eventList.remove();
					runEvent(event);
				}

				for (ServicePoint p : servicePoints) {
					if(!p.isReserved() && p.isOnQueue()) {
						p.beginService();
					}
				}
			} catch (Exception e) {
				Trace.out(Trace.Level.ERR, "Error during simulation: " + e.getMessage());
				e.printStackTrace();
			}
		}
		results();
	}

	@Override
	protected void runEvent(Event t) {
		if (t.getType() == EventType.NPC_ARRIVAL) {
			//-> MainController -> Queues player
			controller.createPlayer();
		}
		if (t.getType() == EventType.TIME_ADVANCE){
			controller.timeAdvance();
		}
	}

	@Override
	protected void results() {
		controller.showEndTime(Clock.getInstance().getTime());
	}
}
