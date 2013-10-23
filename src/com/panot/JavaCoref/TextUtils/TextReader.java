package com.panot.JavaCoref.TextUtils;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Vector;

public class TextReader {
	private TextReader() { }

	public static Vector<String> ReadFiles(String filelistName) throws Exception {
		File file = new File(filelistName);

		BufferedReader reader = new BufferedReader(new FileReader(file));

		Vector<String> rawTexts = new Vector<String>();

		String filename;

		while ((filename = reader.readLine()) != null) {
			File nextFile = new File(filename);
			BufferedReader nextReader = new BufferedReader(new FileReader(nextFile));

			StringBuilder wholeText = new StringBuilder();
			String nextLine;

			while ((nextLine = nextReader.readLine()) != null) {
				wholeText.append(nextLine);
				wholeText.append("\n");
			}

			rawTexts.add(wholeText.toString());
		}

		return rawTexts;
	}
}
