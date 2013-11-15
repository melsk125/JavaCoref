package edu.stanford.nlp.dcoref.sievepasses;

import java.util.Set;

import edu.stanford.nlp.dcoref.CorefCluster;
import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.dcoref.Document;
import edu.stanford.nlp.dcoref.Mention;
import edu.stanford.nlp.dcoref.Semantics;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class ParenthesisSieve extends DeterministicCorefSieve {
	public ParenthesisSieve() {
		super();
		System.err.println("Using ParenthesisSieve");
	}

	public boolean skipThisMention(Document document, Mention m1, CorefCluster c, Dictionaries dict) {
		// Check if the mention is in a parenthesis
		int sentNum = m1.sentNum;
		int startIndex = m1.startIndex;
		int endIndex = m1.endIndex;

		CoreMap sentence = document.annotation.get(CoreAnnotations.SentencesAnnotation.class).get(sentNum);

		// Get before
		if (startIndex == 0)
			return true;
		CoreMap beforeToken = sentence.get(CoreAnnotations.TokensAnnotation.class).get(startIndex - 1);
		String beforeWord = beforeToken.get(CoreAnnotations.TextAnnotation.class);
		if (beforeWord != "-LRB-")
			return true;

		// Get after
		if (endIndex + 1 >= sentence.get(CoreAnnotations.TokensAnnotation.class).size())
			return true;
		CoreMap afterToken = sentence.get(CoreAnnotations.TokensAnnotation.class).get(endIndex);
		String afterWord = afterToken.get(CoreAnnotations.TextAnnotation.class);
		if (afterWord != "-RRB-")
			return true;

		// System.err.println("sentNum = " + sentNum);
		// System.err.println("startIndex = " + startIndex);
		// System.err.println("endIndex = " + endIndex);
		// System.err.println(m1.originalSpan.toString());

		return false;
	}

	public boolean coreferent(Document document, CorefCluster mentionCluster,
		CorefCluster potentialAntecedent,
		Mention mention2,
		Mention ant,
		Dictionaries dict,
		Set<Mention> roleSet,
		Semantics semantics) throws Exception {
		// Link the mention in potentialAntecedent that is adjacent to the mention2
		
		// If there is any mention in potentialAntecent that ends just before the -LRB-, link that!
		
		int sentNum = mention2.sentNum;
		int startIndex = mention2.startIndex;

		if (startIndex < 2) return false;

		for (Mention m1 : potentialAntecedent.getCorefMentions()) {
			if (m1.sentNum != sentNum) continue;
			if (m1.endIndex == startIndex - 2) {
				System.err.println(mention2.originalSpan.toString());
				System.err.println(m1.originalSpan.toString());
				return true;
			}
		}

		return false;
	}
}


