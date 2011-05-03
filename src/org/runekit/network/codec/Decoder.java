package org.runekit.network.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.runekit.model.Player;

public class Decoder extends FrameDecoder {

	private Player player;

	public Decoder(Player player) {
		this.player = player;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		if (!buffer.readable()) throw new IOException("Channel buffer unreadable while decoding.");
		int opCode = buffer.readUnsignedByte();
/*		int length = Constants.PACKET_LENGTHS[opCode];
		if (length < 0) length = buffer.readUnsignedByte();
		if (buffer.readableBytes() >= length & length > 0) {
			byte[] payload = new byte[length];
			buffer.readBytes(payload);
			
			Packet packet = new Packet(player, opCode, payload);
			player.getNetQueue().add(packet);
			
			return packet;
		}*/
		return null;
	}

}