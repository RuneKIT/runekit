package org.runekit.network.io;

/**
 * Reads data from a network input stream.
 * @see {@link Buffer}
 */
public class InBuffer extends Buffer {

	/**
	 * Constructs a new <tt>InBuffer</tt>.
	 * @param opCode The <a href=http://en.wikipedia.org/wiki/Opcode>operation code</a> of the data buffer.
	 * @param payload Control information data which is contained.
	 * @param bare Whether or not this buffer contains the standard header.
	 * @param type The type-size of the <tt>InBuffer</tt>.
	 */
	public InBuffer(int opCode, byte[] payload, boolean bare, Type type) {
		super(opCode, payload, bare, type);
	}
	
	/**
	 * Constructs a new <tt>InBuffer</tt>.
	 * @param opCode The <a href=http://en.wikipedia.org/wiki/Opcode>operation code</a> of the data buffer.
	 * @param payload Control information data which is contained.
	 * @param bare Whether or not this buffer contains the standard header.
	 */
	public InBuffer(int opCode, byte[] payload, boolean bare) {
		this(opCode, payload, bare, Type.FIXED);
	}
	
	/**
	 * Constructs a new <tt>InBuffer</tt>.
	 * @param opCode The <a href=http://en.wikipedia.org/wiki/Opcode>operation code</a> of the data buffer.
	 * @param payload Control information data which is contained.
	 * @param bare Whether or not this buffer contains the standard header.
	 */
	public InBuffer(int opCode, byte[] payload) {
		this(opCode, payload, false);
	}
	
	/**
	* Reads the next <tt>byte</tt> from the {@link #payload}.
	* @return The next <tt>byte</tt> from the {@link #payload}.
	*/
	public byte readByte() {
		return payload[caret++];
	}
	
	/**
	* Reads the next <code>short</code> from the {@link #payload}.
	* @return The next <code>short</code> from the {@link #payload}.
	*/
	public short readShort() {
		return (short) ((short) ((payload[caret++] & 0xff) << 8) | (short) (payload[caret++] & 0xff));
	}
	
	/**
	 * Reads the next lesser <tt>short</tt> type-A from the {@link #payload}.
	 * @return The next lesser <tt>short</tt> from the {@link #payload}.
	 */
	public int readLEShortA() {
		int i = ((payload[caret++] - 128 & 0xff)) + ((payload[caret++] & 0xff) << 8);
		if(i > 32767) i -= 0x10000;
		return i;
	}
	
	/**
	 * Reads the next lesser <tt>short</tt> from the {@link #payload}.
	 * @return The next lesser <tt>short</tt> from the {@link #payload}.
	 */
	public int readLEShort() {
		int i = ((payload[caret++] & 0xff)) + ((payload[caret++] & 0xff) << 8);
		if (i > 32767) i -= 0x10000;
		return i;
	}
	
	/**
	* Reads the next <tt>int</tt> from the {@link #payload}.
	* @return The next <tt>int</tt> from the {@link #payload}.
	*/
	public int readInt() {
		return ((payload[caret++] & 0xff) << 24)
		| ((payload[caret++] & 0xff) << 16)
		| ((payload[caret++] & 0xff) << 8)
		| (payload[caret++] & 0xff);
	}
	
	/**
	 * Reads the next lesser <tt>int</tt> from the {@link #payload}.
	 * @return The next lesser <tt>int</tt> from the {@link #payload}.
	 */
	public int readLEInt() {
		return (payload[caret++] & 0xff)
		| ((payload[caret++] & 0xff) << 8)
		| ((payload[caret++] & 0xff) << 16)
		| ((payload[caret++] & 0xff) << 24);
	}
	
	/**
	* Reads the next <tt>long</tt> from the {@link #payload}.
	* @return The next <tt>long</tt> from the {@link #payload}.
	*/
	public long readLong() {
		return ((long) (payload[caret++] & 0xff) << 56)
		| ((long) (payload[caret++] & 0xff) << 48)
		| ((long) (payload[caret++] & 0xff) << 40)
		| ((long) (payload[caret++] & 0xff) << 32)
		| ((long) (payload[caret++] & 0xff) << 24)
		| ((long) (payload[caret++] & 0xff) << 16)
		| ((long) (payload[caret++] & 0xff) << 8)
		| ((payload[caret++] & 0xff));
	}
	
	/**
	* Reads the {@link String} which is formed by the unread portion of the {@link #payload}.
	* @return The {@link String} which is formed by the unread portion of the {@link #payload}.
	*/
	public String readString() {
		return readString(payload.length - caret);
	}
	
	/**
	 * Reads the next RuneScape 2 protocol {@link String} from the {@link #payload}.
	 * @return The next RuneScape 2 protocol {@link String} from the {@link #payload}.
	 */
	public String readRS2String() {
		int start = caret;
		while(payload[caret++] != 0);
		return new String(payload, start, caret - start - 1);
	}
	
	/**
	 * Reads <tt>byte</tt>s from the {@link #payload}.
	 * @param buf Buffer of <tt><tt>byte</tt>s</tt>.
	 * @param off Offset of the {@link caret}.
	 * @param len Length of <tt>byte</tt>s to read.
	 */
	public void readBytes(byte[] buf, int off, int len) {
		for (int i = 0; i < len; i++) buf[off+i] = payload[caret++];
	}
	
	/**
	* Reads a {@link String} of the specified length from the {@link #payload}.
	* @param length The length of the string to be read.
	* @return A string of the specified length from the {@link #payload}.
	*/
	public String readString(int length) {
		String rv = new String(payload, caret, length);
		caret += length;
		return rv;
	}
	
	/**
	* Skips the specified number of <tt>byte</tt>s in the {@link #payload}.
	* @param x The number of <tt>byte</tt>s to be skipped.
	*/
	public void skip(int x) {
		caret += x;
	}
	
	/**
	 * Gets the remaining amount of unread <tt>byte</tt>s left in the {@link #payload}.
	 * @return The remaining amount of unread <tt>byte</tt>s left in the {@link #payload}.
	 */
	public int remaining() {
		return payload.length - caret;
	}
	
	/**
	* Gets this packet as a literal.
	* @return A <tt>String</tt> representing this packet.
	*/
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[op=" + opCode + ",len=" + payload.length + ",data=0x");
		for (int x = 0; x < payload.length; x++) sb.append(byteToHex(payload[x], true));
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Converts a byte to hexidecimal format.
	 * @param b The byte to convert.
	 * @param forceLeadingZero Whether or not we should force a leading zero.
	 * @return The byte in hexidecimal format (as a <tt>String</tt>).
	 */
	private static String byteToHex(byte b, boolean forceLeadingZero) {
		StringBuilder out = new StringBuilder();
		int ub = b & 0xff;
		if (ub / 16 > 0 || forceLeadingZero) out.append(hex[ub / 16]);
		out.append(hex[ub % 16]);
		return out.toString();
	}
	
	/**
	 * Array of <a href=>hexidecimal</a> characters we can read.
	 */
	private static final char[] hex = "0123456789ABCDEF".toCharArray();
	
	/**
	 * Reads the next abstract <tt>int</tt> from the {@link #payload}.
	 * @return The next abstract <tt>int</tt> from the {@link #payload}.
	 */
	public int readInt2() {
		return ((payload[caret++] & 0xff) << 16)
		| ((payload[caret++] & 0xff) << 24)
		| (payload[caret++] & 0xff)
		| ((payload[caret++] & 0xff) << 8);
	}
	
	/**
	 * Reads the next <tt>short</tt> sub-A from the {@link #payload}.
	 * @return The next <tt>short</tt> sub-A from the {@link #payload}.
	 */
	public int readShortA() {
		caret += 2;
		return ((payload[caret-2]&0xFF)<<8)+(payload[caret-1]-128&0xFF);
	}

	/**
	 * Reads the next <tt>byte</tt> sub-C from the {@link #payload}.
	 * @return The next <tt>byte</tt> sub-C from the {@link #payload}.
	 */
	public byte readByteC() {
		return (byte) -readByte();
	}

	/**
	 * Reads the next <tt>byte</tt> sub-S from the {@link #payload}.
	 * @return The next <tt>byte</tt> sub-S from the {@link #payload}.
	 */
	public byte readByteS() {
		return (byte) (128 - readByte());
	}

}