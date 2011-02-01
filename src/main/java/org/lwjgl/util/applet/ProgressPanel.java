package org.lwjgl.util.applet;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.swing.JPanel;

public class ProgressPanel extends JPanel {

	private static final long serialVersionUID = -4725699411709766202L;

	protected Image offscreen;

	/** logo to be shown while loading */
	protected Image logo, logoBuffer;

	/** progressbar to render while loading */
	protected Image progressBar, progressBarBuffer;

	protected Color bgColor = Color.WHITE;

	protected Color fgColor = Color.BLACK;

	protected Progress progress;

	public void setProgress(Progress progress) {
		this.progress = progress;
	}

	public void setLogo(Image logo) {
		this.logo = logo;
	}

	public void setProgressBar(Image progressbar) {
		this.progressBar = progressbar;
	}

	public void paint(Graphics g) {
		// create offscreen if missing

		if (offscreen == null) {
			offscreen = createImage(getWidth(), getHeight());

			// create buffers for animated gifs
			if (logo != null) {
				logoBuffer = createImage(logo.getWidth(null), logo.getHeight(null));
				// add image observer, it will notify when next animated gif frame is ready
				offscreen.getGraphics().drawImage(logo, 0, 0, this);
				// in case image is not animated fill image buffer once
				imageUpdate(logo, ImageObserver.FRAMEBITS, 0, 0, 0, 0);
			}

			if (progressBar != null) {
				progressBarBuffer = createImage(progressBar.getWidth(null), progressBar.getHeight(null));
				// add image observer, it will notify when next animated gif frame is ready
				offscreen.getGraphics().drawImage(progressBar, 0, 0, this);
				// in case image is not animated fill image buffer once
				imageUpdate(progressBar, ImageObserver.FRAMEBITS, 0, 0, 0, 0);
			}
		}

		// draw everything onto an image before drawing to avoid flicker
		Graphics og = offscreen.getGraphics();
		FontMetrics fm = og.getFontMetrics();

		// clear background color
		og.setColor(bgColor);
		og.fillRect(0, 0, offscreen.getWidth(null), offscreen.getHeight(null));

		og.setColor(fgColor);
		String message =  (int) progress.getPercentage() + "% - " + progress.getStatus();

		og.setColor(fgColor);

		// painting = true;

		// get position at the middle of the offscreen buffer
		int x = offscreen.getWidth(null) / 2;
		int y = offscreen.getHeight(null) / 2;

		// draw logo
		if (logo != null)
			og.drawImage(logoBuffer, x - logo.getWidth(null) / 2, y - logo.getHeight(null) / 2, this);

		// draw message
		int messageX = (offscreen.getWidth(null) - fm.stringWidth(message)) / 2;
		int messageY = y + 20;

		if (logo != null)
			messageY += logo.getHeight(null) / 2;
		else if (progressBar != null)
			messageY += progressBar.getHeight(null) / 2;

		og.drawString(message, messageX, messageY);

		// draw subtaskmessage, if any
		// if (subtaskMessage.length() > 0) {
		// messageX = (offscreen.getWidth(null) - fm.stringWidth(subtaskMessage)) / 2;
		// og.drawString(subtaskMessage, messageX, messageY + 20);
		// }

		// draw loading bar, clipping it depending on percentage done
		if (progressBar != null) {
			int barSize = (progressBar.getWidth(null) * (int) progress.getPercentage()) / 100;
			og.clipRect(x - progressBar.getWidth(null) / 2, 0, barSize, offscreen.getHeight(null));
			og.drawImage(progressBarBuffer, x - progressBar.getWidth(null) / 2, y - progressBar.getHeight(null) / 2, this);
		}

		// painting = false;

		og.dispose();

		// finally draw it all centred
		g.drawImage(offscreen, (getWidth() - offscreen.getWidth(null)) / 2, (getHeight() - offscreen.getHeight(null)) / 2, null);
	}

	/**
	 * When an animated gif frame is ready to be drawn the ImageObserver will call this method.
	 * 
	 * The Image frame is copied into a buffer, which is then drawn. This is done to prevent image tearing on gif animations.
	 */
	public boolean imageUpdate(Image img, int flag, int x, int y, int width, int height) {

		// if image frame is ready to be drawn and is currently not being painted
		if (flag == ImageObserver.FRAMEBITS) {
			Image buffer;

			// select which buffer to fill
			if (img == logo)
				buffer = logoBuffer;
			else
				buffer = progressBarBuffer;

			Graphics g = buffer.getGraphics();

			// clear background on buffer
			g.setColor(bgColor);
			g.fillRect(0, 0, buffer.getWidth(null), buffer.getHeight(null));

			// buffer background is cleared, so draw logo under progressbar
			if (img == progressBar && logo != null) {
				g.drawImage(logoBuffer, progressBar.getWidth(null) / 2 - logo.getWidth(null) / 2, progressBar.getHeight(null) / 2 - logo.getHeight(null) / 2, null);
			}

			g.drawImage(img, 0, 0, this);
			g.dispose();

			repaint();
		}

		return true;
	}

}
