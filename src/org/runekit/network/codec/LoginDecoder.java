package org.runekit.network.codec;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.runekit.Server;
import org.runekit.model.Player;
import org.runekit.model.Session;
import org.runekit.network.io.OutBuffer;
import org.runekit.util.BufferUtils;
import org.runekit.util.ReturnCodes;

@SuppressWarnings("unused")
public class LoginDecoder extends ReplayingDecoder<LoginDecoder.LoginState> {

	/**
	 * Represents a state of login request.
	 */
	public static enum LoginState {

		/**
		 * Represents a login stage where the handshake between server & client is done.
		 */
		HANDSHAKE,

		/**
		 * Represents a login stage where the client requests keys to
		 * update on demand or an update which streams cache from the server
		 * module to the client.
		 */
		ON_DEMAND,

		/**
		 * Represents a login stage where the client is processed to
		 * the game interface and completes the login.
		 */
		FINISH;

	}

	/**
	 * Constructs a new <tt>LoginDecoder</tt>.
	 */
	public LoginDecoder() {
		super(false);
		checkpoint(LoginState.HANDSHAKE);
	}

	/**
	 * Secure random generator.
	 */
	private static final SecureRandom RANDOM = new SecureRandom();

	/**
	 * Name hash of the decoder.
	 */
	private int nameHash;

	/**
	 * Server key of the decoder.
	 */
	private long serverKey;

	/**
	 * Used to display information.
	 */
	private static final Logger logger = Logger.getLogger(LoginDecoder.class.getName());

	@Override
	protected Object decode(ChannelHandlerContext ctx, final Channel channel, ChannelBuffer buffer, LoginState state) throws Exception {
		if (!channel.isConnected() | !buffer.readable() | !ctx.getChannel().isConnected()) return null;
		switch (state) {
		case HANDSHAKE:
			int opCode = buffer.readUnsignedByte();
			switch (opCode) {
			case 15: // Update
				int revision = buffer.readInt();
				if (revision != Server.REVISION) 
					throw mismatch("Revision", revision, Server.REVISION);
				channel.write(new OutBuffer().addByte((byte) 0).asInput());
				checkpoint(LoginState.ON_DEMAND);
				break;
			case 14: // Login
				nameHash = buffer.readUnsignedByte();
				channel.write(new OutBuffer().addByte((byte) 0).addLong(serverKey = RANDOM.nextLong()).asInput());
				checkpoint(LoginState.FINISH);
				break;
			}
			break;
		case ON_DEMAND:
			if (buffer.readableBytes() >= 4) {
				buffer.skipBytes(4); //this is request bytes
				OutBuffer response = new OutBuffer();
				for (int key : Server.UPDATE_KEYS) response.addByte((byte) key);
				channel.write(response.asInput()).addListener(ChannelFutureListener.CLOSE);
			}
			break;
		case FINISH:
			if (buffer.readableBytes() >= 3) {
				int connectionType = buffer.readByte() & 0xff;
				if (connectionType != 16 && connectionType != 18) // 16 = new connection, 18 = reconnection
					throw mismatch("Connection type", connectionType, "16/18");
				int payloadLength = buffer.readByte() & 0xff;
				if (payloadLength <= buffer.readableBytes()) {
					int revision = buffer.readInt();
					if (revision != Server.REVISION) 
						throw mismatch("Revision", revision, Server.REVISION);
					
					boolean lowMemoryVersion = buffer.readByte() == 1;
					
					buffer.skipBytes(24);
					for (int n = 0; n < 16; n++) buffer.readInt();
					
					int rsaHeader = buffer.readByte();
					if (rsaHeader != 10) {
						int rsaHeaderEnc = buffer.readByte() & 0xff; // Now hopefully we get 10.
						if (rsaHeaderEnc != 10) throw mismatch("RSA header", rsaHeaderEnc, 10);
					}
					
					long clientSessionKey = buffer.readLong();
					long serverSessionKey = buffer.readLong();
					if (serverKey != serverSessionKey) 
						throw mismatch("Server key", serverSessionKey, serverKey);

					String username = BufferUtils.longToPlayerName(buffer.readLong());
					String password = BufferUtils.readRS2String(buffer);
					
					final Session session = new Session(username, password, channel);
					
					logger.info("Session created: " + session);
					
					int returnCode = ReturnCodes.LOGIN;
					
					// Generate return code.
					
					OutBuffer loginBlock = new OutBuffer();
					
					loginBlock.addByte((byte) returnCode); // return code
					
					if (returnCode != ReturnCodes.LOGIN) {
						session.write(loginBlock.asInput());
						return true;
					}
					
					loginBlock.addByte((byte) 0); // crown
					loginBlock.addByte((byte) 1);
					loginBlock.addShort(0); // index
					loginBlock.addByte((byte) 0);
					loginBlock.addByte((byte) 0);
					
					session.write(loginBlock.asInput());
					
					Player player = new Player(session);
					
					player.getPacketBuilder().login();
					
					logger.info("Session complete: " + session);
				}
				return true;
			}
			break;
		}
		return true;
	}
	
	/**
	 * Creates a new {@link IOException} which refers to mismatching data.
	 */
	private IOException mismatch(String info, Object rec, Object exp) {
		return new IOException(info + " mismatch: received=" + rec + " expected=" + exp);
	}

}