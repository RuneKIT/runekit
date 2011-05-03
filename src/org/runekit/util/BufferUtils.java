package org.runekit.util;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Holds miscelleanous utilities to aside with a <tt>buffer</tt>.
 */
public final class BufferUtils {

	/**
	 * Reads a <a href=http://runescape.com>RuneScape 2</a> string.
	 * @param buffer {@link ChannelBuffer} which we read from.
	 * @return The builded {@link String}.
	 */
	public static final String readRS2String(ChannelBuffer buffer) {
		StringBuilder sb = new StringBuilder();
		byte b;
		while ((b = buffer.readByte()) != 0) sb.append((char) b);
		return sb.toString();
	}

	/**
	 * Converts a {@link Long} to a player's name.
	 * @param l The long to convert.
	 * @return The player's name builded as a {@link String}.
	 */
	public static final String longToPlayerName(long l) {
		if (l <= 0L || l >= 0x5b5b57f8a98a5dd1L) return null;
		if (l % 37L == 0L) return null;
		int i = 0;
		char ac[] = new char[12];
		while (l != 0L) {
			long l1 = l;
			l /= 37L;
			ac[11 - i++] = VALID_CHARS[(int)(l1 - l * 37L)];
		}
		return new String(ac, 12 - i, i);
	}
	
    /**
     * Valid character literals.
     */
    public static final char[] VALID_CHARS = {
    	'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
    	'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
    	't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
    	'3', '4', '5', '6', '7', '8', '9'
    };
    
}