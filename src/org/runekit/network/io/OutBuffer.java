package org.runekit.network.io;

/**
 * Writes data towards a network input stream.
 * @see {@link Buffer}
 */
public class OutBuffer extends Buffer {

	/**
	 * Constructs a new <tt>OutBuffer</tt>.
	 * @param opCode The <a href=http://en.wikipedia.org/wiki/Opcode>operation code</a> of the data buffer.
	 * @param payload Control information data which is contained.
	 * @param bare Whether or not this buffer contains the standard header.
	 * @param type The type-size of the <tt>OutBuffer</tt>.
	 */
	public OutBuffer(int opCode, byte[] payload, boolean bare, Type type) {
		super(opCode, payload, bare, type);
	}

	/**
	 * Constructs a new <tt>OutBuffer</tt>.
	 * @param opCode The <a href=http://en.wikipedia.org/wiki/Opcode>operation code</a> of the data buffer.
	 * @param payload Control information data which is contained.
	 * @param bare Whether or not this buffer contains the standard header.
	 */
	public OutBuffer(int opCode, byte[] payload, boolean bare) {
		this(opCode, payload, bare, Type.FIXED);
	}

	/**
	 * Constructs a new <tt>OutBuffer</tt>.
	 * @param opCode The <a href=http://en.wikipedia.org/wiki/Opcode>operation code</a> of the data buffer.
	 * @param payload Control information data which is contained.
	 * @param bare Whether or not this buffer contains the standard header.
	 */
	public OutBuffer(int opCode, byte[] payload) {
		this(opCode, payload, false);
	}
	
	/**
	 * Constructs a new <tt>OutBuffer</tt>.
	 * @param opCode  The <a href=http://en.wikipedia.org/wiki/Opcode>operation code</a> of the data buffer.
	 */
	public OutBuffer(int opCode) {
		super(opCode);
	}
	
	/**
	 * Constructs a new <tt>OutBuffer</tt>.
	 */
	public OutBuffer() {
		super();
	}

	/**
	 * Current number of bytes used in the buffer.
	 */
	private int curLength = 0;
	
	/**
	 * Retrieves the current length.
	 * @return The current length.
	 */
	public int getCurLength() {
		return curLength;
	}

	/**
	 * Bitmasks for <code>addBits()</code>
	 */
	private static int bitmasks[] = {
		0, 0x1, 0x3, 0x7,
		0xf, 0x1f, 0x3f, 0x7f,
		0xff, 0x1ff, 0x3ff, 0x7ff,
		0xfff, 0x1fff, 0x3fff, 0x7fff,
		0xffff, 0x1ffff, 0x3ffff, 0x7ffff,
		0xfffff, 0x1fffff, 0x3fffff, 0x7fffff,
		0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff,
		0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff,
		-1
	};

	/**
	 * Ensures that the buffer is at least <code>minimumBytes</code> bytes.
	 * @param minimumCapacity The size needed.
	 */
	private void ensureCapacity(int minimumCapacity) {
		if (minimumCapacity >= payload.length) expandCapacity(minimumCapacity);
	}

	/**
	 * Expands the buffer to the specified size.
	 * @param minimumCapacity The minimum capacity to which to expand.
	 * @see java.lang.AbstractStringBuilder#expandCapacity(int)
	 */
	private void expandCapacity(int minimumCapacity) {
		int newCapacity = (payload.length + 1) * 2;
		if (newCapacity < 0) newCapacity = Integer.MAX_VALUE;
		else if (minimumCapacity > newCapacity) newCapacity = minimumCapacity;
		byte[] newPayload = new byte[newCapacity];
		try {
			while (curLength > payload.length) curLength--;
			System.arraycopy(payload, 0, newPayload, 0, curLength);
		} catch(Exception e) {}
		payload = newPayload;
	}

	/**
	 * Initializes the buffer builders bit access.
	 * @return the buffer builder, for chaining.
	 */
	public OutBuffer initBitAccess() {
		caret = curLength * 8;
		return this;
	}

	/**
	 * Finishes the buffer builders bit access.
	 * @return the buffer builder, for chaining.
	 */
	public OutBuffer finishBitAccess() {
		curLength = (caret + 7) / 8;
		return this;
	}

	/**
	 * Add bits for building.
	 * @return the buffer builder, for chaining.
	 */
	public OutBuffer addBits(int numBits, int value) {
		int bytePos = caret >> 3;
		int bitOffset = 8 - (caret & 7);
		caret += numBits;
		curLength = (caret + 7) / 8;
		ensureCapacity(curLength);
		for (; numBits > bitOffset; bitOffset = 8) {
			payload[bytePos] &= ~ bitmasks[bitOffset]; // mask out the desired area
			payload[bytePos++] |= (value >> (numBits - bitOffset)) & bitmasks[bitOffset];	
			numBits -= bitOffset;
		}
		if (numBits == bitOffset) {
			payload[bytePos] &= ~ bitmasks[bitOffset];
			payload[bytePos] |= value & bitmasks[bitOffset];
		} else {
			payload[bytePos] &= ~ (bitmasks[numBits] << (bitOffset - numBits));
			payload[bytePos] |= (value & bitmasks[numBits]) << (bitOffset - numBits);
		}
		return this;
	}

	/**
	 * Adds the contents of <tt>byte</tt> array <code>data</code>
	 * to the buffer. The size of this buffer will grow by the length of
	 * the provided array.
	 * @param data The bytes to add to this buffer
	 * @return A reference to this object
	 */
	public OutBuffer addBytes(byte[] data) {
		return addBytes(data, 0, data.length);
	}

	/**
	 * Adds the contents of <tt>byte</tt> array <code>data</code>,
	 * starting at index <code>offset</code>. The size of this buffer will
	 * grow by <code>len</code> bytes.
	 * @param data The bytes to add to this buffer
	 * @param offset The index of the first byte to append
	 * @param len The number of bytes to append
	 * @return A reference to this object
	 */
	public OutBuffer addBytes(byte[] data, int offset, int len) {
		int newLength = curLength + len;
		ensureCapacity(newLength);
		System.arraycopy(data, offset, payload, curLength, len);
		curLength = newLength;
		return this;
	}

	public OutBuffer addLEShortA(int i) {
		ensureCapacity(curLength + 2);
		addByte((byte)(i + 128), false);
		addByte((byte)(i >> 8), false);
		return this;
	}

	public OutBuffer addShortA(int i) {
		ensureCapacity(curLength + 2);
		addByte((byte)(i >> 8), false);
		addByte((byte)(i + 128), false);
		return this;
	}

	/**
	 * Adds a <tt>byte</tt> to the data buffer. The size of this
	 * buffer will grow by one byte.
	 * @param val The <tt>byte</tt> value to add
	 * @return A reference to this object
	 */
	public OutBuffer addByte(byte val) {
		return addByte(val, true);
	}

	public OutBuffer addByteA(int i) {
		return addByte((byte)(i + 128), true);
	}

	/**
	 * Adds a <tt>byte</tt> to the data buffer. The size of this
	 * buffer will grow by one byte.
	 * @param val The <tt>byte</tt> value to add
	 * @param checkCapacity Whether the buffer capacity should be checked
	 * @return A reference to this object
	 */
	private OutBuffer addByte(byte val, boolean checkCapacity) {
		if (checkCapacity) ensureCapacity(curLength + 1);
		payload[curLength++] = val;
		return this;
	}

	/**
	 * Adds a <tt>short</tt> to the data stream. The size of this
	 * buffer will grow by two bytes.
	 * @param val The <tt>short</tt> value to add
	 * @return A reference to this object
	 */
	public OutBuffer addShort(int val) {
		ensureCapacity(curLength + 2);
		addByte((byte) (val >> 8), false);
		addByte((byte) val, false);
		return this;
	}

	public OutBuffer addLEShort(int val) {
		ensureCapacity(curLength + 2);
		addByte((byte) val, false);
		addByte((byte) (val >> 8), false);
		return this;
	}

	public OutBuffer setShort(int val, int offset) {
		payload[offset++] = (byte) (val >> 8);
		payload[offset++] = (byte) val;
		if(curLength < offset + 2) curLength += 2;
		return this;
	}

	/**
	 * Adds a <code>int</code> to the data stream. The size of this
	 * buffer will grow by four bytes.
	 * @param val The <code>int</code> value to add
	 * @return A reference to this object
	 */
	public OutBuffer addInt(int val) {
		ensureCapacity(curLength + 4);
		addByte((byte) (val >> 24), false);
		addByte((byte) (val >> 16), false);
		addByte((byte) (val >> 8), false);
		addByte((byte) val, false);
		return this;
	}

	public OutBuffer addInt1(int val) {
		ensureCapacity(curLength + 4);
		addByte((byte) (val >> 8), false);
		addByte((byte) val, false);
		addByte((byte) (val >> 24), false);
		addByte((byte) (val >> 16), false);
		return this;
	}

	public OutBuffer addInt2(int val) {
		ensureCapacity(curLength + 4);
		addByte((byte) (val >> 16), false);
		addByte((byte) (val >> 24), false);
		addByte((byte) val, false);
		addByte((byte) (val >> 8), false);
		return this;
	}

	public OutBuffer addLEInt(int val) {
		ensureCapacity(curLength + 4);
		addByte((byte) val, false);
		addByte((byte) (val >> 8), false);
		addByte((byte) (val >> 16), false);
		addByte((byte) (val >> 24), false);
		return this;
	}

	/**
	 * Adds a <code>long</code> to the data stream. The size of this
	 * buffer will grow by eight bytes.
	 * @param val The <code>long</code> value to add
	 * @return A reference to this object
	 */
	public OutBuffer addLong(long val) {
		addInt((int) (val >> 32));
		addInt((int) (val & -1L));
		return this;
	}

	public OutBuffer addLELong(long val) {
		addLEInt((int) (val & -1L));
		addLEInt((int) (val >> 32));
		return this;
	}

	@SuppressWarnings("deprecation")
	public OutBuffer addString(String s) {
		ensureCapacity(curLength + s.length() + 1);
		s.getBytes(0, s.length(), payload, curLength);
		curLength += s.length();
		payload[curLength++] = 0;
		return this;
	}

	/**
	 * Returns a {@link InBuffer} object for the data contained in this builder.
	 * @return This <tt>OutBuffer</tt> converted to an <tt>InBuffer</tt>.
	 */
	public InBuffer asInput() {
		byte[] data = new byte[curLength];
		System.arraycopy(payload, 0, data, 0, curLength);
		return new InBuffer(opCode, data, false, type);
	}

	public OutBuffer addByteC(int val) {
		addByte((byte) -val);
		return this;
	}

	public OutBuffer addByteS(int val) {
		addByte((byte) (128-val));
		return this;
	}

}