package com.jwk.spring.java.demo.classes;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jwk.spring.java.demo.ReverseMapping;
import com.jwk.spring.java.demo.SPFormatter;
import com.jwk.spring.java.demo.SPIFException;
import com.jwk.spring.java.demo.utils.ReflectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClassReverseMapping implements ReverseMapping {

	@Autowired
	private BeanFactory beanFactory;

	@Override
	public void reverseMapping(Object src, Object... objects) {
		if(Objects.isNull(src)) {
			 throw new SPIFException("입력 값이 비어있습니다.");
		}

		for(Object object : objects) {
			if(Objects.isNull(object)) {
				continue;
			}

			putValue(src, object);
		}
	}

	private <R> boolean containsClasses(Class<?>[] classes, Class<R> type) {
		for(Class<?> klass : classes) {
			if(klass == type) {
				return true;
			}
		}
		return false;
	}

	private void putValue(Object src, Object object) {
		Class<?> type = src.getClass();
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
				putValue(src,type, object, field, classMap);
			}
		}
		Method methods[] = object.getClass().getDeclaredMethods();
		for(Method method: methods) {
			ClassMap[] maps = method.getAnnotationsByType(ClassMap.class);
			if(maps == null || maps.length == 0) {
				continue;
			}
			for(ClassMap classMap : maps) {
				if(!containsClasses(classMap.classes(), type) || !classMap.isReverseMethod()) {
					continue;
				}
				putValue(src, type, object, method, classMap);
			}
		}
	}
	private void putValue(Object src, Class<?> type, Object object, Method method, ClassMap classMap) {
		if(method.getParameterCount()  != 1 || !method.getParameterTypes()[0].isAssignableFrom(type)) {
			log.warn("지원하지 않는 Method 처리 입니다. {} ", method.getName());
			return;
		}
		try {
			method.invoke(object, src);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//			e.printStackTrace();
			throw new SPIFException("자동 매핑 처리 실패 : " + classMap.value());
		}

	}

	private void putValue(Object src, Class<?> type, Object object, Field field, ClassMap classMap) {

		SPFormatter formatter = null;
		try {
			formatter = getFormatter(classMap.format());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SPIFException("SPF0ormatter 생성 실패", e);
		}
		Object value = null;
		try {
			switch (classMap.type()) {
			case NONE:
				value = getValue(src, type, classMap.value());
				if(Objects.isNull(value)) {
					return;
				}
				ReflectionUtils.setFieldValue(field, object, formatter.parse(value));
				break;
			case LIST:
				if(!(value instanceof Collection<?>) || field.getType().isAssignableFrom(Collection.class)) {
					log.warn("list mapping 을 수행할수 없습니다.");
					break;
				}
				putValueList((Collection)value, object, field, classMap.reversListInputType());
				break;
			case OBJECT:
				if(classMap.value().equals(ClassMap.THIS)) {
					value = src;
				}else {
					value = getValue(src, type, classMap.value());
				}
				Object inObj = ReflectionUtils.getFieldValue(field, object);
				if(Objects.isNull(inObj)) {
					inObj = field.getType().newInstance();
					ReflectionUtils.setFieldValue(field, object, inObj);
				}
				reverseMapping(value, inObj);
				break;
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new SPIFException("데이터 자동 입력 실패 : " + field.getName(), e);
		} catch (InstantiationException e) {
			throw new SPIFException("데이터 자동 생성  실패 : " + field.getName(), e);
		}
	}

	private void putValueList(Collection values, Object object, Field field, Class<?> type) {
		try {
			if(type == Object.class) {
				return;
			}
			Collection list = new ArrayList();
			ReflectionUtils.setFieldValue(field, object, list);
			for(Object value : values) {
				Object temp = type.newInstance();
				reverseMapping(temp, value);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			log.warn("list mapping 을 수행할수 없습니다. 자동 생성 불가", e);
			return;
		}
	}

	private Object getValue(Object src, Class<?> type, String value) {
		Object obj = src;
		String fieldKeys[] = value.split("\\.");
		for(String key : fieldKeys) {
			try {
				Field field = type.getDeclaredField(key);
				obj = ReflectionUtils.getFieldValue(field, obj);
				if(Objects.isNull(obj)) {
					return obj;
				}
			} catch (NoSuchFieldException | SecurityException e) {
				throw new SPIFException("자동 매핑 key 설정이 잘못됐습니다. :" + value , e);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new SPIFException("자동 매핑 key 설정값을 얻어 올수 없습니다. :" + value, e);
			}
		}
		return obj;
	}

	private SPFormatter getFormatter(Class<? extends SPFormatter<?, ?>> format) throws InstantiationException, IllegalAccessException {
		SPFormatter<?,?> ret = null;
		try {
			if(Objects.isNull(beanFactory)) {
				ret = format.newInstance();
			}else {
				ret = beanFactory.getBean(format);
			}
		}catch (BeansException e) {
			log.warn("해당 Bean을 찾을수 없습니다. 자동 생성 시도 {}", format.getName());
			ret = format.newInstance();
		}
		return ret;
	}

}
