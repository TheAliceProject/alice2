package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Enumeration;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

class NewNoteDialog extends JWindow
{
  public static final int WIDTH = 300;
  public static final int HEIGHT = 300;
  private StencilManager stencilManager;
  private ButtonGroup noteTypesGroup;
  private static final int NOTE = 0;
  private static final int REFERENCE = 1;
  private static final int HOLE = 2;
  private JTextArea textArea;
  private JRadioButton[] noteTypeButtons;
  private JCheckBox hasNextCheckbox;
  private JCheckBox autoAdvanceCheckbox;
  private static Image topLeftArrow;

  public NewNoteDialog(StencilManager paramStencilManager, TutorialEditor paramTutorialEditor)
    throws HeadlessException
  {
    super(paramTutorialEditor);
    this.stencilManager = paramStencilManager;
    this.textArea = new JTextArea(10, 30);
    Container localContainer = getContentPane();
    JPanel localJPanel1 = new JPanel(new BorderLayout());
    JPanel localJPanel2 = new JPanel();
    localJPanel2.add(createTypePanel());
    localJPanel2.add(createModifierPanel());
    localJPanel1.add(localJPanel2, "South");
    JPanel localJPanel3 = new JPanel(new BorderLayout());
    localJPanel3.add(new JScrollPane(this.textArea), "Center");
    localContainer.add(createOkCancelPanel(), "North");
    localContainer.add(localJPanel1, "Center");
    localContainer.add(localJPanel3, "South");
    ((JPanel)getContentPane()).setBorder(BorderFactory.createEtchedBorder());
    addMouseMotionListener(new MouseAdapter()
    {
      public void mouseDragged(MouseEvent paramAnonymousMouseEvent)
      {
        NewNoteDialog.this.setLocation(paramAnonymousMouseEvent.getLocationOnScreen());
        NewNoteDialog.this.repaint();
      }
    });
    setLocation(300, 300);
    if (topLeftArrow == null)
      topLeftArrow = createImage("images/topLeftArrow.jpg");
  }

  private static JButton makeButton(String paramString, Action paramAction)
  {
    JButton localJButton = new JButton(paramString);
    localJButton.setAction(paramAction);
    localJButton.setText(paramString);
    return localJButton;
  }

  private JPanel createTypePanel()
  {
    JPanel localJPanel = new JPanel(new GridLayout(3, 1));
    this.noteTypesGroup = new ButtonGroup();
    this.noteTypeButtons = new JRadioButton[] { new JRadioButton("note"), new JRadioButton("reference"), new JRadioButton("hole") };
    this.noteTypeButtons[0].setSelected(true);
    for (JRadioButton localJRadioButton : this.noteTypeButtons)
    {
      this.noteTypesGroup.add(localJRadioButton);
      localJPanel.add(localJRadioButton);
    }
    this.noteTypeButtons[0].addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        NewNoteDialog.this.hasNextCheckbox.setEnabled(true);
        NewNoteDialog.this.autoAdvanceCheckbox.setEnabled(false);
      }
    });
    this.noteTypeButtons[1].addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        NewNoteDialog.this.hasNextCheckbox.setEnabled(true);
        NewNoteDialog.this.autoAdvanceCheckbox.setEnabled(false);
      }
    });
    this.noteTypeButtons[2].addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        NewNoteDialog.this.autoAdvanceCheckbox.setEnabled(true);
        NewNoteDialog.this.hasNextCheckbox.setEnabled(false);
      }
    });
    return localJPanel;
  }

  private JPanel createModifierPanel()
  {
    JPanel localJPanel = new JPanel(new GridLayout(3, 1));
    this.hasNextCheckbox = new JCheckBox("has next");
    this.autoAdvanceCheckbox = new JCheckBox("auto advance");
    this.hasNextCheckbox.setSelected(true);
    this.autoAdvanceCheckbox.setSelected(false);
    this.autoAdvanceCheckbox.setEnabled(false);
    localJPanel.add(this.hasNextCheckbox);
    localJPanel.add(new JLabel(" "));
    localJPanel.add(this.autoAdvanceCheckbox);
    return localJPanel;
  }

  private JPanel createOkCancelPanel()
  {
    JButton localJButton1 = makeButton("OK", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        Note localNote = null;
        if (NewNoteDialog.getSelection(NewNoteDialog.this.noteTypesGroup) == NewNoteDialog.this.noteTypeButtons[0])
          localNote = NewNoteDialog.this.stencilManager.createNewNote(NewNoteDialog.this.getPosition(), NewNoteDialog.this.hasNextCheckbox.isSelected());
        else if (NewNoteDialog.getSelection(NewNoteDialog.this.noteTypesGroup) == NewNoteDialog.this.noteTypeButtons[1])
          localNote = NewNoteDialog.this.stencilManager.createNewFrame(NewNoteDialog.this.getPosition(), NewNoteDialog.this.hasNextCheckbox.isSelected());
        else if (NewNoteDialog.getSelection(NewNoteDialog.this.noteTypesGroup) == NewNoteDialog.this.noteTypeButtons[2])
          localNote = NewNoteDialog.this.stencilManager.createNewHole(NewNoteDialog.this.getPosition(), NewNoteDialog.this.autoAdvanceCheckbox.isSelected());
        localNote.addText(NewNoteDialog.this.textArea.getText(), null);
        NewNoteDialog.this.dispose();
      }
    });
    JButton localJButton2 = makeButton("Cancel", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        NewNoteDialog.this.dispose();
      }
    });
    JPanel localJPanel1 = new JPanel(new BorderLayout());
    JPanel localJPanel2 = new JPanel(new BorderLayout());
    JPanel localJPanel3 = new JPanel();
    localJPanel3.add(new JLabel("      "));
    localJPanel3.add(localJButton1);   
    JPanel localJPanel4 = new JPanel();
    localJPanel4.add(localJButton2);
    localJPanel4.add(new JLabel("     "));   
    localJPanel2.add(localJPanel3, "West");
    localJPanel2.add(localJPanel4, "East");
    localJPanel1.add(localJPanel2, "Center");
    return localJPanel1;
  }

  protected ImageIcon createImageIcon(String paramString)
  {
    URL localURL = getClass().getResource("images/" + paramString);
    if (localURL != null)
      return new ImageIcon(localURL);
    return null;
  }

  protected Image createImage(String paramString)
  {
    return Toolkit.getDefaultToolkit().getImage(paramString);
  }

  public void paint(Graphics paramGraphics)
  {
    super.paint(paramGraphics);
    paramGraphics.drawImage(topLeftArrow, 0, 0, null);
  }

  private Point getPosition()
  {
    Point localPoint = getLocationOnScreen();
    SwingUtilities.convertPointFromScreen(localPoint, AuthoringTool.getHack().getJAliceFrame().getRootPane().getLayeredPane());
    return localPoint;
  }

  private static JRadioButton getSelection(ButtonGroup paramButtonGroup)
  {
    Enumeration localEnumeration = paramButtonGroup.getElements();
    while (localEnumeration.hasMoreElements())
    {
      JRadioButton localJRadioButton = (JRadioButton)localEnumeration.nextElement();
      if (localJRadioButton.getModel() == paramButtonGroup.getSelection())
        return localJRadioButton;
    }
    return null;
  }

  public void display(boolean paramBoolean)
  {
    setPreferredSize(new Dimension(300, 300));
    setMinimumSize(new Dimension(300, 300));
    validate();
    setVisible(paramBoolean);
    repaint();
  }
}

/* Location:           /Users/ace/Dropbox (Alice Project)/Temporary/Mama/Source/
 * Qualified Name:     edu.cmu.cs.stage3.caitlin.stencilhelp.client.NewNoteDialog
 * JD-Core Version:    0.6.2
 */