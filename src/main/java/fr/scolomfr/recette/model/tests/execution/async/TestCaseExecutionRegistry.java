package fr.scolomfr.recette.model.tests.execution.async;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import fr.scolomfr.recette.model.tests.execution.TestCaseExecutionTracker;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result;
import fr.scolomfr.recette.model.tests.organization.TestCase;
import fr.scolomfr.recette.utils.log.Log;

@Component
public class TestCaseExecutionRegistry implements TestCaseExecutionTracker {

	@Log
	Logger logger;

	private static Integer counter = 0;
	private static Map<Integer, Thread> executions = new HashMap<>();
	private static Map<Integer, TestCase> runningTestCases = new HashMap<>();
	private Queue<Integer> markedForDeletion = new PriorityBlockingQueue<>();

	public TestCaseExecutionRegistry() {
		counter = 0;
	}

	public Integer newTestCaseExecution(TestCase testCase) {
		cleanOldTestCases();
		Object lock = new Object();
		synchronized (lock) {
			counter++;
			testCase.setExecutionIdentifier(counter);
			testCase.setExecutionTracker(this);
			Thread thread = new Thread(testCase);
			thread.start();
			executions.put(counter, thread);
			runningTestCases.put(counter, testCase);
			return counter;
		}
	}

	private void cleanOldTestCases() {
		while (!markedForDeletion.isEmpty()) {
			Integer idToDelete = markedForDeletion.poll();
			logger.info(MessageFormat.format("Thread {0} eligible for garbage collection.", idToDelete));
			executions.remove(idToDelete);
			runningTestCases.remove(idToDelete);
		}

	}

	public TestCase getTestCase(final Integer executionIdentifier) {
		if (runningTestCases.containsKey(executionIdentifier))
			return runningTestCases.get(executionIdentifier);
		return null;
	}

	@Override
	public void markForFutureDeletion(Integer executionIdentifier) {
		markedForDeletion.add(executionIdentifier);

	}

	@Override
	public void notify(Message message) {
		// Nothing : only for console mode execution tracking
	}

	@Override
	public void notifyTestCaseTermination(Result result) {
		// Nothing : only for console mode execution tracking

	}

}
