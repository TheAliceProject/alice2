package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.io.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.Vector;
import java.awt.Point;
import edu.cmu.cs.stage3.caitlin.stencilhelp.application.StencilApplication;

import javax.swing.ProgressMonitor;
public class StencilParser { //extends org.xml.sax.helpers.DefaultHandler{
  StringBuffer textBuffer;
  StencilManager stencilManager;
  ObjectPositionManager positionManager;
  StencilApplication stencilApp;

  public StencilParser(StencilManager stencilManager, ObjectPositionManager positionManager,
  StencilApplication stencilApp) {
    this.stencilManager = stencilManager;
    this.positionManager = positionManager;
    this.stencilApp = stencilApp;
  }

  protected void loadStateCapsule(Node node, StencilManager.Stencil newStencil) {
    NodeList nl = node.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node child = nl.item(i);
      if (child.getNodeValue() != null) {
        String capsuleString = child.getNodeValue().trim();
        if (capsuleString.length() > 0) {
          edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule stateCapsule = stencilApp.getStateCapsuleFromString(capsuleString);
          newStencil.setEndState(stateCapsule);
        }
      }
    }
  }

  protected void loadNote(Node node, StencilManager.Stencil newStencil, NavigationBar navBar) {
    NodeList noteParts = node.getChildNodes();

    // get the attributes of the node
    NamedNodeMap attr = node.getAttributes();
    Node objectType = attr.getNamedItem("type"); //$NON-NLS-1$
    Node xPosNode = attr.getNamedItem("xPos"); //$NON-NLS-1$
    Node yPosNode = attr.getNamedItem("yPos"); //$NON-NLS-1$
    Node autoAdvanceNode = attr.getNamedItem("autoAdvance"); //$NON-NLS-1$
    Node advanceEventNode = attr.getNamedItem("advanceEvent"); //$NON-NLS-1$
    Node hasNextNode = attr.getNamedItem("hasNext"); //$NON-NLS-1$
    boolean hasNext = false;
    int advanceEvent = 0;

    if (hasNextNode != null) {
      if (hasNextNode.getNodeValue().equals("true")){ //$NON-NLS-1$
        hasNext = true;
      }
    }

    // then we need to get the text of the note too....
    String message = "hello world"; //$NON-NLS-1$
    Vector msgs = new Vector();
    Vector colors = new Vector();
    String id = "id"; //$NON-NLS-1$
    for (int i = 0; i < noteParts.getLength(); i++) {
      Node noteDetails = noteParts.item(i);
      if (noteDetails.getNodeName().equals("id")) { //$NON-NLS-1$
		id = edu.cmu.cs.stage3.xml.NodeUtilities.getNodeText( noteDetails );
      } else if (noteDetails.getNodeName().equals("message")) { //$NON-NLS-1$
		message = edu.cmu.cs.stage3.xml.NodeUtilities.getNodeText( noteDetails );
		if (message.length() > 0) msgs.addElement(message);
        /*
        NodeList textList = noteDetails.getChildNodes();
        for (int j = 0; j < textList.getLength(); j++) {
          Node textIHope = textList.item(j);
          message = textIHope.getNodeValue();
          if (message != null) message = message.trim();
          if (message.length() > 0) msgs.addElement(message);
          
        } */

        NamedNodeMap textAttr = noteDetails.getAttributes();
        if (textAttr != null) {
          Node textColor = textAttr.getNamedItem("color"); //$NON-NLS-1$
          if (textColor != null) {
            colors.addElement(textColor.getNodeValue());
            //System.out.println("adding color");
          } else {
            colors.addElement(null);
            //System.out.println("adding color");
          }
        } else {
          colors.addElement(null);
          //System.out.println("adding color");
        }
      }
    }

    // create the appropriate note or frame
    // COME BACK - make this save and restore the author's approximate positions for the objects
    if (objectType.getNodeValue().equals("hole")) { //$NON-NLS-1$
      Hole hole = new Hole(id, positionManager, stencilApp, stencilManager);
      Point p = hole.getNotePoint();
      Point initPos = new Point((int)Double.parseDouble(xPosNode.getNodeValue()), (int)Double.parseDouble(yPosNode.getNodeValue()));
      Note note = new Note( p, initPos, hole, positionManager, stencilManager, hasNext );
      //note.setText(message);
      for (int i = 0; i < msgs.size(); i++) {
        note.addText((String)msgs.elementAt(i), (String)colors.elementAt(i));
      }
      boolean autoAdvance = false;
      if (autoAdvanceNode != null) {
        if (autoAdvanceNode.getNodeValue().equals("true")){ //$NON-NLS-1$
          autoAdvance = true;
        }
      }
      if (advanceEventNode != null) {
        if (advanceEventNode.getNodeValue().equals("mousePress")) { //$NON-NLS-1$
          advanceEvent = Hole.ADVANCE_ON_PRESS;
        } else if (advanceEventNode.getNodeValue().equals("mouseClick")) { //$NON-NLS-1$
          advanceEvent = Hole.ADVANCE_ON_CLICK;
        } else {
          advanceEvent = Hole.ADVANCE_ON_ENTER;
        }
      }
      hole.setAutoAdvance(autoAdvance, advanceEvent);
      newStencil.addObject(hole);
      newStencil.addObject(note);
    } else if (objectType.getNodeValue().equals("frame")) { //$NON-NLS-1$
      Frame frame = new Frame(id, positionManager);
      Point p = frame.getNotePoint();
      Point initPos = new Point((int)Double.parseDouble(xPosNode.getNodeValue()), (int)Double.parseDouble(yPosNode.getNodeValue()));
      Note note = new Note( p, initPos, frame, positionManager, stencilManager, hasNext );
      //note.setText(message);
      for (int i = 0; i < msgs.size(); i++) {
        note.addText((String)msgs.elementAt(i), (String)colors.elementAt(i));
      }
      newStencil.addObject(frame);
      newStencil.addObject(note);
    } else if (objectType.getNodeValue().equals("navBar")) { //$NON-NLS-1$
      Point p = navBar.getNotePoint();
      Point initPos = new Point((int)Double.parseDouble(xPosNode.getNodeValue()), (int)Double.parseDouble(yPosNode.getNodeValue()));
      Note note = new Note( p, initPos, navBar, positionManager, stencilManager, hasNext );
      //note.setText(message);
      for (int i = 0; i < msgs.size(); i++) {
        note.addText((String)msgs.elementAt(i), (String)colors.elementAt(i));
      }
      newStencil.addObject(note);
    } else {
      double xRatio = Double.parseDouble(xPosNode.getNodeValue());
      double yRatio = Double.parseDouble(yPosNode.getNodeValue());
      Point p = new Point( (int)(stencilApp.getScreenSize().getWidth() * xRatio), (int)(stencilApp.getScreenSize().getHeight() * yRatio) );
      Note note = new Note( p, new Point(0,0), null, positionManager, stencilManager, hasNext );
      //note.setText(message);
      for (int i = 0; i < msgs.size(); i++) {
        note.addText((String)msgs.elementAt(i), (String)colors.elementAt(i));
      }
      newStencil.addObject(note);
    }
  }

  protected StencilManager.Stencil loadStencil(Node node) {
    NamedNodeMap attr = node.getAttributes();
    Node stencilTitle = attr.getNamedItem("title"); //$NON-NLS-1$
    NavigationBar navBar = new NavigationBar(stencilManager, positionManager);
    if ( (stencilTitle != null) && (stencilTitle.getNodeValue() != null))  {
      navBar.setTitleString(stencilTitle.getNodeValue());
    }

    NodeList objects = node.getChildNodes();
    Node stepsToGoBackNode = attr.getNamedItem("stepsToGoBack"); //$NON-NLS-1$
    int stepsToGoBack = 1;
    if ( (stepsToGoBackNode != null) && (stepsToGoBackNode.getNodeValue() != null) ) {
      stepsToGoBack = Integer.parseInt(stepsToGoBackNode.getNodeValue());
    }
    StencilManager.Stencil newStencil = stencilManager.newStencil(stepsToGoBack);
    newStencil.addObject(navBar );
    newStencil.addObject( new Menu(stencilManager) );
    for (int i = 0; i < objects.getLength(); i++) {
      Node childNode = objects.item(i);
      if (childNode.getNodeName().equals("note") ) { //$NON-NLS-1$
        loadNote(childNode, newStencil, navBar);
      } else if (childNode.getNodeName().equals("stateCapsule")){ //$NON-NLS-1$
        loadStateCapsule(childNode, newStencil);
      }
    }
    return newStencil;
  }

  public Vector parseFile(java.io.File fileToLoad) {
    Document document;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse(fileToLoad);
    } catch( IOException ioe ) {
      document = null;
      ioe.printStackTrace();
    } catch( ParserConfigurationException pce ) {
      document = null;
      pce.printStackTrace();
    } catch( org.xml.sax.SAXException se) {
      document = null;
      se.printStackTrace();
    }

    if (document != null) {
      Vector stencilList = new Vector();
      NamedNodeMap attr = document.getDocumentElement().getAttributes();
      Node readPermission = attr.getNamedItem("access"); //$NON-NLS-1$
      if (readPermission.getNodeValue().equals("read")) { //$NON-NLS-1$
        stencilManager.setWriteEnabled(false);
      } else {
        stencilManager.setWriteEnabled(true);
      }
      Node worldToLoad = attr.getNamedItem("world"); //$NON-NLS-1$
      if (worldToLoad != null) {
        stencilManager.setWorld(worldToLoad.getNodeValue());
      }
      //load next and previous stacks
      String nextStack = null;
      String previousStack = null;
      Node nextStackNode = attr.getNamedItem("nextStack"); //$NON-NLS-1$
      if (nextStackNode != null) {
        nextStack = nextStackNode.getNodeValue();
      }
      Node previousStackNode = attr.getNamedItem("previousStack"); //$NON-NLS-1$
      if (previousStackNode != null) {
        previousStack = previousStackNode.getNodeValue();
      }
      if ( (nextStack != null) || (previousStack != null) ) {
        stencilManager.setNextAndPreviousStacks(previousStack, nextStack);
      }
      NodeList stencils = document.getElementsByTagName( "stencil" ); //$NON-NLS-1$
      ProgressMonitor monitor = new ProgressMonitor(null, Messages.getString("StencilParser.28"), "", 0, stencils.getLength()); //$NON-NLS-1$ //$NON-NLS-2$
      monitor.setProgress(0);
      monitor.setMillisToDecideToPopup(1000);
      for (int i = 0; i < stencils.getLength(); i++) {
        Node stencilNode = stencils.item(i);
        StencilManager.Stencil newStencil = loadStencil(stencilNode);
        stencilList.addElement(newStencil);
        monitor.setProgress(i);
      }
      monitor.close();
      return stencilList;
    } else {
      System.out.println(Messages.getString("StencilParser.30")); //$NON-NLS-1$
      System.out.flush();
      return null;
    }
  }

  public StencilManager.Stencil getErrorStencil() {
    NavigationBar navBar = new NavigationBar(stencilManager, positionManager, true);
    navBar.setTitleString("Ooops!"); //$NON-NLS-1$

    StencilManager.Stencil newStencil = stencilManager.newStencil();
    newStencil.addObject(navBar );

    // create note
    Point p = new Point( (int)( (float)stencilApp.getScreenSize().getWidth() * 0.292), (int)( (float)stencilApp.getScreenSize().getHeight() * 0.448) );
    Note note = new Note( p, new Point(0,0), null, positionManager, stencilManager, false );
    note.addText(Messages.getString("StencilParser.32"), null); //$NON-NLS-1$
    note.addText(Messages.getString("StencilParser.33"), null); //$NON-NLS-1$
    note.initializeNote();

    newStencil.addObject(note);


    return newStencil;
  }
}