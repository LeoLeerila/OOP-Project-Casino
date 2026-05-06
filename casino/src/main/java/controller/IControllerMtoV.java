package controller;

import simu.framework.SimuUpdateEvent;

/* interface for the engine */
public interface IControllerMtoV {
		public void showEndTime(double time);
		public void visualiseCustomer();
		public void logEvent(String message);
		public void createPlayer();
		void timeAdvance();
		public void queueUpdate(SimuUpdateEvent event);
}
