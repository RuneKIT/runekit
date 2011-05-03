package org.runekit.network;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

import org.runekit.network.codec.*;

public class PipelineFactory implements ChannelPipelineFactory {

	@Override
	public ChannelPipeline getPipeline() {
		ChannelPipeline next = Channels.pipeline();
		
		// setup codec
		next.addLast("encoder", new Encoder());
		next.addLast("decoder", new LoginDecoder());
		
		// foward handler
		next.addLast("handler", new ChannelHandler());
		
		return next;
	}

}