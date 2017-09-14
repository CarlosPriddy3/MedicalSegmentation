package main;

public class PixelObjects 
{
	float x, y, z;
    float[] distancesAllCentroids;
    int w, h;
    float distance;
    int affiliatedCentroidIndex;
    
    PixelObjects(){}
    PixelObjects(int i, int j, int rGB)
    {
        //X and Y locations on a 2d grid of the picture
        w = j;
        h = i;
        x = (rGB >> 16) & 0xFF;
        y = (rGB >> 8) & 0xFF;
        z = rGB & 0xFF;
    }
    public int getW()
    {
        return w;
    }
    public int getH()
    {
        return h;
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
    
    public float getDistance()
    {
        return distance;
    }
    public void setAffiliatedCentroidIndex(int inputIndex)
    {
    	this.affiliatedCentroidIndex = inputIndex;
    }
    public int getAffiliatedCentroidIndex()
    {
        return affiliatedCentroidIndex;
    }
    public void setW(int w)
    {
        this.w = w;
    }
    public void setH(int h)
    {
        this.w = h;
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
    
    public void setDistance(float inputDistance, int centroidIndex)
    {
        distance = inputDistance;
        affiliatedCentroidIndex = centroidIndex;
    }
    public void setDistanceAllCentroids(float[] distancesAllCentroidsList)
    {
        this.distancesAllCentroids = distancesAllCentroidsList;
    }
    public float[] getDistanceAllCentroids()
    {
        return distancesAllCentroids;
    }
    
    public float getIndividualDistance(int index)
    {
        float distance = distancesAllCentroids[index];
        return distance;
    }
}
