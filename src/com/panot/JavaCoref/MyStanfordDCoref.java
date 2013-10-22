package com.panot.JavaCoref;

import java.util.Properties;

import edu.stanford.nlp.dcoref.SieveCoreferenceSystem;
import edu.stanford.nlp.util.StringUtils;

public class MyStanfordDCoref {

	public static void main(String[] args) throws Exception {
	    Properties props = StringUtils.argsToProperties(args);

	    SieveCoreferenceSystem corefSystem = new SieveCoreferenceSystem(props);

		System.out.println("Hello, World!");
	}
}
