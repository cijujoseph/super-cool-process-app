package com.alfresco.aps.supercoolapp.process;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alfresco.aps.testutils.AbstractBpmnTest;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import static com.alfresco.aps.testutils.TestUtilsConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:activiti.cfg.xml", "classpath:common-beans-and-mocks.xml" })
@TestPropertySource(value="classpath:local-dev-test.properties")
public class UserTaskUnitTest extends AbstractBpmnTest {
	
	static {
		appName = "Super Cool App";
		processDefinitionKey = "SuperCoolProcess";
	}

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

}