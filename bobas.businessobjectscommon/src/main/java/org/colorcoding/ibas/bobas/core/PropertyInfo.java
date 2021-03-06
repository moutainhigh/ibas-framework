package org.colorcoding.ibas.bobas.core;

import java.lang.annotation.Annotation;

import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;

public final class PropertyInfo<P> implements IPropertyInfo<P> {

	public final static String DEFALUT_NAME = "";

	public PropertyInfo(String name, Class<P> type) {
		this.setName(name);
		this.setValueType(type);
	}

	private String name = DEFALUT_NAME;

	public final String getName() {
		return this.name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	private int index = -1;

	public final int getIndex() {
		return this.index;
	}

	public final void setIndex(int value) {
		this.index = value;
	}

	private Class<P> valueType = null;

	public final Class<P> getValueType() {
		return this.valueType;
	}

	public final void setValueType(Class<P> value) {
		this.valueType = value;
	}

	private P defaultValue;

	public final P getDefaultValue() {
		return this.defaultValue;
	}

	public final void setDefaultValue(P value) {
		this.defaultValue = value;
	}

	private List<Annotation> annotations = null;

	@SuppressWarnings("unchecked")
	public final <A extends Annotation> A getAnnotation(Class<A> type) {
		if (this.annotations == null) {
			return null;
		}
		for (Annotation annotation : this.annotations) {
			if (annotation.annotationType().equals(type)) {
				return (A) annotation;
			}
		}
		return null;
	}

	public final void addAnnotation(Annotation item) {
		if (item == null) {
			return;
		}
		if (this.annotations == null) {
			this.annotations = new ArrayList<Annotation>();
		}
		this.annotations.add(item);
	}

	public final void addAnnotation(Annotation[] items) {
		if (items == null || items.length == 0) {
			return;
		}
		if (this.annotations == null) {
			this.annotations = new ArrayList<Annotation>(items.length);
		}
		for (Annotation item : items) {
			this.annotations.add(item);
		}
	}

	@Override
	public final String toString() {
		return String.format("{property info: %s}", this.getName());
	}
}
