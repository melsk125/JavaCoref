package com.panot.JavaCoref;

import java.util.Properties;

import edu.stanford.nlp.dcoref.ACEMentionExtractor;
import edu.stanford.nlp.dcoref.CoNLLMentionExtractor;
import edu.stanford.nlp.dcoref.Constants;
import edu.stanford.nlp.dcoref.MentionExtractor;
import edu.stanford.nlp.dcoref.MUCMentionExtractor;
import edu.stanford.nlp.dcoref.SieveCoreferenceSystem;
import edu.stanford.nlp.util.StringUtils;

public class MyStanfordDCoref {

	public static void main(String[] args) throws Exception {
	    Properties props = StringUtils.argsToProperties(args);

	    // instantiate coref system
	    SieveCoreferenceSystem corefSystem = new SieveCoreferenceSystem(props);

	    // MentionExtractor
	    MentionExtractor mentionExtractor = null;
	    if (props.containsKey(Constants.MUC_PROP)) {
	    	mentionExtractor = new MUCMentionExtractor(corefSystem.dictionaries(), props, corefSystem.semantics(), corefSystem.singletonPredictor);
	    } else if (props.containsKey(Constants.ACE2004_PROP) || props.containsKey(Constants.ACE2005_PROP)) {
	    	mentionExtractor = new ACEMentionExtractor(corefSystem.dictionaries(), props, corefSystem.semantics(), corefSystem.singletonPredictor);
	    } else if (props.containsKey(Constants.CONLL2011_PROP)) {
	    	mentionExtractor = new CoNLLMentionExtractor(corefSystem.dictionaries(), props, corefSystem.semantics(), corefSystem.singletonPredictor);
	    }
	    if (mentionExtractor == null) {
	    	throw new RuntimeException("No input file specified!");
	    }

	    // If not using gold mention extractor
	    if (!props.containsKey(MyConstants.USE_GOLD_MENTION_PROP)) {
	    	// Set mention finder
	    	System.err.println("Not use gold mention");
	    }
	    else {
	    	System.err.println("Use gold mention");
	    }

		System.err.println("Hello, World!");
	}
}
