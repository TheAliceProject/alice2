package movieMaker;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class that holds the buttons for the movie player
 * @author Barb Ericson
 */
public class ButtonPanel extends JPanel
{
  //////////////// fields ////////////////////////
   /** list for the frame rate */
  private JList frameRateList = null;
  /** label for frame rate */
  private JLabel frameRateLabel = null;
  private JButton nextButton = new JButton(Messages.getString("Next")); 
  private JButton playButton = new JButton(Messages.getString("Play_Movie")); 
  private JButton prevButton = new JButton(Messages.getString("Prev")); 
  private JButton delBeforeButton = 
    new JButton(Messages.getString("Delete_All_Previous")); 
  private JButton delAfterButton = 
    new JButton(Messages.getString("Delete_All_After")); 
  private JButton writeQuicktimeButton = 
    new JButton(Messages.getString("Write_Quicktime")); 
  private JButton writeAVIButton = new JButton(Messages.getString("Write_AVI")); 
  private MoviePlayer moviePlayer = null;
  
  ///////////////// Constructors /////////////////
  
  /**
   * Constructor that doesn't take any parameters
   */
  public ButtonPanel(MoviePlayer player) 
  {
    this.moviePlayer = player;
    
    // add the previous and next buttons to this panel
    this.add(prevButton);
    this.add(nextButton);
    
    // set up the frame rate list
    frameRateLabel = new JLabel(Messages.getString("Frames_per_Second__") + " "); 
    this.add(frameRateLabel);
    String[] rates = {"16","24","30"};   
    frameRateList = new JList(rates);
    JScrollPane scrollPane = new JScrollPane(frameRateList);
    frameRateList.setSelectedIndex(0);
    frameRateList.setVisibleRowCount(1);
    frameRateList.setToolTipText(Messages.getString("The_number_of_frames_per_second_in_the_movie")); 
    frameRateList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        String rateS = (String) frameRateList.getSelectedValue();
        int rate = Integer.parseInt(rateS);
        moviePlayer.setFrameRate(rate);
      }
    });
    this.add(scrollPane);
    
    this.add(playButton);
    this.add(delBeforeButton);
    this.add(delAfterButton);
    this.add(writeQuicktimeButton);
    this.add(writeAVIButton);
    
    // add the action listeners to the buttons
    nextButton.setToolTipText(Messages.getString("Click_to_see_the_next_frame")); 
    nextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moviePlayer.showNext();
      }});
    prevButton.setToolTipText(Messages.getString("Click_to_see_the_previous_frame")); 
    prevButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e)  {
        moviePlayer.showPrevious();
      }});
    playButton.setToolTipText(Messages.getString("Click_to_play_the_movie")); 
    playButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moviePlayer.playMovie();
      }});
    delBeforeButton.setToolTipText(Messages.getString("Click_to_delete_all_frames_before_the_current_one")); 
    delBeforeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moviePlayer.delAllBefore();
      }});
    delAfterButton.setToolTipText(Messages.getString("Click_to_delete_all_frames_after_the_current_one")); 
    delAfterButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moviePlayer.delAllAfter();
      }});
    writeQuicktimeButton.setToolTipText(Messages.getString("Click_to_write_out_a_Quicktime_movie_from_the_frames")); 
    writeQuicktimeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moviePlayer.writeQuicktime();
      }});
    writeAVIButton.setToolTipText(Messages.getString("Click_to_write_out_an_AVI_movie_from_the_frames")); 
    writeAVIButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moviePlayer.writeAVI();
      }});
  }
  
}
