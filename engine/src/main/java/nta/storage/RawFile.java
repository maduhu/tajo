package nta.storage;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.nio.ByteBuffer;

import nta.catalog.Column;
import nta.catalog.Schema;
import nta.conf.NtaConf;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class RawFile implements UpdatableScanner {

	private final Path dataPath;
	private final FileSystem fs;
	private final Schema schema;
	private final FileStatus[] filelist;
	
	private FSDataInputStream in;
	private FSDataOutputStream out;
	
	public RawFile(NtaConf conf, Store store) throws IOException {
		this.schema = store.getSchema();

		dataPath = new Path(new Path(store.getURI()), "data");
		this.fs = dataPath.getFileSystem(conf);
		if (!this.fs.exists(dataPath)) {
			this.fs.mkdirs(dataPath);
		}
		filelist = fs.listStatus(dataPath);
	}
	
	@Override
	public void init() throws IOException {
		if (filelist.length > 0) {
			in = fs.open(filelist[0].getPath());
		} else {
			out = fs.create(new Path(dataPath, "table"+filelist.length+".raw"));
		}
	}

	@Override
	public Tuple next() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VTuple next2() throws IOException {
		if (in.available() == 0) {
			return null;
		}
		
		VTuple tuple = new VTuple(schema.getColumnNum());

//		if (in.available() < StorageUtils.getRowByteSize(schema))
//			return null;
		boolean [] contains = new boolean[schema.getColumnNum()];
		for (int i = 0; i < schema.getColumnNum(); i++) {
			contains[i] = in.readBoolean();
		}

		Column col = null;
		for (int i = 0; i < schema.getColumnNum(); i++) {
			if (contains[i]) {
				col = schema.getColumn(i);
				switch (col.getDataType()) {
				case BYTE:
					tuple.put(i, in.readByte());
					break;
				case SHORT:
					tuple.put(i, in.readShort());
					break;
				case INT:
					tuple.put(i, in.readInt());
					break;
				case LONG:
					tuple.put(i, in.readLong());
					break;
				case FLOAT:
					tuple.put(i, in.readFloat());
					break;
				case DOUBLE:
					tuple.put(i, in.readDouble());
					break;
				case STRING:
					short len = in.readShort();
					byte[] buf = new byte[len];
					in.read(buf, 0, len);
					tuple.put(i, new String(buf));
					break;
				case IPv4:
					byte[] ipv4 = new byte[4];
					in.read(ipv4, 0, 4);
					tuple.put(i, ipv4);
					break;
				default:
					break;
				}
			}
		}

		return tuple;
	}

	@Override
	public void reset() throws IOException {
		in.reset();
	}

	@Override
	public void close() throws IOException {
		if (in != null) {
			in.close();
		}
		if (out != null) {
			out.flush();
			out.close();
		}
	}

	@Override
	public Schema getSchema() {
		return this.schema;
	}

	@Override
	public boolean isLocalFile() {
		return true;
	}

	@Override
	public boolean readOnly() {
		return false;
	}

	@Override
	public boolean canRandomAccess() {
		return false;
	}

	@Override
	public void putAsByte(int fid, byte val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsShort(int fid, short val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsInt(int fid, int val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsLong(int fid, long val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsFloat(int fid, float val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsDouble(int fid, double val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsBytes(int fid, byte[] val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsBytes(int fid, ByteBuffer val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsIPv4(int fid, Inet4Address val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsIPv4(int fid, byte[] val) throws Exception {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsIPv6(int fid, Inet6Address val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsChars(int fid, char[] val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsChars(int fid, String val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void putAsChars(int fid, byte[] val) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException();
	}

	@Override
	public void addTuple(Tuple tuple) throws IOException {
		Column col = null;
		for (int i = 0; i < schema.getColumnNum(); i++) {
			out.writeBoolean(tuple.contains(i));
		}
		for (int i = 0; i < schema.getColumnNum(); i++) {
			if (tuple.contains(i)) {
				col = schema.getColumn(i);
				switch (col.getDataType()) {
				case BYTE:
					out.writeByte(tuple.getByte(i));
					break;
				case STRING:
					byte[] buf = tuple.getString(i).getBytes();
					if (buf.length > 256) {
						buf = new byte[256];
						byte[] str = tuple.getString(i).getBytes();
						System.arraycopy(str, 0, buf, 0, 256);
					} 
					out.writeShort(buf.length);
					out.write(buf, 0, buf.length);
					break;
				case SHORT:
					out.writeShort(tuple.getShort(i));
					break;
				case INT:
					out.writeInt(tuple.getInt(i));
					break;
				case LONG:
					out.writeLong(tuple.getLong(i));
					break;
				case FLOAT:
					out.writeFloat(tuple.getFloat(i));
					break;
				case DOUBLE:
					out.writeDouble(tuple.getDouble(i));
					break;
				case IPv4:
					out.write(tuple.getIPv4Bytes(i));
					break;
				case IPv6:
					out.write(tuple.getIPv6Bytes(i));
					break;
				default:
					break;
				}
			}
		}
	}

}
