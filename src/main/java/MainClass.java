package main;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
//import GUI.KMeansGUI;
@SuppressWarnings("unused")
/**
 *
 * @author Carlos Bradley Priddy II
 */
public class MainClass extends Operations implements Runnable
{

		//macros
		int 	RGB = 1, // STANDARD RGB COLORSPACE
				LAB = 2, // L*a*b* Colorspace
	            HSB = 3, // HSB Colorspace
				EUC = 1, // Euclidean Distance Calculation
				NEI = 2, // Euclidean Distance with Neighbourhood Weights
				MAH = 3, // Mahalanobis Distance Calculation
				MAHNEI = 4, // Mahalanobis Distance with Mahalanobis Neighbourhood Weights
				MAHEUCNEI = 5; // Mahalanobis Distance with Euclidean Neighbourhood Weights
				
		
		//variables
	    private int k;
	    private String fileName;
	    private double specificity;
	    private BufferedImage inputImage;
	    private BufferedImage myOutputImage;
	    private RandomCentroid[] centroidList;
	    private PixelObjects[] pixelObjectArray;
	    private float[][] meansList;
	    private int imageWidth;
	    private int imageHeight;
	    private int nLoops;
	    private double[][][] varianceCovarianceMatrix;
	    //private KMeansGUI gui;
	    private int colorSpaceSelection;
	    private int distanceSelection;
	    private boolean valueChanged;
	    private boolean converged = false;
	    public boolean stop=false;
	    public boolean unusedCentroids;
	    Thread myThread;
	    public MainClass(){}
	    //public MainClass(BufferedImage inputImage, int k, double specificity, int colorSpaceSelection, int distanceSelection, KMeansGUI gui)
	    public MainClass(BufferedImage inputImage, int k, double specificity, int colorSpaceSelection, int distanceSelection, int[] boundingBox)
	    {
	        //Distances!! Vector space.
	        //Mahalanobis Distance
	        //super(k, specificity, colorSpaceSelection, distanceSelection, gui, inputImage);
	    	super(k, specificity, colorSpaceSelection, distanceSelection, inputImage, boundingBox);
	        this.k = k;
	        this.inputImage = inputImage;
	        this.specificity = specificity;
	        //this.gui = gui;
	        this.colorSpaceSelection = colorSpaceSelection;
	        this.distanceSelection = distanceSelection;
	        myThread = new Thread(this);
	        myThread.start();
	        
	    }
	    public void run()
	    {
	        init();
	        process();
	        finalize();
	    }
	    
	    public void init()
	    {
	        //gui.sendMessage("Color Segmentation Starting!");
	        //inputImage = Operations.importImage(fileName);
	        imageHeight = inputImage.getHeight();
	        imageWidth = inputImage.getWidth();

	        nLoops = 0;
	        stop = false;
	        unusedCentroids = true;
	        valueChanged = true;
	        converged = false;
	        
	        pixelObjectArray = assignPixels(inputImage); 
	        //gui.sendImage(inputImage);
	        //gui.sendImage(inputImage);
	        
	        centroidList = RandomCentroid.createCentroidList(k, RGB, specificity);
	        pixelObjectArray = findDistances(centroidList, pixelObjectArray);
	        //POSSIBLY UNNECESSARY CODE - ALL CENTROIDS START WITH SOME NUMBER OF PIXELS
	        centroidList = initialCentroidsHavePixels(centroidList, pixelObjectArray);
	        pixelObjectArray = findDistances(centroidList, pixelObjectArray);

	    	if(colorSpaceSelection==LAB)
	    	{
	            pixelObjectArray = arrayToLab(pixelObjectArray);
	            pixelObjectArray = normalizeArray(pixelObjectArray, 0, 255);
	        }
	        else if(colorSpaceSelection == HSB)
	        {
	            pixelObjectArray = arrayToHSB(pixelObjectArray);
	            pixelObjectArray = normalizeArray(pixelObjectArray, 0, 255);
	        }
	    	if (distanceSelection == MAH || distanceSelection == MAHNEI || distanceSelection == MAHEUCNEI)
	    	{
	    		varianceCovarianceMatrix = findVarianceMatrices(centroidList, pixelObjectArray);
	    	}
	    }
	    public void process()
	    {
	    	converged=false;
	        while (!converged)
	        {   
	            loop();
	            if (stop)
	            {
	                //if(gui.debug)
	                //{
	                //gui.sendMessage("BREAKING LOOP");
	                //}
	                break;
	            }          
	        }
	    }
		public void finalize()
	    {
			setOutputImage(outputImage(centroidList, pixelObjectArray));
	    	//gui.updateImage(myOutputImage, ""+nLoops);
	        //gui.sendStop();
	        saveImage("kMeansApplication.jpg");
	        for (int h = 0; h < myOutputImage.getHeight(); h++)
				for (int w = 0; w < myOutputImage.getWidth(); w++)
				{
					int red, green, blue, rgb;
					rgb = myOutputImage.getRGB(w, h);
					red = (rgb >> 16) & 0xFF;
					green = (rgb >> 8) & 0xFF;
					blue = (rgb & 0xFF);
				}

	    }
	    public void loop()
	    {
	        //BASED ON INITIAL EUCLIDEAN DISTANCE SET ACTIVE CENTROIDS
	        unusedCentroids = false;
	        valueChanged = false;
	        centroidList = setIsActive(centroidList, pixelObjectArray);
	        
	        if (distanceSelection == NEI)
	        {
	        	pixelObjectArray = findDistances(centroidList, pixelObjectArray);
	            pixelObjectArray = findDistancesNeighbourhood(centroidList, pixelObjectArray);
	        }
	        else if (distanceSelection == MAH)
	        {
	        	//gui.sendMessage("Inverted Variance Matrix");
	        	
	            varianceCovarianceMatrix = findZeroMatrices(pixelObjectArray, centroidList, varianceCovarianceMatrix);
	            pixelObjectArray = findDistancesMahalanobis(centroidList, pixelObjectArray, varianceCovarianceMatrix);
	            varianceCovarianceMatrix = findVarianceMatrices(centroidList, pixelObjectArray);
	            
	        }
	        else if (distanceSelection == MAHNEI)
	        {
	        	varianceCovarianceMatrix = findZeroMatrices(pixelObjectArray, centroidList, varianceCovarianceMatrix);
	            pixelObjectArray = findDistancesMahalanobis(centroidList, pixelObjectArray, varianceCovarianceMatrix);
	            pixelObjectArray = findDistancesNeighbourhood(centroidList, pixelObjectArray);
	            varianceCovarianceMatrix = findVarianceMatrices(centroidList, pixelObjectArray);
	        }
	        else if(distanceSelection == MAHEUCNEI)
	        {
	        	varianceCovarianceMatrix = findZeroMatrices(pixelObjectArray, centroidList, varianceCovarianceMatrix);
	            pixelObjectArray = findDistancesMahalanobis(centroidList, pixelObjectArray, varianceCovarianceMatrix);
	            pixelObjectArray = findDistancesNeighbourhood(centroidList, pixelObjectArray);
	            varianceCovarianceMatrix = findVarianceMatrices(centroidList, pixelObjectArray);
	        }
	        else
	        {
	            pixelObjectArray = findDistances(centroidList, pixelObjectArray);
	        }
	        meansList = returnMeans(pixelObjectArray);
	        valueChanged = checkForConvergence(meansList, centroidList, nLoops);
	        if (valueChanged)
	        {
	            centroidList = updateValues(meansList, centroidList);
	        }
	    	setOutputImage(outputImage(centroidList, pixelObjectArray));
	    	//gui.updateImage(myOutputImage, ""+nLoops);
	    	nLoops++;
	    	if (nLoops%10 == 0)
	    	{
	    		//gui.sendMessage("Loops: " + nLoops);
	    	}
		for (int m = 0; m < k; m++)
	        {
	            if (centroidList[m].getIsActive() == 0)
	            {
	            	unusedCentroids = true;
	            	centroidList[m] = reinitializeCentroids(centroidList[m]);
	            }        
	        }
		//centroidList = initialCentroidsHavePixels(centroidList, pixelObjectArray);
        //pixelObjectArray = findDistances(centroidList, pixelObjectArray);
	    	if(!valueChanged && !unusedCentroids || stop)converged=true;
	    }
	    
	    public int getK()
	    {
	        return k;
	    }
	    public String getFileName()
	    {
	        return fileName;
	    }
	    public double getSpecificity()
	    {
	        return specificity;
	    }
	    public BufferedImage getInputImage()
	    {
	        return inputImage;
	    }
	    public BufferedImage getOutputImage()
	    {
	        return myOutputImage;
	    }
	    public RandomCentroid[] getCentroidList()
	    {
	        return centroidList;
	    }
	    public PixelObjects[] getPixelObjectArray()
	    {
	        return pixelObjectArray;
	    }
	    public float[][] getMeansList()
	    {
	        return meansList;
	    }
	    public int getImageWidth()
	    {
	        return imageWidth;
	    }
	    public int getImageHeight()
	    {
	        return imageHeight;
	    }
	    public int getNumberOfLoops()
	    {
	        return nLoops;
	    }
	    public void setK(int k)
	    {
	        this.k = k;
	    }
	    public void setFileName(String fileName)
	    {
	        this.fileName = fileName;
	    }
	    public void setSpecificity(double specificity)
	    {
	        this.specificity = specificity;
	    }
	    public void setInputImage(BufferedImage inputImage)
	    {
	        this.inputImage = inputImage;
	    }
	    public void setOutputImage(BufferedImage tempOutputImage)
	    {
	        myOutputImage = tempOutputImage;
	    }
	    public void setCentroidList(RandomCentroid[] centroidList)
	    {
	        this.centroidList = centroidList;
	    }
	    public void setPixelObjectArray(PixelObjects[] pixelObjectArray)
	    {
	        this.pixelObjectArray = pixelObjectArray;
	    }
	    public void setMeansList(float[][] meansList)
	    {
	        this.meansList = meansList;
	    }
	    public void setImageWidth(int imageWidth)
	    {
	        this.imageWidth = imageWidth;
	    }
	    public void setImageHeight(int imageHeight)
	    {
	        this.imageHeight = imageHeight;
	    }
	    public void saveImage(String fileName)
	    {
	        try
	        {   
	        File outputFile = new File(fileName);
	        ImageIO.write(myOutputImage, "png", outputFile);
	        }       
	        catch (IOException e) 
	        {
	             //gui.sendMessage("An Error Occured saving your image");
	            System.exit(1);
	        }
	        
	        if (stop)
	        {
	            //gui.sendMessage("COLOR SEGMENTATION ABORTED, DISPLAYING CURRENT IMAGE!");
	        }
	        //gui.sendMessage("TOTAL NUMBER OF LOOPS: " + (nLoops-1));
	    }
}
