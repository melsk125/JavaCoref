package com.panot.Sandbox;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;


public class TryRunProgram {
	public static void main(String[] args) throws Exception {
		Process p = new ProcessBuilder("crfsuite", "-h").start();

		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		InputStream es = p.getErrorStream();
		InputStreamReader esr = new InputStreamReader(es);
		BufferedReader ebr = new BufferedReader(esr);

		String line;

		System.out.printf("Output of running %s is:\n", Arrays.toString(args));

		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}

		System.out.println();
		System.out.printf("Error of running %s is:\n", Arrays.toString(args));

		while ((line = ebr.readLine()) != null) {
			System.out.println(line);
		}
	}
}
