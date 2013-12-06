package com.panot.JavaCoref;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.stanford.nlp.dcoref.Document;
import edu.stanford.nlp.dcoref.Mention;
import edu.stanford.nlp.util.IntPair;

public class MentionStatCounter {
	private double matchCount;
	private double goldMentionCount;
	private double mentionCount;

	public MentionStatCounter() {
		matchCount = 0.0;
		goldMentionCount = 0.0;
		mentionCount = 0.0;
	}

	public double getPrecision() {
		return mentionCount == 0.0 ? 0.0 : matchCount / mentionCount;
	}

	public double getRecall() {
		return goldMentionCount == 0.0 ? 0.0 : matchCount / goldMentionCount;
	}

	public double getF1() {
		double p = getPrecision();
		double r = getRecall();
		return (p + r) == 0 ? 0.0 : 2.0 * p * r / (p + r);
	}

	public void addDocument(Document doc) {
		List<List<Mention>> orderedMentionsBySentence = doc.predictedOrderedMentionsBySentence;
		List<List<Mention>> orderedGoldMentionsBySentence = doc.goldOrderedMentionsBySentence;

		for (int sentI = 0; sentI < orderedMentionsBySentence.size(); sentI++) {
			Set<IntPair> mentionSet = new HashSet<IntPair>();
			Set<IntPair> goldMentionSet = new HashSet<IntPair>();

			for (Mention m : orderedMentionsBySentence.get(sentI)) {
				mentionSet.add(new IntPair(m.startIndex, m.endIndex));
			}

			for (Mention m : orderedGoldMentionsBySentence.get(sentI)) {
				goldMentionSet.add(new IntPair(m.startIndex, m.endIndex));
			}

			mentionCount += mentionSet.size();
			goldMentionCount += goldMentionSet.size();

			mentionSet.retainAll(goldMentionSet);
			matchCount += mentionSet.size();
		}
	}

	public static String prettyPrint(MentionStatCounter counter) {
		NumberFormat nf = new DecimalFormat("00.0");

		double p  = counter.getPrecision();
		double r  = counter.getRecall();
		double f1 = counter.getF1();

		String sp  = nf.format(p * 100);
		String sr  = nf.format(r * 100);
		String sf1 = nf.format(f1* 100);

		StringBuilder os = new StringBuilder();

		os.append("P = " + sp + "\tR = " + sr + "\tF1 = " + sf1 + "\n");

		return os.toString();
	}
}
