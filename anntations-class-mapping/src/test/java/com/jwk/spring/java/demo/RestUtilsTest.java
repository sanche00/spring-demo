package com.jwk.spring.java.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.jwk.spring.java.demo.utils.RestUtils;

public class RestUtilsTest {

	@Test
	public void createPathTest() {
		TestPath path = new TestPath();
		path.setId("test!!!!!!!!!");
		path.setSeq(13);
		
		assertEquals("http://test.com/13/test!!!!!!!!!", RestUtils.createPath("http://test.com/{seq}/{id}", path));
	}
}
