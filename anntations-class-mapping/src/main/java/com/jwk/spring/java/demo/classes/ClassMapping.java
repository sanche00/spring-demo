package com.jwk.spring.java.demo.classes;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jwk.spring.java.demo.Condition;
import com.jwk.spring.java.demo.Mapping;
import com.jwk.spring.java.demo.SPFormatter;
import com.jwk.spring.java.demo.SPIFException;
import com.jwk.spring.java.demo.utils.ReflectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Qualifier("ClassMapping")
@Component
public class ClassMapping implements Mapping {

	@Autowired
	private BeanFactory beanFactory;

	@Override
	public Object map(Class<?> type, Object... objects) {
		try {
			return map(type.newInstance(), type, objects);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SPIFException("데이터 자동 매핑 실패");
		}
	}

	private int getListIndex(Object... objects) {
		int count = 0;
		int index = -1;
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] instanceof Collection<?>) {
				count++;
				index = i;
			}
		}

		if (count > 1) {
			throw new SPIFException("지원 하지 않는 매핑 입력입니다.");
		}
		return index;
	}

	private boolean isListMapping(int index) {
		return index != -1;
	}

	private List mappingList(Collection<?> list, Class<?> type, Object... objects)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			NoSuchFieldException, SecurityException, InvocationTargetException {
		List result = new ArrayList<>();
		for (Object o : list) {
			Object ret = map(type, o);
			if (ret instanceof List && !((List) ret).isEmpty() && ((List) ret).get(0).getClass() == type) {
				result.addAll((List) ret);
				ret = null;
			}
			for (Object object : objects) {
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

			if (isListMapping(index)) {
				return mappingList((Collection<?>) objects[index], type, objects);
			}

			for (Object object : objects) {
				ret = mapping(ret, type, object);
			}
			return ret;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| NoSuchFieldException | SecurityException | InvocationTargetException e) {
			throw new SPIFException("자동 매핑 실패", e);
		}
	}

	public List<?> mappingList(Collection objects, Class<?> type) {
		List ret = new ArrayList<>();
		for (Object object : objects) {
			Object r = mapping(null, type, object);
			ret.add(r);
		}
		return ret;
	}

	public Object mapping(Object r, Class<?> type, Object object) {
		if (object == null || object instanceof Collection) {
			return r;
		}
		Object ret = r;
		try {
			if (ret == null) {
				ret = type.newInstance();
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SPIFException("자동 생성 실패", e);
		}
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			ClassMap[] maps = field.getAnnotationsByType(ClassMap.class);
			if (maps == null || maps.length == 0) {
				continue;
			}
			for (ClassMap classMap : maps) {
				if (!containsClasses(classMap.classes(), type)) {
					continue;
				}
				if(!condition(classMap, object, ret, type)) {
					continue;
				}
				put(ret, type, object, field, classMap);
			}
		}

		Method methods[] = object.getClass().getDeclaredMethods();
		for (Method method : methods) {
			ClassMap[] maps = method.getAnnotationsByType(ClassMap.class);
			if (maps == null || maps.length == 0) {
				continue;
			}
			for (ClassMap classMap : maps) {
				if (!containsClasses(classMap.classes(), type) || classMap.isReverseMethod()) {
					continue;
				}
				if(!condition(classMap, object, ret, type)) {
					continue;
				}
				put(ret, type, object, method, classMap);
			}
		}
		return ret;
	}

	private boolean condition(ClassMap classMap, Object object, Object retObj, Class<?> type) {
		boolean ret = true;
		if(!classMap.conditionMethod().isEmpty()) {
			ret = methodConditon(classMap, object, retObj ,type);
		}
		if(ret && classMap.conditonClass() != Condition.class) {
			ret = classConditon(classMap, object, retObj ,type);
		}
		return ret;
	}

	private boolean classConditon(ClassMap classMap, Object object, Object retObj, Class<?> type) {
		return true;
	}

	private boolean valueConditon(ClassMap classMap,  Object value) {
		return true;
	}

	private boolean methodConditon(ClassMap classMap, Object object, Object retObj, Class<?> type) {
		return true;
	}

	private void put(Object r, Class<?> type, Object object, Method method, ClassMap classMap) {
		if (method.getParameterCount() > 0) {
			log.warn("지원하지 않는 Method 처리 입니다. {} ", method.getName());
			return;
		}
		SPFormatter formatter = getFormatter(classMap.format());

		try {
			Object value = method.invoke(object, null);
			setDescValue(r, value, classMap, type, formatter);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new SPIFException("매핑 매소드 호출 실패", e);
		}

	}

	private void setDescValue(Object r, Object value, ClassMap classMap, Class<?> type, SPFormatter formatter) {
		if (Objects.isNull(value)) {
			return;
		}
		if (putDepthVaule(classMap, type, value, r)) {
			return;
		}

		try {
			Field descField = null;
			switch (classMap.type()) {
			case NONE:
				descField = type.getDeclaredField(classMap.value());
				if (!classMap.reversFmt()) {
					ReflectionUtils.setFieldValue(descField, r, formatter.format(value));
				} else {
					ReflectionUtils.setFieldValue(descField, r, formatter.parse(value));
				}
				break;
			case LIST:
				descField = type.getDeclaredField(classMap.value());
				valideteListMapMeta(descField, value, classMap);
				Object valueList = mappingList((Collection) value,
							getClass().getClassLoader().loadClass(classMap.listClassName()));
				ReflectionUtils.setFieldValue(descField, r, valueList);
				break;
			case OBJECT:

				if (classMap.value().equals(ClassMap.THIS)) {
					r = map(r, r.getClass(), value);
				} else {
					descField = type.getDeclaredField(classMap.value());
					Object valueObj = map(descField.getType(), value);
					ReflectionUtils.setFieldValue(descField, r, valueObj);
				}

				break;
			default:
				break;
			}
		} catch (NoSuchFieldException | SecurityException e) {
			throw new SPIFException("매핑 Field 추출 실패",e);
		} catch (ClassNotFoundException e) {
			throw new SPIFException("매핑 클래스 추출 실패",e);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new SPIFException("매핑 데이터 입력 실패",e);
		}
	}

	private boolean putDepthVaule(ClassMap classMap, Class<?> type, Object ret, Object r) {
		String fieldKeys[] = classMap.value().split("\\.");
		if (fieldKeys.length > 1) {
			if (findField(fieldKeys, type)) {
				putDepthVaule(fieldKeys, type, ret, r);
			} else {
				log.warn("mapping faild");
			}
			return true;
		}
		return false;

	}

	private void put(Object r, Class<?> type, Object object, Field field, ClassMap classMap) {
		SPFormatter formatter = getFormatter(classMap.format());
		Object value;
		try {
			value = ReflectionUtils.getFieldValue(field, object);
			if(classMap.conditionValue().length > 0 && !valueConditon(classMap, value)) {
				return ; 
			}
			setDescValue(r, value, classMap, type, formatter);
		} catch (IllegalArgumentException | IllegalAccessException e) {

		}
	}

	private void valideteListMapMeta(Field descField, Object ret, ClassMap classMap) {
		if (!descField.getType().isAssignableFrom(List.class) || !(ret instanceof Collection)
				|| classMap.listClassName().isEmpty()) {
			throw new RuntimeException("리스트 매핑 설정이 맞지 않습니다.");
		}
	}

	private boolean isLast(int i, int len) {
		return i + 1 >= len;
	}

	private void putDepthVaule(String[] fieldKeys, Class<?> type, Object value, Object obj) {
		Class<?> nowType = type;
		Object nowObj = obj;
		try {
			for (int i = 0; i < fieldKeys.length; i++) {
				Field f = findField(fieldKeys[i], nowType);
				if (!isLast(i, fieldKeys.length)) {
					Object temp;
					temp = ReflectionUtils.getFieldValue(f, nowObj);
					if (Objects.isNull(temp)) {
						temp = f.getType().newInstance();
						ReflectionUtils.setFieldValue(f, nowObj, temp);
					}
					nowObj = temp;
					nowType = nowObj.getClass();
				} else {
					ReflectionUtils.setFieldValue(f, nowObj, value);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new SPIFException("매핑 데이터 입력 실패", e);
		} catch (InstantiationException e) {
			throw new SPIFException("자동 생성 실패", e);
		}
	}

	private boolean findField(String[] fieldKeys, Class<?> type) {
		Class<?> nowType = type;
		for (String key : fieldKeys) {
			nowType = findField(key, nowType).getType();
			if (nowType == null)
				return false;
		}
		return true;
	}

	private Field findField(String key, Class<?> type) {

		try {
			Field f = type.getDeclaredField(key);
			if (Objects.isNull(f)) {
				return f;
			}
			return f;
		} catch (NoSuchFieldException | SecurityException e) {
			return null;
		}

	}

	private SPFormatter getFormatter(Class<? extends SPFormatter<?, ?>> format) {
		try {
			SPFormatter<?, ?> ret = null;
			try {
				if (Objects.isNull(beanFactory)) {
					ret = format.newInstance();
				} else {
					ret = beanFactory.getBean(format);
				}
			} catch (BeansException e) {
				log.warn("해당 Bean을 찾을수 없습니다. 자동 생성 시도 {}", format.getName());
				ret = format.newInstance();
			}
			return ret;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SPIFException("Formatter 생성 실패 : ", e);
		}
	}

	private <R> boolean containsClasses(Class<?>[] classes, Class<R> type) {
		for (Class<?> klass : classes) {
			if (klass == type) {
				return true;
			}
		}
		return false;
	}

}
