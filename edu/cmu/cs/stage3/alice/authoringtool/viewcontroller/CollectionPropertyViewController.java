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

package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.lang.Messages;

/**
 * @author Jason Pratt
 */
public class CollectionPropertyViewController extends javax.swing.JButton implements edu.cmu.cs.stage3.alice.authoringtool.util.GUIElement, edu.cmu.cs.stage3.alice.authoringtool.util.Releasable {
	protected edu.cmu.cs.stage3.alice.core.Property property;
	protected boolean omitPropertyName;
	protected edu.cmu.cs.stage3.alice.core.event.PropertyListener propertyListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {}
		public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {
			refreshGUI();
		}
	};

	public CollectionPropertyViewController() {
		addActionListener(
			new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
					if( CollectionPropertyViewController.this.property != null ) {
						edu.cmu.cs.stage3.alice.authoringtool.util.CollectionEditorPanel collectionEditorPanel = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getCollectionEditorPanel();
						collectionEditorPanel.setCollection( (edu.cmu.cs.stage3.alice.core.Collection)CollectionPropertyViewController.this.property.getValue() );
						edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( collectionEditorPanel, Messages.getString("Collection_Editor"), javax.swing.JOptionPane.PLAIN_MESSAGE ); 
					}
				}
			}
		);
	}

	public void set( edu.cmu.cs.stage3.alice.core.Property property, boolean omitPropertyName ) {
		clean();
		this.property = property;
		this.omitPropertyName = omitPropertyName;
		setBackground( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "propertyViewControllerBackground" ) ); 
		setMargin( new java.awt.Insets( 0, 4, 0, 4 ) );
		startListening();
		refreshGUI();
	}
	
	public edu.cmu.cs.stage3.alice.core.Property getProperty(){
		return property;
	}

	public void goToSleep() {
		stopListening();
	}

	public void wakeUp() {
		startListening();
	}

	public void clean() {
		stopListening();
	}

	public void die() {
		stopListening();
	}

	public void release() {
		edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.releaseGUI( this );
	}

	public void startListening() {
		if( property != null ) {
			property.addPropertyListener( propertyListener );
			if( property.getValue() != null ) {
				((edu.cmu.cs.stage3.alice.core.Collection)property.getValue()).values.addPropertyListener( propertyListener );
			}
		}
	}

	public void stopListening() {
		if( property != null ) {
			property.removePropertyListener( propertyListener );
			if( property.getValue() != null ) {
				((edu.cmu.cs.stage3.alice.core.Collection)property.getValue()).values.removePropertyListener( propertyListener );
			}
		}
	}

	protected void refreshGUI() {
		Object value = property.get();
		StringBuffer repr = new StringBuffer();

		if( ! omitPropertyName ) {
			repr.append( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( property ) + " = " ); 
		}

		if( value instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
			repr.append( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameInContext( (edu.cmu.cs.stage3.alice.core.Element)value, property.getOwner() ) );
		} else if( value == null ) {
			repr.append( Messages.getString("_None_") ); 
		} else if( value instanceof edu.cmu.cs.stage3.alice.core.Collection ) {
			Object[] items = ((edu.cmu.cs.stage3.alice.core.Collection)value).values.getArrayValue();
			if( items != null ) {
				for( int i = 0; i < items.length; i++ ) {
					repr.append( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( items[i] ) );
					if( i < items.length - 1 ) {
						repr.append( ", " ); 
					}
				}
			} else {
				repr.append( Messages.getString("_None_") ); 
			}
		} else {
			throw new RuntimeException( Messages.getString("Bad_value__", value )); 
		}

		setText( repr.toString() );
		revalidate();
		repaint();
	}
	
	protected String getHTMLColorString(java.awt.Color color){
		String r = Integer.toHexString(color.getRed());
		String g = Integer.toHexString(color.getGreen());
		String b = Integer.toHexString(color.getBlue());
	
		if (r.length() == 1){
			r = "0"+r;
		}
		if (g.length() == 1){
			g = "0"+g;
		}
		if (b.length() == 1){
			b = "0"+b;
		}
		return new String("#"+r+g+b);
	}
	
	public void getHTML(StringBuffer toWriteTo){
		String tempString = "";
	
		tempString += "<span style=\"background-color: "+getHTMLColorString(this.getBackground())+"\">";
		
		String labelText = getText();
		if (getFont().isBold()){
			tempString += "<b>"+labelText+"</b>";  
		}
		if (getFont().isItalic()){
			tempString += "<i>"+labelText+"</i>";  
		}
		tempString += "</span>";
		tempString = " "+tempString+" ";  
		
		toWriteTo.append(tempString);
	}
}
