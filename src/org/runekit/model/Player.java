package org.runekit.model;

import org.runekit.network.io.builder.PacketBuilder;

public class Player extends Combatable {
	
	private PacketBuilder packetBuilder = new PacketBuilder(this);
	
	public PacketBuilder getPacketBuilder() {
		return packetBuilder;
	}
	
	private Session session;
	
	public Session getSession() {
		return session;
	}
	
	public Player(Session session) {
		this.session = session;
	}

}