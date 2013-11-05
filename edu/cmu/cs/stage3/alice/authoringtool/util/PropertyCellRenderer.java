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

import edu.cmu.cs.stage3.lang.Messages;
import javax.swing.JTable;

public class PropertyCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
	protected ColorRenderer colorRenderer = new ColorRenderer();

	public PropertyCellRenderer() {}

	public java.awt.Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
		super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );

		String toolTipText = null;
		if( value != null ) {
			if( value instanceof edu.cmu.cs.stage3.alice.core.Element ) {
				toolTipText = ((edu.cmu.cs.stage3.alice.core.Element)value).getKey();
			} else if( value instanceof java.lang.String ) {
				toolTipText = "\"" + getText() + "\"";  
			} else {
				toolTipText = getText();
			}
			String classString = value.getClass().getName();
			if( classString.startsWith( "edu.cmu.cs.stage3." ) ) { 
				classString = classString.substring( "edu.cmu.cs.stage3.".length() ); 
			}
			toolTipText = toolTipText + " (" + classString + ")";  
		}
		this.setToolTipText( toolTipText );

		if( value instanceof edu.cmu.cs.stage3.alice.core.Element ) {
			setText( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( value ) );
		} else if( (value instanceof java.awt.Color) || (value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color) ) {
			colorRenderer.setToolTipText( toolTipText );
			return colorRenderer.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
		} else if( value != null && value.getClass().isArray() ) {
			String text = ""; 
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
			nf.setMaximumFractionDigits( 3 );
			text = "{ "; 
			int m = java.lang.reflect.Array.getLength( value );
			for( int i = 0; i < m; i++ ) {
				Object o = java.lang.reflect.Array.get( value, i );
				if( o.getClass().isArray() ) {
					text += "{ "; 
					int n = java.lang.reflect.Array.getLength( o );
					for( int j=0; j<n; j++ ) {
						Object p = java.lang.reflect.Array.get( o, j );
						if( p instanceof Number ) {
							text += nf.format( p );
						} else {
							text += p;
						}
						if( j<n-1 ) {
							text += ", "; 
						}
						else {
							text += " "; 
						}
					}
					text += "}"; 
				} else {
					if( o instanceof edu.cmu.cs.stage3.alice.core.Element ) {
						text += edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( o );
					} else if( o instanceof Number ) {
						text += nf.format( o );
					} else {
						text += o;
					}
				}
				if( i < m - 1 ) {
					text += ", "; 
				} else {
					text += " "; 
				}
				if( text.length() > 64 ) {
					text += "..."; 
					break;
				}
			}
			text += "}"; 
			setText( text );
		} else if( value instanceof edu.cmu.cs.stage3.util.Enumerable ) {
			setText( ((edu.cmu.cs.stage3.util.Enumerable)value).getRepr() );
		} else if( value instanceof java.util.Enumeration ) {
			setText( Messages.getString("_enumeration_") 