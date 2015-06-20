package com.dgmltn.pimatic.model;

import java.util.List;

/**
 * Created by doug on 6/19/15.
 */
public class Message {
	public long time;
	public String level;
	public List<String> tags;
	public String text;
}
