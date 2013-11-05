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

package edu.cmu.cs.stage3.alice.core.navigation;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Stage3
 * @author Ben Buchwald
 * @version 1.0
 */

public class KeyMapping extends edu.cmu.cs.stage3.alice.core.Element implements java.awt.event.KeyListener {

    public static final int NAV_DONOTHING = 0;
    public static final int NAV_MOVEFORWARD = 1;
    public static final int NAV_MOVEBACKWARD = 2;
    public static final int NAV_MOVELEFT = 4;
    public static final int NAV_MOVERIGHT = 8;
    public static final int NAV_MOVEUP = 16;
    public static final int NAV_MOVEDOWN = 32;
    public static final int NAV_TURNLEFT = 64;
    public static final int NAV_TURNRIGHT = 128;
    public static final int NAV_TURNUP = 256;
    public static final int NAV_TURNDOWN = 512;
    public static final int NAV_ROLLLEFT = 1024;
    public static final int NAV_ROLLRIGHT = 2048;
    //public static final int NAV_LOOKLEFT = 4096;
    //public static final int NAV_LOOKRIGHT = 8192;
    //public static final int NAV_LOOKUP = 16384;
    //public static final int NAV_LOOKDOWN = 32768;
    public static final int NAV_HEADSUP = 65536;
    public static final int NAV_STRAFE_MODIFIER = -1;

    public edu.cmu.cs.stage3.alice.core.property.IntArrayProperty keyFunction = new edu.cmu.cs.stage3.alice.core.property.IntArrayProperty(this, "keyFunction", new int[java.awt.event.KeyEvent.KEY_LAST]);

    private boolean[] keyState;
    private boolean strafing;

    public KeyMapping() {
        //keyFunction.set(new int[java.awt.event.KeyEvent.KEY_LAST]);
        keyState = new boolean[java.awt.event.KeyEvent.KEY_LAST];

        // default key mapping
        setFunction(java.awt.event.KeyEvent.VK_UP,NAV_MOVEFORWARD);
        setFunction(java.awt.event.KeyEvent.VK_DOWN,NAV_MOVEBACKWARD);
        //setFunction(java.awt.event.KeyEvent.VK_CLEAR,NAV_MOVEBACKWARD);
        setFunction(java.awt.event.KeyEvent.VK_LEFT,NAV_TURNLEFT);
        setFunction(java.awt.event.KeyEvent.VK_RIGHT,NAV_TURNRIGHT);
        //setFunction(java.awt.event.KeyEvent.VK_SHIFT,NAV_STRAFE_MODIFIER);
        //setFunction(107/* NUMPAD_PLUS */,NAV_LOOKUP);
        //setFunction(java.awt.event.KeyEvent.VK_ENTER,NAV_LOOKDOWN);
        //setFunction(109/* NUMPAD_MINUS */,NAV_HEADSUP);
        //setFunction(java.awt.event.KeyEvent.VK_PAGE_UP,NAV_MOVEUP);
        //setFunction(java.awt.event.KeyEvent.VK_PAGE_DOWN,NAV_MOVEDOWN);
    }

    public void setFunction(int key, int function) {
        int[] functions = keyFunction.getIntArrayValue();
        functions[key]=function;
        keyFunction.set(functions);
    }

    public int getActions() {
        int actions = 0;
        for (int i=0; i<keyState.length; i++)
            if (keyState[i] == true) {
                int val = keyFunction.getIntArrayValue()[i];
                if (strafing && val == NAV_TURNLEFT)
                    val = NAV_MOVELEFT;
                if (strafing && val == NAV_TURNRIGHT)
                    val = NAV_MOVERIGHT;
                if (strafing && val == NAV_TURNUP)
                    