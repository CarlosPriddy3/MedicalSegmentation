package main;

import java.awt.image.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
//import GUI.KMeansGUI;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.awt.color.ColorSpace;
import javax.imageio.*;
import java.awt.Color;
import java.text.DecimalFormat;

public class Operations 
{

    private static int k;
    private static double specificity;
    private static int colorSpaceSelection;
    private static int distanceSelection;
    //private KMeansGUI gui;
    private static int imageWidth;
    private static int imageHeight;
    private static float[] distanceArray;
    private static int min = 0;
    private static int xMax, yMax;
    private static int EUC = 1, NEI = 2, MAH = 3, MAHNEI = 4, MAHEUCNEI = 5;
    private static int RGB = 1, LAB = 2, HSB = 3;
    private static int[]boundingBox;


    public Operations(){}
    //public Operations(int k, double specificity, int colorSpaceSelection, int distanceSelection, KMeansGUI gui, BufferedImage inputImage)
    public Operations(int k, double specificity, int colorSpaceSelection, int distanceSelection, BufferedImage inputImage)
    {
        this.k = k;
        this.specificity = specificity;
        this.colorSpaceSelection = colorSpaceSelection;
        this.distanceSelection = distanceSelection;
        //this.gui = gui;
        imageWidth = inputImage.getWidth();
        imageHeight = inputImage.getHeight();
        xMax = imageWidth-1;
        yMax = imageHeight-1;
        
    }
    public Operations(int k, double specificity, int colorSpaceSelection, int distanceSelection, BufferedImage inputImage, int[] boundingBox)
    {
    	this.boundingBox = boundingBox;
        this.k = k;
        this.specificity = specificity;
        this.colorSpaceSelection = colorSpaceSelection;
        this.distanceSelection = distanceSelection;
        //this.gui = gui;
        imageWidth = inputImage.getWidth();
        imageHeight = inputImage.getHeight();
        xMax = imageWidth-1;
        yMax = imageHeight-1;
        
    }
    public static float[][] returnMeans(PixelObjects[] pixelObjectArray)
    {
        //NEW ARRAY FOR R G and B MEANS
        float[][] meansList = new float[k][5];
        int serialCount = 0;

        	for (int l = 0; l < pixelObjectArray.length; l++)
	        {    	
	            float x = pixelObjectArray[serialCount].getX();
	            float y = pixelObjectArray[serialCount].getY();
	            float z = pixelObjectArray[serialCount].getZ();
	            int minIndex = pixelObjectArray[serialCount].getAffiliatedCentroidIndex();
	
	            meansList[minIndex][0] += x;
	            meansList[minIndex][1] += y;
	            meansList[minIndex][2] += z;
	            meansList[minIndex][3] += 1;
	            serialCount++;
	        }
	        for (int i = 0; i < k; i++)
	        {
	            if (meansList[i][3] > 0)
	            {
	            //X TOTAL / TOTAL PIXELS ADDED TO CLUSTER
	            meansList[i][0] = (meansList[i][0]/meansList[i][3]);
	            //Y TOTAL / TOTAL PIXELS ADDED TO CLUSTER
	            meansList[i][1] = (meansList[i][1]/meansList[i][3]);
	            //Z TOTAL / TOTAL PIXELS ADDED TO CLUSTER
	            meansList[i][2] = (meansList[i][2]/meansList[i][3]);
	            meansList[i][4] = 1;
	            }
	            else
	                meansList[i][4] = 0;
	        }
        
        
        return meansList;
    }
    public static PixelObjects[] findDistances(RandomCentroid[] centroidList, PixelObjects[] pixelObjectArray)
    {
        int serialCount = 0;
        for (int l = 0; l < pixelObjectArray.length; l++)
        {
            float x = pixelObjectArray[serialCount].getX();
            float y = pixelObjectArray[serialCount].getY();
            float z = pixelObjectArray[serialCount].getZ();
            distanceArray = new float[k];
            
            //Calculate Euclidean Distance to Each Centroid - Store in distanceArray -> PixelObject
            for (int m = 0; m < k; m++)
            {
                distanceArray[m] = (float)(Math.sqrt(Math.pow((centroidList[m].getX()-x), 2)
                        + (Math.pow(centroidList[m].getY()-y, 2))
                        + (Math.pow(centroidList[m].getZ()-z, 2))));
            }

            pixelObjectArray[serialCount].setDistanceAllCentroids(distanceArray);
            int minIndex = 0;
            float minDistance = distanceArray[0];
            
            //SELECTION: CLOSEST CENTROID
            for (int m = 1; m < k; m++)
            {
                if (distanceArray[m] < minDistance)
                {
                    minDistance = distanceArray[m];
                    minIndex = m;
                }
            }
            pixelObjectArray[serialCount].setDistance(minDistance, minIndex);
            serialCount++;
        }
        return pixelObjectArray;
    }
    public PixelObjects[] findDistancesNeighbourhood(RandomCentroid[] centroidList, PixelObjects[] pixelObjectArray)
    {
        int serialCount = 0;
        for (int i = 0; i < imageHeight; i++)
        {
            for(int j = 0; j < imageWidth; j++)
            {
                distanceArray = pixelObjectArray[serialCount].getDistanceAllCentroids();
                float distanceArrayNeighbourhood[] = new float[k];
                float sum = 0;
                
                if (j>min && i>min && j<xMax && i<yMax)
                {
                    for (int m = 0; m < k; m++)
                    {
                        sum += pixelObjectArray[serialCount+1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth+1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth-1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth+1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth-1].getIndividualDistance(m);
                        sum = sum/8;
                        distanceArrayNeighbourhood[m] = sum;
                    }
                }
                else if (j != min  && j != xMax && i == min)
                {
                    for (int m = 0; m < k; m++)
                    {
                        sum += pixelObjectArray[serialCount+1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth+1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth-1].getIndividualDistance(m);
                        sum = sum/5;
                        distanceArrayNeighbourhood[m] = sum;
                    }
                }
                else if (j != min && j != xMax && i == yMax)
                {
                    for (int m = 0; m < k; m++)
                    {
                        sum += pixelObjectArray[serialCount+1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth+1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth-1].getIndividualDistance(m);
                        sum = sum/5;
                        distanceArrayNeighbourhood[m] = sum;
                    }
                }
                else if ( i != min && i != yMax && j == min)
                {
                    for (int m = 0; m < k; m++)
                    {
                        sum += pixelObjectArray[serialCount+1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth+1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth+1].getIndividualDistance(m);
                        sum = sum/5;
                        distanceArrayNeighbourhood[m] = sum;
                    }
                }
                else if(i != min && i != yMax && j == xMax)
                {
                    for (int m = 0; m < k; m++)
                    {
                        sum += pixelObjectArray[serialCount-1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth-1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth-1].getIndividualDistance(m);
                        sum = sum/5;
                        distanceArrayNeighbourhood[m] = sum;
                    }
                }
                else if( i == min && j == min)
                {
                    for (int m = 0; m < k; m++)
                    {
                        sum += pixelObjectArray[serialCount+1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth+1].getIndividualDistance(m);
                        sum = sum/3;
                        distanceArrayNeighbourhood[m] = sum;
                    }
                }
                else if ( i == yMax && j == min)
                {
                    for (int m = 0; m < k; m++)
                    {
                        sum += pixelObjectArray[serialCount+1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth+1].getIndividualDistance(m);
                        sum = sum/3;
                        distanceArrayNeighbourhood[m] = sum;
                    }
                }
                else if ( i == min && j == xMax)
                {
                    for (int m = 0; m < k; m++)
                    {
                        sum += pixelObjectArray[serialCount-1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount+imageWidth-1].getIndividualDistance(m);
                        sum = sum/3;
                        distanceArrayNeighbourhood[m] = sum;
                    }
                }
                else if ( i == yMax && j == xMax)
                {
                    for (int m = 0; m < k; m++)
                    {
                        sum += pixelObjectArray[serialCount-1].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth].getIndividualDistance(m);
                        sum += pixelObjectArray[serialCount-imageWidth-1].getIndividualDistance(m);
                        sum = sum/3;
                        distanceArrayNeighbourhood[m] = sum;
                    }
                }
                        
                float findLowestDistance[] = new float[k];
                if (distanceSelection == MAH || distanceSelection == MAHNEI || distanceSelection == MAHEUCNEI)
                {
                	for(int m = 0; m < k; m++)
                    {
                        findLowestDistance[m] = (float)((distanceArray[m]*0.4) + (distanceArrayNeighbourhood[m]*0.6));
                    }
                }
                else
                {
	                for(int m = 0; m < k; m++)
	                {
	                    findLowestDistance[m] = (float)((distanceArray[m]*0.2) + (distanceArrayNeighbourhood[m]*0.8));
	                }
                }
                
                int minIndex = 0;
                double minDistance = findLowestDistance[0];
                for (int m = 1; m < k; m++)
                {
                    if (findLowestDistance[m] < minDistance)
                    {
                        minDistance = findLowestDistance[m];
                        minIndex = m;
                    }
                }
                pixelObjectArray[serialCount].setAffiliatedCentroidIndex(minIndex);
                serialCount++;
            }
        }
        return pixelObjectArray;
    }
    public static PixelObjects[] findDistancesMahalanobis(RandomCentroid[] centroidList, PixelObjects[] pixelObjectArray, double[][][] varianceMatrices)
    {
        int serialCount = 0;
        for (int l = 0; l < (pixelObjectArray.length); l++)
        {
            float[] tempVector = new float[3];
            float x = pixelObjectArray[serialCount].getX();
            float y = pixelObjectArray[serialCount].getY();
            float z = pixelObjectArray[serialCount].getZ();
            
            distanceArray = new float[k];
            float[] differenceMu = new float[3];
            if (colorSpaceSelection == RGB)
            {
	            for (int m = 0; m < k; m++)
	            {
	                differenceMu[0] = (x - centroidList[m].getX());
	                differenceMu[1] = (y - centroidList[m].getY());
	                differenceMu[2] = (z - centroidList[m].getZ());
	                tempVector[0] = (float)((differenceMu[0]*(varianceMatrices[m][0][0]*1.5)) + (differenceMu[1]*varianceMatrices[m][1][0]) + (differenceMu[2]*varianceMatrices[m][2][0]));
	                tempVector[1] = (float)((differenceMu[0]*varianceMatrices[m][0][1]) + (differenceMu[1]*(varianceMatrices[m][1][1]*2) + (differenceMu[2]*varianceMatrices[m][2][1])));
	                tempVector[2] = (float)((differenceMu[0]*varianceMatrices[m][0][2]) + (differenceMu[1]*varianceMatrices[m][1][2]) + (differenceMu[2]*(varianceMatrices[m][2][2]*2)));
	                distanceArray[m] = ((tempVector[0]*differenceMu[0]) + (tempVector[1]*differenceMu[1]) + (tempVector[2]*differenceMu[2]));
	            }
            }
            else if (colorSpaceSelection == LAB)
            {
            	for (int m = 0; m < k; m++)
	            {
	                differenceMu[0] = (x - centroidList[m].getX());
	                differenceMu[1] = (y - centroidList[m].getY());
	                differenceMu[2] = (z - centroidList[m].getZ());
	                tempVector[0] = (float)((differenceMu[0]*varianceMatrices[m][0][0]) + (differenceMu[1]*varianceMatrices[m][1][0]) + (differenceMu[2]*varianceMatrices[m][2][0]));
	                tempVector[1] = (float)((differenceMu[0]*varianceMatrices[m][0][1]) + (differenceMu[1]*varianceMatrices[m][1][1]*50) + (differenceMu[2]*varianceMatrices[m][2][1]*10));
	                tempVector[2] = (float)((differenceMu[0]*varianceMatrices[m][0][2]) + (differenceMu[1]*varianceMatrices[m][1][2]*10) + (differenceMu[2]*varianceMatrices[m][2][2]*50));
	                distanceArray[m] = ((tempVector[0]*differenceMu[0]) + (tempVector[1]*differenceMu[1]) + (tempVector[2]*differenceMu[2]));
	            }
            }
              
            int minIndex = 0;
            float minDistance = distanceArray[0];
            
            //SELECTION: CLOSEST CENTROID
            for (int m = 1; m < k; m++)
            {
                if (distanceArray[m] < minDistance)
                {
                    minDistance = distanceArray[m];
                    minIndex = m;
                }
            }
            pixelObjectArray[serialCount].setDistance(minDistance, minIndex);
            pixelObjectArray[serialCount].setDistanceAllCentroids(distanceArray);
            serialCount++; 
        }
        return pixelObjectArray;
    }
    public static double[][][] findZeroMatrices(PixelObjects[] pixelObjectArray, RandomCentroid[] centroidList, double[][][] varianceMatrix)
    {
        int arrayLength = centroidList.length;
        int n = 3;
        for (int m = 0; m < arrayLength; m++)
        {
            if (centroidList[m].getIsActive() == 0)
            {
               // double a[][] = new double[n][n];
                for(int i=0; i<n; i++)
                    for(int j=0; j<=i; j++)
                    {
                        double randomNumber = Math.random()/100;
                        varianceMatrix[m][i][j] = randomNumber;
                        varianceMatrix[m][j][i] = randomNumber;
                    }
                /*for(int i=0; i<n; i++)
                    for(int j=0; j<n; j++)
                        a[i][j] = varianceMatrix[m][i][j];
                double d[][] = invert(a);
                for(int i=0; i<n; i++)
                    for(int j=0; j<n; j++)
                        varianceMatrix[m][i][j] = d[i][j];*/
            }
                
        }
        return varianceMatrix;
    }
    public static RandomCentroid[] setIsActive(RandomCentroid[] centroidList, PixelObjects[] pixelObjectArray)
    {
        int[] count = new int[k];
        for (int m = 0; m < centroidList.length; m++)
        {
            int serialCount = 0;
            for (int i = 0; i < pixelObjectArray.length; i++)
            {
                if (pixelObjectArray[serialCount].getAffiliatedCentroidIndex() == m)
                    count[m]++;
                serialCount++;
            }
        }
        for (int m = 0; m < centroidList.length; m++)
        {
            if (count[m] > 0)
                centroidList[m].setIsActive(1);
        }
        return centroidList;
    }
    public static BufferedImage importImage(String s)
    {
        //IMPORTS IMAGE
        BufferedImage image1 = null;
        try 
        {
        image1 = ImageIO.read(new File(s));
        } 
        catch (IOException e) 
        {
            System.out.println("Invalid image");
            System.exit(1);
        }
        return image1;
    }
    public static PixelObjects[] assignPixels(BufferedImage image1)
    {
        //NOTE NOTE NOTE - Should it be Width/Height or Height/Width
        PixelObjects[] pixelObjectArray = new PixelObjects[image1.getWidth() * image1.getHeight()];
        int serialCount = 0;
        //LOOP ITERATION TO CYCLE THROUGH ALL PIXELS AND ASSIGN TO 2DIM ARRAY
        for (int i = 0; i < image1.getHeight(); i++)
        {
            for (int j = 0; j < image1.getWidth(); j++)
            {    
                //pixelColors[j][i] = image1.getRGB(j, i);
                //int rGB = image1.getRGB(j, i);
                //int r = (rGB >> 16) & 0xFF;
                //int g = (rGB >> 8) & 0xFF;
                //int b = rGB & 0xFF;
                //J and I passed to identify location of specific pixel object
                pixelObjectArray[serialCount] = new PixelObjects(i, j, image1.getRGB(j, i));
                serialCount++;
            }
        }   
        return pixelObjectArray;
    }
    public static BufferedImage assignNewRGB(BufferedImage outputImage, RandomCentroid[] centroidList, PixelObjects[] pixelObjectArray)
    {
        int serialCount = 0;
        CIELab cSpace = new CIELab();
        for(int m = 0; m < k; m++)
        {
            serialCount = 0;
            for (int i = 0; i < imageHeight; i++)
            {
                for (int j = 0; j < imageWidth; j++)
                { 
                    if (pixelObjectArray[serialCount].getAffiliatedCentroidIndex() == m)
                    {
                        float[] tempArray = new float[3];
                        tempArray[0] = centroidList[m].getX();
                        tempArray[1] = centroidList[m].getY();
                        tempArray[2] = centroidList[m].getZ();
                        if (colorSpaceSelection == LAB)
                        {
                            tempArray = cSpace.toRGB(tempArray);
                        }
                        int rGB = 0;
                        int red = (int)tempArray[0];
                        int green = (int) tempArray[1];
                        int blue = (int)tempArray[2];
                        rGB = new Color(red, green, blue).getRGB();
                        /*rGB = rGB | (red << 16);
                        rGB = rGB | (green << 8);
                        rGB = rGB | (blue);*/
                        outputImage.setRGB(j, i, rGB);
                    }
                    serialCount++;
                }
            }
        }
        /*
        //NOTE NOTE NOTE - Should it be Width/Height or Height/Width
        Color green = new Color(0, 255, 0);
        Color red = new Color(255, 0, 0);
        Color blue = new Color(0, 0, 255);
        Color yellow = new Color(255, 255, 0);
        Color cyan = new Color(0, 255, 255);
        Color magenta = new Color(255, 0, 255);
        Color white = new Color(255, 255, 255);
        Color black = new Color(0, 0, 0);
        int serialCount = 0;
        for (int i = 0; i < height; i++)
        {
            for(int j = 0; j < imageWidth; j++)
            {
                if (pixelObjectArray[serialCount].getAffiliatedCentroidIndex() == 0)
                {
                    outputImage.setRGB(j, i, black.getRGB());
                }
                else if (pixelObjectArray[serialCount].getAffiliatedCentroidIndex() == 1)
                {
                    outputImage.setRGB(j, i, blue.getRGB()); 
                }
                else if (pixelObjectArray[serialCount].getAffiliatedCentroidIndex() == 2)
                {
                    outputImage.setRGB(j, i, green.getRGB());
                }
                else if (pixelObjectArray[serialCount].getAffiliatedCentroidIndex() == 3)
                {
                    outputImage.setRGB(j, i, red.getRGB());
                }
                else if (pixelObjectArray[serialCount].getAffiliatedCentroidIndex() == 4)
                {
                    outputImage.setRGB(j, i, cyan.getRGB());
                }
                else if (pixelObjectArray[serialCount].getAffiliatedCentroidIndex() == 5)
                {
                    outputImage.setRGB(j, i, white.getRGB()); 
                }
                else if (pixelObjectArray[serialCount].getAffiliatedCentroidIndex() == 6)
                {
                    outputImage.setRGB(j, i, yellow.getRGB());
                }
                else if (pixelObjectArray[serialCount].getAffiliatedCentroidIndex() == 7)
                {
                    outputImage.setRGB(j, i, magenta.getRGB());
                }
                serialCount++;
            }
        }*/
        return outputImage;
    } 
    public static BufferedImage outputImage(RandomCentroid[] centroidList, PixelObjects[] pixelObjectArray)
    {
        BufferedImage outputImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        outputImage = assignNewRGB(outputImage, centroidList, pixelObjectArray);
        return outputImage;
    
    }
    public static PixelObjects[] arrayToLab(PixelObjects[] pixelObjects)
    {
        CIELab myColorSpace = new CIELab();
        float[] convertColorSpace;
        int serialCount = 0;
        for (int i = 0; i < imageHeight; i++)
        {
            for (int j = 0; j < imageWidth; j++)
            {
                //POSSIBLY NEED TO NORMALIZE PRIOR TO SET X // Y // Z
                int r = (int)pixelObjects[serialCount].getX();
                int g = (int)pixelObjects[serialCount].getY();
                int b = (int)pixelObjects[serialCount].getZ();
                convertColorSpace = myColorSpace.fromRGB(r, g, b);
                pixelObjects[serialCount].setX(convertColorSpace[0]);
                pixelObjects[serialCount].setY(convertColorSpace[1]);
                pixelObjects[serialCount].setZ(convertColorSpace[2]);
                serialCount++;
            }
        }
        return pixelObjects;
    }
    public static PixelObjects[] arrayToHSB(PixelObjects[] pixelObjects)
    {
        CIELab myColorSpace = new CIELab();
        float[] convertColorSpace;
        int serialCount = 0;
        for (int i = 0; i < imageHeight; i++)
        {
            for (int j = 0; j < imageWidth; j++)
            {
                int r = (int)pixelObjects[serialCount].getX();
                int g = (int)pixelObjects[serialCount].getY();
                int b = (int)pixelObjects[serialCount].getZ();
                convertColorSpace = myColorSpace.toHSB(r, g, b);
                pixelObjects[serialCount].setX(convertColorSpace[0]);
                pixelObjects[serialCount].setY(convertColorSpace[1]);
                pixelObjects[serialCount].setZ(convertColorSpace[2]);
                serialCount++;
            }
        }
        return pixelObjects;
    }
    public static double[][][] initializeVarianceMatrices(int k)
    {
        double[][][] newMatrices = new double[k][3][3];
        int n = 3;
        for (int m = 0; m < k; m++)
            for(int i=0; i<n; i++)
                for(int j=0; j<n; j++)
                    newMatrices[m][i][j] = Math.random()/100;
        return newMatrices;
    }
    public static double[][][] findVarianceMatrices(RandomCentroid[] centroidList, PixelObjects[] pixelObjectArray)
    {
        //Variance Matrix unique for EACH set/centroid
        double[][][] varianceMatrix = new double[k][3][3];
        int serialCount = 0;
        int n = 0;
        DecimalFormat df2 = new DecimalFormat("###.###");
        for (int m = 0; m < k; m++)
        {
            double centroidX = centroidList[m].getX();
            double centroidY = centroidList[m].getY();
            double centroidZ = centroidList[m].getZ();
            double sumXX = 0;
            double sumXY = 0;
            double sumXZ = 0;
            double sumYZ = 0;
            double sumYY = 0;
            double sumZZ = 0;
            double varXX = 0, varXY = 0, varXZ = 0, varYZ = 0, varYY = 0, varZZ = 0;
            n = 0;
            serialCount = 0;
            for (int i = 0; i < (imageHeight*imageWidth); i++)
            {
                if (pixelObjectArray[serialCount].getAffiliatedCentroidIndex() == m)
                {
                    double pixelX = 0, pixelY = 0, pixelZ = 0;
                    pixelX = pixelObjectArray[serialCount].getX();
                    pixelY = pixelObjectArray[serialCount].getY();
                    pixelZ = pixelObjectArray[serialCount].getZ();
                    
                    sumXX += ((pixelX - centroidX) * (pixelX - centroidX));
                    sumXY += ((pixelX - centroidX) * (pixelY - centroidY));
                    sumXZ += ((pixelX - centroidX) * (pixelZ - centroidZ));
                    sumYZ += ((pixelY - centroidY) * (pixelZ - centroidZ));
                    sumYY += ((pixelY - centroidY) * (pixelY - centroidY));
                    sumZZ += ((pixelZ - centroidZ) * (pixelZ - centroidZ));
                    n++;
                }
                serialCount++;
            }
            if (n > 1)
            {
                varXX = (sumXX/(n-1));
                varXY = (sumXY/(n-1));
                varXZ = (sumXZ/(n-1));
                varYZ = (sumYZ/(n-1));
                varYY = (sumYY/(n-1));
                varZZ = (sumZZ/(n-1));
            }
            /*varianceMatrix[m][0][0] = Double.valueOf(df2.format(varXX));
            varianceMatrix[m][0][1] = Double.valueOf(df2.format(varXY));
            varianceMatrix[m][0][2] = Double.valueOf(df2.format(varXZ));
            varianceMatrix[m][1][0] = Double.valueOf(df2.format(varXY));
            varianceMatrix[m][1][1] = Double.valueOf(df2.format(varYY));
            varianceMatrix[m][1][2] = Double.valueOf(df2.format(varYZ));
            varianceMatrix[m][2][0] = Double.valueOf(df2.format(varXZ));
            varianceMatrix[m][2][1] = Double.valueOf(df2.format(varYZ));
            varianceMatrix[m][2][2] = Double.valueOf(df2.format(varZZ));*/
            varianceMatrix[m][0][0] = varXX;
            varianceMatrix[m][0][1] = varXY;
            varianceMatrix[m][0][2] = varXZ;
            varianceMatrix[m][1][0] = varXY;
            varianceMatrix[m][1][1] = varYY;
            varianceMatrix[m][1][2] = varYZ;
            varianceMatrix[m][2][0] = varXZ;
            varianceMatrix[m][2][1] = varYZ;
            varianceMatrix[m][2][2] = varZZ;
        }
        double invertMatrix[][][] = varianceMatrix;
        invertMatrix = invertMatrix(varianceMatrix, k);
        return invertMatrix;
    }
    public static PixelObjects[] normalizeArray(PixelObjects[] pixelObjectArray, int min, int max)
    {
        int arrayLength = pixelObjectArray.length;

        float minX = pixelObjectArray[0].getX();
        float minY = pixelObjectArray[0].getY(); 
        float minZ = pixelObjectArray[0].getZ();
        float maxX = pixelObjectArray[0].getX();
        float maxY = pixelObjectArray[0].getY(); 
        float maxZ = pixelObjectArray[0].getZ();
        
        for (int i = 1; i < arrayLength; i++)
        {
            if (pixelObjectArray[i].getX() < minX)
                minX = pixelObjectArray[i].getX();
            else if (pixelObjectArray[i].getX() > maxX)
                maxX = pixelObjectArray[i].getX();
            
            if (pixelObjectArray[i].getY() < minY)
                minY = pixelObjectArray[i].getY();
            else if (pixelObjectArray[i].getY() > maxY)
                maxY = pixelObjectArray[i].getY();
            
            if (pixelObjectArray[i].getZ() < minZ)
                minZ = pixelObjectArray[i].getZ();
            else if (pixelObjectArray[i].getZ() > maxZ)
                maxZ = pixelObjectArray[i].getZ();      
        }

        for (int i = 0; i < arrayLength; i++)
        {
            float newX = 0;
            float newY = 0;
            float newZ = 0;
            float oldX = pixelObjectArray[i].getX();
            float oldY = pixelObjectArray[i].getY();
            float oldZ = pixelObjectArray[i].getZ();

            newX = ((oldX - minX) / (maxX - minX))*max;
            newY = ((oldY - minY) / (maxY - minY))*max;
            newZ = ((oldZ - minZ) / (maxZ - minZ))*max;
            pixelObjectArray[i].setX(newX);
            pixelObjectArray[i].setY(newY);
            pixelObjectArray[i].setZ(newZ);
        }
        return pixelObjectArray;
        
    }
    public static RandomCentroid[] normalizeCentroidArray(RandomCentroid[] centroidList, int min, int max)
        {
        int arrayLength = centroidList.length;

        float minX = centroidList[0].getX();
        float minY = centroidList[0].getY(); 
        float minZ = centroidList[0].getZ();
        float maxX = centroidList[0].getX();
        float maxY = centroidList[0].getY(); 
        float maxZ = centroidList[0].getZ();

        for (int i = 1; i < arrayLength; i++)
        {
            if (centroidList[i].getX() < minX)
                minX = centroidList[i].getX();
            else if (centroidList[i].getX() > maxX)
                maxX = centroidList[i].getX();
            
            if (centroidList[i].getY() < minY)
                minY = centroidList[i].getY();
            else if (centroidList[i].getY() > maxY)
                maxY = centroidList[i].getY();
            
            if (centroidList[i].getZ() < minZ)
                minZ = centroidList[i].getZ();
            else if (centroidList[i].getZ() > maxZ)
                maxZ = centroidList[i].getZ();      
        }

        for (int i = 0; i < arrayLength; i++)
        {
            float newX = 0;
            float newY = 0;
            float newZ = 0;
            float oldX = centroidList[i].getX();
            float oldY = centroidList[i].getY();
            float oldZ = centroidList[i].getZ();

            newX = ((oldX - minX) / (maxX - minX))*max;
            newY = ((oldY - minY) / (maxY - minY))*max;
            newZ = ((oldZ - minZ) / (maxZ - minZ))*max;
            centroidList[i].setX(newX);
            centroidList[i].setY(newY);
            centroidList[i].setZ(newZ);
        }
        return centroidList; 
    }
    public static double[][][] invertMatrix(double[][][] inputMatrix, int k) 
    {
        int n = 3;
        double a[][]= new double[n][n];
        double outputMatrix[][][] = new double[k][n][n];
        for (int m = 0; m < k; m++)
        {
            for(int i=0; i<n; i++)
                for(int j=0; j<n; j++)
                    a[i][j] = inputMatrix[m][i][j];
            double d[][] = invert(a);
            for(int i=0; i<n; i++)
                for(int j=0; j<n; j++)
                    outputMatrix[m][i][j] = d[i][j];
        }
        return outputMatrix;
 
    } 
    public static double[][] invert(double a[][]) 
    {
        int n = a.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i=0; i<n; ++i) 
            b[i][i] = 1;
 
 // Transform the matrix into an upper triangle
        gaussian(a, index);
 
 // Update the matrix b[i][j] with the ratios stored
        for (int i=0; i<n-1; ++i)
            for (int j=i+1; j<n; ++j)
                for (int k=0; k<n; ++k)
                    b[index[j]][k]
                    	    -= a[index[j]][i]*b[index[i]][k];
 
 // Perform backward substitutions
        for (int i=0; i<n; ++i) 
        {
            x[n-1][i] = b[index[n-1]][i]/a[index[n-1]][n-1];
            for (int j=n-2; j>=0; --j) 
            {
                x[j][i] = b[index[j]][i];
                for (int k=j+1; k<n; ++k) 
                {
                    x[j][i] -= a[index[j]][k]*x[k][i];
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }
    public static void gaussian(double a[][], int index[]) 
    {
        int n = index.length;
        double c[] = new double[n];
 
 // Initialize the index
        for (int i=0; i<n; ++i) 
            index[i] = i;
 
 // Find the rescaling factors, one from each row
        for (int i=0; i<n; ++i) 
        {
            double c1 = 0;
            for (int j=0; j<n; ++j) 
            {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) c1 = c0;
            }
            c[i] = c1;
        }
 
 // Search the pivoting element from each column
        int k = 0;
        for (int j=0; j<n-1; ++j) 
        {
            double pi1 = 0;
            for (int i=j; i<n; ++i) 
            {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1) 
                {
                    pi1 = pi0;
                    k = i;
                }
            }
 
   // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i=j+1; i<n; ++i) 	
            {
                double pj = a[index[i]][j]/a[index[j]][j];
 
 // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;
 
 // Modify other elements accordingly
                for (int l=j+1; l<n; ++l)
                    a[index[i]][l] -= pj*a[index[j]][l];
            }
        }
    }
    public boolean checkForConvergence(float[][] meansList, RandomCentroid[] centroidList, int nLoops)
    {
        boolean valueChanged = false;
        float oldX, oldY, oldZ, newX, newY, newZ, diffX, diffY, diffZ;
        
        for (int m = 0; m < k; m++)
        {
            oldX = centroidList[m].getX(); oldY = centroidList[m].getY(); oldZ = centroidList[m].getZ();
            newX = meansList[m][0]; newY = meansList[m][1]; newZ = meansList[m][2];
            diffX = Math.abs(oldX-newX); diffY = Math.abs(oldY-newY); diffZ = Math.abs(oldZ-newZ);
            if (nLoops%10 == 0)
            	{
            		float tempX = 100 - (diffX/255);
            		float tempY = 100 - (diffY/255);
            		float tempZ = 100 - (diffZ/255);
            		float tempTotal = (tempX+tempY+tempZ)/3;
            		//gui.sendMessage("Percent Converged: " + tempTotal);
            	}
            if ((diffX > (specificity*255)) || (diffY > (specificity*255)) || (diffZ > (specificity*255)))
            {
                valueChanged = true;
            }
        }
        return valueChanged;
    }
    public static RandomCentroid[] updateValues(float[][] meansList, RandomCentroid[] centroidList)
    {
        for (int m = 0; m < k; m++)
        {
            centroidList[m].setX(meansList[m][0]);
            centroidList[m].setY(meansList[m][1]);
            centroidList[m].setZ(meansList[m][2]);
            centroidList[m].setNumOfPixels((int)(meansList[m][3]));
            centroidList[m].setIsActive((int)(meansList[m][4]));
        }
        return centroidList;
    }
    public static RandomCentroid[] initialCentroidsHavePixels(RandomCentroid[] centroidList, PixelObjects[] pixelObjectArray)
    {
        centroidList = setIsActive(centroidList, pixelObjectArray);
        boolean allCentroidsHavePixels = false;
        while (!allCentroidsHavePixels)
        {
            allCentroidsHavePixels = true;
            for (int m = 0; m < k; m ++)
            {
                if (centroidList[m].getIsActive() == 0)
                {
                    allCentroidsHavePixels = false;
                    centroidList[m].setX((float)(Math.random()*256));
                    centroidList[m].setY((float)(Math.random()*256));
                    centroidList[m].setZ((float)(Math.random()*256));
                }
            }
            pixelObjectArray = findDistances(centroidList, pixelObjectArray);
            centroidList = setIsActive(centroidList, pixelObjectArray);
        }
        return centroidList;
    }
    public static RandomCentroid reinitializeCentroids(RandomCentroid centroid)
    {
        for (int m = 0; m < k; m++)
        {
            if (centroid.getIsActive() == 0)
            {
                centroid.setX((float)(Math.random()*256));
	        centroid.setY((float)(Math.random()*256));
	        centroid.setZ((float)(Math.random()*256));
            }
        }
        return centroid;
    }
}
