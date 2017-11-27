package com.ai.iot.bill.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XLR
 *
 */
public class ByteUtil {
	private static final Logger logger = LoggerFactory.getLogger(ByteUtil.class);
	
	static char[] chs = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	public static byte[] getBytes(Serializable obj) {
		try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(bout)){			
			out.writeObject(obj);
			out.flush();
			return bout.toByteArray();
		} catch (Exception e) {
			logger.error("getBytes Exception:{}",e);
		}

		return null;
	}

	public static int sizeof(Serializable obj) {
		if (CheckNull.isNull(obj)) {
			return 0;
		} else {
			byte[] bt = getBytes(obj);
			return (bt == null ? 0 : bt.length);
		}
	}

	public static Object getObject(byte[] bytes) {
		try (ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
				ObjectInputStream oi = new ObjectInputStream(bi)){
			return oi.readObject();
		} catch (Exception e) {
			// pass
		}

		return null;
	}

	/**
	 * 压缩
	 * 
	 * @param data
	 *            待压缩数据
	 * @return byte[] 压缩后的数据
	 */
	public static byte[] compress(byte[] data) {
		byte[] output;

		Deflater compresser = new Deflater();

		compresser.reset();
		compresser.setInput(data);
		compresser.finish();
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length)){
			byte[] buf = new byte[1024];
			while (!compresser.finished()) {
				int i = compresser.deflate(buf);
				bos.write(buf, 0, i);
			}
			output = bos.toByteArray();
		} catch (Exception e) {
			output = data;
			logger.error("compress IOException:{}",e);
		}
		compresser.end();
		return output;
	}

	/**
	 * 压缩
	 * 
	 * @param data
	 *            待压缩数据
	 * 
	 * @param os
	 *            输出流
	 */
	public static void compress(byte[] data, OutputStream os) {
		try (DeflaterOutputStream dos = new DeflaterOutputStream(os)){
			dos.write(data, 0, data.length);

			dos.finish();

			dos.flush();
		} catch (IOException e) {
			logger.error("compress IOException:{}",e);
		}
	}

	/**
	 * 解压缩
	 * 
	 * @param data
	 *            待压缩的数据
	 * @return byte[] 解压缩后的数据
	 */
	public static byte[] decompress(byte[] data) {
		byte[] output = new byte[0];

		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(data);
		
		try(ByteArrayOutputStream o = new ByteArrayOutputStream(data.length)) {
			byte[] buf = new byte[1024];
			while (!decompresser.finished()) {
				int i = decompresser.inflate(buf);
				o.write(buf, 0, i);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			output = data;
			logger.error("decompress IOException:{}",e);
		}

		decompresser.end();
		return output;
	}

	/**
	 * 解压缩
	 * 
	 * @param is
	 *            输入流
	 * @return byte[] 解压缩后的数据
	 */
	public static byte[] decompress(InputStream is) {
		InflaterInputStream iis = new InflaterInputStream(is);
		ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
		try {
			int i = 1024;
			byte[] buf = new byte[i];

			while ((i = iis.read(buf, 0, i)) > 0) {
				o.write(buf, 0, i);
			}

		} catch (IOException e) {
			logger.error("decompress IOException:{}",e);
		}
		return o.toByteArray();
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
	 * 
	 * @param value
	 *            要转换的int值
	 * @return byte数组
	 */
	public static byte[] intToBytes(int value) {
		byte[] src = new byte[4];
		src[3] = (byte) ((value >> 24) & 0xFF);
		src[2] = (byte) ((value >> 16) & 0xFF);
		src[1] = (byte) ((value >> 8) & 0xFF);
		src[0] = (byte) (value & 0xFF);
		return src;
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。 和bytesToInt2（）配套使用
	 */
	public static byte[] intToBytes2(int value) {
		byte[] src = new byte[4];
		src[0] = (byte) ((value >> 24) & 0xFF);
		src[1] = (byte) ((value >> 16) & 0xFF);
		src[2] = (byte) ((value >> 8) & 0xFF);
		src[3] = (byte) (value & 0xFF);
		return src;
	}

	/**
	 * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
	 * 
	 * @param src
	 *            byte数组
	 * @param offset
	 *            从数组的第offset位开始
	 * @return int数值
	 */
	public static int bytesToInt(byte[] src, int offset) {
		int value;
		value = ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8) | ((src[offset + 2] & 0xFF) << 16)
				| ((src[offset + 3] & 0xFF) << 24));
		return value;
	}

	/**
	 * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
	 */
	public static int bytesToInt2(byte[] src, int offset) {
		int value;
		value = (((src[offset] & 0xFF) << 24) | ((src[offset + 1] & 0xFF) << 16) | ((src[offset + 2] & 0xFF) << 8)
				| (src[offset + 3] & 0xFF));
		return value;
	}

	/// char转化为byte
	public static byte[] charToByte(char c) {
		byte[] b = new byte[2];
		b[0] = (byte) ((c & 0xFF00) >> 8);
		b[1] = (byte) (c & 0xFF);
		return b;
	}

	// chars转bytes
	public static byte[] charsToBytes(char[] chars) {
		CharBuffer cb = CharBuffer.allocate(chars.length);
		cb.put(chars);
		cb.flip();
		ByteBuffer bb = Const.UTF8.encode(cb);
		return bb.array();
	}

	/// byte转换为char
	public static char byteToChar(byte[] b) {
		return (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
	}

	// bytes转chars
	public static char[] bytesToChars(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = Const.UTF8.decode(bb);
		return cb.array();
	}
	
	//hex bytes转string
	public static String hexToString(byte[] b,int start,int end) {
		StringBuilder sb = new StringBuilder();
        
    	for(int i=start;i<end;i++){
    		sb.append(chs[(b[i] & 0x0F0)>>4]);
    		sb.append(chs[(b[i] & 0x00F)]);
    	}
    	
        return sb.toString();
    }
	
	public static String hexToString(byte[] b){
		return hexToString(b,0,b.length);
	}
	
	//byte to TBCD
	public static String byteToTBCD(byte[] b,int start,int end,char fillChar) {
		StringBuilder sb = new StringBuilder();
        
    	for(int i=start;i<end;i++){
    		char c = chs[(b[i] & 0x00F)];
    		if(c != fillChar){
    			sb.append(c);
    		}
    		c = chs[(b[i] & 0x0F0)>>4];
    		if(c != fillChar){
    			sb.append(c);
    		}
    	}
    	
        return sb.toString();
    }
	//byte to PBCD
	public static String byteToPBCD(byte[] b,int start,int end,char fillChar) {
		StringBuilder sb = new StringBuilder();
        
    	for(int i=start;i<end;i++){
    		char c = chs[(b[i] & 0x0F0)>>4];
    		if(c != fillChar){
    			sb.append(c);
    		}
    		c = chs[(b[i] & 0x00F)];
    		if(c != fillChar){
    			sb.append(c);
    		}
    	}
    	
        return sb.toString();
    }
	
	public static void dispByte(byte[] b,int start,int end) {
		int i = 0;
		StringBuilder sb = new StringBuilder();

		sb.append("\n").append("byte [ ").append(end - start).append(" ] start:");
		for (int pos = start;pos <= end;pos ++,i++) {			
			if (i % 16 == 15) {
				sb.append("\n");
				i = 0;
			}
			
			int hex = b[pos] & 0x0FF;
			
			if(hex/16>0){
				sb.append(chs[hex/16]);
	        }else{
	        	sb.append("0");
	        }
			sb.append(chs[hex % 16]).append(" ");
		}
		logger.debug(sb.toString());
	}
	
	private ByteUtil() {
	    throw new IllegalStateException("Utility class");
	  }
}
