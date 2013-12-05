package com.panot.JavaCoref.TermUtils;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

public class CrfsuiteCaller {
	private CrfsuiteCaller() {}

	public static void train(String trainCrf, String modelFileName) throws Exception {
		System.err.println("Start process");

		Process p = new ProcessBuilder("crfsuite", "learn", "-m", modelFileName, "-").start();

		OutputStream os = p.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);

		bw.write(trainCrf);
		bw.flush();
		bw.close();

		osw.close();

		System.err.println("Finished data input");
		System.err.println("Start training");

		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		String line;
		while ((line = br.readLine()) != null) {
			System.err.println("Crfsuite: " + line);
		}

		System.err.println("Finished training");
	}

	public static List<List<String>> tag(String dataCrf, String modelFileName) throws Exception {
		System.err.println("Start process");

		Process p = new ProcessBuilder("crfsuite", "tag", "-m", modelFileName, "-").start();

		OutputStream os = p.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);

		bw.write(dataCrf);
		bw.flush();
		bw.close();

		osw.close();

		System.err.println("Finished data input");
		System.err.println("Start tagging");

		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		String line;
		List<List<String>> tags = new ArrayList<List<String>>();

		tags.add(new ArrayList<String>());
		int tagsI = 0;

		while ((line = br.readLine()) != null) {
			if (line.equals("")) {
				tags.add(new ArrayList<String>());
				tagsI++;
			} else {
				tags.get(tagsI).add(line);
			}
		}

		System.err.println("Finished tagging");

		if (tags.get(tagsI).size() == 0) {
			tags.remove(tagsI);
		}

		return tags;
	}
}

