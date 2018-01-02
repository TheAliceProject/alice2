package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class TutorialEditor extends JDialog {
  private StencilManager stencilManager;
  private JButton showHideTutorialButton;
  private boolean toggleShowTutorial = true;
  private JPanel pagesPanel;
  int pageCount;
  int pageIndex;
  private JScrollPane pagesScrollPane;
  private static String TITLE = "Tutorial Builder";

  public TutorialEditor(StencilManager paramStencilManager)
    throws HeadlessException
  {
    super(paramStencilManager != null ? AuthoringTool.getHack().getJAliceFrame() : (JFrame)null, TITLE, false);
    this.stencilManager = paramStencilManager;
    Container localContainer = getContentPane();
    JPanel localJPanel = new JPanel(new BorderLayout());
    localJPanel.add(createPagePanel(), "North");
    localContainer.add(localJPanel, "North");
    localContainer.add(createMenuPanel(), "Center");
    localContainer.add(createTutorialMenuPanel(), "South");
    setDefaultCloseOperation(0);
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        TutorialEditor.this.stencilManager.setInstructorMode(false);
      }

      public void windowStateChanged(WindowEvent paramAnonymousWindowEvent)
      {
        super.windowStateChanged(paramAnonymousWindowEvent);
        TutorialEditor.this.pagesPanel.setSize(400, 50);
      }
    });
    setMinimumSize(new Dimension(450, 350));
    this.pagesPanel.setSize(400, 50);
    setLocation(400, 400);
    setResizable(false);
  }

  private Component createPagePanel()
  {
    this.pagesPanel = new JPanel();
    TitledBorder localTitledBorder = BorderFactory.createTitledBorder("Pages");
    this.pagesPanel.setBorder(localTitledBorder);
    resetPagesPanel();
    this.pagesPanel.setSize(new Dimension(440, 50));
    this.pagesPanel.setMaximumSize(new Dimension(440, 100));
    this.pagesScrollPane = new JScrollPane(this.pagesPanel, 21, 32);
    return this.pagesScrollPane;
  }

  private void resetPagesPanel()
  {
    this.pageIndex = this.stencilManager.getStencilNumber();
    this.pageCount = this.stencilManager.getNumberOfStencils();
    this.pagesPanel.removeAll();
    for (int i = 0; i < this.pageCount; i++)
    {
      final int j = i;
      JButton localJButton = makeButton("" + (i + 1), new AbstractAction()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          TutorialEditor.this.onPageClicked(j);
        }
      });
      this.pagesPanel.add(localJButton);
    }
  }

  private void updatePagePanel(int paramInt)
  {
    this.pageIndex = this.stencilManager.getStencilNumber();
    this.pageCount = this.stencilManager.getNumberOfStencils();
    if (paramInt > 0)
    {
      final int i = this.pageCount - 1;
      JButton localJButton = makeButton("" + this.pageCount, new AbstractAction()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          TutorialEditor.this.onPageClicked(i);
        }
      });
      this.pagesPanel.add(localJButton);
    }
    else if ((paramInt < 0) && (this.pageCount > 1))
    {
      this.pagesPanel.remove(this.pageCount);
      invalidate();
    }
    else
    {
      resetPagesPanel();
    }
    this.pagesPanel.validate();
    pagesPanel.repaint();
    this.pagesScrollPane.validate();
  }

  private void onPageClicked(int paramInt)
  {
    this.stencilManager.gotoStencil(paramInt);
  }

  private JPanel createMenuPanel()
  {
    JPanel localJPanel = new JPanel(new GridLayout(2, 1));
    localJPanel.add(createNoteMenuPanel());
    localJPanel.add(createPageMenuPanel());
    return localJPanel;
  }

  private static JButton makeButton(String paramString, Action paramAction)
  {
    JButton localJButton = new JButton(paramString);
    localJButton.setAction(paramAction);
    localJButton.setText(paramString);
    return localJButton;
  }

  private JPanel createTutorialMenuPanel()
  {
    JButton localJButton1 = makeButton("New tutorial", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        TutorialEditor.this.stencilManager.newTutorial();
        TutorialEditor.this.stencilManager.setWriteEnabled(true);
      }
    });
    JButton localJButton2 = makeButton("Open tutorial...", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        TutorialEditor.this.stencilManager.loadStencilsFile();
        TutorialEditor.this.stencilManager.setWriteEnabled(true);
      }
    });
    JButton localJButton3 = makeButton("Save tutorial", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        TutorialEditor.this.stencilManager.saveStencilsFile();
      }
    });
    this.showHideTutorialButton = makeButton(getToggleButtonText(), new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        TutorialEditor.this.toggleShowTutorial = (!TutorialEditor.this.toggleShowTutorial);
        TutorialEditor.this.stencilManager.showStencilPanel(TutorialEditor.this.toggleShowTutorial);
      }
    });
    JPanel localJPanel1 = new JPanel(new GridLayout(3, 1));
    TitledBorder localTitledBorder = BorderFactory.createTitledBorder("Tutorial menu");
    localJPanel1.setBorder(localTitledBorder);
    JPanel localJPanel2 = new JPanel();
    localJPanel2.add(localJButton1);
    localJPanel2.add(localJButton2);
    localJPanel2.add(localJButton3);
    JPanel localJPanel3 = new JPanel();
    localJPanel3.add(this.showHideTutorialButton);
    localJPanel1.add(localJPanel2);
    localJPanel1.add(localJPanel3);
    return localJPanel1;
  }

  public void update()
  {
    updatePagePanel(0);
  }

  private JPanel createPageMenuPanel()
  {
    JButton localJButton1 = makeButton("New page", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        TutorialEditor.this.stencilManager.insertNewStencil(true);
        TutorialEditor.this.updatePagePanel(1);
        TutorialEditor.this.displayNewNoteDialog();
      }
    });
    JButton localJButton2 = makeButton("Remove page", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        TutorialEditor.this.stencilManager.removeCurrStencil();
        TutorialEditor.this.updatePagePanel(-1);
      }
    });
    JButton localJButton3 = makeButton("Next", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        TutorialEditor.this.stencilManager.showNextStencil();
      }
    });
    JButton localJButton4 = makeButton("Back", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        TutorialEditor.this.stencilManager.showPreviousStencil();
      }
    });
    JPanel localJPanel = new JPanel();
    TitledBorder localTitledBorder = BorderFactory.createTitledBorder("Page menu");
    localJPanel.setBorder(localTitledBorder);
    localJPanel.add(localJButton4);
    localJPanel.add(localJButton1);
    localJPanel.add(localJButton2);
    localJPanel.add(localJButton3);
    return localJPanel;
  }

  private JPanel createNoteMenuPanel()
  {
    JButton localJButton1 = makeButton("New note...", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        TutorialEditor.this.displayNewNoteDialog();
      }
    });
    JButton localJButton2 = makeButton("Remove last note", new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        TutorialEditor.this.stencilManager.removeLastStencilObject();
      }
    });
    JPanel localJPanel = new JPanel();
    TitledBorder localTitledBorder = BorderFactory.createTitledBorder("Note menu");
    localJPanel.setBorder(localTitledBorder);
    localJPanel.add(localJButton1);
    localJPanel.add(new JLabel("      "));
    localJPanel.add(localJButton2);
    return localJPanel;
  }

  private void displayNewNoteDialog()
  {
    NewNoteDialog localNewNoteDialog = new NewNoteDialog(this.stencilManager, this);
    localNewNoteDialog.display(true);
  }

  private Point getPosition()
  {
    return new Point(getX(), getY());
  }

  private String getToggleButtonText()
  {
    return "Show/Hide tutorial";
  }

  public static JRadioButton getSelection(ButtonGroup paramButtonGroup)
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
    validate();
    setVisible(paramBoolean);
  }

/*  public static void main(String[] paramArrayOfString)
  {
    TutorialEditor localTutorialEditor = new TutorialEditor(null);
    localTutorialEditor.display(true);
  }*/
}
