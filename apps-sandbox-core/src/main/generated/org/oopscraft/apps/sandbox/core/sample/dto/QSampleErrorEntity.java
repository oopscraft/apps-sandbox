package org.oopscraft.apps.sandbox.core.sample.dto;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSampleErrorEntity is a Querydsl query type for SampleErrorEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSampleErrorEntity extends EntityPathBase<SampleErrorEntity> {

    private static final long serialVersionUID = 1084425237L;

    public static final QSampleErrorEntity sampleErrorEntity = new QSampleErrorEntity("sampleErrorEntity");

    public final org.oopscraft.apps.core.data.QBaseEntity _super = new org.oopscraft.apps.core.data.QBaseEntity(this);

    public final NumberPath<java.math.BigDecimal> bigDecimal = createNumber("bigDecimal", java.math.BigDecimal.class);

    public final StringPath data = createString("data");

    public final NumberPath<Double> doubleNumber = createNumber("doubleNumber", Double.class);

    public final StringPath id = createString("id");

    public final DatePath<java.time.LocalDate> localDate = createDate("localDate", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> localDateTime = createDateTime("localDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> longNumber = createNumber("longNumber", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDateTime = _super.modifyDateTime;

    //inherited
    public final StringPath modifyUserId = _super.modifyUserId;

    public final StringPath name = createString("name");

    public final NumberPath<Integer> number = createNumber("number", Integer.class);

    public final DatePath<java.sql.Date> sqlDate = createDate("sqlDate", java.sql.Date.class);

    //inherited
    public final BooleanPath systemData = _super.systemData;

    public final DateTimePath<java.sql.Timestamp> timestamp = createDateTime("timestamp", java.sql.Timestamp.class);

    public final DateTimePath<java.util.Date> utilDate = createDateTime("utilDate", java.util.Date.class);

    public QSampleErrorEntity(String variable) {
        super(SampleErrorEntity.class, forVariable(variable));
    }

    public QSampleErrorEntity(Path<? extends SampleErrorEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSampleErrorEntity(PathMetadata metadata) {
        super(SampleErrorEntity.class, metadata);
    }

}

