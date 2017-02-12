package fr.scolomfr.recette.model.sources;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Converts manifest file from yaml format to inner memory representation
 */
@Component
public class YamlManifestParser implements ManifestParser {

	private Manifest manifest;

	@Override
	public Manifest getManifest() {
		return manifest;
	}

	@Override
	public ManifestParser load(InputStream manifestFile) throws IOException {
		Constructor constructor = new Constructor(Manifest.class);// Car.class
																	// is root
		TypeDescription carDescription = new TypeDescription(Manifest.class);
		carDescription.putListPropertyType("content", LinkedList.class);
		constructor.addTypeDescription(carDescription);
		Yaml yaml = new Yaml(constructor);
		manifest = (Manifest) yaml.load(manifestFile);

		return this;
	}

}
