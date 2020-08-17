package com.jwk.spring.java.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.jwk.spring.java.demo.utils.RefectionUtils;

public class RefectionUtilsTest {
	
	@Test
	public void setTest() throws NoSuchFieldException, SecurityException {
		TestPath path = new TestPath();

//		path.setId("test!!!!!!!!!");
//		path.setSeq(13);
//		
		RefectionUtils.setFieldValue(path.getClass().getDeclaredField("id"), path, "TEST");
		RefectionUtils.setFieldValue(path.getClass().getDeclaredField("seq"), path, 14);
		assertEquals("TEST", path.getId());
		assertEquals(14, path.getSeq());
	}
	
	@Test
	public void getTest() throws NoSuchFieldException, SecurityException {
		TestPath path = new TestPath();

		path.setId("test!!!!!!!!!");
		path.setSeq(13);
//		
		RefectionUtils.getFieldValue(path.getClass().getDeclaredField("id"), path);
		RefectionUtils.getFieldValue(path.getClass().getDeclaredField("seq"), path);
		assertEquals("test!!!!!!!!!",RefectionUtils.getFieldValue(path.getClass().getDeclaredField("id"), path));
		assertEquals(13, RefectionUtils.getFieldValue(path.getClass().getDeclaredField("seq"), path));
	}
}
