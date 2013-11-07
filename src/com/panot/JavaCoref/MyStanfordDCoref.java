package com.panot.JavaCoref;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Properties;

import edu.stanford.nlp.dcoref.ACEMentionExtractor;
import edu.stanford.nlp.dcoref.CoNLLMentionExtractor;
import edu.stanford.nlp.dcoref.Constants;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.dcoref.CorefMentionFinder;
import edu.stanford.nlp.dcoref.Document;
import edu.stanford.nlp.dcoref.Mention;
import edu.stanford.nlp.dcoref.MentionExtractor;
import edu.stanford.nlp.dcoref.MUCMentionExtractor;
import edu.stanford.nlp.dcoref.SieveCoreferenceSystem;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

import com.panot.JavaCoref.TextUtils.TextReader;
import com.panot.JavaCoref.TextUtils.TokenMatcher;

public class MyStanfordDCoref {

	public static void main(String[] args) throws Exception {
	    Properties props = StringUtils.argsToProperties(args);

	    // instantiate coref system
	    SieveCoreferenceSystem corefSystem = new SieveCoreferenceSystem(props);

	    // MentionExtractor
	    MentionExtractor mentionExtractor = null;
	    if (props.containsKey(Constants.MUC_PROP)) {
	    	mentionExtractor = new MyMUCMentionExtractor(corefSystem.dictionaries(), props, corefSystem.semantics(), corefSystem.singletonPredictor);
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

		List<String> rawTexts = null;
		Vector<String> filenames = null;

		if (props.containsKey(MyConstants.RAWTEXT_LIST_PROP)) {
			rawTexts = TextReader.ReadFiles(props.getProperty(MyConstants.RAWTEXT_LIST_PROP));

			File fread = new File(props.getProperty(MyConstants.RAWTEXT_LIST_PROP));
			BufferedReader reader = new BufferedReader(new FileReader(fread));

			filenames = new Vector<String>();

			String filename;
			while ((filename = reader.readLine()) != null) {
				filenames.add(filename);
			}

			reader.close();
		}

		int count = 0;

		while (true) {
			Document document = mentionExtractor.nextDoc();
			if (document == null) break;

			count += 1;
			System.err.println("Start coref document no: " + count);

			Map<Integer, CorefChain> result = corefSystem.coref(document);
			document.annotation.set(CorefCoreAnnotations.CorefChainAnnotation.class, result);

			if (props.containsKey(MyConstants.RAWTEXT_LIST_PROP)) {
				String rawText = rawTexts.get(count - 1);

				try {
					boolean offsetPass = TokenMatcher.SetOffset(document.annotation, rawText);
				} catch (Exception e) {
					System.err.println("Error at SetOffset");
				}

				boolean output = props.containsKey(MyConstants.OUTPUT_PROP);
				boolean output_token = props.containsKey(MyConstants.OUTPUT_TOKEN_PROP);
				boolean output_mention = props.containsKey(MyConstants.OUTPUT_MENTION_PROP);

				File outfile = null;
				BufferedWriter bufferedWriter = null;

				File outfile_token = null;
				BufferedWriter bufferedWriter_token = null;

				File outfile_mention = null;
				BufferedWriter bufferedWriter_mention = null;

				if (output) {
					outfile = new File(filenames.get(count - 1) + ".ann");
					bufferedWriter = new BufferedWriter(new FileWriter(outfile));
				}

				if (output_token) {
					outfile_token = new File(filenames.get(count - 1) + ".token");
					bufferedWriter_token = new BufferedWriter(new FileWriter(outfile_token));
				}

				if (output_mention) {
					outfile_mention = new File(filenames.get(count - 1) + ".mention");
					bufferedWriter_mention = new BufferedWriter(new FileWriter(outfile_mention));
				}

				try {
					//String standoff = documentToStandOff(document, rawText);
					//System.err.println(standoff);
					
					String standoff = null; //documentToStandOff(document, rawText);
					String tokens = null;
					String mentions = null;

					System.err.println(filenames.get(count - 1));

					if (output) {
						standoff = documentToStandOff(document, rawText);
						System.err.println(standoff);
						bufferedWriter.write(standoff);
					}

					if (output_token) {
						tokens = documentToToken(document);
						System.err.println(tokens);
						bufferedWriter_token.write(tokens);
					}

					if (output_mention) {
						mentions = documentToMention(document);
						System.err.println(mentions);
						bufferedWriter_mention.write(mentions);
					}

				} catch (Exception e) {
					System.err.println("Error at docToStandOff");
				}

				if (output)
					bufferedWriter.close();

				if (output_token)
					bufferedWriter_token.close();

				if (output_mention)
					bufferedWriter_mention.close();
			}

			System.err.println("Finished!");

			// only first doc for debugging
			//if (count >= 2)
			//	break;
		}

		System.err.println("Resolved all: " + count + " doc(s)");
	}

	public static String annotationToToken(Annotation annotation) {
		StringBuilder tokens_text = new StringBuilder();

		// Print tokens
		if (annotation.get(CoreAnnotations.SentencesAnnotation.class) != null) {
			int sentCount = 1;

			// for each sentence
			for (CoreMap sentence: annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
				for (int j = 0; j < tokens.size(); j++) {

					tokens_text.append("T\t" + sentCount + ":" + (j+1) + "\t");
					tokens_text.append(tokens.get(j).get(CoreAnnotations.TextAnnotation.class) + "\n");
				}
				sentCount ++;
			}
		}

		return tokens_text.toString();
	}

	public static String annotationToCoref(Annotation annotation) {
		StringBuilder tokens_text = new StringBuilder();

		// Print coref chains
		Map<Integer, CorefChain> corefChains = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);

		int mentionCount = 0;
		for (CorefChain chain : corefChains.values()) {
			if (chain.getMentionsInTextualOrder().size() <= 1)
				continue;
			tokens_text.append("C\t");
			for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder()) {
				tokens_text.append(mention.sentNum + ":" + mention.startIndex + ":" + mention.endIndex + " ");
			}
			tokens_text.append("\n");
		}

		return tokens_text.toString();
	}

	public static String documentToToken(Document document) {
		Annotation annotation = document.annotation;
		StringBuilder tokens_text = new StringBuilder();

		tokens_text.append(annotationToToken(annotation));
		tokens_text.append(annotationToCoref(annotation));

		return tokens_text.toString();
	}

	public static String documentToMention(Document document) {
		Annotation annotation = document.annotation;
		StringBuilder os = new StringBuilder();

		os.append(annotationToToken(annotation));

		List<List<Mention>> orderedMentionsBySentence = document.getOrderedMentions();

		for (int sentI = 0; sentI < orderedMentionsBySentence.size(); sentI++) {
	    	List<Mention> orderedMentions = orderedMentionsBySentence.get(sentI);

	    	for (int mentionI = 0; mentionI < orderedMentions.size(); mentionI++) {

	        	Mention m1 = orderedMentions.get(mentionI);

	        	os.append("M\t");
	        	os.append(m1.sentNum + 1);
	        	os.append(":");
	        	os.append(m1.startIndex + 1);
	        	os.append(":");
	        	os.append(m1.endIndex + 1);
	        	os.append("\n");
	        }
		}

		return os.toString();
	}

	public static String documentToStandOff(Document document, String text) {
		Annotation annotation = document.annotation;
		Map<Integer, CorefChain> corefChains = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);

		int mentionCount = 0;

		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		StringBuilder standoff = new StringBuilder();

		for (CorefChain chain : corefChains.values()) {
			if (chain.getMentionsInTextualOrder().size() <= 1)
				continue;

			Vector<Integer> mentionInChain = new Vector<Integer>();

			for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder()) {
				CoreMap currentSentence = sentences.get(mention.sentNum - 1);
				List<CoreLabel> tokens = currentSentence.get(CoreAnnotations.TokensAnnotation.class);
				int offsetBegin = tokens.get(mention.startIndex - 1).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
				int offsetEnd = tokens.get(mention.endIndex - 2).get(CoreAnnotations.CharacterOffsetEndAnnotation.class);

				mentionCount ++;

				standoff.append("T" + mentionCount + "\t");
				standoff.append("Mention " + offsetBegin + " " + offsetEnd + "\t");
				standoff.append(text.substring(offsetBegin, offsetEnd) + "\n");

				mentionInChain.add(mentionCount);
			}

			standoff.append("*\tCoreference");
			for (int mentionNum : mentionInChain) {
				standoff.append(" T" + mentionNum);
			}
			standoff.append("\n");
		}

		return standoff.toString();
	}

	public static String documentToString(Document document) {
		StringBuilder standoff = new StringBuilder();
		Annotation annotation = document.annotation;

		String docId = annotation.get(CoreAnnotations.DocIDAnnotation.class);

		if (docId != null) {
			standoff.append(docId);
		} else {
			standoff.append("No docId");
		}
		standoff.append("\n");
		standoff.append("Sentences:\n");

		if (annotation.get(CoreAnnotations.SentencesAnnotation.class) != null) {
			int sentCount = 1;

			// for each sentence
			for (CoreMap sentence: annotation.get(CoreAnnotations.SentencesAnnotation.class)) {

				// info about sentence
				standoff.append("Sentence no.: " + sentCount + "\n");
				Integer lineNumber = sentence.get(CoreAnnotations.LineNumberAnnotation.class);
				if (lineNumber != null) {
					standoff.append(" line: " + lineNumber + "\n");
				}

				// info about each token
				List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
				for (int j = 0; j < tokens.size(); j++) {
					// addWordInfo
					standoff.append("  token no: " + (j+1) + "\n");
					standoff.append("   " + addWordInfo(tokens.get(j)));
				}

				sentCount += 1;
			}

			// info about coref
			Map<Integer, CorefChain> corefChains = annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
			if (corefChains != null) {
				// addCorefGraphInfo
				standoff.append("With coref!\n");
				standoff.append(" " + addCorefGraphInfo(corefChains));
			} else {
				standoff.append("Without coref!\n");
			}
		}

		return standoff.toString();
	}

	public static String addWordInfo(CoreLabel token) {
		StringBuilder wordinfo  = new StringBuilder();

		wordinfo.append(token.get(CoreAnnotations.TextAnnotation.class) + "\t");
		if (token.containsKey(CoreAnnotations.CharacterOffsetBeginAnnotation.class) && token.containsKey(CoreAnnotations.CharacterOffsetEndAnnotation.class)) {
			wordinfo.append(token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) + " " + token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
		}

		wordinfo.append("\n");

		return wordinfo.toString();
	}

	public static String addCorefGraphInfo(Map<Integer, CorefChain> corefChains) {
		StringBuilder corefGraphInfo = new StringBuilder();

		boolean foundCoref = false;
		for (CorefChain chain : corefChains.values()) {
			if (chain.getMentionsInTextualOrder().size() <= 1)
				continue;
			foundCoref = true;
			corefGraphInfo.append("New coref: \n");
			CorefChain.CorefMention source = chain.getRepresentativeMention();
			corefGraphInfo.append(addCorefMention(source));
			for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder()) {
				if (mention == source)
					continue;
				corefGraphInfo.append(addCorefMention(mention));
			}
		}

		return corefGraphInfo.toString();
	}

	public static String addCorefMention(CorefChain.CorefMention mention) {
		StringBuilder corefMentionInfo = new StringBuilder();

		corefMentionInfo.append(mention.sentNum);
		corefMentionInfo.append("[" + mention.startIndex + "," + mention.endIndex + "]\n");

		return corefMentionInfo.toString();
	}
}
