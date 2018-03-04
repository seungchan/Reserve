package com.example.reserve;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Duration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

@Component
public class RateLimitFilter implements javax.servlet.Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {	
	}
	
	// Creating a bucket that refill 1 per minute
	private Bucket createNewBucket() {
        long overdraft = 1; 
        Refill refill = Refill.smooth(1, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(overdraft, refill);
        return Bucket4j.builder().addLimit(limit).build();
   }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);
        
        ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest(
				(HttpServletRequest) request);
		String body = IOUtils.toString(wrappedRequest.getReader()).toLowerCase();
		wrappedRequest.resetInputStream();
        
        if (isRateLimitable(httpRequest,body)) {
	        Bucket bucket = (Bucket) session.getAttribute("ratelimiter-" + "stateUpdate");
	        if (bucket == null) {
	        		bucket = createNewBucket();
	            session.setAttribute("ratelimiter-" + "stateUpdate", bucket);
	        } 
	        
	        if (bucket.tryConsume(1)) {
	        		System.out.println("bucket consumed......");
	        		chain.doFilter(wrappedRequest, response);
	        } else {
	            // limit is exceeded
	            //HttpServletResponse httpResponse = (HttpServletResponse) response;
	            httpResponse.setContentType("text/plain");
	            httpResponse.setStatus(429);
	            httpResponse.getWriter().append("Too many requests");
	        }
        } else {
        		chain.doFilter(wrappedRequest, response);
        }
	}
	
	private boolean isRateLimitable(HttpServletRequest request, String body) throws IOException {
		return "PUT".equalsIgnoreCase(request.getMethod()) && (body != null) && body.contains("state");
	}
	
	private static class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

		private byte[] rawData;
		private HttpServletRequest request;
		private ResettableServletInputStream servletStream;
		
		public ResettableStreamHttpServletRequest(HttpServletRequest request) {
			super(request);
			this.request = request;
			this.servletStream = new ResettableServletInputStream();
		}
		
		
		public void resetInputStream() {
			servletStream.stream = new ByteArrayInputStream(rawData);
		}
		
		@Override
		public ServletInputStream getInputStream() throws IOException {
			if (rawData == null) {
				rawData = IOUtils.toByteArray(this.request.getReader(), Charset.defaultCharset());
				servletStream.stream = new ByteArrayInputStream(rawData);
			}
			return servletStream;
		}
		
		@Override
		public BufferedReader getReader() throws IOException {
			if (rawData == null) {
				rawData = IOUtils.toByteArray(this.request.getReader(), Charset.defaultCharset());
				servletStream.stream = new ByteArrayInputStream(rawData);
			}
			return new BufferedReader(new InputStreamReader(servletStream));
		}
		
		
		private class ResettableServletInputStream extends ServletInputStream {
		
			private InputStream stream;
		
			@Override
			public int read() throws IOException {
				return stream.read();
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
			public void setReadListener(ReadListener listener) {
			}
		}
	}

	@Override
	public void destroy() {
	}
}
