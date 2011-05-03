package org.runekit.network;

import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class ChannelHandler extends SimpleChannelHandler {
	
	private static final Logger logger = Logger.getLogger(ChannelHandler.class.getName());
	
	@Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
    	// Handle packet message
    }
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		e.getCause().printStackTrace();
	}
	
	@Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	logger.info("Channel connected from: " + ctx.getChannel().getRemoteAddress());
    }
    
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
    	logger.info("Channel disconnected: " + ctx.getChannel().getRemoteAddress());
    }

}