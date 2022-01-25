package com.kazurayam.ks.visualinspection

import com.google.gson.Gson
import com.google.gson.GsonBuilder

public class ResponseInspected {

	private final Integer status
	private final URL url
	private final String mimeType

	ResponseInspected(Integer status, URL url, String mimeType) {
		this.status = status
		this.url = url
		this.mimeType = mimeType
	}

	Integer getStatus() {
		return this.status
	}

	URL getUrl() {
		return this.url
	}

	String getMimeType() {
		return this.mimeType
	}

	@Override
	String toString() {
		Map<String,String> m = new HashMap<>()
		m.put("status", Integer.toString(getStatus()))
		m.put("url", getUrl().toString())
		m.put("mime-type", getMimeType())
		Gson gson = new GsonBuilder().setPrettyPrinting().create()
		return gson.toJson(m)
	}
}
