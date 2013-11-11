package com.panot.JavaCoref.TextUtils;

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
		if (!annotation.has(CoreAnnotations.SentencesAnnotation.class)) {
			return false;
		}

		int currentIndex = 0;

		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

			for (int j = 0; j < tokens.size(); j++) {
				CoreMap token = tokens.get(j);
				String word = token.get(CoreAnnotations.TextAnnotation.class);

				if (token.has(CoreAnnotations.OriginalTextAnnotation.class)) {
					word = token.get(CoreAnnotations.OriginalTextAnnotation.class);
				}

				if (word == "-LRB-") {
					word = "(";
				} else if (word == "-RRB-") {
					word = ")";
				} else if (word == "-LSB-") {
					word = "[";
				} else if (word == "-RSB-") {
					word = "]";
				} else if (word == "-LCB-") {
					word = "{";
				} else if (word == "-RCB-") {
					word = "}";
				}

				int offsetBegin, offsetEnd;

				int nextIndex = text.indexOf(word, currentIndex);
				if (nextIndex < 0) {
					// Heuristic solution
					// If text starts with non-alnum, the whole token should be non-alnum
					// Else, the whole token should be all alnum
					//
					// How to do it? we check one character at a time!
					
					// Find beginning of the token
					
					//System.err.println("index: " + word);
					int index = currentIndex;
					//System.err.print(text.charAt(index));
					while (Character.isWhitespace(text.codePointAt(index))) {
						index ++;
						//System.err.print(text.charAt(index));
					}
					nextIndex = index;

					int typeChecker = text.codePointAt(index);
					if (Character.isLetter(typeChecker) || Character.isDigit(typeChecker)) {
						while (Character.isLetter(typeChecker) || Character.isDigit(typeChecker)) {
							index ++;
							//System.err.print(text.charAt(index));
							typeChecker = text.codePointAt(index);
						}
					} else {
						while (!Character.isLetter(typeChecker) && !Character.isDigit(typeChecker)) {
							index ++;
							//System.err.print(text.charAt(index));
							typeChecker = text.codePointAt(index);
						}
					}
					//System.err.println();

					offsetBegin = nextIndex;
					offsetEnd   = index;
				} else {
					offsetBegin = nextIndex;
					offsetEnd   = nextIndex + word.length();
				}
				token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, offsetBegin);
				token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class  , offsetEnd);

				currentIndex = offsetEnd;

				if (!text.substring(offsetBegin, offsetEnd).equals(word))
					System.err.println(text.substring(offsetBegin, offsetEnd) + " " + word + " " + offsetBegin + " " + offsetEnd);
			}
		}

		return true;
	}
}

