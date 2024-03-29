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

import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.cmu.cs.stage3.alice.authoringtool.AikMin;

/**
 * @author Jason Pratt, David Culyba, Dennis Cosgrove
 */

import edu.cmu.cs.stage3.lang.Messages;

class CustomCheckBox extends javax.swing.JCheckBox implements java.awt.image.ImageObserver {
	int index = 0;
	java.awt.Image image;
	javax.swing.JComponent gui;
	Object object;

	public void setIndex(int index) {
		this.index = index;
	}

	private int size = 30; // 14

	public void paint(java.awt.Graphics g) {
		super.paint(g);
		if (image != null) {
			g.drawImage(image, size, 0, java.awt.Color.white, this);
		}
	}

	public java.awt.Dimension getPreferredSize() {
		if (image == null) {
			return super.getPreferredSize();
		} 
		int x = image.getWidth(this);
		int y = image.getHeight(this);
		return new java.awt.Dimension((x + size), y + 1);
	}

	public java.awt.Dimension getMinimumSize() {
		if (image == null) {
			return super.getMinimumSize();
		}
		int x = image.getWidth(this);
		int y = image.getHeight(this);
		return new java.awt.Dimension((x + size), y);
	}

	public java.awt.Dimension getMaximumSize() {
		if (image == null) {
			return super.getMaximumSize();
		}
		int x = image.getWidth(this);
		int y = image.getHeight(this);
		return new java.awt.Dimension((x + size), y);
	}

	public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y, int width, int height) {
		return true;
	}
}

class CustomListButton extends javax.swing.JButton implements java.awt.event.ActionListener {
	private java.util.Vector checkBoxes = new java.util.Vector();

	public CustomListButton() {
		this.addActionListener(this);
		if (!AikMin.isLTR()) 
			this.setHorizontalAlignment(SwingConstants.RIGHT);
		else
			this.setHorizontalAlignment(SwingConstants.LEFT);
		// this.setBorder(null);
	}

	public void addCheckBox(CustomCheckBox c) {
		checkBoxes.add(c);
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		boolean areAllSelected = true;
		for (int i = 0; i < checkBoxes.size(); i++) {
			CustomCheckBox currentCheckBox = (CustomCheckBox) checkBoxes.get(i);
			if (!currentCheckBox.isSelected()) {
				areAllSelected = false;
				break;
			}
		}
		if (areAllSelected) {
			for (int i = 0; i < checkBoxes.size(); i++) {
				CustomCheckBox currentCheckBox = (CustomCheckBox) checkBoxes.get(i);
				currentCheckBox.setSelected(false);
			}
		} else {
			for (int i = 0; i < checkBoxes.size(); i++) {
				CustomCheckBox currentCheckBox = (CustomCheckBox) checkBoxes.get(i);
				currentCheckBox.setSelected(true);
			}
		}
	}
}

public class ExportCodeForPrintingContentPane extends edu.cmu.cs.stage3.swing.ContentPane {
	private edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool m_authoringTool;

	private javax.swing.JFileChooser m_pathFileChooser = new javax.swing.JFileChooser();

	private javax.swing.JTextField m_authorNameTextField = new javax.swing.JTextField();
	private javax.swing.JTextField m_pathTextField = new javax.swing.JTextField();
	private javax.swing.JPanel m_elementsToBeExportedPanel = new javax.swing.JPanel();

	private java.util.Vector m_okActionListeners = new java.util.Vector();
	private javax.swing.JButton m_exportButton = new javax.swing.JButton(Messages.getString("Export_Code"));
	private javax.swing.JButton m_cancelButton = new javax.swing.JButton(Messages.getString("Cancel"));

	public ExportCodeForPrintingContentPane(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		m_authoringTool = authoringTool;

		java.util.ArrayList extensions = new java.util.ArrayList();
		extensions.add(new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter("htm", "*.htm"));
		extensions.add(new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter("html", "*.html"));
		m_pathFileChooser.setFileFilter(new edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter(extensions, Messages.getString("Web_pages")));

		edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());
		String path = authoringToolConfig.getValue("directories.worldsDirectory");
		if (path != null) {
			final java.io.File dir = new java.io.File(path);
			if (dir != null && dir.exists() && dir.isDirectory()) {
				try {
					// m_pathFileChooser.setCurrentDirectory( dir );
					Runnable testRun = new Runnable() {
						public void run() {
							m_pathFileChooser.setCurrentDirectory(dir);
						}
					};
					if (!SwingUtilities.isEventDispatchThread()) {
						SwingUtilities.invokeAndWait(testRun);
					}
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					// for some reason this can potentially fail in jdk1.4.2_04
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		m_pathFileChooser.setDialogType(javax.swing.JFileChooser.OPEN_DIALOG);
		m_pathFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		m_pathFileChooser.setApproveButtonText(Messages.getString("Set_File"));

		javax.swing.JButton selectAllButton = new javax.swing.JButton(Messages.getString("Select_All"));
		selectAllButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				ExportCodeForPrintingContentPane.this.setAllSelected(true);
			}
		});

		javax.swing.JButton deselectAllButton = new javax.swing.JButton(Messages.getString("Deselect_All"));
		deselectAllButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				ExportCodeForPrintingContentPane.this.setAllSelected(false);
			}
		});

		m_authorNameTextField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				ExportCodeForPrintingContentPane.this.m_exportButton.doClick();
			}
		});

		m_pathTextField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				ExportCodeForPrintingContentPane.this.m_exportButton.doClick();
			}
		});

		javax.swing.JButton browseButton = new javax.swing.JButton(Messages.getString("Browse___"));
		browseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				ExportCodeForPrintingContentPane.this.handleBrowseButton();
			}
		});

		m_exportButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				ExportCodeForPrintingContentPane.this.handleExportButton();
			}
		});

		javax.swing.JScrollPane whatToPrintScrollPane = new javax.swing.JScrollPane(m_elementsToBeExportedPanel);
		m_elementsToBeExportedPanel.setLayout(new java.awt.GridBagLayout());
		m_elementsToBeExportedPanel.setBackground(java.awt.Color.white);		
		// int height = 200;
		// int width = edu.cmu.cs.stage3.math.GoldenRatio.getLongerSideLength(
		// height );
		// m_elementsToBeExportedPanel.setPreferredSize( new java.awt.Dimension(
		// width, height ) );

		javax.swing.JPanel selectPanel = new javax.swing.JPanel();
		selectPanel.setLayout(new java.awt.GridBagLayout());
		GridBagConstraints gbcSelect = new GridBagConstraints();
		gbcSelect.anchor = GridBagConstraints.NORTHWEST;
		gbcSelect.fill = GridBagConstraints.BOTH;
		gbcSelect.gridwidth = GridBagConstraints.REMAINDER;
		selectPanel.add(selectAllButton, gbcSelect);
		selectPanel.add(deselectAllButton, gbcSelect);
		gbcSelect.weighty = 1.0;
		selectPanel.add(new javax.swing.JLabel(), gbcSelect);

		javax.swing.JPanel pathPanel = new javax.swing.JPanel();
		pathPanel.setLayout(new java.awt.GridBagLayout());
		GridBagConstraints gbcPath = new GridBagConstraints();
		gbcPath.anchor = GridBagConstraints.NORTHWEST;
		gbcPath.fill = GridBagConstraints.BOTH;
		gbcPath.gridwidth = GridBagConstraints.RELATIVE;
		pathPanel.add(new javax.swing.JLabel(Messages.getString("Export_to_")), gbcPath);
		gbcPath.gridwidth = GridBagConstraints.REMAINDER;
		gbcPath.weightx = 1.0;
		pathPanel.add(m_pathTextField, gbcPath);
		gbcPath.weightx = 0.0;

		javax.swing.JPanel authorPanel = new javax.swing.JPanel();
		authorPanel.setLayout(new java.awt.GridBagLayout());
		GridBagConstraints gbcAuthor = new GridBagConstraints();
		gbcAuthor.anchor = GridBagConstraints.NORTHWEST;
		gbcAuthor.fill = GridBagConstraints.BOTH;
		gbcAuthor.gridwidth = GridBagConstraints.RELATIVE;
		authorPanel.add(new javax.swing.JLabel(Messages.getString("Author_s_name_")), gbcAuthor);
		gbcAuthor.gridwidth = GridBagConstraints.REMAINDER;
		gbcAuthor.weightx = 1.0;
		authorPanel.add(m_authorNameTextField, gbcAuthor);
		gbcAuthor.weightx = 0.0;

		javax.swing.JPanel okCancelPanel = new javax.swing.JPanel();
		okCancelPanel.setLayout(new java.awt.GridBagLayout());
		GridBagConstraints gbcOKCancel = new GridBagConstraints();
		gbcOKCancel.insets.left = 8;
		okCancelPanel.add(m_exportButton, gbcOKCancel);
		okCancelPanel.add(m_cancelButton, gbcOKCancel);

		setLayout(new java.awt.GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets.left = 8;
		gbc.insets.top = 8;
		gbc.insets.right = 8;

		gbc.gridwidth = GridBagConstraints.RELATIVE;
		add(new javax.swing.JLabel(Messages.getString("What_to_export_")), gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(new javax.swing.JLabel(), gbc);

		gbc.insets.top = 0;
		gbc.weighty = 1.0;
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.weightx = 1.0;
		add(whatToPrintScrollPane, gbc);
		gbc.weightx = 0.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(selectPanel, gbc);
		gbc.weighty = 0.0;

		gbc.insets.top = 8;

		gbc.gridwidth = GridBagConstraints.RELATIVE;
		add(pathPanel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(browseButton, gbc);

		gbc.gridwidth = GridBagConstraints.RELATIVE;
		add(authorPanel, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(new javax.swing.JLabel(), gbc);

		gbc.insets.bottom = 8;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(okCancelPanel, gbc);

		int height = 400;
		int width = edu.cmu.cs.stage3.math.GoldenRatio.getLongerSideLength(height);
		setPreferredSize(new java.awt.Dimension(width, height));
		
		whatToPrintScrollPane.setLayout(new javax.swing.ScrollPaneLayout() {	
			@Override
		   	public void layoutContainer(java.awt.Container parent) {
		   		javax.swing.JScrollPane scrollPane = (javax.swing.JScrollPane) parent;
		   		scrollPane.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
		   	       super.layoutContainer(parent);
		   	       scrollPane.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
		   	}
		});
	}

	public void preDialogShow(javax.swing.JDialog dialog) {
		super.preDialogShow(dialog);
		initialize("");
	}

	public void postDialogShow(javax.swing.JDialog dialog) {
		super.postDialogShow(dialog);
	}

	public String getTitle() {
		return Messages.getString("Export_to_HTML___");
	}

	private void fireOKActionListeners() {
		java.awt.event.ActionEvent e = new java.awt.event.ActionEvent(this, java.awt.event.ActionEvent.ACTION_PERFORMED, Messages.getString("OK"));
		for (int i = 0; i < m_okActionListeners.size(); i++) {
			java.awt.event.ActionListener l = (java.awt.event.ActionListener) m_okActionListeners.elementAt(i);
			l.actionPerformed(e);
		}
	}

	public void addOKActionListener(java.awt.event.ActionListener l) {
		m_okActionListeners.addElement(l);
	}

	public void removeOKActionListener(java.awt.event.ActionListener l) {
		m_okActionListeners.removeElement(l);
	}

	public void addCancelActionListener(java.awt.event.ActionListener l) {
		m_cancelButton.addActionListener(l);
	}

	public void removeCancelActionListener(java.awt.event.ActionListener l) {
		m_cancelButton.removeActionListener(l);
	}

	public void initialize(String authorName) {
		setAllSelected(true);
		m_authorNameTextField.setText(authorName);
		m_authorNameTextField.setName(authorName);

		// Aik Min
		edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());
		java.io.File file = new java.io.File(authoringToolConfig.getValue("directories.worldsDirectory"), getWorldName(m_authoringTool.getCurrentWorldLocation()) + ".html");
		m_pathTextField.setText(file.getAbsolutePath());

		edu.cmu.cs.stage3.alice.core.World world = m_authoringTool.getWorld();
		java.util.Vector objectsToEdit = new java.util.Vector();
		addObjectsToEdit(world, objectsToEdit);
		if (world != null) {
			for (int i = 0; i < world.sandboxes.size(); i++) {
				addObjectsToEdit((edu.cmu.cs.stage3.alice.core.Sandbox) world.sandboxes.get(i), objectsToEdit);
			}
			for (int i = 0; i < world.groups.size(); i++) {
				edu.cmu.cs.stage3.alice.core.Group o = (edu.cmu.cs.stage3.alice.core.Group) world.groups.get(i);
				for (int j = 0; j < o.size(); j++) {
					addObjectsToEdit((edu.cmu.cs.stage3.alice.core.Sandbox) o.getChildAt(j), objectsToEdit);
				}
			}
		}
		buildWhatToPrintPanel(objectsToEdit);
	}

	public java.io.File getFileToExportTo() {
		String path = m_pathTextField.getText();
		return new java.io.File(path);
	}

	private void setAllSelected(boolean isSelected) {
		for (int i = 0; i < m_elementsToBeExportedPanel.getComponentCount(); i++) {
			java.awt.Component componentI = m_elementsToBeExportedPanel.getComponent(i);
			if (componentI instanceof CustomCheckBox) {
				((CustomCheckBox) componentI).setSelected(isSelected);
			}
		}
	}

	private void addObjectsToEdit(edu.cmu.cs.stage3.alice.core.Sandbox sandbox, java.util.Vector toAddTo) {
		if (sandbox != null && (sandbox.behaviors.size() != 0 || sandbox.responses.size() != 0 || sandbox.questions.size() != 0)) {
			toAddTo.add(sandbox.name.getStringValue());
			for (int i = sandbox.behaviors.size() - 1; i >= 0; i--) {
				toAddTo.add(sandbox.behaviors.get(i));
			}
			for (int i = 0; i < sandbox.responses.size(); i++) {
				toAddTo.add(sandbox.responses.get(i));
			}
			for (int i = 0; i < sandbox.questions.size(); i++) {
				toAddTo.add(sandbox.questions.get(i));
			}
		}
	}

	protected void buildWhatToPrintPanel(java.util.Vector objectsToPrint) {
		m_elementsToBeExportedPanel.removeAll(); // Aik Min - need work
		CustomListButton currentButton = null;
		int count = 0;
		boolean isWorld = false;
		for (int i = 0; i < objectsToPrint.size(); i++) {
			Object currentObject = objectsToPrint.elementAt(i);
			javax.swing.JComponent toAdd = null;
			int leftIndent = 0;
			if (currentObject instanceof String) {
				currentButton = new CustomListButton();
				currentButton.setText(currentObject.toString());
				toAdd = currentButton;
				leftIndent = 0;
				if (currentButton.getText().equalsIgnoreCase("world")) {
					isWorld = true;
				} else {
					isWorld = false;
				}
			} else {
				// String checkBoxText = "";
				toAdd = new CustomCheckBox();
				if (currentButton != null) {
					currentButton.addCheckBox((CustomCheckBox) toAdd);
				}
				if (currentObject instanceof edu.cmu.cs.stage3.alice.core.Behavior) {
					javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(currentObject);
					if (!AikMin.isLTR()) 
						gui.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					
					((CustomCheckBox) toAdd).image = m_authoringTool.getJAliceFrame().getImageForComponent(gui);
					((CustomCheckBox) toAdd).gui = gui;
					((CustomCheckBox) toAdd).object = currentObject;
					toAdd.setOpaque(false);
					
				} else if (currentObject instanceof edu.cmu.cs.stage3.alice.core.responses.UserDefinedResponse) {
					edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype((edu.cmu.cs.stage3.alice.core.responses.UserDefinedResponse) currentObject);
					javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(callToUserDefinedResponsePrototype);
					if (!AikMin.isLTR()) 
						gui.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					
					((CustomCheckBox) toAdd).image = m_authoringTool.getJAliceFrame().getImageForComponent(gui);
					((CustomCheckBox) toAdd).gui = gui;
					((CustomCheckBox) toAdd).object = currentObject;
					toAdd.setOpaque(false);
				} else if (currentObject instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
					edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype((edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) currentObject);
					javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI(callToUserDefinedQuestionPrototype);
					if (!AikMin.isLTR()) 
						gui.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					
					((CustomCheckBox) toAdd).image = m_authoringTool.getJAliceFrame().getImageForComponent(gui);
					((CustomCheckBox) toAdd).gui = gui;
					((CustomCheckBox) toAdd).object = currentObject;
					toAdd.setOpaque(false);
				}
				if (isWorld) {
					((CustomCheckBox) toAdd).setSelected(true);
				} else {
					((CustomCheckBox) toAdd).setSelected(false);
				}
				((CustomCheckBox) toAdd).setIndex(i);
			}
			java.awt.Color bgColor = java.awt.Color.white;
			if (currentObject instanceof edu.cmu.cs.stage3.alice.core.Response) {
				bgColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedResponseEditor");
			} else if (currentObject instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) {
				bgColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedQuestionEditor");
			} else if (currentObject instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BasicBehaviorPanel) {
				bgColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("behavior");
			}
			// toAdd.setBorder(javax.swing.BorderFactory.createLineBorder(bgColor,
			// 2));
			toAdd.setBackground(bgColor);
			m_elementsToBeExportedPanel.add(toAdd, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new java.awt.Insets(0, leftIndent, 0, 0), 0, 0));
			count++;
		}
		m_elementsToBeExportedPanel.add(javax.swing.Box.createVerticalGlue(), new GridBagConstraints(0, count, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		m_elementsToBeExportedPanel.revalidate();
		m_elementsToBeExportedPanel.repaint();
		if (!AikMin.isLTR())
			applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
	}

	private void handleBrowseButton() {
		// todo extract directory from text editor and
		// m_pathFileChooser.setCurrentDirectory
		int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog(m_pathFileChooser, null);
		if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
			java.io.File file = m_pathFileChooser.getSelectedFile();
			if (file != null) {
				String path = file.getAbsolutePath();
				if (path.endsWith(".html") || path.endsWith(".htm")) {
					// pass
				} else {
					path += ".html";
				}
				m_pathTextField.setText(path);
			}
		}
	}

	private void handleWriteProblem(java.io.File file) {
		edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("Cannot_write_to___", file.getAbsolutePath()), Messages.getString("Cannot_write"), javax.swing.JOptionPane.ERROR_MESSAGE);
	}

	private void handleExportButton() {
		if (m_authorNameTextField.getText().length() == 0) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog(Messages.getString("You_must_enter_the_author_s_name_before_proceeding_"), Messages.getString("You_have_not_entered_the_author_s_name"), javax.swing.JOptionPane.ERROR_MESSAGE);
		} else {
			java.io.File file = getFileToExportTo();
			if (file.exists()) {
				int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(Messages.getString("You_are_about_to_save_over_an_existing_file__Are_you_sure_you_want_to_"), Messages.getString("Save_Over_Warning"), javax.swing.JOptionPane.YES_NO_OPTION);
				if (result == javax.swing.JOptionPane.YES_OPTION) {
					// pass
				} else {
					return;
				}
			} else {
				try {
					file.createNewFile();
				} catch (Throwable t) {
					handleWriteProblem(file);
					return;
				}
			}
			if (file.canWrite()) {
				fireOKActionListeners();
			} else {
				handleWriteProblem(file);
			}
		}
	}

	private static String getWorldName(java.io.File worldFile) {
		if (worldFile != null) {
			return edu.cmu.cs.stage3.io.FileUtilities.getBaseName(worldFile);
		}
		return Messages.getString("Unnamed_World");
	}

	private javax.swing.JComponent getComponentForObject(Object toFind) {
		for (int i = 0; i < m_elementsToBeExportedPanel.getComponentCount(); i++) {
			java.awt.Component c = m_elementsToBeExportedPanel.getComponent(i);
			if (c instanceof CustomCheckBox) {
				if (((CustomCheckBox) c).object == toFind) {
					return ((CustomCheckBox) c).gui;
				}
			}
		}
		return null;
	}

	public void getHTML(StringBuffer buffer, java.io.File worldFile, boolean addHeaderAndFooter, boolean addAuthor, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver) throws edu.cmu.cs.stage3.progress.ProgressCancelException {
		if (progressObserver != null) {
			progressObserver.progressBegin(m_elementsToBeExportedPanel.getComponentCount());
		}

		String worldName = getWorldName(worldFile);
		if (addHeaderAndFooter) {
			buffer.append("<html>\n<head>\n<title>" + worldName + "'s Code</title>\n</head>\n<body>\n");
		}

		buffer.append("<h1>" + worldName + "'s Code</h1>\n");
		if (addAuthor) {
			buffer.append("<h1> Created by: " + m_authorNameTextField.getText() + "</h1>\n");
		}
		boolean notOnBehaviorsYet = true;
		boolean notOnResponsesYet = true;
		boolean notOnQuestionsYet = true;
		String currentTitle = "";
		boolean anyItemsYet = false;
		for (int i = 0; i < m_elementsToBeExportedPanel.getComponentCount(); i++) {
			if (m_elementsToBeExportedPanel.getComponent(i) instanceof CustomListButton) {
				String name = ((CustomListButton) m_elementsToBeExportedPanel.getComponent(i)).getText();
				currentTitle = "<h2>" + name + "</h2>\n";
				anyItemsYet = false;
				notOnBehaviorsYet = true;
				notOnResponsesYet = true;
				notOnQuestionsYet = true;
			} else if (m_elementsToBeExportedPanel.getComponent(i) instanceof CustomCheckBox) {
				CustomCheckBox currentBox = (CustomCheckBox) m_elementsToBeExportedPanel.getComponent(i);
				if (currentBox.isSelected()) {
					if (!anyItemsYet) {
						buffer.append(currentTitle);
						anyItemsYet = true;
					}
					if (!(currentBox.object instanceof edu.cmu.cs.stage3.alice.core.Behavior)) {
						java.awt.Component currentEditor = m_authoringTool.getEditorForElement((edu.cmu.cs.stage3.alice.core.Element) currentBox.object);
						if (currentEditor instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor) {
							if (currentEditor instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ResponseEditor) {
								if (notOnResponsesYet) {
									buffer.append("<h3>Methods</h3>\n");
									notOnResponsesYet = false;
								}
							} else {
								if (notOnQuestionsYet) {
									String cappedQuestionString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING.substring(0, 1).toUpperCase() + edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING.substring(1);
									buffer.append("<h3>" + cappedQuestionString + "s</h3>\n");
									notOnQuestionsYet = false;
								}
							}
							buffer.append("<table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">\n");
							((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor) currentEditor).getHTML(buffer, true);
							buffer.append("\n</table>\n<br>\n<br>\n");
						}
					} else {
						if (notOnBehaviorsYet) {
							buffer.append("<h3>Events</h3>\n");
							notOnBehaviorsYet = false;
						}
						javax.swing.JComponent component = getComponentForObject(currentBox.object);
						if (component instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BasicBehaviorPanel) {
							edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BasicBehaviorPanel behaviorPanel = (edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BasicBehaviorPanel) component;
							buffer.append("<table  style=\"border-left: 1px solid #c0c0c0; border-top: 1px solid #c0c0c0; border-bottom: 1px solid #c0c0c0; border-right: 1px solid #c0c0c0\" cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">\n");
							(behaviorPanel).getHTML(buffer, true);
							buffer.append("\n</table>\n<br>\n<br>\n");
						}
					}
				}
			}
			if (progressObserver != null) {
				progressObserver.progressUpdate(i, null);
			}
		}
		if (addHeaderAndFooter) {
			buffer.append("</body>\n</html>\n");
		}
		if (progressObserver != null) {
			progressObserver.progressEnd();
		}
	}
}
