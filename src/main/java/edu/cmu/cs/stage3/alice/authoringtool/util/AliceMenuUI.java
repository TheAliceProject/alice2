/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

/**
 * adapted from javax.swing.plaf.basic.BasicMenuUI to improve on popup menu behavior.
 * @author Jason Pratt
 */
public class AliceMenuUI extends javax.swing.plaf.basic.BasicMenuUI {
	protected MouseInputListener createMouseInputListener( JComponent c ) {
		return new AliceMouseInputHandler();
	}

	protected class AliceMouseInputHandler implements MouseInputListener {
		public void mousePressed( MouseEvent e ) {
			JMenu menu = (JMenu)menuItem;

			if( ! menu.isEnabled() ) {
				return;
			}

			MenuSelectionManager manager = MenuSelectionManager.defaultManager();

			if( menu.isTopLevelMenu() ) {
				if( menu.isSelected() ) {
					manager.clearSelectedPath();
				} else {
					Container cnt = menu.getParent();

					if( ( cnt != null ) && cnt instanceof JMenuBar ) {
						MenuElement[] me = new MenuElement[2];
						me[0] = (MenuElement)cnt;
						me[1] = menu;
						manager.setSelectedPath( me );
					}
				}
			}

			MenuElement[] selectedPath = manager.getSelectedPath();

			if( ! ( selectedPath.length > 0 && selectedPath[selectedPath.length - 1] == menu.getPopupMenu() ) ) {
				if( menu.isTopLevelMenu() || ( menu.getDelay() == 0 ) ) {
					MenuElement[] newPath = new MenuElement[selectedPath.length + 1];
					System.arraycopy( selectedPath, 0, newPath, 0, selectedPath.length );
					newPath[selectedPath.length] = menu.getPopupMenu();
					manager.setSelectedPath( newPath );
				} else {
					setupPostTimer( menu );
				}
			}
		}

		public void mouseReleased( MouseEvent e ) {
			JMenu menu = (JMenu)menuItem;

			if( ! menu.isEnabled() ) {
				return;
			}

			MenuSelectionManager manager = MenuSelectionManager.defaultManager();
			manager.processMouseEvent( e );

			if( ! e.isConsumed() ) {
				manager.clearSelectedPath();
			}
		}

		public void mouseEntered( MouseEvent e ) {
			JMenu menu = (JMenu)menuItem;

			if( ! menu.isEnabled() ) {
				return;
			}

			MenuSelectionManager manager = MenuSelectionManager.defaultManager();
			MenuElement[] selectedPath = manager.getSelectedPath();

			if( ! menu.isTopLevelMenu() ) {
				if( menu.getDelay() == 0 ) {
					MenuElement[] path = getPath();
					MenuElement[] newPath = new MenuElement[getPath().length + 1];
					System.arraycopy( path, 0, newPath, 0, path.length );
					newPath[path.length] = menu.getPopupMenu();
					manager.setSelectedPath( newPath );
//					System.out.print( "0 setSelectedPath: " );
//					printPath( newPath );
				} else {
					manager.setSelectedPath( getPath() );
					setupPostTimer( menu );
				}
			} else {
				if( ( selectedPath.length > 0 ) && ( selectedPath[0] == menu.getParent() ) ) 