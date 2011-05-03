package org.runekit.network.io.builder;

import org.runekit.model.Player;
import org.runekit.network.io.OutBuffer;

public final class PacketBuilder extends PacketSender {
	
	public PacketBuilder(Player player) {
		super(player);
	}
	
	public void login() {
		sendMapRegion();
		sendWindowPane(548);
		sendMessage("Welcome to RuneScape.");
	}
	
	public void sendMapRegion() {
		// Send map region changed update flag here.

		OutBuffer bf = (OutBuffer) new OutBuffer().setOpCode(31).setType(Type.VAR_SHORT);
		bf.addLEShortA(player.getLocation().getRegionY());
		boolean forceSend = true;
		if ((((player.getLocation().getRegionX() / 8) == 48) || ((player.getLocation().getRegionX() / 8) == 49)) && ((player.getLocation().getRegionY() / 8) == 48))
			forceSend = false;
		if (((player.getLocation().getRegionX() / 8) == 48) && ((player.getLocation().getRegionY() / 8) == 148))
			forceSend = false;
		for (int xCalc = (player.getLocation().getRegionX() - 6) / 8; xCalc <= ((player.getLocation().getRegionX() + 6) / 8); xCalc++)
			for (int yCalc = (player.getLocation().getRegionY() - 6) / 8; yCalc <= ((player.getLocation().getRegionY() + 6) / 8); yCalc++)
				if (forceSend || ((yCalc != 49) && (yCalc != 149) && (yCalc != 147) && (xCalc != 50) && ((xCalc != 49) || (yCalc != 47))))
					for (byte b = 0; b < 4; b++) bf.addInt(0);
		bf.addLEShortA(player.getLocation().getRegionX())
		.addByteC((byte) player.getLocation().getZ())
		.addShort(player.getLocation().getLocalX())
		.addLEShortA(player.getLocation().getLocalY());
		send(bf);
	}
	
	public void sendWindowPane(int pane) {
		OutBuffer bf = (OutBuffer) new OutBuffer().setOpCode(207);
		bf.addLEShortA(pane);
		send(bf);
	}
	
	public void sendMessage(String message) {
		OutBuffer bf = (OutBuffer) setOpCode(27).setType(Type.VAR_BYTE);
		bf.addString(message);
		send(bf);
	}

}