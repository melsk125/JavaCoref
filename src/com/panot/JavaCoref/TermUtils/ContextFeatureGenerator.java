package com.panot.JavaCoref.TermUtils;

import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class ContextFeatureGenerator {
	private ContextFeatureGenerator() {}

	public static void populate(List<CoreLabel> sentenceTokens, int tokenI, Map<String, String> map) {

		for (int startOffset = -2; startOffset <= 2; startOffset++)
			populateFeatures(CoreAnnotations.TextAnnotation.class, sentenceTokens, tokenI, startOffset, 1, map);

		for (int startOffset = -1; startOffset <= 1; startOffset++)
			populateFeatures(CoreAnnotations.TextAnnotation.class, sentenceTokens, tokenI, startOffset, 2, map);

		for (int startOffset = -2; startOffset <= 2; startOffset++)
			populateFeatures(CoreAnnotations.PartOfSpeechAnnotation.class, sentenceTokens, tokenI, startOffset, 1, map);

		for (int startOffset = -2; startOffset <= 1; startOffset++)
			populateFeatures(CoreAnnotations.PartOfSpeechAnnotation.class, sentenceTokens, tokenI, startOffset, 2, map);

		for (int startOffset = -2; startOffset <= 0; startOffset++)
			populateFeatures(CoreAnnotations.PartOfSpeechAnnotation.class, sentenceTokens, tokenI, startOffset, 3, map);
		
	}

	public static void populateFeatures(Class cl, List<CoreLabel> sentenceTokens, int tokenI, int startOffset, int size, Map<String, String> map) {
		int start = tokenI + startOffset;

		if (start < 0) return;
		if (size < 1) return;
		if (start + size > sentenceTokens.size()) return;

		StringBuilder keyString = new StringBuilder();
		StringBuilder valString = new StringBuilder();

		keyString.append("w[" + startOffset + "]");
		for (int i = 1; i < size; i++) {
			keyString.append("|w[" + (startOffset + i) + "]");
		}

		valString.append(sentenceTokens.get(start).get(cl));
		for (int i = 1; i < size; i++) {
			valString.append("|" + sentenceTokens.get(start + i).get(cl));
		}

		map.put(keyString.toString(), valString.toString());
	}
}
