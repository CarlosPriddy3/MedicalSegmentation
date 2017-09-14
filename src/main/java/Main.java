package main;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import java.util.Scanner;
public class Main
{
	static BufferedImage manualImage;
	static BufferedImage dstImage;
	static BufferedImage mSlice;
	static BufferedImage aSlice;
	static BufferedImage srcImage;
	static int[] boundingBox;
	public static void main(String[] args)
	{
		String inputImageName = "ID_0090_Manual110.jpg";
		setManualImage(inputImageName);
		String inputMSliceName = "ID_0090_MSlice110.jpg";
		setMSlice(inputMSliceName);
		String inputASliceName = "ID_0090_MSlice111.jpg";
		setASlice(inputASliceName);
		dstImage = mSlice;

		getLiver();
		srcImage = dstImage;

		GaussianApplication gaussian = new GaussianApplication(srcImage, aSlice);
		gaussian.calculateMean();
		gaussian.calculateSigma();
		dstImage = gaussian.performGaussian();



		// Bounding Box based on manual entry, or previous slice
		dstImage = BoundingBox.runAndCut(manualImage, dstImage);
		srcImage = dstImage;



		// Run Color Segmentation inside Bounding Box
		MainClass colorSeg = new MainClass(srcImage, 3, .000005, 1, 2, boundingBox);
		dstImage = colorSeg.getOutputImage();


		// Target contains fewer pixels


	}
	public static void setManualImage(String filename)
	{
		try {
			manualImage = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("Found file but couldn't load it, it might be corrupt.");
		}
	}
	public static void setMSlice(String filename)
	{
		try {
			mSlice = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("Found file but couldn't load it, it might be corrupt.");
		}
	}
	public static void setASlice(String filename)
	{
		try {
			aSlice = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("Found file but couldn't load it, it might be corrupt.");
		}
	}
	public static void getLiver()
	{
		int width = manualImage.getWidth();
		int height = manualImage.getHeight();
		int[] srcRGB = new int[3];
		int[] mSliceRGB = new int[3];
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++)
			{
				srcRGB = rgbToRGB(manualImage.getRGB(w, h));
				mSliceRGB = rgbToRGB(mSlice.getRGB(w, h));

				if (((srcRGB[0]*mSliceRGB[0]) != 0) || ((srcRGB[1]*mSliceRGB[1]) != 0) || ((srcRGB[2]*mSliceRGB[2]) != 0))
				{
					int rgb = mSlice.getRGB(w, h);
					dstImage.setRGB(w, h, rgb);
				}
				else
				{
					dstImage.setRGB(w, h, manualImage.getRGB(w, h));
				}
			}
	}
	public static int[] rgbToRGB(int rgb)
	{
		int[] RGB = new int[3];
		RGB[0] = (rgb >> 16) & 0xFF;
		RGB[1] = (rgb >> 8) & 0xFF;
		RGB[2] = (rgb & 0xFF);
		return RGB;
	}

}
