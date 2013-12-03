package com.panot.JavaCoref.TermUtils;

import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class OrthographicFeatureGenerator {
	private OrthographicFeatureGenerator() {}
	
	public static void populate(CoreMap token, Map<String, String> map) {
		String word = token.get(CoreAnnotations.TextAnnotation.class);

		if (token.has(CoreAnnotations.OriginalTextAnnotation.class)) {
			word = token.get(CoreAnnotations.OriginalTextAnnotation.class);
		}

		map.put("text", text(word));
		map.put("lowerCased", lowerCased(word));

		for (int i = 3; i <= 5; i++) {
			String pref = prefix(word, i);
			if (pref != null) {
				map.put("prefix" + i, pref);
			}
		}

		for (int i = 3; i <= 5; i++) {
			String suff = suffix(word, i);
			if (suff != null) {
				map.put("suffix" + i, suff);
			}
		}

		map.put("stem", stem(token));

		if (isPairOfDigits(word))
			map.put("isPairOfDigits", "");

		if (isFourDigits(word))
			map.put("isFourDigits", "");

		if (isLettersAndDigits(word))
			map.put("isLettersAndDigits", "");

		if (isDigitsAndHyphens(word))
			map.put("isDigitsAndHyphens", "");

		if (isDigitsAndSlashes(word))
			map.put("isDigitsAndSlashes", "");

		if (isDigitsAndColons(word))
			map.put("isDigitsAndColons", "");

		if (isDigitsAndDots(word))
			map.put("isDigitsAndDots", "");

		if (isUpperCaseAndDots(word))
			map.put("isUpperCaseAndDots", "");

		if (isInitialUpperCase(word))
			map.put("isInitialUpperCase", "");

		if (isOnlyUpperCase(word))
			map.put("isOnlyUpperCase", "");

		if (isOnlyLowerCase(word))
			map.put("isOnlyLowerCase", "");

		if (isOnlyDigits(word))
			map.put("isOnlyDigits", "");

		if (containsUpperCase(word))
			map.put("containsUpperCase", "");

		if (containsLowerCase(word))
			map.put("containsLowerCase", "");

		if (containsDigits(word))
			map.put("containsDigits", "");

		if (containsNonAlphaNum(word))
			map.put("containsNonAlphaNum", "");

		// if (matchesDateRegularExpression(word))
		// 	map.put("matchesDateRegularExpression", "");

		// map.put("pattern", pattern(word));
		// map.put("collapsedPattern", collapsedPattern(word));
	}

	public static String text(String word) {
		return word;
	}

	public static String lowerCased(String word) {
		return word.toLowerCase();
	}

	public static String prefix(String word, int size) {
		if (size > word.length()) return null;
		else return word.substring(0, size);
	}

	public static String suffix(String word, int size) {
		if (size > word.length()) return null;
		return word.substring(word.length() - size);
	}

	public static String stem(CoreMap token) {
		return token.get(CoreAnnotations.StemAnnotation.class);
	}

	public static boolean isPairOfDigits(String word) {
		if (word.length() != 2) return false;
		for (char ch : word.toCharArray()) {
			if (!Character.isDigit(ch))
				return false;
		}
		return true;
	}

	public static boolean isFourDigits(String word) {
		if (word.length() != 4) return false;
		for (char ch : word.toCharArray()) {
			if (!Character.isDigit(ch))
				return false;
		}
		return true;
	}

	public static boolean isLettersAndDigits(String word) {
		for (char ch : word.toCharArray()) {
			if (!Character.isLetterOrDigit(ch))
				return false;
		}
		return true;
	}

	public static boolean isDigitsAndHyphens(String word) {
		for (char ch : word.toCharArray()) {
			if (!Character.isDigit(ch) && ch != '-')
				return false;
		}
		return true;
	}

	public static boolean isDigitsAndSlashes(String word) {
		for (char ch : word.toCharArray()) {
			if (!Character.isDigit(ch) && ch != '/')
				return false;
		}
		return true;
	}

	public static boolean isDigitsAndColons(String word) {
		for (char ch : word.toCharArray()) {
			if (!Character.isDigit(ch) && ch != ':')
				return false;
		}
		return true;
	}

	public static boolean isDigitsAndDots(String word) {
		for (char ch : word.toCharArray()) {
			if (!Character.isDigit(ch) && ch != '.')
				return false;
		}
		return true;
	}

	public static boolean isUpperCaseAndDots(String word) {
		for (char ch : word.toCharArray()) {
			if (!Character.isUpperCase(ch) && ch != '.')
				return false;
		}
		return true;
	}

	public static boolean isInitialUpperCase(String word) {
		if (!Character.isUpperCase(word.charAt(0)))
			return false;
		return true;
	}

	public static boolean isOnlyUpperCase(String word) {
		for (char ch : word.toCharArray()) {
			if (!Character.isUpperCase(ch))
				return false;
		}
		return true;
	}

	public static boolean isOnlyLowerCase(String word) {
		for (char ch : word.toCharArray()) {
			if (!Character.isLowerCase(ch))
				return false;
		}
		return true;
	}

	public static boolean isOnlyDigits(String word) {
		for (char ch : word.toCharArray()) {
			if (!Character.isDigit(ch))
				return false;
		}
		return true;
	}

	public static boolean isOnlyNonAlphaNum(String word) {
		for (char ch : word.toCharArray()) {
			if (Character.isLetterOrDigit(ch))
				return false;
		}
		return true;
	}

	public static boolean containsUpperCase(String word) {
		for (char ch : word.toCharArray()) {
			if (Character.isUpperCase(ch))
				return true;
		}
		return false;
	}

	public static boolean containsLowerCase(String word) {
		for (char ch : word.toCharArray()) {
			if (Character.isLowerCase(ch))
				return true;
		}
		return false;
	}

	public static boolean containsDigits(String word) {
		for (char ch : word.toCharArray()) {
			if (Character.isDigit(ch))
				return true;
		}
		return false;
	}

	public static boolean containsNonAlphaNum(String word) {
		for (char ch : word.toCharArray()) {
			if (Character.isLetterOrDigit(ch))
				return true;
		}
		return false;
	}

	// public static boolean matchesDateRegularExpression(String word) {

	// }

	// public static String pattern(String word) {

	// }

	// public static String collapsedPattern(String word) {

	// }
}

