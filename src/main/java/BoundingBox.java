package main;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
public class BoundingBox 
{
	public BoundingBox(){}
	public static BufferedImage runAndCut(BufferedImage imgSrc, BufferedImage aSlice)
	{
		int RGB = imgSrc.getRGB(1, 1);
		int maxY = 0, maxX = 0, minY = 0, minX = 0;
		Color myGreen = new Color(145, 245, 0); // Color white
		int rgbPaint = myGreen.getRGB();
		int width = imgSrc.getWidth();
		int height = imgSrc.getHeight();
		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				Color myColor2 = new Color(imgSrc.getRGB(x, y));
				if (RGB != (imgSrc.getRGB(x, y)))
				{
					maxY = y;
					break;
				}
			}
		}
		RGB = imgSrc.getRGB(0,height-1);
		for (int y = height-1; y >= 0; y--)
		{
			for (int x = 0; x < width; x++)
			{
				if (RGB != (imgSrc.getRGB(x, y)))
				{
					minY = y;
					break;
				}
			}
		}
		RGB = imgSrc.getRGB(0, 0);
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				if (RGB != (imgSrc.getRGB(x, y)))
				{
					maxX = x;
					break;
				}
			}
		}
		RGB = imgSrc.getRGB(width-1, 0);
		Color myColor = new Color(RGB);
		
		for (int x = width-1; x >= 0; x--)
		{
			for (int y = 0; y < height; y++)
			{
				if (RGB != (imgSrc.getRGB(x, y)))
				{
					minX = x;
					break;
				}
			}
		}
		maxY = maxY+10;
		maxX = maxX+10;
		minY = minY-10;
		minX = minX-10;
		int imgDstWidth = maxX-minX+1;
		int imgDstHeight = maxY-minY+1;
		BufferedImage dstImg = new BufferedImage(imgDstWidth, imgDstHeight, 1);
		
		int xCounter = 0;
		int yCounter = 0;

		for (int y = minY; y <= maxY; y++)
		{
			xCounter = 0;
			for (int x = minX; x <= maxX; x++)
			{
				dstImg.setRGB(xCounter, yCounter, aSlice.getRGB(x, y));
				xCounter++;
			}
			yCounter++;
		}

		int[] boundingBox = new int[4];
		boundingBox[0] = minX;
		boundingBox[1] = maxX;
		boundingBox[2] = minY;
		boundingBox[3] = maxY;
		return dstImg;
	}
}
