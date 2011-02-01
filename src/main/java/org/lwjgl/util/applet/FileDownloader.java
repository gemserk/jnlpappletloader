package org.lwjgl.util.applet;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileDownloader {

	public void download(InputStream in, OutputStream out) {

		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
				out.write(buf, 0, len);

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
			close(out);
		}

	}

	public void close(Closeable closeable) {
		if (closeable == null)
			return;
		try {
			closeable.close();
		} catch (IOException e) {

		}
	}

}
