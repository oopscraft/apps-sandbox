package org.oopscraft.apps.sandbox.core.sample.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SampleBackupEntity.class)
public abstract class SampleBackupEntity_ extends org.oopscraft.apps.core.data.BaseEntity_ {

	public static volatile SingularAttribute<SampleBackupEntity, LocalDateTime> localDateTime;
	public static volatile SingularAttribute<SampleBackupEntity, Integer> number;
	public static volatile SingularAttribute<SampleBackupEntity, String> data;
	public static volatile SingularAttribute<SampleBackupEntity, String> name;
	public static volatile SingularAttribute<SampleBackupEntity, Double> doubleNumber;
	public static volatile SingularAttribute<SampleBackupEntity, String> id;
	public static volatile SingularAttribute<SampleBackupEntity, Date> utilDate;
	public static volatile SingularAttribute<SampleBackupEntity, LocalDate> localDate;
	public static volatile SingularAttribute<SampleBackupEntity, BigDecimal> bigDecimal;
	public static volatile SingularAttribute<SampleBackupEntity, Long> longNumber;
	public static volatile SingularAttribute<SampleBackupEntity, java.sql.Date> sqlDate;
	public static volatile SingularAttribute<SampleBackupEntity, Timestamp> timestamp;

	public static final String LOCAL_DATE_TIME = "localDateTime";
	public static final String NUMBER = "number";
	public static final String DATA = "data";
	public static final String NAME = "name";
	public static final String DOUBLE_NUMBER = "doubleNumber";
	public static final String ID = "id";
	public static final String UTIL_DATE = "utilDate";
	public static final String LOCAL_DATE = "localDate";
	public static final String BIG_DECIMAL = "bigDecimal";
	public static final String LONG_NUMBER = "longNumber";
	public static final String SQL_DATE = "sqlDate";
	public static final String TIMESTAMP = "timestamp";

}

