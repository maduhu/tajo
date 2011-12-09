package nta.datum;

import nta.datum.exception.InvalidCastException;

public class IPv6Datum extends Datum {

	public IPv6Datum(DatumType type) {
		super(type);
	}

	@Override
	public boolean asBool() {
		return false;
	}

	@Override
	public byte asByte() {
		return 0;
	}
	
	@Override
	public short asShort() {	
		throw new InvalidCastException();
	}

	@Override
	public int asInt() {
		return 0;
	}

	@Override
	public long asLong() {
		return 0;
	}

	@Override
	public byte[] asByteArray() {
		return null;
	}

	@Override
	public float asFloat() {
		return 0;
	}

	@Override
	public double asDouble() {
		return 0;
	}

	@Override
	public String asChars() {
		return null;
	}

}
