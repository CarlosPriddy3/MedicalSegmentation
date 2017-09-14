package main;

public class RandomCentroid 
{
	private float x, y, z;
    CIELab cSpace;
    double specificity;
    private int isActive;
    private int numOfPixels;
    
    public RandomCentroid(int chooseType, double specificity)
    {
        this.specificity = specificity;
        cSpace = new CIELab();
        x = (int)(Math.random()*256);
        y = (int)(Math.random()*256);
        z = (int)(Math.random()*256);
        isActive = 0;
        numOfPixels = 0;
    }
    public float getX()
    {
        return x;
    }
    public float getY()
    {
        return y;
    }
    public float getZ()
    {
        return z;
    }
    public void setX(float x)
    {
        this.x = x;
    }
    public void setY(float y)
    {
            this.y = y;
    }
    public void setZ(float z)
    {
            this.z = z;
    }
    public void setXYZ(float x, float y, float z, int numOfPixels, float isActive)
    {
        setIsActive((int)isActive);
        setX(x); setY(y); setZ(z);
        setNumOfPixels(numOfPixels);
    }
    public void setIsActive(int isActive)
    {
       this.isActive = isActive;
    }
    public int getIsActive()
    {
        return isActive;
    }
    public void setNumOfPixels(int numOfPixels)
    {
        this.numOfPixels = numOfPixels;
    }
    public int getNumOfPixels()
    {
    	return numOfPixels;
    }
    public static RandomCentroid[] createCentroidList(int k, int type, double specificity)
    {
        RandomCentroid[] centroidList = new RandomCentroid[k];
        for (int i = 0; i < k; i++)
        {
            centroidList[i] = new RandomCentroid(type, specificity);
        }
        return centroidList;
    }
    @Override
    public String toString()
    {
        String returnToString = "Red: " + x + " Green: " + y + " Blue: " + z;
        return returnToString;
    }
}
