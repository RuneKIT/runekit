package org.runekit.network.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.runekit.network.io.Buffer.Type;
import org.runekit.network.io.InBuffer;

/**
 * Encodes individual {@link OutBuffer} units to be streamed
 * outward to a client.
 * @see {@link OneToOneEncoder}
 */
public class Encoder extends OneToOneEncoder {
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		if (!(msg instanceof InBuffer)) 
			throw new IOException("Message not instance of input buffer while encoding!");
		InBuffer buffer = (InBuffer) msg;
		if (!buffer.isBare()) {
			int packetLength = 1 + buffer.getLength(); // op + packet length
			
			Type type = buffer.getType();
			if (type.equals(Type.VAR_BYTE)) packetLength += 1;
			else if (type.equals(Type.VAR_SHORT)) packetLength += 2;
			
			ChannelBuffer cBuffer = ChannelBuffers.buffer(packetLength);
			
			cBuffer.writeByte((byte) buffer.getOpCode());
			
			if (type.equals(Type.VAR_BYTE)) cBuffer.writeByte((byte) buffer.getLength());
			else if (type.equals(Type.VAR_SHORT)) cBuffer.writeShort(buffer.getLength());
			cBuffer.writeBytes(buffer.getPayload(), 0, buffer.getLength());
			return cBuffer;
		}
		return ChannelBuffers.wrappedBuffer(buffer.getPayload());
	}

}