package main;
import java.awt.Color;
import java.awt.color.ColorSpace;
public class CIELab extends ColorSpace
{
	//FIND CORRECT MATRICES FOR CONVERSION
    final float eps = 216.f/24389.f;
    final float k = 24389.f/27.f;
    final float Xr = 0.95682f;  // reference white D55
    final float Yr = 1.0f;
    final float Zr = 0.92149f;
    CIELab()
    {
        super(ColorSpace.TYPE_Lab, 3);
    }
    public float[] fromCIEXYZ(float[] colorValue)
    {
        float X = colorValue[0];
        float Y = colorValue[1];
        float Z = colorValue[2];
        
        float xr = X/Xr;
        float yr = Y/Yr;
        float zr = Z/Zr;
        
        float fx, fy, fz;
        float Ls, as, bs;

        if ( xr > eps )
            fx =  (float) Math.pow(xr, 1/3.);
        else
            fx = (float) ((k * xr + 16.) / 116.);

        if ( yr > eps )
            fy =  (float) Math.pow(yr, 1/3.);
        else
            fy = (float) ((k * yr + 16.) / 116.);

        if ( zr > eps )
            fz =  (float) Math.pow(zr, 1/3.);
        else
            fz = (float) ((k * zr + 16.) / 116);
        
        Ls = ( 116 * fy ) - 16;
        as = 500*(fx-fy);
        bs = 200*(fy-fz);
        
        return new float[] {Ls, as, bs};
    }
    public float[] toCIEXYZ(float[] colorValue)
    {
        float fx, fy, fz, Ls, as, bs, xr, yr, zr, X, Y, Z;
        Ls = colorValue[0];
        as = colorValue[1];
        bs = colorValue[2];
        
        fy = (Ls+16) / 116;
        fz = fy - (bs/200);
        fx = (as/500)+fy;
        
        if (Math.pow(fx, 3) > eps)
            xr = (float)Math.pow(fx, 3);
        else
            xr = (116*fx-16)/k;
        if (Ls > (eps * k))
            yr = (float)Math.pow(((Ls+16)/116), 3);
        else
            yr = Ls/k;
        if (Math.pow(fz, 3) > eps)
            zr = (float) Math.pow(fz, 3);
        else
            zr = ((116*fz)-16)/k;
        
        X = xr * Xr;
        Y = yr * Yr;
        Z = zr * Zr;
        
        return new float[] {X, Y, Z};
    }
    public float[] fromRGB(float[] colorValue)
    {
        float r, g, b, R, G, B, X, Y, Z;
        R = colorValue[0];
        G = colorValue[1];
        B = colorValue[2];
        
        r = R/255.f; //R 0..1
        g = G/255.f; //G 0..1
        b = B/255.f; //B 0..1

        // assuming sRGB (D65)
        if (r <= 0.04045)
            r = r/(1292/100);
        else
            r = (float) Math.pow((r+0.055)/1.055,2.4);

        if (g <= 0.04045)
            g = g/(1292/100);
        else
            g = (float) Math.pow((g+0.055)/1.055,2.4);

        if (b <= 0.04045)
            b = b/(1292/100);
        else
            b = (float) Math.pow((b+0.055)/1.055,2.4);


        X =  0.436052025f*r     + 0.385081593f*g + 0.143087414f *b;
        Y =  0.222491598f*r     + 0.71688606f *g + 0.060621486f *b;
        Z =  0.013929122f*r     + 0.097097002f*g + 0.71418547f  *b;
        
        float[] intermediateArray = new float[3];
        intermediateArray[0] = X;
        intermediateArray[1] = Y;
        intermediateArray[2] = Z;
        return fromCIEXYZ(intermediateArray);
    }
    public float[] toRGB(float[] colorValue)
    {
        float[] intermediateArray = new float[3];
        float X, Y, Z, r, g, b; 
        int R, G, B;
        intermediateArray = toCIEXYZ(colorValue);
        X = intermediateArray[0];
        Y = intermediateArray[1];
        Z = intermediateArray[2];
        
        r =  (3.1338561f*X)     + (-1.6168667f*Y)      + (-0.4906146f*Z);
        g =  (-0.9787684f*X)    + (1.9161415f*Y)       + (0.0334540f*Z);
        b =  (0.0719453f*X)     + (-0.2289914f*Y)      + (1.4052427f*Z);
        
        if (r <= 0.0031308)
            r = r * 12.92f;
        else
        {
            float imp = 1/2.4f;
            r = (float) (1.055f * (Math.pow(r, imp))-0.055);
        }
        
        if (g <= 0.0031308)
            g = g * 12.92f;
        else
            g =(float) (1.055 * (Math.pow(g, (1/2.4))) - 0.055);
        
        if (b <= 0.0031308)
            b = b * 12.92f;
        else
            b = (float) (1.055 * (Math.pow(b, (1/2.4f))) - 0.055);
        
        R = (int)r*255;
        G = (int)g*255;
        B = (int)b*255;
        return new float[] {R, G, B};
    }
    public float[] fromRGB(int r, int g, int b)
    {
       float R, G, B, X, Y, Z;
       R = r/255.f; //R 0..1
       G = g/255.f; //G 0..1
       B = b/255.f; //B 0..1
        // assuming sRGB (D65)
        if (R <= 0.04045)
            R = R/(1292/100);
        else
            R = (float) Math.pow((R+0.055)/1.055,2.4);

        if (G <= 0.04045)
            G = G/(1292/100);
        else
            G = (float) Math.pow((G+0.055)/1.055,2.4);

        if (B <= 0.04045)
            B = B/(1292/100);
        else
            B = (float) Math.pow((B+0.055)/1.055,2.4);


        X =  0.436052025f*R     + 0.385081593f*G + 0.143087414f *B;
        Y =  0.222491598f*R     + 0.71688606f *G + 0.060621486f *B;
        Z =  0.013929122f*R     + 0.097097002f*G + 0.71418547f  *B;
        
        float[] intermediateArray = new float[3];
        intermediateArray[0] = X;
        intermediateArray[1] = Y;
        intermediateArray[2] = Z;
        return fromCIEXYZ(intermediateArray);
    }
    public float[] toHSB(int r, int g, int b)
    {
        float[] tempArray = new float[3];
        Color.RGBtoHSB(r, g, b, tempArray);
        return tempArray;
    }
}
