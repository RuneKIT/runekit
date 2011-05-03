package org.runekit.model;

import org.jboss.netty.channel.Channel;

public class Session {
	
	private String user, pass;
	
	public String getUsername() {
		return user;
	}
	
	public String getPassword() {
		return pass;
	}
	
	private Channel channel;
	
	public Channel getChannel() {
		return channel;
	}
	
	public Session setChannel(Channel c) {
		channel = c; 
		return this;
	}
	
	public void write(Object o) {
		channel.write(o);
	}
	
	public Session(String user, String pass, Channel channel) {
		this.user = user; this.pass = pass; this.channel = channel;
	}
	
	public Session(String user, String pass) {
		this(user, pass, null);
	}
	
	@Override
	public String toString() {
		return "[" + user + "," + pass + "]";
	}

}