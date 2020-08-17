package com.jwk.spring.java.demo;

import java.lang.reflect.Field;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Qualifier("ClassMapping")
@Component
public class ClassMapping implements Mapping {

	@Autowired
	ApplicationContext applicationContext;
	
	@Override
	public <R> R map(R r, Class<R> type, Object... objects) {
		R ret = r;
		for(Object object : objects) {
			ret = map(ret, type, object);	
		}
		return ret;
	}
	
	public <R> R map(R r, Class<R> type, Object object) {
		R ret = r;
		try {
			if(ret == null) {
				ret = type.newInstance();
			}
			
			Field[] fields = object.getClass().getDeclaredFields();
			for(Field field: fields) {
				ClassMap[] maps = field.getAnnotationsByType(ClassMap.class);
				if(maps == null || maps.length == 0) {
					continue;
				}
				for(ClassMap classMap : maps) {
					if(!containsClasses(classMap.classes(), type)) {
						continue;
					}
					log.info("find !!! : {}", field.getName());
					put(ret,type, object, field, classMap);
				}
			}
			
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private <R> void put(R r, Class<R> type,Object object, Field field, ClassMap classMap) {
		Formatter formatter = getFormatter(classMap.format());
		Field field2 = null;
		try {
			field.setAccessible(true);
			Object ret = field.get(object);
			field2 = type.getDeclaredField(classMap.value());
			field2.setAccessible(true);
			field2.set(r, formatter.format(ret));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}finally {
			field.setAccessible(false);
			if(field2 != null) {
				field2.setAccessible(false);
			}
		}
	}

	private Formatter getFormatter(Class<? extends Formatter<?, ?>> format) {
		return new DefaultFormatter();
	}

	private <R> boolean containsClasses(Class<?>[] classes, Class<R> type) {
		for(Class<?> klass : classes) {
			if(klass == type) {
				return true;
			}
		}
		return false;
	}
}
