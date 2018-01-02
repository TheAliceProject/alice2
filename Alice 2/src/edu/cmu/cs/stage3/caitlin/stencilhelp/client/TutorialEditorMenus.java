package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Iterator;
import java.util.Vector;

public class TutorialEditorMenus
  implements StencilObject, StencilPanelMessageListener, MouseEventListener, StencilFocusListener, ReadWriteListener
{
  protected Vector shapes = new Vector();
  protected Vector stencilObjectPositionListeners = new Vector();
  protected StencilManager stencilManager = null;
  protected boolean isShowing = false;
  protected int level = 0;
  protected Point centerPoint;
  protected boolean writeEnabled = true;
  protected boolean isModified = true;
  protected Rectangle previousRect = null;
  protected Rectangle contextMenuRect = null;
  private RoundRectangle2D newNoteOption = null;
  private RoundRectangle2D newNoteNextOption = null;
  private RoundRectangle2D newReferenceNote = null;
  private RoundRectangle2D newHoleNote = null;
  private RoundRectangle2D newHoleNoteAutoAdvance = null;
  private RoundRectangle2D newReferenceNextNote = null;
  private static int fontSize = 13;
  private static Font font = new Font("SansSerif", 0, fontSize);
  private static final int MENU_WIDTH = 350;
  protected static Vector editorShapes = new Vector();
  protected static Vector<RoundRectangle2D> editorRects = new Vector();
  private static RoundRectangle2D.Double editorMenuTitle;
  private static RoundRectangle2D.Double editorMenuNewNote;
  private static RoundRectangle2D.Double editorMenuSave;
  private static RoundRectangle2D.Double editorMenuNew;
  private static RoundRectangle2D.Double editorMenuOpen;
  private static RoundRectangle2D.Double editorMenuNewFrame;
  private static RoundRectangle2D.Double editorMenuNewPage;
  private static RoundRectangle2D.Double editorMenuClearPage;
  private static RoundRectangle2D.Double editorMenuRemovePage;
  private static RoundRectangle2D.Double editorMenuRemoveLastNote;
  private static RoundRectangle2D.Double editorMenuExitPage;
  static Color defaultShapeBGColor = new Color(220, 220, 220, 220);
  static Color defaultTextColor = new Color(0, 0, 255);
  static final double padX = 7.0D;
  static final double padY = 7.0D;

  public TutorialEditorMenus(StencilManager paramStencilManager)
  {
    this.stencilManager = paramStencilManager;
  }

  public RoundRectangle2D.Double createEditingShapeContextMenu(String paramString, Point paramPoint)
  {
    Shape localShape = null;
    Color localColor1 = new Color(255, 200, 240, 100);
    Color localColor2 = new Color(220, 220, 220, 220);
    Color localColor3 = new Color(0, 0, 255);
    TextLayout localTextLayout = new TextLayout(paramString, font, new FontRenderContext(null, false, false));
    AffineTransform localAffineTransform = new AffineTransform();
    double d1 = localTextLayout.getBounds().getWidth();
    double d2 = localTextLayout.getBounds().getHeight();
    localAffineTransform.translate(paramPoint.x, paramPoint.y + 10);
    localShape = localTextLayout.getOutline(localAffineTransform);
    RoundRectangle2D.Double localDouble = new RoundRectangle2D.Double(paramPoint.x - 3.5D, paramPoint.y - 3.5D, d1 + 7.0D, d2 + 7.0D, 10.0D, 10.0D);
    this.shapes.add(new ScreenShape(localColor1, localDouble, true, this.shapes.size() + 1));
    this.shapes.add(new ScreenShape(localColor2, localDouble, true, this.shapes.size() + 1));
    this.shapes.add(new ScreenShape(localColor3, localShape, true, this.shapes.size() + 1));
    return localDouble;
  }

  protected void updateContextMenuShapes(Point paramPoint)
  {
    this.centerPoint = paramPoint;
    Point2D.Double localDouble = Note.translatePositionToVisiblePart(new Rectangle2D.Double(paramPoint.x, paramPoint.y, 240.0D, 190.0D));
    paramPoint = new Point(paramPoint.x + (int)localDouble.getX(), paramPoint.y + (int)localDouble.getY());
    this.previousRect = getRectangle();
    int i = (int)(fontSize * 1.5D);
    this.shapes.removeAllElements();
    if (this.writeEnabled)
    {
      Point localPoint = paramPoint.getLocation();
      this.newNoteNextOption = createEditingShapeContextMenu("[ new note with 'next' ]", localPoint);
      localPoint.translate(0, i);
      this.newNoteOption = createEditingShapeContextMenu("[ new note ]", localPoint);
      localPoint.translate(0, i);
      this.newReferenceNextNote = createEditingShapeContextMenu("[ new reference note with 'next' ]", localPoint);
      localPoint.translate(0, i);
      this.newReferenceNote = createEditingShapeContextMenu("[ new reference note ]", localPoint);
      localPoint.translate(0, i);
      this.newHoleNoteAutoAdvance = createEditingShapeContextMenu("[ new note with hole - auto advance ]", localPoint);
      localPoint.translate(0, i);
      this.newHoleNote = createEditingShapeContextMenu("[ new note with hole ]", localPoint);
      this.previousRect = this.contextMenuRect;
      this.contextMenuRect = new Rectangle((int)this.newNoteOption.getX(), (int)this.newNoteOption.getY(), (int)this.newNoteOption.getWidth(), (int)this.newNoteOption.getHeight()).union(new Rectangle((int)this.newHoleNote.getX(), (int)this.newHoleNote.getY(), (int)this.newHoleNote.getWidth(), (int)this.newHoleNote.getHeight()));
    }
    else
    {
      this.previousRect = this.contextMenuRect;
      this.contextMenuRect = new Rectangle(paramPoint.x - 40, paramPoint.y - 15, paramPoint.x + 25, paramPoint.y + 50);
    }
    this.isModified = true;
  }

  public static RoundRectangle2D.Double createEditingShape(String paramString, Point paramPoint)
  {
    return createEditingShape(paramString, paramPoint, defaultShapeBGColor, defaultTextColor);
  }

  public static RoundRectangle2D.Double createEditingShape(String paramString, Point paramPoint, Color paramColor1, Color paramColor2)
  {
    Shape localShape = null;
    Color localColor1 = new Color(255, 200, 240, 100);
    Color localColor2 = paramColor1 != null ? paramColor1 : defaultShapeBGColor;
    Color localColor3 = paramColor2 != null ? paramColor2 : defaultTextColor;
    TextLayout localTextLayout = new TextLayout(paramString, font, new FontRenderContext(null, false, false));
    AffineTransform localAffineTransform = new AffineTransform();
    double d1 = localTextLayout.getBounds().getWidth();
    double d2 = localTextLayout.getBounds().getHeight();
    localAffineTransform.translate(paramPoint.x, paramPoint.y + 10);
    localShape = localTextLayout.getOutline(localAffineTransform);
    RoundRectangle2D.Double localDouble = new RoundRectangle2D.Double(paramPoint.x - 3.5D, paramPoint.y - 3.5D, d1 + 7.0D, d2 + 7.0D, 10.0D, 10.0D);
    editorShapes.add(new ScreenShape(localColor1, localDouble, true, editorShapes.size() + 1));
    editorShapes.add(new ScreenShape(localColor2, localDouble, true, editorShapes.size() + 1));
    editorShapes.add(new ScreenShape(localColor3, localShape, true, editorShapes.size() + 1));
    editorRects.add(localDouble);
    return localDouble;
  }

  private static int prevItemWidth(RoundRectangle2D.Double paramDouble)
  {
    return (int)(paramDouble.x + paramDouble.width) + 15;
  }

  public static void generateEditorMenu()
  {
    double d = AuthoringTool.getHack().getScreenSize().getWidth();
    Point localPoint = new Point((int)(d - 350.0D), 5);
    editorMenuTitle = createEditingShape("Instructor Mode", localPoint, null, Color.black);
    int i = (int)(editorMenuTitle.y + editorMenuTitle.height + 7.0D);
    editorMenuOpen = createEditingShape("Open", new Point(localPoint.x, i));
    editorMenuNew = createEditingShape("New", new Point(prevItemWidth(editorMenuOpen), i));
    editorMenuSave = createEditingShape("Save", new Point(prevItemWidth(editorMenuNew), i));
    editorMenuExitPage = createEditingShape("Exit Editor", new Point(prevItemWidth(editorMenuSave), i));
    int j = (int)(editorMenuOpen.y + editorMenuOpen.height + 7.0D);
    editorMenuNewPage = createEditingShape("New Page", new Point(localPoint.x, j));
    editorMenuClearPage = createEditingShape("Clear Page", new Point(prevItemWidth(editorMenuNewPage), j));
    editorMenuRemovePage = createEditingShape("Remove Page", new Point(prevItemWidth(editorMenuClearPage), j));
    int k = (int)(editorMenuNewPage.y + editorMenuNewPage.height + 7.0D);
    RoundRectangle2D.Double localDouble = createEditingShape("(Right-click to add new notes)", new Point(localPoint.x, k), null, Color.black);
    editorMenuRemoveLastNote = createEditingShape("Remove Last Note", new Point(prevItemWidth(localDouble), k));
  }

  public static Vector getEditorShapes()
  {
    return editorShapes;
  }

  protected Shape createWordShape(String paramString, Point paramPoint)
  {
    TextLayout localTextLayout = new TextLayout(paramString, font, new FontRenderContext(null, false, false));
    AffineTransform localAffineTransform = new AffineTransform();
    localAffineTransform.translate(paramPoint.x, paramPoint.y);
    Shape localShape = localTextLayout.getOutline(localAffineTransform);
    return localShape;
  }

  public Vector getShapes()
  {
    if (this.isShowing)
      return this.shapes;
    return null;
  }

  public Rectangle getRectangle()
  {
    if (this.isShowing)
      return this.contextMenuRect;
    return null;
  }

  public Rectangle getPreviousRectangle()
  {
    Rectangle localRectangle = this.previousRect;
    return localRectangle;
  }

  public boolean isModified()
  {
    if (this.isModified)
    {
      this.isModified = false;
      return true;
    }
    return false;
  }

  public boolean intersectsRectangle(Rectangle paramRectangle)
  {
    Rectangle localRectangle = getRectangle();
    if (localRectangle != null)
      return paramRectangle.intersects(localRectangle);
    return false;
  }

  public void addStencilObjectPositionListener(StencilObjectPositionListener paramStencilObjectPositionListener)
  {
    this.stencilObjectPositionListeners.addElement(paramStencilObjectPositionListener);
  }

  public void removeStencilObjectPositionListener(StencilObjectPositionListener paramStencilObjectPositionListener)
  {
    this.stencilObjectPositionListeners.remove(paramStencilObjectPositionListener);
  }

  public String getComponentID()
  {
    return null;
  }

  public void messageReceived(int paramInt, Object paramObject)
  {
    if (this.writeEnabled)
    {
      this.isShowing = true;
      this.level = 0;
      updateContextMenuShapes((Point)paramObject);
      this.stencilManager.requestFocus(this);
    }
  }

  public boolean contains(Point paramPoint)
  {
    if (this.writeEnabled)
    {
      Iterator localIterator = editorRects.iterator();
      while (localIterator.hasNext())
      {
        RoundRectangle2D localRoundRectangle2D = (RoundRectangle2D)localIterator.next();
        if (localRoundRectangle2D.contains(paramPoint))
          return true;
      }
      if (this.isShowing)
        return true;
    }
    return false;
  }

  public boolean mousePressed(MouseEvent paramMouseEvent)
  {
    return false;
  }

  public boolean mouseReleased(MouseEvent paramMouseEvent)
  {
    return false;
  }

  public boolean mouseClicked(MouseEvent paramMouseEvent)
  {
    this.isShowing = false;
    int i = 0;
    if (this.writeEnabled)
    {
      this.previousRect = this.contextMenuRect;
      if (this.newNoteOption != null)
        if (this.newNoteOption.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.createNewNote(this.centerPoint, false);
          i = 1;
        }
        else if (this.newNoteNextOption.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.createNewNote(this.centerPoint, true);
          i = 1;
        }
        else if (this.newReferenceNote.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.createNewFrame(this.centerPoint, false);
          i = 1;
        }
        else if (this.newReferenceNextNote.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.createNewFrame(this.centerPoint, true);
          i = 1;
        }
        else if (this.newHoleNote.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.createNewHole(this.centerPoint, false);
          i = 1;
        }
        else if (this.newHoleNoteAutoAdvance.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.createNewHole(this.centerPoint, true);
          i = 1;
        }
      if ((i == 0) && (this.stencilManager.isInstructorMode()))
        if (editorMenuOpen.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.loadStencilsFile();
          this.stencilManager.setWriteEnabled(true);
        }
        else if (editorMenuNew.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.newTutorial();
          this.stencilManager.setWriteEnabled(true);
        }
        else if (editorMenuSave.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.saveStencilsFile();
        }
        else if (editorMenuNewPage.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.insertNewStencil(true);
        }
        else if (editorMenuClearPage.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.removeAllObjectsFromCurrentStencil();
        }
        else if (editorMenuRemovePage.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.removeCurrStencil();
        }
        else if (editorMenuRemoveLastNote.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.removeLastStencilObject();
        }
        else if (editorMenuExitPage.contains(paramMouseEvent.getPoint()))
        {
          this.stencilManager.setInstructorMode(false);
        }
      this.isModified = true;
    }
    else
    {
      this.previousRect = this.contextMenuRect;
      this.isModified = true;
    }
    return true;
  }

  public boolean mouseEntered(MouseEvent paramMouseEvent)
  {
    return false;
  }

  public boolean mouseExited(MouseEvent paramMouseEvent)
  {
    return false;
  }

  public boolean mouseMoved(MouseEvent paramMouseEvent)
  {
    return false;
  }

  public boolean mouseDragged(MouseEvent paramMouseEvent)
  {
    return false;
  }

  public void focusGained()
  {
    this.isShowing = true;
  }

  public void focusLost()
  {
    this.isShowing = false;
    this.previousRect = this.contextMenuRect;
    this.isModified = true;
  }

  public void setWriteEnabled(boolean paramBoolean)
  {
    this.writeEnabled = paramBoolean;
  }

  static
  {
    generateEditorMenu();
  }
}

/* Location:           /Users/ace/Dropbox (Alice Project)/Temporary/Mama/Source/
 * Qualified Name:     edu.cmu.cs.stage3.caitlin.stencilhelp.client.TutorialEditorMenus
 * JD-Core Version:    0.6.2
 */