package com.panot.JavaCoref.TermUtils;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.dcoref.Document;
import edu.stanford.nlp.dcoref.Mention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class NltkCrfFormatter {
	public static class CrfToken {
		public String bioTag;
		public String token;

		public CrfToken() {
			bioTag = "O";
			token = "";
		}
	}

	public List<List<CrfToken>> crfTokens;

	public NltkCrfFormatter() {
		crfTokens = new ArrayList<List<CrfToken>>();
	}

	public void addDocument(Annotation ann) {
		List<CoreMap> sentences = ann.get(CoreAnnotations.SentencesAnnotation.class);

		for (int sentenceI = 0; sentenceI < sentences.size(); sentenceI++) {
			CoreMap sentence = sentences.get(sentenceI);

			// Fill CrfToken.token
			List<CoreLabel> sentenceTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
			List<CrfToken> sentenceCrfTokens = new ArrayList<CrfToken>();
			for (int tokenI = 0; tokenI < sentenceTokens.size(); tokenI++) {
				CoreMap token = sentenceTokens.get(tokenI);
				CrfToken crfToken = new CrfToken();
				crfToken.token = token.get(CoreAnnotations.TextAnnotation.class);
				sentenceCrfTokens.add(crfToken);
			}
			crfTokens.add(sentenceCrfTokens);
		}
	}

	public static String annotationToCrfString(Annotation ann) {
		NltkCrfFormatter crfFormatter = new NltkCrfFormatter();
		crfFormatter.addDocument(ann);
		return crfFormatter.toString();
	}

	public void addDocument(Document doc) {
		Annotation ann = doc.annotation;

		List<CoreMap> sentences = ann.get(CoreAnnotations.SentencesAnnotation.class);
		List<List<Mention>> orderedMentionsBySentence = doc.getOrderedMentions();

		for (int sentenceI = 0; sentenceI < sentences.size(); sentenceI++) {
			CoreMap sentence = sentences.get(sentenceI);

			List<CoreLabel> sentenceTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
			List<CrfToken> sentenceCrfTokens = new ArrayList<CrfToken>();

			// Fill CrfToken.token
			for (int tokenI = 0; tokenI < sentenceTokens.size(); tokenI++) {
				CoreMap token = sentenceTokens.get(tokenI);
				CrfToken crfToken = new CrfToken();
				crfToken.token = token.get(CoreAnnotations.TextAnnotation.class);
				sentenceCrfTokens.add(crfToken);
			}
			crfTokens.add(sentenceCrfTokens);

			// Fill CrfToken.bioTag
			List<Mention> orderedMentionsInSentence = orderedMentionsBySentence.get(sentenceI);
			for (Mention mention : orderedMentionsInSentence) {
				sentenceCrfTokens.get(mention.startIndex).bioTag = "B";
				for (int tokenI = mention.startIndex + 1 ; tokenI < mention.endIndex ; tokenI++) {
					sentenceCrfTokens.get(tokenI).bioTag = "I";
				}
			}
		}
	}

	private String toPythonInput() {
		StringBuilder os = new StringBuilder();

		for (List<CrfToken> sentenceCrfTokens : crfTokens) {
			for (CrfToken crfToken : sentenceCrfTokens) {
				os.append(crfToken.bioTag);
				os.append("\t");
				os.append(crfToken.token);
				os.append("\n");
			}
			os.append("\n");
		}

		return os.toString();
	}

	private static String pythonCaller(String pythonInput) throws Exception {
		System.err.println("Prepare calling python feature generator");

		Process p = new ProcessBuilder("python", "py/tte_feature.py").start();

		OutputStream os = p.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);

		bw.write(pythonInput);
		bw.flush();
		bw.close();
		osw.close();

		System.err.println("Finished data input");
		System.err.println("Start populating features");

		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		String line;
		StringBuilder out = new StringBuilder();

		while ((line = br.readLine()) != null) {
			out.append(line);
			out.append("\n");
		}

		System.err.println("Finished populating features");
		return out.toString();
	}

	public String toString() {
		String pythonInputString = toPythonInput();
		String features = "";
		try {
			features = pythonCaller(pythonInputString);
			//System.err.println(features);
		} catch (Exception e) {
			System.err.println("ERROR CALLING PYTHON");
			return "";
		}
		return features;
	}

}
