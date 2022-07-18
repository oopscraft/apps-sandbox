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
@StaticMetamodel(SampleEntity.class)
public abstract class SampleEntity_ extends org.oopscraft.apps.core.data.BaseEntity_ {

	public static volatile SingularAttribute<SampleEntity, LocalDateTime> localDateTime;
	public static volatile SingularAttribute<SampleEntity, Date> utilDate;
	public static volatile SingularAttribute<SampleEntity, Long> longNumber;
	public static volatile SingularAttribute<SampleEntity, java.sql.Date> sqlDate;
	public static volatile SingularAttribute<SampleEntity, String> cryptoText;
	public static volatile SingularAttribute<SampleEntity, Integer> number;
	public static volatile SingularAttribute<SampleEntity, String> name;
	public static volatile SingularAttribute<SampleEntity, Double> doubleNumber;
	public static volatile SingularAttribute<SampleEntity, String> id;
	public static volatile SingularAttribute<SampleEntity, LocalDate> localDate;
	public static volatile SingularAttribute<SampleEntity, String> lobText;
	public static volatile SingularAttribute<SampleEntity, BigDecimal> bigDecimal;
	public static volatile SingularAttribute<SampleEntity, Timestamp> timestamp;

	public static final String LOCAL_DATE_TIME = "localDateTime";
	public static final String UTIL_DATE = "utilDate";
	public static final String LONG_NUMBER = "longNumber";
	public static final String SQL_DATE = "sqlDate";
	public static final String CRYPTO_TEXT = "cryptoText";
	public static final String NUMBER = "number";
	public static final String NAME = "name";
	public static final String DOUBLE_NUMBER = "doubleNumber";
	public static final String ID = "id";
	public static final String LOCAL_DATE = "localDate";
	public static final String LOB_TEXT = "lobText";
	public static final String BIG_DECIMAL = "bigDecimal";
	public static final String TIMESTAMP = "timestamp";

}

