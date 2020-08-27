package com.jwk.spring.java.demo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jwk.spring.java.demo.ClassMap.ClassType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Qualifier("ClassMapping")
@Component
public class ClassMapping implements Mapping {

	@Autowired
	ApplicationContext applicationContext;
	
	@Override
	public Object map(Object r, Class<?> type, Object... objects) throws ClassNotFoundException {
		Object ret = r;
		int count = 0; 
		int index = -1;
		for(int i = 0 ; i < objects.length ; i++)  {
			if(objects[i] instanceof Collection<?>) {
				count ++;
				index = i;
			}
		}
		
		if(count > 1) {
			throw new RuntimeException("지원 하지 않는 매핑 입력입니다.");
		}
		
		if(index == -1) {
			for(Object object : objects) {
				ret = mapz(ret, type, object);	
			}
			return ret;
		}else {
			Collection<?> list = (Collection<?>) objects[index];
			List result = new ArrayList<>();
			for(Object o:list) {
				ret = null;
				ret = map(ret, type, o);
				for(Object object : objects) {
					ret = mapz(ret, type, object);	
				}
				result.add(ret);
			}
			return result;
		}
	}
	
	public List<?> mapList(Collection objects, Class<?> type) throws ClassNotFoundException {
		List ret = new ArrayList<>();
		for(Object object: objects) {
			Object r = mapz(null, type, object);
			ret.add(r);
		}
		return ret;
	}
	
	public Object mapz(Object r, Class<?> type, Object object) throws ClassNotFoundException {
		if(object == null || object instanceof Collection) {
			return r;
		}
		
		Object ret = r;
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

	private void put(Object r, Class<?> type,Object object, Field field, ClassMap classMap) throws ClassNotFoundException {
		Formatter formatter = getFormatter(classMap.format());
		Field field2 = null;
		try {
			field.setAccessible(true);
			Object ret = field.get(object);
			if(Objects.isNull(ret)) {
				return;
			}
			field2 = type.getDeclaredField(classMap.value());
			if(classMap.type() == ClassType.NONE) {
				field2.setAccessible(true);
				field2.set(r, formatter.format(ret));
			}else if(classMap.type() == ClassType.LIST){
				if(!field2.getType().isAssignableFrom(List.class) || !(ret instanceof Collection) || classMap.listClassName().isEmpty()) {
					return ;
				}
				Object value = mapList((Collection)ret, getClass().getClassLoader().loadClass(classMap.listClassName()));
				field2.setAccessible(true);
				field2.set(r, value);
				
			}else if(classMap.type() == ClassType.OBJECT) {
				Object value = map(null, field2.getType() , ret);
				field2.setAccessible(true);
				field2.set(r, value);
			}
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
