package simu.model;

import controller.IControllerMtoV;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.*;


public class MyEngine extends Engine {
	private ArrivalProcess arrivalProcess;

	public MyEngine(IControllerMtoV controller){ //Apua
		super(controller);
		Trace.setTraceLevel(Trace.Level.INFO);
		//Kun aletaan lisäämään pelipöytiä jotka toimis SP:nä, ServicePoint.Java vois muuttua interfaceks
		//-jonka gameAbstract implementoi. Sillee ei ehkä tarttis säätää uudelleen kirjoittamisen kans
		servicePoints = new ServicePoint[0];
	}
	@Override
	public void setPaused(boolean paused){
		this.isPaused = paused;
	}
	@Override
	public void setReset(boolean reset){
		this.isReset = reset;
	}
	@Override
	protected void initialization() {
		//arrivalProcess = new ArrivalProcess(new Negexp(5.0), EventType.ARRIVAL);
		//eventList.add(arrivalProcess.nextEvent());
		//
		for(double t = 1; t<= simulationTime; t++){
			eventList.add(new Event(EventType.ARR1, t));
		}
	}
	//overwrite Engine.java run method to be able to pause and reset simu (was not fun)
	@Override
	public void run() {
		initialization();
		//check to make sure simulation is actually running
		while(Clock.getInstance().getTime() < simulationTime && !isReset) {
			while (isPaused) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			}
			//Log time and delay for debugging purposes
			Trace.out(Trace.Level.INFO, "Time is: " + Clock.getInstance().getTime());
			Trace.out(Trace.Level.INFO, "Delay " + getDelay());

			try {
				sleep(getDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//A whole bunch of hoohaa that I don't actually quite get. It is however very necessary for the simulation to work.
			Clock.getInstance().setTime(eventList.getNextTime());
			while (eventList.getNextTime() == Clock.getInstance().getTime()){
				runEvent(eventList.remove());
			}
			for (ServicePoint p : servicePoints) {
				if(!p.isReserved() && p.isOnQueue()) {
					p.beginService();
				}
			}
		}
		results();
	}

	//now THIS is where we actually spawn simuation activity
	@Override
	protected void runEvent(Event t) {  // B phase events
		if (t.getType() == EventType.ARR1) {
			controller.logEvent("A second has passed");
		}

		//create new customer ->
		//Send them to game table ->
		//Schedule next arrival ->
	}

	@Override
	protected void results() {
		controller.showEndTime(Clock.getInstance().getTime());
	}
}
