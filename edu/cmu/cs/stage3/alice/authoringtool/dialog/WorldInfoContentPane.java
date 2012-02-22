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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Jason Pratt, Dennis Cosgrove
 */
public class WorldInfoContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	protected edu.cmu.cs.stage3.alice.core.World world;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.ElementUsageGraph responseUsageGraph;

	public WorldInfoContentPane() {
		jbInit();
		guiInit();
	}

	
	public String getTitle() {
		return Messages.getString("WorldInfoContentPane.0"); //$NON-NLS-1$
	}

	private void guiInit() {
		setSize( 500, 600 );
		responseUsageGraph = new edu.cmu.cs.stage3.alice.authoringtool.util.ElementUsageGraph();
		responseUsageGraph.setElementCriterion( new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( edu.cmu.cs.stage3.alice.core.Response.class ) );
		this.responseUsagePanel.add( responseUsageGraph, java.awt.BorderLayout.CENTER );
	}

	public void setWorld( edu.cmu.cs.stage3.alice.core.World world ) {
		this.world = world;
		responseUsageGraph.setRoot( world );
		refresh();
	}

	private void refresh() {
		if( world != null ) {
			edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter itaCounter = new edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter();
			edu.cmu.cs.stage3.alice.core.util.TextureMapCounter textureMapCounter = new edu.cmu.cs.stage3.alice.core.util.TextureMapCounter();

			world.visit( itaCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
			world.visit( textureMapCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS );

			int playCount = 0;
			int saveCount = 0;
			long worldOpenTime = 0;

			if( world.data.get( "edu.cmu.cs.stage3.alice.authoringtool.playCount" ) != null ) { //$NON-NLS-1$
				playCount = Integer.parseInt( (String)world.data.get( "edu.cmu.cs.stage3.alice.authoringtool.playCount" ) ); //$NON-NLS-1$
			}
			if( world.data.get( "edu.cmu.cs.stage3.alice.authoringtool.saveCount" ) != null ) { //$NON-NLS-1$
				saveCount = Integer.parseInt( (String)world.data.get( "edu.cmu.cs.stage3.alice.authoringtool.saveCount" ) ); //$NON-NLS-1$
			}
			if( world.data.get( "edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime" ) != null ) { //$NON-NLS-1$
				worldOpenTime = Long.parseLong( (String)world.data.get( "edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime" ) ); //$NON-NLS-1$
			}
			worldOpenTime /= 60000L;

			objectCountLabel.setText( Messages.getString("WorldInfoContentPane.7") + itaCounter.getShownIndexedTriangleArrayCount() ); //$NON-NLS-1$
			polyCountLabel.setText( Messages.getString("WorldInfoContentPane.8") + (itaCounter.getShownIndexCount() / 3) ); //$NON-NLS-1$
			textureCountLabel.setText( Messages.getString("WorldInfoContentPane.9") + textureMapCounter.getTextureMapCount() ); //$NON-NLS-1$
			textureMemoryLabel.setText( Messages.getString("WorldInfoContentPane.10") + edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.formatMemorySize( textureMapCounter.getTextureMapMemoryCount() ) ); //$NON-NLS-1$
			userDefinedResponseCountLabel.setText( Messages.getString("WorldInfoContentPane.11") + world.getDescendants( edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse.class ).length ); //$NON-NLS-1$
			userDefinedQuestionCountLabel.setText( Messages.getString("WorldInfoContentPane.12") + world.getDescendants( edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion.class ).length ); //$NON-NLS-1$
			behaviorCountLabel.setText( Messages.getString("WorldInfoContentPane.13") + world.getDescendants( edu.cmu.cs.stage3.alice.core.Behavior.class ).length ); //$NON-NLS-1$
			playCountLabel.setText( Messages.getString("WorldInfoContentPane.14") + playCount ); //$NON-NLS-1$
			saveCountLabel.setText( Messages.getString("WorldInfoContentPane.15") + saveCount ); //$NON-NLS-1$
			worldOpenTimeLabel.setText( Messages.getString("WorldInfoContentPane.16") + worldOpenTime + ((worldOpenTime == 1) ? Messages.getString("WorldInfoContentPane.17") : Messages.getString("WorldInfoContentPane.18")) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			currentRendererLabel.setText(Messages.getString("WorldInfoContentPane.19")+edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getCurrentRendererText()); //$NON-NLS-1$

			responseUsageGraph.refresh();
		}
	}

	///////////////////
	// Autogenerated
	///////////////////

	Border border1;
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel buttonPanel = new JPanel();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JButton doneButton = new JButton();
	Border border2;
	JScrollPane mainScrollPane = new JScrollPane();
	JLabel textureMemoryLabel = new JLabel();
	JPanel mainPanel = new JPanel();
	JLabel responseUsageLabel = new JLabel();
	JLabel objectCountLabel = new JLabel();
	JLabel polyCountLabel = new JLabel();
	JLabel currentRendererLabel = new JLabel();
	Component component1;
	JPanel responseUsagePanel = new JPanel();
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	JLabel behaviorCountLabel = new JLabel();
	JLabel userDefinedResponseCountLabel = new JLabel();
	JLabel textureCountLabel = new JLabel();
	JLabel playCountLabel = new JLabel();
	JLabel saveCountLabel = new JLabel();
	JLabel worldOpenTimeLabel = new JLabel();
	JLabel userDefinedQuestionCountLabel = new JLabel();

	private void jbInit() {
		border1 = BorderFactory.createEmptyBorder(8,8,8,8);
		border2 = BorderFactory.createEmptyBorder(8,8,8,8);
		component1 = Box.createGlue();
		setBackground(new Color(232, 230, 255));
		setLayout(borderLayout1);
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(gridBagLayout1);
		doneButton.setText(Messages.getString("WorldInfoContentPane.20")); //$NON-NLS-1$
		textureMemoryLabel.setText(Messages.getString("WorldInfoContentPane.21")); //$NON-NLS-1$
		mainPanel.setBorder(border2);
		mainPanel.setOpaque(false);
		mainPanel.setLayout(gridBagLayout2);
		responseUsageLabel.setText(Messages.getString("WorldInfoContentPane.22")); //$NON-NLS-1$
		objectCountLabel.setText(Messages.getString("WorldInfoContentPane.23")); //$NON-NLS-1$
		polyCountLabel.setText(Messages.getString("WorldInfoContentPane.24")); //$NON-NLS-1$
		responseUsagePanel.setOpaque(false);
		responseUsagePanel.setLayout(borderLayout2);
		behaviorCountLabel.setText(Messages.getString("WorldInfoContentPane.25")); //$NON-NLS-1$
		userDefinedResponseCountLabel.setText(Messages.getString("WorldInfoContentPane.26")); //$NON-NLS-1$
		mainScrollPane.getViewport().setBackground(new Color(232, 230, 255));
		mainScrollPane.setOpaque(false);
		textureCountLabel.setText(Messages.getString("WorldInfoContentPane.27")); //$NON-NLS-1$
		playCountLabel.setText(Messages.getString("WorldInfoContentPane.28")); //$NON-NLS-1$
		saveCountLabel.setText(Messages.getString("WorldInfoContentPane.29")); //$NON-NLS-1$
		worldOpenTimeLabel.setText(Messages.getString("WorldInfoContentPane.30")); //$NON-NLS-1$
		userDefinedQuestionCountLabel.setText(Messages.getString("WorldInfoContentPane.31")); //$NON-NLS-1$
		add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(doneButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
		add(mainScrollPane, BorderLayout.CENTER);
		mainScrollPane.getViewport().add(mainPanel, null);
		mainPanel.add(objectCountLabel,      new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(polyCountLabel,      new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(textureMemoryLabel,      new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(userDefinedResponseCountLabel,       new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(behaviorCountLabel,       new GridBagConstraints(0, 6, 2, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(responseUsageLabel,          new GridBagConstraints(0, 11, 2, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(8, 0, 2, 0), 0, 0));
		mainPanel.add(responseUsagePanel,       new GridBagConstraints(0, 12, 2, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 0), 0, 0));
		mainPanel.add(component1,       new GridBagConstraints(0, 13, 2, 1, 1.0, 1.0
			,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		mainPanel.add(textureCountLabel,      new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(playCountLabel,         new GridBagConstraints(0, 7, 2, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(saveCountLabel,     new GridBagConstraints(0, 8, 2, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(worldOpenTimeLabel,    new GridBagConstraints(0, 9, 1, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
		mainPanel.add(currentRendererLabel,    new GridBagConstraints(0, 10, 1, 1, 1.0, 0.0
					,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
			
		mainPanel.add(userDefinedQuestionCountLabel,   new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
	}

	
	public void addOKActionListener( java.awt.event.ActionListener l ) {
		doneButton.addActionListener( l );
	}
	
	public void removeOKActionListener( java.awt.event.ActionListener l ) {
		doneButton.removeActionListener( l );
	}
}