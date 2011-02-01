package org.lwjgl.util.applet.tests;

import java.net.URL;
import java.net.URLConnection;

public class HttpRequestEncodingTest {

	public static void main(String[] args) throws Exception {
		
		URL url = new URL("http://acoppes-laptop.local/prototipos/discoverthename-test/animation-0.0.2-SNAPSHOT.jar");
		
		URLConnection connection = url.openConnection();
		
		connection.addRequestProperty("Accept-Encoding", "pack200-gzip, gzip");
		connection.connect();

		System.out.println(connection.getContentEncoding());
		
	}

}
