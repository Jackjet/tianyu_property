package com.vguang.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class MAPIHttpServletRequestWrapper extends HttpServletRequestWrapper {
	private final byte[] body; //报文

	public MAPIHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = request.getInputStream();
		
		byte[] buffer = new byte[1024];
		int len = -1;
		while((len = in.read(buffer)) != -1){
			out.write(buffer, 0, len);
		}
		out.close();
		in.close();
		body = out.toByteArray();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream bais = new ByteArrayInputStream(body);
		return new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				
			}
		};
	}
}
