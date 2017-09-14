
public class Steganography {
	
	public static void main(String[] args) {
		String flag = args[0];
		String imageFile = args[1];
		String messageFile = args[2];
		
		Image image;
		Message message;
		int nextBit;
		
		switch(flag) {
		case "-E":
			// encode
			image = new Image(imageFile);
			message = new Message(messageFile, true);
			
			image.printInfo();
			
			boolean freeBitsRemain;
			do {
				nextBit = message.getNextBit();
				freeBitsRemain = image.encodeNextBit(nextBit);
			}
			while(freeBitsRemain);
			
			image.writeImageToFile();
			
			message.close();
			
			break;
		case "-D":
			// decode
			image = new Image(imageFile);
			message = new Message(messageFile, false);
			
			image.printInfo();
			
			do {
				nextBit = image.decodeNextBit();
				message.writeNextBit(nextBit);
			}
			while(nextBit > -1);
			
			message.close();
			
			break;
		default:
			break;
		}
	}

}

// -E inputImage.bmp my-message
// -D inputImage-steg.bmp my-message-out
