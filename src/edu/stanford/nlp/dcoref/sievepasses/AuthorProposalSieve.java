package edu.stanford.nlp.dcoref.sievepasses;

import java.util.Set;

import edu.stanford.nlp.dcoref.CorefCluster;
import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.dcoref.Document;
import edu.stanford.nlp.dcoref.Mention;
import edu.stanford.nlp.dcoref.Semantics;
import edu.stanford.nlp.ling.CoreAnnotations;

public class AuthorProposalSieve extends DeterministicCorefSieve {
	public AuthorProposalSieve() {
		super();
		System.err.println("Using AuthorProposalSieve");
	}

	public boolean skipThisMention(Document document, Mention m1, CorefCluster c, Dictionaries dict) {
		String firstWord = m1.originalSpan.get(0).get(CoreAnnotations.TextAnnotation.class).toLowerCase();
		if (dict.firstPersonPronouns.contains(firstWord) && dict.possessivePronouns.contains(firstWord))
			return false;
		return true;
	}

	public boolean coreferent(Document document, CorefCluster mentionCluster,
		CorefCluster potentialAntecedent,
		Mention mention2,
		Mention ant,
		Dictionaries dict,
		Set<Mention> roleSet,
		Semantics semantics) throws Exception {
		
		// mention2 already starts with our/my
		// Check only ant
		
		String headWordMention = mention2.headWord.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
		String headWordAnt = ant.headWord.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();

		/*
		System.err.println("mf: " + mention2.spanToString());
		System.err.println("af: " + ant.spanToString());
		System.err.println();
		System.err.println("mh: " + headWordMention);
		System.err.println("ah: " + headWordAnt);
		System.err.println();
		*/
	
		if (headWordMention != headWordAnt) return false;
	
		String firstWordAnt = ant.originalSpan.get(0).get(CoreAnnotations.TextAnnotation.class).toLowerCase();
		if (dict.firstPersonPronouns.contains(firstWordAnt) && dict.possessivePronouns.contains(firstWordAnt)) {
			System.err.println("mf: " + mention2.spanToString());
			System.err.println("af: " + ant.spanToString());
			System.err.println();
			return true;
		}



		return false;
	}
}
