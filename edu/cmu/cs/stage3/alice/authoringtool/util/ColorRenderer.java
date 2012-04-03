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

class ColorRenderer extends javax.swing.JLabel implements javax.swing.ListCellRenderer, javax.swing.table.TableCellRenderer, javax.swing.tree.TreeCellRenderer {
	//TODO: cache icons while paying attention to width and height?
	//static java.util.Hashtable colorsToIcons = new java.util.Hashtable();

	public ColorRenderer() {}

	public java.awt.Component getListCellRendererComponent( javax.swing.JList list, Object color, int index, boolean isSelected, boolean cellHasFocus )  {
		return getComponent( color, isSelected, list.getBackground(), list.getSelectionBackground(), list.getFixedCellHeight(), list.getFont() );
	}

	public java.awt.Component getTableCellRendererComponent( javax.swing.JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column ) {
		return getComponent( color, isSelected, table.getBackground(), table.getSelectionBackground(), table.getRowHeight(), table.getFont() );
	}

	public java.awt.Component getTreeCellRendererComponent( javax.swing.JTree tree, Object color, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus ) {
		// JTree renderers are a little funky.  background selection color won't work well until I account for this.
		// TODO: support background selection color better
		return getComponent( color, isSelected, tree.getBackground(), tree.getBackground(), tree.getRowHeight(), tree.getFont() );
	}

	public java.awt.Component getComponent( Object color, boolean isSelected, java.awt.Color backgroundColor, java.awt.Color selectionBackgroundColor, int cellHeight, java.awt.Font font ) {
		setOpaque( true );
		setBackground( isSelected ? selectionBackgroundColor : backgroundColor );
		setFont( font );

		if( color instanceof edu.cmu.cs.stage3.alice.scenegraph.Color ) {
			edu.cmu.cs.stage3.alice.scenegraph.Color c = (edu.cmu.cs.stage3.alice.scenegraph.Color)color;
			color = new java.awt.Color( c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() );
		} else if( color instanceof String ) { // for convenience, we'll handle this
			setIcon( null );
			setText( (String)color );
			return this;
		}

		int height = cellHeight - 4;
		if( height < 10 ) {
			height = 10;
		}
		int width = height * 2;

		setIcon( getIconFromColor( (java.awt.Color)color, width, height ) );
		setText( getTextFromColor( (java.awt.Color)color ) );

		return this;
	}

	public static javax.swing.Icon getIconFromColor( java.awt.Color color, int width, int height ) {
		java.awt.image.BufferedImage colorImage = new java.awt.image.BufferedImage( width, height, java.awt.image.BufferedImage.TYPE_INT_RGB );
		java.awt.Graphics2D g = colorImage.createGraphics();
		g.setColor( color );
		g.fill3DRect( 0, 0, width, height, true );
		return new javax.swing.ImageIcon( colorImage );
	}

	public static String getTextFromColor( java.awt.Color color ) {
		String text = ""; 
		if( color.equals( java.awt.Color.black ) ) { text = Messages.getString("ColorRenderer.1"); } 
		else if( color.equals( java.awt.Color.blue ) ) { text = Messages.getString("ColorRenderer.2"); } 
		else if( color.equals( java.awt.Color.cyan ) ) { text = Messages.getString("ColorRenderer.3"); } 
		else if( color.equals( java.awt.Color.darkGray ) ) { text = Messages.getString("ColorRenderer.4"); } 
		else if( color.equals( java.awt.Color.gray ) ) { text = Messages.getString("ColorRenderer.5"); } 
		else if( color.equals( java.awt.Color.green ) ) { text = Messages.getString("ColorRenderer.6"); } 
		else if( color.equals( java.awt.Color.lightGray ) ) { text = Messages.getString("ColorRenderer.7"); } 
		else if( color.equals( java.awt.Color.magenta ) ) { text = Messages.getString("ColorRenderer.8"); } 
		else if( color.equals( java.awt.Color.orange ) ) { text = Messages.getString("ColorRenderer.9"); } 
		else if( color.equals( java.awt.Color.pink ) ) { text = Messages.getString("ColorRenderer.10"); } 
		else if( color.equals( java.awt.Color.red ) ) { text = Messages.getString("ColorRenderer.11"); } 
		else if( color.equals( java.awt.Color.white ) ) { text = Messages.getString("ColorRenderer.12"); } 
		else if( color.equals( java.awt.Color.yellow ) ) { text = Messages.getString("ColorRenderer.13"); } 
		else {
			float[] rgba = new float[4];
			color.getComponents( rgba );
			text = Messages.getString("ColorRenderer.14") + rgba[0] + Messages.getString("ColorRenderer.15") + rgba[1] + Messages.getString("ColorRenderer.16") + rgba[2] + Messages.getString("ColorRenderer.17") + rgba[3] + ">";    //$NON-NLS-4$ //$NON-NLS-5$
		}

		return text;
	}
}
