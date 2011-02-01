package org.lwjgl.util.applet;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;

public class TestApplet extends Applet {

	private static final long serialVersionUID = 3820300919362340202L;
	
	@Override
	public void start() {
		super.start();
	}
	
	@Override
	public void stop() {
		super.stop();
	}
	
	@Override
	public void paint(Graphics g) {
		
		super.paint(g);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 320, 240);
		
		g.setColor(Color.WHITE);
		g.drawString("APPLET TEST", 100, 100);
		
	}

}
