package com.moneydance.modules.features.mdcsvimporter;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author stan
 */


public class WinProps {
    
    int width = 800;
    int height = 500;
    int atX = 0;
    int atY = 0;

    public WinProps()
    {
        //GraphicsConfiguration gc = getGraphicsConfiguration_NoClientCode();
        //Rectangle gcBounds = gc.getBounds();
        //Dimension windowSize = getSize();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        Rectangle gcBounds = gc.getBounds();
        Point centerPoint = ge.getCenterPoint();
        atX = centerPoint.x - width / 2;
        atY = centerPoint.y - height / 2;    
    }
    
    public WinProps( int width, int height, int atX, int atY )
    {
        this.width = width;
        this.height = height;
        this.atX = atX;
        this.atY = atY;
    }
    
    public WinProps( ArrayList<Integer> winprops )
    {
        width = winprops.get(0);
        height = winprops.get(1);
        atX = winprops.get(2);
        atY = winprops.get(3);
    }
    
    public ArrayList<Integer> getWinPropsAsList( )
    {
        ArrayList<Integer> winprops = new ArrayList<Integer>();
        winprops.add( width );
        winprops.add( height );
        winprops.add( atX );
        winprops.add( atY );
        
        return winprops;
    }
    
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAtX() {
        return atX;
    }

    public void setAtX(int atX) {
        this.atX = atX;
    }

    public int getAtY() {
        return atY;
    }

    public void setAtY(int atY) {
        this.atY = atY;
    }
        
}
