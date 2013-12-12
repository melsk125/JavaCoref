package com.panot.JavaCoref.TermUtils;

import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class ContextFeatureGenerator {
	private ContextFeatureGenerator() {}

	public static void populate(List<CoreLabel> sentenceTokens, int tokenI, Map<String, String> map) {

		for (int startOffset = -2; startOffset <= 2; startOffset++)
			populateFeatures(CoreAnnotations.TextAnnotation.class, sentenceTokens, tokenI, startOffset, 1, map, "w");

		for (int startOffset = -1; startOffset <= 1; startOffset++)
			populateFeatures(CoreAnnotations.TextAnnotation.class, sentenceTokens, tokenI, startOffset, 2, map, "w");

		for (int startOffset = -2; startOffset <= 2; startOffset++)
			populateFeatures(CoreAnnotations.PartOfSpeechAnnotation.class, sentenceTokens, tokenI, startOffset, 1, map, "pos");

		for (int startOffset = -2; startOffset <= 1; startOffset++)
			populateFeatures(CoreAnnotations.PartOfSpeechAnnotation.class, sentenceTokens, tokenI, startOffset, 2, map, "pos");

		for (int startOffset = -2; startOffset <= 0; startOffset++)
			populateFeatures(CoreAnnotations.PartOfSpeechAnnotation.class, sentenceTokens, tokenI, startOffset, 3, map, "pos");
		
	}

	public static void populateFeatures(Class cl, List<CoreLabel> sentenceTokens, int tokenI, int startOffset, int size, Map<String, String> map, String featureName) {
		int start = tokenI + startOffset;

		if (start < 0) return;
		if (size < 1) return;
		if (start + size > sentenceTokens.size()) return;

		StringBuilder keyString = new StringBuilder();
		StringBuilder valString = new StringBuilder();

		keyString.append(featureName + "[" + startOffset + "]");
		for (int i = 1; i < size; i++) {
			keyString.append("|" + featureName + "[" + (startOffset + i) + "]");
		}

		valString.append(sentenceTokens.get(start).get(cl));
		for (int i = 1; i < size; i++) {
			valString.append("|" + sentenceTokens.get(start + i).get(cl));
		}

		map.put(keyString.toString(), valString.toString());
	}
}
