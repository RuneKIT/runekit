package org.runekit.network.io.builder;

import org.runekit.model.Player;
import org.runekit.network.io.OutBuffer;

public abstract class PacketSender extends OutBuffer {
	
	protected Player player;
	
	public PacketSender(Player player) {
		this.player = player;
	}
	
	protected void send(OutBuffer bf) {
		player.getSession().write(bf.asInput());
		reset();
	}
	
	protected void send() {
		send(this);
	}

}