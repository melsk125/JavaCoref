package com.panot.JavaCoref.TermUtils;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.dcoref.CorefMentionFinder;
import edu.stanford.nlp.dcoref.Dictionaries;
import edu.stanford.nlp.dcoref.Mention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class TermAsMentionFinder implements CorefMentionFinder {
	protected List<List<String>> tags;
	protected int maxID = -1;

	public TermAsMentionFinder() {

	}

	public void setTags(List<List<String>> tags) {
		this.tags = tags;
	}

	public int getMaxID() {
		return maxID;
	}

	@Override
	public List<List<Mention>> extractPredictedMentions(Annotation doc, int _maxID, Dictionaries dict) {
		List<List<Mention>> termMentions = new ArrayList<List<Mention>>();

		List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
		for (int sentI = 0; sentI < sentences.size(); sentI++) {
			List<Mention> mentions = new ArrayList<Mention>();
			termMentions.add(mentions);

			CoreMap s = sentences.get(sentI);
			List<CoreLabel> tokens = s.get(CoreAnnotations.TokensAnnotation.class);

			SemanticGraph dependency = s.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);

			String prevTag = "O";
			int beginIndex = -1;
			List<String> sentTags = tags.get(sentI);
			for (int tokenI = 0; tokenI < sentTags.size(); tokenI++) {
				String tag = sentTags.get(tokenI);
				if (tag.equals("B")) {
					if (!prevTag.equals("O")) {
						int mentionId = ++maxID;
						int endIndex = tokenI;
						Mention m = new Mention(mentionId, beginIndex, endIndex, dependency, new ArrayList<CoreLabel>(tokens.subList(beginIndex, endIndex)));
						mentions.add(m);
					}
					beginIndex = tokenI;
				} else if (tag.equals("I")) {
					if (prevTag.equals("O")) {
						beginIndex = tokenI;
					}
				} else {
					if (!prevTag.equals("O")) {
						int mentionId = ++maxID;
						int endIndex = tokenI;
						Mention m = new Mention(mentionId, beginIndex, endIndex, dependency, new ArrayList<CoreLabel>(tokens.subList(beginIndex, endIndex)));
						mentions.add(m);
					}
				}
			}

			if (!prevTag.equals("O")) {
				int mentionId = ++maxID;
				int endIndex = sentTags.size();
				Mention m = new Mention(mentionId, beginIndex, endIndex, dependency, new ArrayList<CoreLabel>(tokens.subList(beginIndex, endIndex)));
				mentions.add(m);
			}
		}

		return termMentions;
	}
}
