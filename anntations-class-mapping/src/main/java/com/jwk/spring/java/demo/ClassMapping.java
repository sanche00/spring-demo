package com.jwk.spring.java.demo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jwk.spring.java.demo.utils.RefectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Qualifier("ClassMapping")
@Component
public class ClassMapping implements Mapping {

	@Autowired
	ApplicationContext applicationContext;
	
	@Override
	public Object map(Class<?> type, Object... objects) {
		try {
			return map(type.newInstance(), type, objects);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("자동 매핑 실패");
		}
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
	
	private List mappingList(Collection<?> list, Class<?> type, Object... objects) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException, InvocationTargetException {
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
	public Object map(Object r, Class<?> type, Object... objects) {
		try {
			
			Object ret = r;
			int index = getListIndex(objects);
			
			if(isListMapping(index)) {
				return mappingList((Collection<?>) objects[index], type, objects);
			}
			for(Object object : objects) {
				ret = mapping(ret, type, object);	
			}
			return ret;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| NoSuchFieldException | SecurityException | InvocationTargetException e) {
			throw new RuntimeException("자동 매핑 실패");
		}
	}
	
	public List<?> mappingList(Collection objects, Class<?> type) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException, InvocationTargetException {
		List ret = new ArrayList<>();
		for(Object object: objects) {
			Object r = mapping(null, type, object);
			ret.add(r);
		}
		return ret;
	}
	
	public Object mapping(Object r, Class<?> type, Object object) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException, InvocationTargetException {
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
		
		Method methods[] = object.getClass().getDeclaredMethods();
		for(Method method: methods) {
			ClassMap[] maps = method.getAnnotationsByType(ClassMap.class);
			if(maps == null || maps.length == 0) {
				continue;
			}
			for(ClassMap classMap : maps) {
				if(!containsClasses(classMap.classes(), type)) {
					continue;
				}
				put(ret,type, object, method, classMap);
			}
		}
		return ret;
	}

	private void put(Object r, Class<?> type, Object object, Method method, ClassMap classMap) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchFieldException, SecurityException, ClassNotFoundException {
		if(method.getParameterCount() > 0) {
			log.warn("지원하지 않는 Method 처리 입니다. {} ", method.getName());
			return;
		}
		
		Formatter formatter = getFormatter(classMap.format());
		Object value = method.invoke(object, null);
		setDescValue(r, value, classMap, type, formatter);
		
	}
	


	private void setDescValue(Object r, Object value, ClassMap classMap, Class<?> type, Formatter formatter) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchFieldException, SecurityException, ClassNotFoundException, InvocationTargetException {
		if(Objects.isNull(value)) {
			return;
		}
		if(putDepthVaule(classMap, type, value, r)) {
			return;
		}
		
		Field descField = type.getDeclaredField(classMap.value());
		switch (classMap.type() ) {
		case NONE:
			RefectionUtils.setFieldValue(descField, r, formatter.format(value));
			break;
		case LIST:
			valideteListMapMeta(descField, value, classMap);
			Object valueList = mappingList((Collection)value, getClass().getClassLoader().loadClass(classMap.listClassName()));
			RefectionUtils.setFieldValue(descField, r, valueList);
			break;
		case OBJECT:
			Object valueObj = map(descField.getType() , value);
			RefectionUtils.setFieldValue(descField, r, valueObj);
			break;
		default:
			break;
		}
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

	
	private void put(Object r, Class<?> type,Object object, Field field, ClassMap classMap) throws ClassNotFoundException, InstantiationException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InvocationTargetException {
		Formatter formatter = getFormatter(classMap.format());
		Object value = RefectionUtils.getFieldValue(field, object);
		setDescValue(r, value, classMap, type, formatter);
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
