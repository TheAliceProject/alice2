package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Class to show a frame-based animation
 * Copyright Georgia Institute of Technology 2007
 * @author Barb Ericson ericson@cc.gatech.edu
 */
public class AnimationPanel extends JComponent
{
  /////////////// fields /////////////////////
  
  /** list of image objects */
  private List imageList = new ArrayList();
  
  /** List of the file names */
  private List nameList = new ArrayList();
  
  /** index of currently displayed image */
  private int currIndex = 0;
  
  /** number of frames per second */
  private int framesPerSec = 16;
  
  ////////////// constructors /////////////////
  
  /**
   * Constructor that takes no parameters
   */
  public AnimationPanel()
  {
    this.setSize(new Dimension(100,100));
  }
  
  /**
   * Constructor that takes a list of pictures 
   * @param pictList the list of pictures
   */
  public AnimationPanel(List pictList)
  {
    Image image = null;
    Picture picture = null;
    for (int i = 0; i < pictList.size(); i++)
    {
      picture = (Picture)pictList.get(i);
      nameList.add(picture.getFileName());
      image = picture.getImage();
      imageList.add(image);
    }
    
    BufferedImage bi = (BufferedImage) image;
    int width = bi.getWidth();
    int height = bi.getHeight();
    this.setSize(new Dimension(width,height));
    this.setMinimumSize(new Dimension(width,height));
    this.setPreferredSize(new Dimension(width,height));
  }
  
  /**
   * Constructor that takes the directory to read the frames from 
   * @param directory the directory to read from 
   */
  public AnimationPanel(String directory)
  {
    
    // get the list of files in the directory
    File dirObj = new File(directory);
    String[] fileArray = dirObj.list();
    ImageIcon imageIcon = null;
    Image image = null;
    
    // loop through the files
    for (int i = 0; i < fileArray.length; i++)
    {
      if (fileArray[i].indexOf(".jpg") >= 0) 
      {
        
        imageIcon = new ImageIcon(directory + fileArray[i]);
        nameList.add(directory + fileArray[i]);
        imageList.add(imageIcon.getImage());
      }
    }

    // set size of this panel
    if (imageIcon != null)
    {
      image = (Image) imageList.get(0);
      int width = image.getWidth(null);
      int height = image.getHeight(null);
      this.setSize(new Dimension(width, 
                               height));
      this.setMinimumSize(new Dimension(width,
                                      height));
      this.setPreferredSize(new Dimension(width,
                                          height));
    }
  }
  
  /**
   * Constructor that takes the directory to 
   * read from and the number of frames per
   * second
   * @param directory the frame direcotry
   * @param theFramesPerSec the number of frames
   * per second
   */
  public AnimationPanel(String directory, 
                        int theFramesPerSec)
  {
    this(directory);
    this.framesPerSec = theFramesPerSec;
  }
  
  ////////////// methods /////////////////////////
  
  /**
   * Method to get the current index 
   * @return the current index
   */
  public int getCurrIndex() { return currIndex;}
  
  /**
   * Method to set the frames per second to show the movie
   * @param numFramesPerSec the number of frames to show per second
   */
  public void setFramesPerSec(int numFramesPerSec)
  {
    this.framesPerSec = numFramesPerSec;
  }
  
  /**
   * Method to get the frames per second
   * @return the number of frames per second 
   */
  public int getFramesPerSec() 
  { return this.framesPerSec;}
  
  /**
   * Method to add a picture
   * @param picture the picture to add
   */
  public void add(Picture picture)
  {
    Image image = picture.getImage();
    imageList.add(image);
    nameList.add(picture.getFil