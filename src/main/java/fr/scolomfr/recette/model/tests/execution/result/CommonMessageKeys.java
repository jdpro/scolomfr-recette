package fr.scolomfr.recette.model.tests.execution.result;

public enum CommonMessageKeys {
	TEST_PARAMETERS("test_parameters"), FILE_AVAILABLE("file_available"), SPARQL_REQUEST_AVAILABLE(
			"sparql_request_available"), FILE_OPENED("file_opened"), FILE_FORMAT(
					"file_format"), NO_EXECUTION("no_execution"), NO_TEST("no_test"), QSKOS_ERROR("qskos_error");

	private String value;

	private CommonMessageKeys(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
