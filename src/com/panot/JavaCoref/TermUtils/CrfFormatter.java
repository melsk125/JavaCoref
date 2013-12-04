package com.panot.JavaCoref.TermUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.dcoref.Document;
import edu.stanford.nlp.dcoref.Mention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class CrfFormatter {
	public static class CrfToken {
		public Map<String, String> features;

		public CrfToken() {
			features = new HashMap<String, String>();
		}
	}

	public List<List<CrfToken>> crfTokens;

	public CrfFormatter() {
		crfTokens = new ArrayList<List<CrfToken>>();
	}

	public void addDocument(Document doc) {
		Annotation ann = doc.annotation;

		for (CoreMap sentence : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
			List<CoreLabel> sentenceTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
			crfTokens.add(compileSentence(sentenceTokens));
		}
	}

	private static List<CrfToken> compileSentence(List<CoreLabel> sentenceTokens) {
		List<CrfToken> sentenceCrfTokens = new ArrayList<CrfToken>();

		for (int tokenI = 0; tokenI < sentenceTokens.size(); tokenI++) {
			CoreMap token = sentenceTokens.get(tokenI);
			
			CrfToken crfToken = new CrfToken();

			// Orthographic features
			OrthographicFeatureGenerator.populate(token, crfToken.features);

			// Context features
			ContextFeatureGenerator.populate(sentenceTokens, tokenI, crfToken.features);
		}

		return sentenceCrfTokens;
	}

	public String toString() {
		return "";
	}
}
