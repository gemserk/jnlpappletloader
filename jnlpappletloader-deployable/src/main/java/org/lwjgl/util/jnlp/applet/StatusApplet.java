package org.lwjgl.util.jnlp.applet;

import java.applet.Applet;
import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class StatusApplet extends Applet {

	private static final long serialVersionUID = -4497910610619789813L;
	
	@Override
	public void init() {
		super.init();
		
		String html = "<div>This applet shows that JNLP Applet Loader works correctly</div>";
		
		setLayout(new BorderLayout());
		
		add(new JEditorPane("text/html", html) {
			private static final long serialVersionUID = -2344915756882112715L;
			{
				setEditable(false);

				addHyperlinkListener(new HyperlinkListener() {

					public void hyperlinkUpdate(HyperlinkEvent e) {
						if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
							System.out.println(e.getURL());
							getAppletContext().showDocument(e.getURL());
						}
					}
				});

			}

		});
	}

}
