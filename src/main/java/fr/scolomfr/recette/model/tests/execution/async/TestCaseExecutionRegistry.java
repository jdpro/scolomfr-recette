package fr.scolomfr.recette.model.tests.execution.async;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import fr.scolomfr.recette.model.tests.execution.result.Result;
import fr.scolomfr.recette.model.tests.organization.TestCase;

@Component
public class TestCaseExecutionRegistry {

	private static Integer counter = 0;
	private static Map<Integer, Thread> executions = new HashMap<>();
	private static Map<Integer, TestCase> runningTestCases = new HashMap<>();

	public TestCaseExecutionRegistry() {
		counter = 0;
	}

	public Integer newTestCaseExecution(TestCase testCase) {
		Object lock = new Object();
		synchronized (lock) {
			counter++;
			testCase.setExecutionIdentifier(counter);
			testCase.setExecutionRegistry(this);
			Thread thread = new Thread(testCase);
			thread.start();
			executions.put(counter, thread);
			runningTestCases.put(counter, testCase);
			return counter;
		}
	}

	public TestCase getTestCase(final Integer executionIdentifier) {
		if (runningTestCases.containsKey(executionIdentifier))
			return runningTestCases.get(executionIdentifier);
		return null;
	}

}
