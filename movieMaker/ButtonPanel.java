package movieMaker;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

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
  private JButton nextButton = new JButton(Messages.getString("ButtonPanel.0")); //$NON-NLS-1$
  private JButton playButton = new JButton(Messages.getString("ButtonPanel.1")); //$NON-NLS-1$
  private JButton prevButton = new JButton(Messages.getString("ButtonPanel.2")); //$NON-NLS-1$
  private JButton delBeforeButton = 
    new JButton(Messages.getString("ButtonPanel.3")); //$NON-NLS-1$
  private JButton delAfterButton = 
    new JButton(Messages.getString("ButtonPanel.4")); //$NON-NLS-1$
  private JButton writeQuicktimeButton = 
    new JButton(Messages.getString("ButtonPanel.5")); //$NON-NLS-1$
  private JButton writeAVIButton = new JButton(Messages.getString("ButtonPanel.6")); //$NON-NLS-1$
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
    frameRateLabel = new JLabel(Messages.getString("ButtonPanel.7")); //$NON-NLS-1$
    this.add(frameRateLabel);
    String[] rates = {"16","24","30"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    frameRateList = new JList(rates);
    JScrollPane scrollPane = new JScrollPane(frameRateList);
    frameRateList.setSelectedIndex(0);
    frameRateList.setVisibleRowCount(1);
    frameRateList.setToolTipText(Messages.getString("ButtonPanel.11")); //$NON-NLS-1$
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
    nextButton.setToolTipText(Messages.getString("ButtonPanel.12")); //$NON-NLS-1$
    nextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moviePlayer.showNext();
      }});
    prevButton.setToolTipText(Messages.getString("ButtonPanel.13")); //$NON-NLS-1$
    prevButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e)  {
        moviePlayer.showPrevious();
      }});
    playButton.setToolTipText(Messages.getString("ButtonPanel.14")); //$NON-NLS-1$
    playButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moviePlayer.playMovie();
      }});
    delBeforeButton.setToolTipText(Messages.getString("ButtonPanel.15")); //$NON-NLS-1$
    delBeforeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moviePlayer.delAllBefore();
      }});
    delAfterButton.setToolTipText(Messages.getString("ButtonPanel.16")); //$NON-NLS-1$
    delAfterButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moviePlayer.delAllAfter();
      }});
    writeQuicktimeButton.setToolTipText(Messages.getString("ButtonPanel.17")); //$NON-NLS-1$
    writeQuicktimeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moviePlayer.writeQuicktime();
      }});
    writeAVIButton.setToolTipText(Messages.getString("ButtonPanel.18")); //$NON-NLS-1$
    writeAVIButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moviePlayer.writeAVI();
      }});
  }
  
}