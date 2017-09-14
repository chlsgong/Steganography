import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

// Once you encode you cannot decode and vice versa
public class Image {
	private BufferedImage originalImage;
	private BufferedImage stegImage;
	private String name;
	private int height;
	private int width;
	private long pixels;
	private long bitsRemaining;
	private int x;
	private int y;
	private short encodingFlag;
	private Pixel pixel;
	private int zeroCnt;
	
	class Pixel {
		private int[] rgba;
		private int bitPtr;
		boolean isFull;
		boolean isNew;
		
		public Pixel() {
			rgba = new int[4];
			bitPtr = 0;
			isFull = false;
			isNew = true;
		}
		
		void setRGBA(int a, int r, int g, int b) {
			rgba[0] = r;
			rgba[1] = g;
			rgba[2] = b;
			rgba[3] = a;
			
			bitPtr = 0;
			isNew = false;
		}
		
		void encodeBit(int bit) {
			if(isFull) {
				return;
			}
			
			int color = rgba[bitPtr];
			color += ((color % 2) ^ bit);
			if(color > 255) {
				color -= 2;
			}
			rgba[bitPtr] = color;
			
			if(++bitPtr >= 3) {
				isFull = true;
			}
		}
		
		int decodeBit() {
			if(isFull) {
				return -1;
			}
			
			int color = rgba[bitPtr];
			int bit = color % 2 == 0 ? 0 : 1;
			
			if(++bitPtr >= 3) {
				isFull = true;
			}
			
			return bit;
		}
		
		int pack() {
			isFull = false;
			bitPtr = 0;
			
			int r = rgba[0];
			int g = rgba[1];
			int b = rgba[2];
			int a = rgba[3];
			
			int pixel = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
			return pixel; 
		}
		
		void reset() {
			isFull = false;
			bitPtr = 0;
		}
	}
	
	public Image(String imageFileName) {
		originalImage = null;
		stegImage = null;
        try {
        	originalImage = ImageIO.read(new File(imageFileName));
        }
        catch(IOException e) {
        	System.out.println(e.getLocalizedMessage());
        }
        name = imageFileName;
        height = originalImage.getHeight();
        width = originalImage.getWidth();
        pixels = height * width;
        bitsRemaining = pixels * 3;
        x = 0;
        y = 0;
        encodingFlag = 0;
        pixel = new Pixel();
        zeroCnt = 0;
	}
	
	boolean encodeNextBit(int messageBit) {
		if(encodingFlag == 2 || bitsRemaining <= 0) {
			return false;
		}
		
		initEncoder();
				
		if(messageBit == 0) {
			zeroCnt++;
		}
		else {
			zeroCnt = 0;
		}
				
		if(bitsRemaining <= 8 && zeroCnt <= 8) {
			encodeZeros((int) bitsRemaining);	
			printError();
		}
		else if(messageBit < 0) {
			encodeZeros(8);
		}
		else {
			encodeBit(messageBit);
		}
		
		encodingFlag = 1;
		
		return freeBitsRemain();
	}
	
	int decodeNextBit() {
		if(encodingFlag == 1 || bitsRemaining <= 0) {
			return -1;
		}
				
		encodingFlag = 2;
		
		return decodeBit();
	}
	
	void writeImageToFile() {
		try {
			String[] parts = name.split(Pattern.quote("."));
			String fileName = parts[0];
			String extension = parts[1];
			String stegFileName = fileName + "-steg." + extension;
			
		    ImageIO.write(stegImage, extension, new File(stegFileName));
		}
		catch (IOException e) {
		    System.out.println(e.getLocalizedMessage());
		}
	}
	
	void printInfo() {
		System.out.println("File name: " + name);
		System.out.println("Pixels:    " + pixels);
		System.out.println("Height:    " + height);
		System.out.println("Width:     " + width);
	}
	
	// Helper functions
	
	private void encodeBit(int bit) {
		if(pixel.isFull || pixel.isNew) {
			if(!pixel.isNew) {
				stegImage.setRGB(x, y, pixel.pack());
				incrementPixel();
			}
			
			int rgba = originalImage.getRGB(x, y);
			int alpha = (rgba >> 24) & 0xFF;
			int red = (rgba >> 16) & 0xFF;
			int green = (rgba >> 8) & 0xFF;
			int blue = rgba & 0xFF;
			
			pixel.setRGBA(alpha, red, green, blue);
		}

		pixel.encodeBit(bit);
		
		bitsRemaining--;
	}
	
	private int decodeBit() {
		if(pixel.isFull || pixel.isNew) {
			if(!pixel.isNew) {
				pixel.reset();
				incrementPixel();
			}
			int rgba = originalImage.getRGB(x, y);
			int alpha = (rgba >> 24) & 0xFF; 
			int red = (rgba >> 16) & 0xFF;
			int green = (rgba >> 8) & 0xFF;
			int blue = rgba & 0xFF;
			
			pixel.setRGBA(alpha, red, green, blue);
		}
		
		bitsRemaining--;
		
		return pixel.decodeBit();
	}
	
	private void encodeZeros(int numZeros) {
		for(int i = 0; i < numZeros; i++) {
			encodeBit(0);
		}
	}
	
	private void printError() {
		System.out.println("Not enough pixels to store the whole message (Message was truncated).");
	}
	
	private void initEncoder() {
		if(stegImage == null) {
			stegImage = new BufferedImage(width, height, originalImage.getType());
		}
	}
	
	private boolean freeBitsRemain() {
		if(bitsRemaining < 8) {
			// end of image file
			return false;
		}
		return true;
	}
	
	private void incrementPixel() {
		x++;		
		if(x >= width) {
			x = 0;
			y++;
			if(y >= height) {
				x = width - 1;
				y--;
			}
		}
	}
}
