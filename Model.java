package com;
import java.util.HashMap;
import java.util.HashSet;
public class Model
{
	/**
	program to create and store inverted index and vector space model
	*/
	String filename;
	HashMap<String,Integer> vector = new HashMap<String,Integer>();
public Model(String name,String words[],HashSet<String> att){
	filename = name;
	for(int p=0;p<words.length;p++){
		if(!StopWordList.check(words[p])){
			att.add(words[p]);
			vector.put(words[p],(vector.containsKey(words[p]) ? 1 + vector.get(words[p]) : 1));
		}
	}
}
}