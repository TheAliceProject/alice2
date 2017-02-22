/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import edu.cmu.cs.stage3.lang.Messages;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

public class ImagePreview extends JComponent implements PropertyChangeListener {
	ImageIcon thumbnail = null;
	File file = null;
	int width = 0, height = 0;
	boolean needUpdating = false;

	public ImagePreview(JFileChooser fc) {
		setPreferredSize(new Dimension(300, 300));
		setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
		fc.addPropertyChangeListener(this);
		needUpdating = true;
	}
	
	public ImagePreview(JFileChooser fc, Image Icon) {
		setPreferredSize(new Dimension(300, 300));
		setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
		fc.addPropertyChangeListener(this);
		needUpdating = false;
		ImageIcon tmpIcon = new ImageIcon(Icon);
		height = tmpIcon.getIconHeight();
		width = tmpIcon.getIconWidth();
		if (tmpIcon != null) {
			if (tmpIcon.getIconWidth() > 200) {
				thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(
						200, -1, Image.SCALE_DEFAULT));
			} else { // no need to miniaturize
				thumbnail = tmpIcon;
			}
		}
		repaint();
	}
	
	public void loadImage() {
		if (file == null) {
			thumbnail = null;
			return;
		}

		ImageIcon tmpIcon = new ImageIcon(file.getPath());
		height = tmpIcon.getIconHeight();
		width = tmpIcon.getIconWidth();
		if (tmpIcon != null) {
			if (tmpIcon.getIconWidth() > 200) {
				thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(
						200, -1, Image.SCALE_DEFAULT));
			} else { // no need to miniaturize
				thumbnail = tmpIcon;
			}
		}
	}

	public void propertyChange(PropertyChangeEvent e) {
		boolean update = false;
		String prop = e.getPropertyName();

		// If the directory changed, don't show an image.
		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
			file = null;
			update = true;

			// If a file became selected, find out which one.
		} else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
			file = (File) e.getNewValue();
			update = true;
		}

		// Update the preview accordingly.
		if (update && needUpdating) {
			thumbnail = null;
			loadImage();
			repaint();
		}
	}

	protected void paintComponent(Graphics g) {
		if (thumbnail == null) {
			loadImage();
		}

		if (thumbnail != null) {
			int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
			int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;

			if (y < 60) {
				y = 60;
			}

			thumbnail.paintIcon(this, g, x, y);
			g.drawString(Messages.getString("Dimensions___", String.valueOf(width), String.valueOf(height)), 50, 30);

		}
	}
}
