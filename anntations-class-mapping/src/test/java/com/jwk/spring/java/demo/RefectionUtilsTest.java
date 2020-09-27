package com.jwk.spring.java.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.jwk.spring.java.demo.utils.ReflectionUtils;

public class RefectionUtilsTest {
	
	@Test
	public void setTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		TestPath path = new TestPath();

//		path.setId("test!!!!!!!!!");
//		path.setSeq(13);
//		
		ReflectionUtils.setFieldValue(path.getClass().getDeclaredField("id"), path, "TEST");
		ReflectionUtils.setFieldValue(path.getClass().getDeclaredField("seq"), path, 14);
		assertEquals("TEST", path.getId());
		assertEquals(14, path.getSeq());
	}
	
	@Test
	public void getTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		TestPath path = new TestPath();

		path.setId("test!!!!!!!!!");
		path.setSeq(13);
//		
		ReflectionUtils.getFieldValue(path.getClass().getDeclaredField("id"), path);
		ReflectionUtils.getFieldValue(path.getClass().getDeclaredField("seq"), path);
		assertEquals("test!!!!!!!!!",ReflectionUtils.getFieldValue(path.getClass().getDeclaredField("id"), path));
		assertEquals(13, ReflectionUtils.getFieldValue(path.getClass().getDeclaredField("seq"), path));
	}
}
