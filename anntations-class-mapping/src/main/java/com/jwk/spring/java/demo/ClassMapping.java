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
import com.jwk.spring.java.demo.utils.RefectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Qualifier("ClassMapping")
@Component
public class ClassMapping implements Mapping {

	@Autowired
	ApplicationContext applicationContext;
	
	@Override
	public Object map(Class<?> type, Object... objects) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		return map(type.newInstance(), type, objects);
	}
	
	private int getListIndex(Object... objects) {
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
		return index;
	}
	
	private boolean isListMapping(int index) {
		return index != -1;
	}
	
	private List mappingList(Collection<?> list, Class<?> type, Object... objects) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		List result = new ArrayList<>();
		for(Object o : list) {
			Object ret = map(type, o);
			for(Object object : objects) {
				ret = mapping(ret, type, object);	
			}
			result.add(ret);
		}
		return result;
	}
	
	@Override
	public Object map(Object r, Class<?> type, Object... objects) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		Object ret = r;
		int count = 0; 
		int index = getListIndex(objects);
		
		if(isListMapping(index)) {
			return mappingList((Collection<?>) objects[index], type, objects);
		}
		for(Object object : objects) {
			ret = mapping(ret, type, object);	
		}
		return ret;
	}
	
	public List<?> mappingList(Collection objects, Class<?> type) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		List ret = new ArrayList<>();
		for(Object object: objects) {
			Object r = mapping(null, type, object);
			ret.add(r);
		}
		return ret;
	}
	
	public Object mapping(Object r, Class<?> type, Object object) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		if(object == null || object instanceof Collection) {
			return r;
		}
		Object ret = r;
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
				put(ret,type, object, field, classMap);
			}
		}
		return ret;
	}

	private boolean putDepthVaule(ClassMap classMap, Class<?> type, Object ret, Object r) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		String fieldKeys[] = classMap.value().split("\\.");
		if(fieldKeys.length > 1) {
			if(findField(fieldKeys, type)) {
				putDepthVaule(fieldKeys, type, ret, r);
			}else {
				log.warn("mapping faild");
			}
			return true;
		}
		return false;
		
	}

	
	private void put(Object r, Class<?> type,Object object, Field field, ClassMap classMap) throws ClassNotFoundException, InstantiationException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Formatter formatter = getFormatter(classMap.format());
		Field descField = null;
		
		Object ret = RefectionUtils.getFieldValue(field, object);
		if(Objects.isNull(ret)) {
			return;
		}
		if(putDepthVaule(classMap, type, ret, r)) {
			return;
		}
		
		descField = type.getDeclaredField(classMap.value());
		switch (classMap.type() ) {
		case NONE:
			RefectionUtils.setFieldValue(descField, r, formatter.format(ret));
			break;
		case LIST:
			valideteListMapMeta(descField, ret, classMap);
			Object value = mappingList((Collection)ret, getClass().getClassLoader().loadClass(classMap.listClassName()));
			RefectionUtils.setFieldValue(descField, r, value);
			break;
		case OBJECT:
			value = map(descField.getType() , ret);
			RefectionUtils.setFieldValue(descField, r, value);
			break;
		default:
			break;
		}
	}

	
	private void valideteListMapMeta(Field descField, Object ret, ClassMap classMap) {
		if(!descField.getType().isAssignableFrom(List.class) || !(ret instanceof Collection) || classMap.listClassName().isEmpty()) {
			throw new RuntimeException("리스트 매핑 설정이 맞지 않습니다.");
		}
	}

	private boolean isLast(int i, int len) {
		return i+1 >= len;
	}
	private void putDepthVaule(String[] fieldKeys, Class<?> type, Object value, Object obj) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Class<?> nowType = type; 
		Object nowObj = obj; 
		for(int i = 0; i< fieldKeys.length ; i++) {
			Field f = findField(fieldKeys[i], nowType);
			if(!isLast(i, fieldKeys.length)) {
				Object temp = RefectionUtils.getFieldValue(f, nowObj);
				if(Objects.isNull(temp)) {
					temp = f.getType().newInstance();
					RefectionUtils.setFieldValue(f, nowObj, temp);
				}
				nowObj = temp;
			}else {
				RefectionUtils.setFieldValue(f, nowObj, value);
			}
		}
	}

	private boolean findField(String[] fieldKeys, Class<?> type) {
		Class<?> nowType = type; 
		for(String key : fieldKeys) {
			nowType = findField(key, nowType).getType();
			if(nowType == null)
				return false;
		}
		return true;
	}
	
	private Field findField(String key, Class<?> type) {
		
		try {
			Field f = type.getDeclaredField(key);
			if(Objects.isNull(f)) {
				return f;
			}
			return f;
		} catch (NoSuchFieldException | SecurityException e) {
			return null;
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
