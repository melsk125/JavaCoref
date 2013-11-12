package edu.stanford.nlp.dcoref.sievepasses;

import java.util.Set;

import edu.stanford.nlp.dcoref.CorefCluster;
import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.dcoref.Document;
import edu.stanford.nlp.dcoref.Mention;
import edu.stanford.nlp.dcoref.Semantics;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class AuthorProposalSieve extends DeterministicCorefSieve {
	public AuthorProposalSieve() {
		super();
		System.err.println("Using AuthorProposalSieve");
	}

	public boolean skipThisMention(Document document, Mention m1, CorefCluster c, Dictionaries dict) {

		// Everything goes to coreferent()

		return false;
	}

	public boolean coreferent(Document document, CorefCluster mentionCluster,
		CorefCluster potentialAntecedent,
		Mention mention2,
		Mention ant,
		Dictionaries dict,
		Set<Mention> roleSet,
		Semantics semantics) throws Exception {
		
		String headWordMention = mention2.headWord.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
		String headWordAnt     =      ant.headWord.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();

		// May change to return false when both head words are unsimilar
		if (headWordMention != headWordAnt) return false;


		boolean potentialAuthorProposalMention = false;
		boolean potentialAuthorProposalAnt     = false;

		// Check if mention2 is modified with first-person possessive pronoun
		String firstWordMention = mention2.originalSpan.get(0).get(CoreAnnotations.TextAnnotation.class).toLowerCase();
		if (checkFirstPersonPossessivePronoun(firstWordMention, dict))
			potentialAuthorProposalMention = true;

		// Check if mention2 is an object of a construct with first person pronoun as subject
		boolean isObjectMention = true;
		IndexedWord subjectMention = getSubjectIndexedWord(mention2);
		if (subjectMention != null) {
			String subjectWordMention = subjectMention.get(CoreAnnotations.TextAnnotation.class).toLowerCase();
			if (dict.firstPersonPronouns.contains(subjectWordMention))
				potentialAuthorProposalMention = true;
		}

		// Check if ant is modified with first-person possessive pronoun
		String firstWordAnt = ant.originalSpan.get(0).get(CoreAnnotations.TextAnnotation.class).toLowerCase();
		if (checkFirstPersonPossessivePronoun(firstWordAnt, dict))
			potentialAuthorProposalAnt = false;

		// Check if ant is an object of a construct with first person pronoun as subject
		boolean isObjectAnt = true;
		IndexedWord subjectAnt = getSubjectIndexedWord(ant);
		if (subjectAnt != null) {
			String subjectWordAnt = subjectAnt.get(CoreAnnotations.TextAnnotation.class).toLowerCase();
			if (dict.firstPersonPronouns.contains(subjectWordAnt))
				potentialAuthorProposalAnt = true;
		}

		if (potentialAuthorProposalMention && potentialAuthorProposalAnt) {
			System.err.println(mention2.toString());
			System.err.println(ant.toString());
			System.err.println();
		}

		return (potentialAuthorProposalMention && potentialAuthorProposalAnt);
	}

	private static IndexedWord getSubjectIndexedWord(Mention mention) {
		if (!mention.isDirectObject)
			return null;
		SemanticGraph dependency = mention.dependency;
		IndexedWord dependingVerb = mention.dependingVerb;
		for (SemanticGraphEdge edge : dependency.outgoingEdgeList(dependingVerb)) {
			if (edge.getRelation().toString() == "nsubj") {
				return edge.getTarget();
			}
		}
		return null;
	}

	private static boolean checkFirstPersonPossessivePronoun(String word, Dictionaries dict) {
		if (dict.firstPersonPronouns.contains(word) && dict.possessivePronouns.contains(word))
			return true;
		return false;
	}
}
