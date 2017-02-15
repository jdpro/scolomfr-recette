package fr.scolomfr.recette.tests.execution.result;

public enum CommonMessageKeys {
	FILE_PROVIDED("file_provided"), FILE_OPENING("file_opening"), FILE_FORMAT("file_format");

	private String value;

	private CommonMessageKeys(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
