package org.runekit;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import org.runekit.network.PipelineFactory;

public final class Server {
	
	private static final Logger logger = Logger.getLogger(Server.class.getName());
	
	public static final ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));;
	
	public static void main(String... args) {
		int port = Integer.parseInt(args[0]);
		
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("reuseAddress", true);
		bootstrap.setOption("child.tcpNoDelay", true);
		
		bootstrap.setPipelineFactory(new PipelineFactory());
		
		bootstrap.bind(new InetSocketAddress(port));
		
		logger.info("Server ready on port: " + port);
	}
	
	// configs
	
	public static final int REVISION = 484;

	public static final int[] UPDATE_KEYS = {
		-1, 0, -1, 0, 0, 0, 0, -128, -54, 85,
		-90, 19, 0, 0, 0, 41, -6, -56, -114, -5,
		0, 0, 0, 25, 73, 100, -126, 55, 0, 0, 2,
		3, 83, 77, 124, 39, 0, 0, 0, -58, -90, -4,
		-52, 43, 0, 0, 0, 51, 35, -43, 58, 85, 0, 0,
		0, -109, -94, -60, 35, 6, 0, 0, 0, 0, -17, 63,
		-17, 42, 0, 0, 0, -43, -32, -120, 113, 124, 0,
		0, 0, 48, -20, 40, 113, 124, 0, 0, 0, 41, 20, -113,
		111, 94, 0, 0, 0, 1, 116, 20, -118, 5, 0, 0, 0, 0, 110,
		102, -6, 110, 0, 0, 0, -121, 7, -114, 106, 62, 0, 0, 0, 1,
		-93, -116, -10, -108, 0, 0, 0, 1, -72, -14, 77, 33, 0, 0, 0, 0
	};
	
}