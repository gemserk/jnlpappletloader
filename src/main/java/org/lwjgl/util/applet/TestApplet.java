package org.lwjgl.util.applet;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;

public class TestApplet extends Applet {

	private static final long serialVersionUID = 3820300919362340202L;

	// @Inject
	// private Something something;
	//
	// public TestApplet() {
	//		
	// System.setSecurityManager(null);
	//
	// final AnotherThing anotherThing = new AnotherThing();
	//
	// Injector injector = Guice.createInjector(new AbstractModule() {
	//
	// @Override
	// protected void configure() {
	//
	// bind(AnotherThing.class).toInstance(anotherThing);
	//
	// }
	// });
	//
	// injector.injectMembers(this);
	//
	// }

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
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.WHITE);
		g.drawString("APPLET TEST", 100, 100);
		// g.drawString(something.getAnotherThing().getMessage(), 100, 100);

	}

	// public static class Something {
	//
	// @Inject
	// private AnotherThing anotherThing;
	//
	// public AnotherThing getAnotherThing() {
	// return anotherThing;
	// }
	//
	// }
	//
	// public static class AnotherThing {
	//
	// String message = "APPLET TEST";
	//
	// public String getMessage() {
	// return message;
	// }
	//
	// }

}
