UTEID: hcg359;
FIRSTNAME: Charles;
LASTNAME: Gong;
CSACCOUNT: hcgong;
EMAIL: chlsgong@gmail.com;

[Program 3]
[Description]

There are 3 java files in this program. In Image.java is where all the logic for decoding and encoding image files lives. It contains the Image class and a nested Pixel class used for storing pixel information such as RGBA values and whether those values contain encoded data. The most important functions are encodeNextBit() and decodeNextBit(). These are the meat of the encryption algorithm. They use helper functions to delegate specific tasks but it is all put together in there. In Message.java is where the logic for writing and reading bits from the message file. The most important functions are readNextBit() and writeNextBit(). To compile the program run javac *.java within the src folder. To run the program use java Steganography -F INPUTIMAGE MESSAGE where -F is the encoding flag (-E for encoding or -D for decoding), INPUTIMAGE is the file name for the image file, and MESSAGE is the file name for the message file. There are two test files in the src folder.  


[Finish]

I finished all parts of the assignment.


[Questions&Answers]

[Question 1]
Comparing your original and modified images carefully, can you detect *any* difference visually (that is, in the appearance of the image)?

[Answer 1]

I cannot visually tell the difference from the original image and the encoded image.
   
[Question 2]
Can you think of other ways you might hide the message in image files (or in other types of files)?

[Answer 2]

I can’t think of any other ways to encode messages within an image file.

[Question 3]
Can you invent ways to increase the bandwidth of the channel?

[Answer 3]

You could use the alpha to encode one more bit than just 3 per pixel.

[Question 4]
Suppose you were tasked to build an "image firewall" that would block images containing hidden messages. Could you do it? How might you approach this problem?

[Answer 4]

You could decode the image and see if you can find any patterns within it.

[Question 5]
Does this fit our definition of a covert channel? Explain your answer.

[Answer 5]

This does fit the definition of a covert channel because the pixels which are not supposed to be used for communication is used to send messages.
