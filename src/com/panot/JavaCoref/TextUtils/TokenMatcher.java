package com.panot.TextUtils;

import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class TokenMatcher {

	private TokenMatcher() {}

	/***
	  * Set CoreAnnotations.CharacterOffset[Begin|End]Annotation is each token in the Annotation
	  */
	public static boolean SetOffset(Annotation annotation, String text) {
		if (annotation.has(CoreAnnotations.SentencesAnnotation.class) == null) {
			return false;
		}

		int currentIndex = 0;

		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

			for (int j = 0; j < tokens.size(); j++) {
				CoreMap token = tokens.get(j);
				String word = token.get(CoreAnnotation.TextAnnotation.class);

				int nextIndex = text.indexOf(word, currentIndex);
				if (nextIndex < currentIndex) {
					return false;
				}

				int offsetBegin = nextIndex;
				int offsetEnd   = nextIndex + word.length();

				token.set(CoreAnnotations.CoreAnnotations.CharacterOffsetBeginAnnotation.class, offsetBegin);
				token.set(CoreAnnotations.CoreAnnotations.CharacterOffsetEndAnnotation.class  , offsetEnd);

				currentIndex = offsetEnd;
			}
		}

		return true;
	}
}

