package edu.stanford.nlp.dcoref.sievepasses;

import java.util.Set;

import edu.stanford.nlp.dcoref.CorefCluster;
import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.dcoref.Document;
import edu.stanford.nlp.dcoref.Mention;
import edu.stanford.nlp.dcoref.Semantics;

public class TestSieve extends DeterministicCorefSieve {
	public TestSieve() {
		super();
		System.err.println("Hello, TestSieve");
	}

	public boolean skipThisMention(Document document, Mention m1, CorefCluster c, Dictionaries dict) {
		return false;
	}

	public boolean coreferent(Document document, CorefCluster mentionCluster,
		CorefCluster potentialAntecedent,
		Mention mention2,
		Mention ant,
		Dictionaries dict,
		Set<Mention> roleSet,
		Semantics semantics) throws Exception {
		return true;
	}
}
