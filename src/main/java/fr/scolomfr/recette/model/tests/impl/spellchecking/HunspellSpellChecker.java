/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  MENESR (DNE), J.Dornbusch
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package fr.scolomfr.recette.model.tests.impl.spellchecking;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.atlascopco.hunspell.Hunspell;

@Component
@PropertySource("classpath:/hunspell/language.properties")
public class HunspellSpellChecker implements SpellChecker {

	private static final String HUNSPELL_DIR = "hunspell/";
	private Map<String, Hunspell> spellers = new HashMap<>();

	@Value("${fr.dic}")
	String frDic;
	@Value("${fr.aff}")
	String frAff;
	@Value("${en.dic}")
	String enDic;
	@Value("${en.aff}")
	String enAff;

	private void addDictionay(String language, String dic, String aff) throws NoDictionaryForLanguageException {
		URL dicUrl = this.getClass().getClassLoader().getResource(HUNSPELL_DIR + dic);
		URL affUrl = this.getClass().getClassLoader().getResource(HUNSPELL_DIR + aff);
		if (null == dicUrl || null == affUrl) {
			throw new NoDictionaryForLanguageException("There's no HunspellDictionaryForLanguage " + language);
		}
		spellers.put(language, new Hunspell(dicUrl.getPath(), affUrl.getPath()));

	}

	@Override
	public boolean spell(String label, String language) throws NoDictionaryForLanguageException {
		if (null == spellers.get(language)) {
			if (language.equals("en")) {
				addDictionay(language, enDic, enAff);
			}
			if (language.equals("fr")) {
				addDictionay(language, frDic, frAff);
			}
		}
		return spellers.get(language).spell(label);
	}

	@PreDestroy
	public void close() {
		for (Iterator<String> iterator = spellers.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();

			spellers.get(key).close();
		}

	}
}
