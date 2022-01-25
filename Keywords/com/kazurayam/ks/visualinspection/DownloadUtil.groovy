package com.kazurayam.ks.visualinspection

import java.nio.file.Path

public class DownloadUtil {

		static long downloadWebResourceIntoFile(URL url, Path file) {
		long size = 0L
		int BUFFER_SIZE = 4096
		BufferedInputStream bis = new BufferedInputStream(url.openStream())
		FileOutputStream fos = new FileOutputStream(file.toFile())
		byte[] data = new byte[BUFFER_SIZE];
		int byteContent;
		while ((byteContent = bis.read(data,0, BUFFER_SIZE)) != -1) {
			fos.write(data, 0, byteContent)
			size += byteContent
		}
		return size
	}
	
}
