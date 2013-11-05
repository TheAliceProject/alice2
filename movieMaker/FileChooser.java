package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
  
/**
 * A class to make working with a file chooser easier
 * for students.  It uses a JFileChooser to let the user
 * pick a file and returns the choosen file name.
 * 
 * Copyright Georgia Institute of Technology 2004
 * @author Barb Ericson ericson@cc.gatech.edu
 */
public class FileChooser 
{

  ///////////////////////////// class fields ///////////////////
   /**
   * Properities to use during execution
   */
  private static Properties appProperties = null;
  
  /**
   * Property key for the media directory
   */
  private static final String MEDIA_DIRECTORY = "mediaDirectory"; 
  
  /**
   * Name for property file
   */
  private static final String PROPERTY_FILE_NAME = 
    "SimplePictureProperties.txt"; 
  
  /////////////////////// methods /////////////////////////////
  
  /**
   * Method to pick an item using the file chooser
   * @param fileChooser the file Chooser to use
   * @return the path name
   */
  public static String pickPath(JFileChooser fileChooser)
  {
    String path = null;
    
    /* create a JFrame to be the parent of the file 
     * chooser open dialog if you don't do this then 
     * you may not see the dialog.
     */
    JFrame frame = new JFrame();
   // frame.setAlwaysOnTop(true);
    
    // get the return value from choosing a file
    int returnVal = fileChooser.showOpenDialog(frame);
    
    // if the return value says the user picked a file 
    if (returnVal == JFileChooser.APPROVE_OPTION)
      path = fileChooser.getSelectedFile().getPath();
    return path;
  }
 
  /**
   * Method to let the user pick a file and return
   * the full file name as a string.  If the user didn't 
   * pick a file then the file name will be null.
   * @return the full file name of the picked file or null
   */
  public static String pickAFile()
  {
    JFileChooser fileChooser = null;
    
    // start off the file name as null
    String fileName = null;
    
    // get the current media directory
    String mediaDir = getMediaDirectory();
    
    /* create a file for this and check that the directory exists
     * and if it does set the file chooser to use it
     */
    try {
      File file = new File(mediaDir);
      if (file.exists())
        fileChooser = new JFileChooser(file);
    } catch (Exception ex) {
    }
    
    // if no file chooser yet create one
    if (fileChooser == null)
      fileChooser = new JFileChooser();
    
    // pick the file
    fileName = pickPath(fileChooser);
    
    return fileName;
  }
  
  /**
   * Method to let the user pick a directory and return
   * the full path name as a string. 
   * @return the full directory path
   */
  public static String pickADirectory()
  {
    JFileChooser fileChooser = null;
    String dirName = null;
    
    // get the current media directory
    String mediaDir = getMediaDirectory();
    
    // if no file chooser yet create one
    if (mediaDir != null)
      fileChooser = new JFileChooser(mediaDir);
    else
      fileChooser = new JFileChooser();
    
    // allow only directories to be picked
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
    // pick the directory
    dirName = pickPath(fileChooser);
    
    return dirName + "/"; 
  }
  
   /**
  * Method to get the full path for the passed file name
  * @param fileName the name of a file
  * @return the full path for the file
  */
 public static String getMediaPath(String fileName) 
 {
   String path = null;
   String directory = getMediaDirectory();
   
   // if the directory is null ask the user for it
   if (directory == null)
   {
     SimpleOutput.showError(Messages.getString("The_media_path__directory_") + 
       Messages.getString("_has_not_been_set_yet__") + 
       Messages.getString("Please_pick_the_directory_") + 
       Messages.getString("that_contains_your_media_") + 