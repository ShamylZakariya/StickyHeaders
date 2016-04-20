package org.zakariya.stickyheadersapp.model;

/**
 * Created by shamyl on 4/19/16.
 */
public class RandomUserResults {

	public static class Info {
		String seed;
		int results;
		int page;
		String version;
	}

	public Person[] results;
	public Info info;
	public String error;
}
