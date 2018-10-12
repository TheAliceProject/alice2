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

package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LicenseContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	private javax.swing.JButton m_okButton = new javax.swing.JButton(
			Messages.getString("OK"));

	public LicenseContentPane() {
		String text  = "";
		try {
			URL urlToDictionary = this.getClass().getResource("license.txt");
			InputStream stream = urlToDictionary.openStream();
			BufferedReader br = new BufferedReader(new  InputStreamReader(stream));
			try {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();
	
			    while (line != null) {
			    	sb.append(line);
			    	sb.append("\n");
			    	line = br.readLine();
			    }
			    text = sb.toString();
			} finally {
				br.close();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		    
		setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		int width = 700;
		setPreferredSize(new Dimension (width,500));   
	    JTextArea headerTextArea = new JTextArea();
	    headerTextArea.setText("Please read the following license agreement carefully.");
	    headerTextArea.setEditable(false);
	    headerTextArea.setLineWrap(true);
	    headerTextArea.setWrapStyleWord(true);
	    headerTextArea.setOpaque(false);

	    JTextArea textArea = new JTextArea();
	    textArea.setText(text);
	    textArea.setEditable(false);
	    textArea.setLineWrap(true);
	    textArea.setWrapStyleWord(true);
	    textArea.setMargin( new java.awt.Insets(10,10,10,10) );
	    	    
	    JScrollPane scrollPane = new JScrollPane(textArea);
	    //scrollPane.setPreferredSize(new Dimension(width, 320));

	    JPanel buttonPane = new JPanel();
	    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
	    buttonPane.add(Box.createHorizontalGlue());
	    buttonPane.add(m_okButton);
	    buttonPane.add(Box.createHorizontalGlue());
	    
	    headerTextArea.setAlignmentX(0.0F);
	    scrollPane.setAlignmentX(0.0F);
	    buttonPane.setAlignmentX(0.0F);
	    
	    BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
	    setLayout(boxLayout);
	    add(headerTextArea);
		add(scrollPane);
		add(buttonPane);
	}

	public String getTitle() {
		return Messages.getString("Alice_license");
	}

	public void addOKActionListener(java.awt.event.ActionListener l) {
		m_okButton.addActionListener(l);
	}

	public void removeOKActionListener(java.awt.event.ActionListener l) {
		m_okButton.removeActionListener(l);
	}
}
