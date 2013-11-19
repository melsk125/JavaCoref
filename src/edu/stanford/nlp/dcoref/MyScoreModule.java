package edu.stanford.nlp.dcoref;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.stanford.nlp.dcoref.Document;
import edu.stanford.nlp.dcoref.ScorerBCubed.BCubedType;

public class MyScoreModule {
	public ScorerPairwise scorePairwise;
	public ScorerMUC scoreMUC;
	public ScorerBCubed scoreBCubed;

	public MyScoreModule() {
		scorePairwise = new ScorerPairwise();
		scoreMUC = new ScorerMUC();
		scoreBCubed = new ScorerBCubed(BCubedType.Bconll);
	}

	public void calculateScore(Document document) {
		scorePairwise.calculateScore(document);
		scoreMUC.calculateScore(document);
		scoreBCubed.calculateScore(document);
	}

	public String toString() {
		StringBuilder os = new StringBuilder();

		os.append("Pairwise:\n");
		os.append(scorerToString(scorePairwise));

		os.append("MUC:\n");
		os.append(scorerToString(scoreMUC));

		os.append("BCubed:\n");
		os.append(scorerToString(scoreBCubed));

		return os.toString();
	}

	public static String scorerToString(CorefScorer scorer) {
		NumberFormat nf = new DecimalFormat("00.0");

		double r  = scorer.getRecall();
		double p  = scorer.getPrecision();
		double f1 = scorer.getF1();

		String sr  = nf.format(r*100);
		String sp  = nf.format(p*100);
		String sf1 = nf.format(f1*100);

		StringBuilder os = new StringBuilder();
		os.append("P\tR\tF1\n");
		os.append(sp + "\t" + sr + "\t" + sf1 + "\n");

		return os.toString();
	}
}
