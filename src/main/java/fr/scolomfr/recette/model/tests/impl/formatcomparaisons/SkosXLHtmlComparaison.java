/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  Direction du Numérique pour l'éducation - Ministère de l'éducation nationale, de l'enseignement supérieur et de la Recherche
 * Copyright (C) 2017 Joachim Dornbusch
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
package fr.scolomfr.recette.model.tests.impl.formatcomparaisons;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.util.Pair;
import org.springframework.util.CollectionUtils;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result.State;
import fr.scolomfr.recette.model.tests.impl.AbstractJenaTestCase;
import fr.scolomfr.recette.model.tests.impl.DuplicateErrorCodeException;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * Compare list of uris in both formats
 */
@TestCaseIndex(index = "a19")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY,
		TestParameters.Values.SKOSTYPE })
public class SkosXLHtmlComparaison extends AbstractJenaTestCase {

	@Override
	public void run() {
		int numerator = 0;
		int denominator = 0;

		Version version = getVersion();
		String format = getSkosType();
		String vocabulary = getVocabulary();
		if (null == version || StringUtils.isEmpty(format) || StringUtils.isEmpty(vocabulary)) {
			return;
		}
		progressionMessage(i18n.tr("tests.impl.data.loading.title"), 0);
		Model model = getModel(version, vocabulary, format);
		if (null == model) {
			return;
		}
		Map<String, String> prefLabelsInSkos = jenaEngine.getAllPrefLabels(model);
		Map<String, String> htmlFilePaths = null;
		if (vocabulary.equals(GLOBAL_VOCABULARY)) {
			htmlFilePaths = getFilePathsForAllVocabularies(version, "html");
		} else {
			htmlFilePaths = new HashMap<>();
			String singleHTMLFilePath = getFilePath(version, vocabulary, "html");
			if (null == singleHTMLFilePath) {
				return;
			}
			htmlFilePaths.put(vocabulary, singleHTMLFilePath);
		}

		Map<String, Document> jsoupDocuments = new HashMap<>();
		Iterator<String> it = htmlFilePaths.keySet().iterator();
		while (it.hasNext()) {
			String vocabUri = it.next();
			jsoupDocuments.put(vocabUri, getJsoupDocument(htmlFilePaths.get(vocabUri)));
		}

		Elements labels = null;
		int counter = 0;
		int nbDocs = jsoupDocuments.keySet().size();
		for (String vocabUri : jsoupDocuments.keySet()) {
			counter++;
			String htmlFilePath = htmlFilePaths.get(vocabUri);
			String docInfo = MessageFormat.format("{0} ({1}/{2})", htmlFilePath, counter, nbDocs);

			Document jsoupDocument = jsoupDocuments.get(vocabUri);
			labels = jsoupDocument.getElementsByTag("li");
			int nbLabels = labels.size();
			int step = Math.min(50, nbLabels / 50 + 1);
			for (int i = 0; i < nbLabels; i++) {
				if (i % step == 0) {
					progressionMessage(docInfo, (float) i / (float) nbLabels * 100.f);
				}
				refreshComplianceIndicator(result, (denominator - numerator), denominator);
				denominator++;
				Element labelElement = labels.get(i);
				String rawTextContent = labelElement.text();
				String errorCode = null;

				List<Pair<HTMLFragmentIndex, String>> fragments = findFragmentsIn(rawTextContent);
				List<String> uris = null;
				String htmlPrefLabel = null;
				for (Pair<HTMLFragmentIndex, String> fragment : fragments) {
					if (fragment.getFirst().equals(HTMLFragmentIndex.T)) {

						htmlPrefLabel = fragment.getSecond();
						uris = checkLabelInSkosVokabularyAndReturnUris(htmlPrefLabel, prefLabelsInSkos);
						if (uris.isEmpty()) {
							try {
								errorCode = generateUniqueErrorCode(vocabUri + MESSAGE_ID_SEPARATOR + "invalidlabel"
										+ MESSAGE_ID_SEPARATOR + htmlPrefLabel + MESSAGE_ID_SEPARATOR + rawTextContent);
							} catch (DuplicateErrorCodeException e) {
								logger.debug(ERROR_CODE_DUPLICATE, errorCode, e);
							}
							boolean ignored = errorIsIgnored(errorCode);
							result.incrementErrorCount(ignored);
							numerator++;
							Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR,
									errorCode, i18n.tr("tests.impl.a19.result.invalidlabel.title"),
									i18n.tr("tests.impl.a19.result.invalidlabel.content",
											new Object[] { htmlPrefLabel, vocabUri }));
							result.addMessage(message);
						}
					}
				}
				if (!CollectionUtils.isEmpty(uris)) {
					for (Pair<HTMLFragmentIndex, String> fragment : fragments) {
						if (fragment.getFirst().equals(HTMLFragmentIndex.TE)) {

							String htmlAltLabel = fragment.getSecond();
							boolean found = checkIfAltLabelInSkosVokabularyForUris(htmlAltLabel, uris, model);
							if (!found) {
								try {
									errorCode = generateUniqueErrorCode(
											vocabUri + MESSAGE_ID_SEPARATOR + "invalidequiv" + MESSAGE_ID_SEPARATOR
													+ htmlPrefLabel + MESSAGE_ID_SEPARATOR + rawTextContent);
								} catch (DuplicateErrorCodeException e) {
									logger.debug(ERROR_CODE_DUPLICATE, errorCode, e);
								}
								boolean ignored = errorIsIgnored(errorCode);
								result.incrementErrorCount(ignored);
								numerator++;
								Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR,
										errorCode, i18n.tr("tests.impl.a19.result.invalidequiv.title"),
										i18n.tr("tests.impl.a19.result.invalidequiv.content", new Object[] {
												htmlPrefLabel, StringUtils.join(uris, ", "), htmlAltLabel }));
								result.addMessage(message);
							}
						}
						if (fragment.getFirst().equals(HTMLFragmentIndex.NA)) {

							String htmlNote = fragment.getSecond();
							boolean found = checkIfScopeNoteInSkosVokabularyForUris(htmlNote, uris, model);
							if (!found) {
								try {
									errorCode = generateUniqueErrorCode(
											vocabUri + MESSAGE_ID_SEPARATOR + "invalidnote" + MESSAGE_ID_SEPARATOR
													+ htmlPrefLabel + MESSAGE_ID_SEPARATOR + rawTextContent);
								} catch (DuplicateErrorCodeException e) {
									logger.debug(ERROR_CODE_DUPLICATE, errorCode, e);
								}
								boolean ignored = errorIsIgnored(errorCode);
								result.incrementErrorCount(ignored);
								numerator++;
								Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR,
										errorCode, i18n.tr("tests.impl.a19.result.invalidnote.title"),
										i18n.tr("tests.impl.a19.result.invalidnote.content", new Object[] {
												htmlPrefLabel, StringUtils.join(uris, ", "), htmlNote }));
								result.addMessage(message);
							}
						}
						if (fragment.getFirst().equals(HTMLFragmentIndex.TA)) {

							String associatedLabel = fragment.getSecond();
							boolean found = checkIfAssociatedLabelInSkosVokabularyForUris(associatedLabel, uris, model);
							if (!found) {
								try {
									errorCode = generateUniqueErrorCode(
											vocabUri + MESSAGE_ID_SEPARATOR + "invalidassoc" + MESSAGE_ID_SEPARATOR
													+ htmlPrefLabel + MESSAGE_ID_SEPARATOR + rawTextContent);
								} catch (DuplicateErrorCodeException e) {
									logger.debug(ERROR_CODE_DUPLICATE, errorCode, e);
								}
								boolean ignored = errorIsIgnored(errorCode);
								result.incrementErrorCount(ignored);
								numerator++;
								Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR,
										errorCode, i18n.tr("tests.impl.a19.result.invalidassoc.title"),
										i18n.tr("tests.impl.a19.result.invalidassoc.content", new Object[] {
												htmlPrefLabel, StringUtils.join(uris, ", "), associatedLabel }));
								result.addMessage(message);
							}
						}
					}
				}

			}

		}

		progressionMessage("", 100);
		result.setState(State.FINAL);

	}

	private boolean checkIfAltLabelInSkosVokabularyForUris(String htmlAltLabel, List<String> uris, Model model) {
		for (String uri : uris) {
			if (jenaEngine.getAltLabelsForUri(uri, model, false).contains(htmlAltLabel)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkIfScopeNoteInSkosVokabularyForUris(String htmlScopeNote, List<String> uris, Model model) {
		for (String uri : uris) {
			if (jenaEngine.getScopeNotesForUri(uri, model, true).contains(StringUtils.normalizeSpace(htmlScopeNote))) {
				return true;
			}
		}
		return false;
	}

	private boolean checkIfAssociatedLabelInSkosVokabularyForUris(String htmlAssociatedTree, List<String> uris,
			Model model) {
		for (String uri : uris) {
			List<String> associatedUris = jenaEngine.getAssociatedUris(uri, model);
			for (String associatedUri : associatedUris) {
				if (jenaEngine.getPrefLabelFor(associatedUri, model).equals(htmlAssociatedTree)) {
					return true;
				}
			}
		}
		return false;
	}

	private List<String> checkLabelInSkosVokabularyAndReturnUris(String htmlLabel,
			Map<String, String> prefLabelsInSkos) {
		ArrayList<String> list = new ArrayList<>();
		String htmlLabelTrimed = htmlLabel.trim();
		Iterator<String> it = prefLabelsInSkos.keySet().iterator();
		while (it.hasNext()) {
			String uri = it.next();
			if (prefLabelsInSkos.get(uri).equals(htmlLabelTrimed)) {
				list.add(uri);
			}
		}
		return list;
	}

	private List<Pair<HTMLFragmentIndex, String>> findFragmentsIn(String rawTextContent) {
		List<Pair<HTMLFragmentIndex, String>> fragments = new ArrayList<>();
		StringBuilder fragmentBuilder = new StringBuilder();
		String remains;
		for (int i = rawTextContent.length() - 1; i >= 0; i--) {
			char character = rawTextContent.charAt(i);
			fragmentBuilder.append(character);
			Pair<HTMLFragmentIndex, String> fragmentPair;
			for (HTMLFragmentIndex index : HTMLFragmentIndex.values()) {
				fragmentPair = searchFragment(index, fragmentBuilder);
				if (fragmentPair != null) {
					fragments.add(fragmentPair);
					fragmentBuilder = new StringBuilder();
				}
			}
		}
		remains = fragmentBuilder.reverse().toString();
		fragments.add(Pair.of(HTMLFragmentIndex.T, remains.trim()));
		return fragments;
	}

	private Pair<HTMLFragmentIndex, String> searchFragment(HTMLFragmentIndex index, StringBuilder fragmentBuilder) {
		if (fragmentBuilder.toString().endsWith(": " + index.toReversedString())) {
			String fragment = fragmentBuilder.reverse().toString().substring(4);
			return Pair.of(index, fragment.trim());
		}
		return null;
	}

	private enum HTMLFragmentIndex {
		TA("TA"), NA("NA"), TE("TE"), T("T");
		private String value;

		private HTMLFragmentIndex(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

		public String toReversedString() {
			return new StringBuilder(toString()).reverse().toString();
		}
	}

}
