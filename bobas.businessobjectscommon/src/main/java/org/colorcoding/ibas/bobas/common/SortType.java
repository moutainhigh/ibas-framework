package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 排序
 */
@XmlType(name = "SortType", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public enum SortType {

	/** 降序 */
	@Value("D")
	DESCENDING,
	/** 升序 */
	@Value("A")
	ASCENDING;

	public int getValue() {
		return this.ordinal();
	}

	public static SortType forValue(int value) {
		return values()[value];
	}
}
