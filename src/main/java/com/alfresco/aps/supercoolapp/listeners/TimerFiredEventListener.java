package com.alfresco.aps.supercoolapp.listeners;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Component;

@Component("timerFiredEventListener")
public class TimerFiredEventListener implements ActivitiEventListener{

	@Override
	public boolean isFailOnException() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onEvent(ActivitiEvent event) {
		// TODO Auto-generated method stub
		System.out.println(event.getExecutionId());
		
	}

}
