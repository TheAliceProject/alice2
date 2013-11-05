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

package edu.cmu.cs.stage3.alice.authoringtool.editors.questioneditor;

import edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementEditor;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author David Culyba
 * @version 1.0
 */

public class MainCompositeQuestionPanel extends edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel{

    public ComponentQuestionPanel returnPanel;
    protected edu.cmu.cs.stage3.alice.core.question.userdefined.Return returnQuestion;
    protected javax.swing.JPanel questionArea;

    protected class MainCompositeComponentQuestionPanel extends CompositeComponentQuestionPanel{
		protected void updateGUI(){
            if (componentElements.size() > 1){
                this.removeAll();
                resetGUI();
                for (int i=0; i<componentElements.size(); i++){
                    edu.cmu.cs.stage3.alice.core.Element currentElement = (edu.cmu.cs.stage3.alice.core.Element)componentElements.getArrayValue()[i];
                    if (currentElement != returnQuestion){
                        java.awt.Component toAdd = makeGUI(currentElement);
                        if (toAdd != null){
                            addElementPanel(toAdd, i);
                        }
                    }
                }
            }
            else{
                addDropTrough();
            }
            this.revalidate();
            this.repaint();
        }

		protected boolean componentsIsEmpty(){
            return (componentElements.size() == 1);

        }

		protected int getLastElementLocation(){
            return componentElements.size()-1;
        }

		protected boolean checkGUI(){
            java.awt.Component c[] = this.getComponents();
            edu.cmu.cs.stage3.alice.core.Element elements[] = (edu.cmu.cs.stage3.alice.core.Element[])componentElements.get();
            int elementCount = getElementComponentCount();
            boolean aOkay = ((elements.length-1) == elementCount); //There's a return at the end we need to ignore
            if (aOkay){
                //Loops through the components and makes sure that component[i] == componentElement[i]
                for (int i=0; i<elements.length-1; i++){
                    if (c[i] instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel){
                        if (i < elements.length-1){
                            if (((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.CompositeElementPanel)c[i]).getElement() != elements[i]){
                                aOkay = false;
                                break;
                            }
                        }
                        else{
                            aOkay = false;
                            break;
                        }
                    }
                    if (c[i] instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.ComponentElementPanel){
                        if (i < elements.length-1){
                            if (((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.ComponentElementPanel)c[i]).getElement() != elements[i]){
                                aOkay = false;
                                break;
                            }
                        }
                        else{
                            aOkay = false;
                            break;
                        }
                    }
                }
            }
            return aOkay;
        }

		protected void addToElement(edu.cmu.cs.stage3.alice.core.Element toAdd, edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty toAddTo, int location){
            if (location < 0){
                super.addToElement(toAdd, toAddTo, componentElements.size()-1);
            }
            else{
                super.addToElement(toAdd, toAddTo, location);
            }
        }
    };

    public MainCompositeQuestionPanel(){
        super();
    }

	protected java.awt.Color getCustomBackgroundColor(){
        return edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("userDefinedQuestionEditor");
    }
	
	protected String getHeaderHTML(){
		String htmlToReturn = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(returnQuestion.valueClass.getClassValue())+" "+super.getHeaderHTML();
		return htmlToReturn;
	}

	public void getHTML(StringBuffer toWriteTo, int colSpan, boolean useColor){
		super.getHTML(toWriteTo, colSpan, useColor, false);
	}

	public void set(edu.cmu.cs.stage3.alice.core.Element question, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
        if (question instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion){
            edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion setQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion) question;
            if (setQuestion != null){
                if (setQuestion.components.size() > 0){
                    if (setQuestion.components.get(setQuestion.components.size()-1) instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Return){
                        returnQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.Return)setQuestion.components.get(setQuestion.components.size()-1);
                    }
                    else{
                        returnQuestion = new edu.cmu.cs.stage3.alice.core.question.userdefined.Return();
                        returnQuestion.valueClass.set(setQuestion.valueClass.get());
                        returnQuestion.value.set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDefaultValueForClass((Class)setQuestion.valueClass.get()));
                        returnQuestion.setParent(setQuestion);
           
                        setQuestion.components.add(setQuestion.components.size(), returnQuestion);
                    }
                }
                else{
                    returnQuestion = new edu.cmu.cs.stage3.alice.core.question.userdefined.Return();
                    returnQuestion.valueClass.set(setQuestion.valueClass.get());
					returnQuestion.value.set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDefaultValueForClass((Class)setQuestion.valueClass.get()));
                    returnQuestion.setParent(setQuestion);
                    setQuestion.components.add(0, returnQuestion);
                }
                returnPanel.set(returnQuestion);
                disableDrag(returnPanel);
            }
            super.set(question, authoringTool);
        }
        else{
            throw(new java.lang.IllegalArgumentException());
        }
    }

	protected void generateGUI(){
        super.generateGUI();
        returnPanel = new ComponentQuestionPanel();
        returnPanel.setDragEnabled(false);
        if (questionArea == null){
            questionArea = new javax.swing.JPanel();
            questionArea.setOpaque(true);
            questionArea.setBorder(null);
            questionArea.setLayout(