import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class Message {
	private FileInputStream inStream;
	private Byte outByte;
	private LinkedList<Integer> bits;
	private boolean isInput;
	
	class Byte {
		private FileOutputStream outStream;
		private byte outByte;
		private int pos;
		
		public Byte(String fileName) {
			outStream = null;
			try {
				outStream = new FileOutputStream(fileName);
			}
			catch(FileNotFoundException e) {
				System.out.println(e.getLocalizedMessage());
			}
			outByte = 0;
			pos = 0;
		}
		
		void addBit(int bit) {
			outByte |= (bit & 1) << pos;
			pos++;
			if(pos == 8) {
				writeByte();
				outByte = 0;
				pos = 0;
			}
		}
		
		void close() {
			try {
				outStream.close();
			}
			catch (IOException e) {
				e.getLocalizedMessage();
			}
		}
		
		private void writeByte() {
			try {
				outStream.write(outByte);
			}
			catch(Exception e) {
				System.out.println(e.getLocalizedMessage());
			}
		}
	}
	
	public Message(String messageFileName, boolean isInput) {
		inStream = null;
		outByte = null;
		try {
			if(isInput) {
				inStream = new FileInputStream(messageFileName);
			}
			else {
				outByte = new Byte(messageFileName);
			}
		}
		catch(IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
		bits = new LinkedList<Integer>();
		this.isInput = isInput;
	}
	
	int getNextBit() {		
		if(!bits.isEmpty()) {
			return bits.remove();
		}
		
		try {
			int inputByte = inStream.read();
			if(inputByte > -1) {
				for(int i = 0; i < 8; i++) {
					int bit = (inputByte >> i) & 1;
					bits.add(bit);
				}
			}
		}
		catch(IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		if(bits.isEmpty()) {
			return -1;
		}
		return bits.remove();
	}
	
	void writeNextBit(int bit) {
		if(bit > -1) {
			outByte.addBit(bit);
		}
	}
	
	void close() {
		try {
			if(isInput) {
				inStream.close();
			}
			else {
				outByte.close();
			}
		}
		catch(IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
}
