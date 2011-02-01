package org.lwjgl.util.applet;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

public class FileUtils {

	/**
	 * Copies all data from input stream to the output stream.
	 */
	public void copy(InputStream in, OutputStream out) {
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

	/**
	 * Decompress all data from the input stream to the output stream uzin gzip.
	 */
	public void unzip(InputStream in, OutputStream out) {
		try {
			copy(new GZIPInputStream(in), out);
		} catch (IOException e) {
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
		} catch (IOException e) { }
	}

}
