package com.panot.JavaCoref;

import java.io.FileInputStream;
import java.util.Properties;

import edu.stanford.nlp.dcoref.ACEMentionExtractor;
import edu.stanford.nlp.dcoref.CoNLLMentionExtractor;
import edu.stanford.nlp.dcoref.Constants;
import edu.stanford.nlp.dcoref.CorefMentionFinder;
import edu.stanford.nlp.dcoref.Document;
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

	    	String mentionFinderClass = props.getProperty(Constants.MENTION_FINDER_PROP);
	    	if (mentionFinderClass != null) {
	    		String mentionFinderPropFilename = props.getProperty(Constants.MENTION_FINDER_PROPFILE_PROP);
	    		CorefMentionFinder mentionFinder;
	    		if (mentionFinderPropFilename != null) {
	    			Properties mentionFinderProps = new Properties();
	    			mentionFinderProps.load(new FileInputStream(mentionFinderPropFilename));
	    			mentionFinder = (CorefMentionFinder) Class.forName(mentionFinderClass).getConstructor(Properties.class).newInstance(mentionFinderProps);
	    		} else {
	    			mentionFinder = (CorefMentionFinder) Class.forName(mentionFinderClass).newInstance();
	    		}
	    		mentionExtractor.setMentionFinder(mentionFinder);
	    	}
	    	if (mentionExtractor.mentionFinder == null) {
	    		System.err.println("No mention finder specified, but not using gold mentions");
	    	}
	    }
	    else {
	    	System.err.println("Use gold mention");
	    }

		System.err.println("Start runCoref!");

		try {
			runCoref(corefSystem, mentionExtractor, props);
		} catch (Exception ex) {
			System.err.println("[SEVERE] ERROR in running coreference");
			throw ex;
		}
	}

	public static void runCoref(SieveCoreferenceSystem corefSystem, MentionExtractor mentionExtractor, Properties props) throws Exception {
		System.err.println("In runCoref!");

		mentionExtractor.resetDocs();

		int count = 0;

		while (true) {
			Document doc = mentionExtractor.nextDoc();
			if (doc == null) break;
			count += 1;
			System.err.println("Document no: " + count);
		}

		System.err.println("Resolved all: " + count + " doc(s)");
	}
}
