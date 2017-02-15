package fr.scolomfr.recette.tests.execution.result;

public enum CommonMessageKeys {
	FILE_PROVIDED("Fichier fourni"), FILE_OPENING("Ouverture du fichier"), FILE_FORMAT("Format du fichier");

	private String value;

	private CommonMessageKeys(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
