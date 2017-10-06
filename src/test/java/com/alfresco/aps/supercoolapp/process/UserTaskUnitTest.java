package com.alfresco.aps.supercoolapp.process;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alfresco.aps.supercoolapp.listeners.TimerFiredEventListener;
import com.alfresco.aps.testutils.AbstractBpmnTest;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import static com.alfresco.aps.testutils.TestUtilsConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:activiti.cfg.xml", "classpath:common-beans-and-mocks.xml", "classpath:process-beans-and-mocks.xml" })
@TestPropertySource(value="classpath:local-dev-test.properties")
public class UserTaskUnitTest extends AbstractBpmnTest {
	
	static {
		appName = "Super Cool App";
		processDefinitionKey = "SuperCoolProcess";
	}

	@Autowired
	TimerFiredEventListener timerFiredEventListener;
	
	@Test
	public void testProcessExecution() throws Exception {
		Map<String, Object> processVars = new HashMap<String, Object>();
		processVars.put("initiator", "$INITIATOR");
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey(processDefinitionKey, processVars);

		assertNotNull(processInstance);
		
		assertEquals(1, taskService.createTaskQuery().count());

		Task task = taskService.createTaskQuery().singleResult();
		
		unitTestHelpers.assertTaskDueDate(5, TIME_UNIT_DAY, task);

		unitTestHelpers.assertUserAssignment("$INITIATOR", task, false, false);
		
		taskService.complete(task.getId());

		unitTestHelpers.assertNullProcessInstance(processInstance.getProcessInstanceId());
	}
	
	@Test
	public void testProcessExecutionViaBoundary() throws Exception {

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				
				Object[] arg = invocation.getArguments();
				ActivitiEvent event = (ActivitiEvent) arg[0];
				assertTrue(event.getType().equals(ActivitiEventType.TIMER_FIRED));
				return null;
			}
		}).when(timerFiredEventListener).onEvent((ActivitiEvent) any());

		ProcessInstance processInstance = activitiRule.getRuntimeService()
				.startProcessInstanceByKey(processDefinitionKey);

		assertNotNull(processInstance);


		//Assert in seconds and execute/action timer
		unitTestHelpers.assertTimerJob(1, 5, TIME_UNIT_MINUTE, true);
		
		verify(timerFiredEventListener, times(1)).onEvent((ActivitiEvent) any());

		unitTestHelpers.assertNullProcessInstance(processInstance.getProcessInstanceId());
	}

}