package com.dgmltn.pimatic.model;

import java.util.List;

/**
 * Created by doug on 6/19/15.
 */
public class Message {
	// Constants for level
	public static final String ERROR = "error";
	public static final String WARN = "warn";
	public static final String INFO = "info";
	public static final String DEBUG = "debug";

	public long time;
	public String level;
	public List<String> tags;
	public String text;
}
