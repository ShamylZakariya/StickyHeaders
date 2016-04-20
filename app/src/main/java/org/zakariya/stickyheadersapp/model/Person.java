package org.zakariya.stickyheadersapp.model;

/**
 * Simplified take on the model vended by http://api.randomuser.me/
 */
public class Person {

	public static class Name {
		public String title;
		public String first;
		public String last;
	}

	public static class Location {
		public String street;
		public String city;
		public String state;
		public String postcode;
	}

	public static class Picture {
		public String large;
		public String medium;
		public String thumbnail;
	}

	public Name name;
	public Location location;
	public String email;
	public String phone;
	public String cell;
	public Picture picture;

	public Person() {
	}
}
