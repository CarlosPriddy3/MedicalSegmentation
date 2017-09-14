package main;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
public class GaussianApplication 
{
	private static BufferedImage srcImage;
	private static BufferedImage dstImage;
	private static int height;
	private static int width;
	private static byte[] srcBuff;
	private static byte[] dstBuff;
	private static double mean;
	private static double sigma;
	private final static double e = Math.E;
	private static int n;
	private final static double zeroConstant = 0.8262859664406144;
	
	
	public GaussianApplication(BufferedImage meanSigmaSource, BufferedImage destImage)
	{
		srcImage = meanSigmaSource;
		dstImage = destImage;
		height = srcImage.getHeight();
		width = srcImage.getWidth();
		srcBuff = ((DataBufferByte) srcImage.getRaster().getDataBuffer()).getData();
		dstBuff = ((DataBufferByte) dstImage.getRaster().getDataBuffer()).getData();
		
		mean = 0;
		sigma = 0;
		n = 0;
	}
	
	//CALCULATE MEAN FROM SOURCE
	public static void calculateMean()
	{
		for (int h = 0; h < height; h++)
		{
			for (int w = 0; w < width; w++)
			{
				int red, green, blue, rgb, greyScaleI;
				rgb = (srcImage.getRGB(w, h));
				red = (rgb >> 16) & 0xFF;
				green = (rgb >> 8) & 0xFF;
				blue = (rgb & 0xFF);
				greyScaleI = (red + green + blue)/3;
				if (greyScaleI != 0)
				{
					mean += greyScaleI;
					n++;
				}
			}
		}
		mean = mean/n;
	}
	
	//CALCULATE SIGMA FROM SOURCE
	public static void calculateSigma()
	{
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++)
			{
				int red, green, blue, rgb, greyScaleI;
				rgb = (srcImage.getRGB(w, h));
				red = (rgb >> 16) & 0xFF;
				green = (rgb >> 8) & 0xFF;
				blue = (rgb & 0xFF);
				greyScaleI = (red + green + blue)/3;
				if (greyScaleI != 0)
				{
					double distance = greyScaleI - mean;
					sigma += (Math.pow(distance,2));
				}

			}
		sigma = sigma/(n-1);
	}
	
	//APPLY GAUSSIAN TO DESTINATION
	public static BufferedImage performGaussian()
	{	
		double[] greyScale = new double[width*height];
		double sigma2 = 2*sigma;
		for (int h = 0; h < height; h++)
		{
			for (int w = 0; w < width; w++)
			{
				int rgb = (dstImage.getRGB(w, h));
				int red, green, blue;
				red = (rgb >> 16) & 0xFF;
				green = (rgb >> 8) & 0xFF;
				blue = (rgb & 0xFF);
				int greyScaleI = (red + green + blue)/3;
				/*int greyScaleI = dstBuff[w+h*width] & 0xFF;
				int red, green, blue, rgb;*/
				red = greyScaleI;
				green = greyScaleI;
				blue = greyScaleI;
				
				rgb = (red << 16 | green << 8 | blue);
				dstImage.setRGB(w, h, rgb);
				
			}
		}
		for (int h = 0; h < height; h++)
			for (int w = 0; w < width; w++)
			{
				int red, green, blue, rgb, greyScaleI;
				rgb = (dstImage.getRGB(w, h));
				red = (rgb >> 16) & 0xFF;
				green = (rgb >> 8) & 0xFF;
				blue = (rgb & 0xFF);
				greyScaleI = (red + green + blue)/3;
				if (greyScaleI != 0)
				{
					double distance = (Math.pow((greyScaleI-mean), 2));
					//GETTING DECIMAL VALUES -> CASTING RESULTS IN DEAD IMAGE
					greyScaleI = (int)((Math.pow(e, (-(distance/sigma2))))*255);//- zeroConstant)*255);
					//greyScale[w+h*width] = greyScaleI;
					red = greyScaleI;
					green = greyScaleI;
					blue = greyScaleI;
					
					rgb = (red << 16 | green << 8 | blue);
					dstImage.setRGB(w, h, rgb);
				}
			}

			for (int h = 0; h < height; h++)
				for (int w = 0; w < width; w++)
				{
					int red, green, blue, rgb, greyScaleI;
					rgb = (dstImage.getRGB(w, h));
					red = (rgb >> 16) & 0xFF;
					green = (rgb >> 8) & 0xFF;
					blue = (rgb & 0xFF);
					greyScaleI = (red + green + blue)/3;
					if (greyScaleI < 255*.55)
					{
						red = 0; green = 0; blue = 0;
					}
					rgb = (red << 16 | green << 8 | blue);
					dstImage.setRGB(w, h, rgb);
				}

		return dstImage;
	}
}
