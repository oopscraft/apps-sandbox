package org.oopscraft.apps.sandbox.core.sample.dto;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSampleBackupEntity is a Querydsl query type for SampleBackupEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSampleBackupEntity extends EntityPathBase<SampleBackupEntity> {

    private static final long serialVersionUID = 203580795L;

    public static final QSampleBackupEntity sampleBackupEntity = new QSampleBackupEntity("sampleBackupEntity");

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

    public QSampleBackupEntity(String variable) {
        super(SampleBackupEntity.class, forVariable(variable));
    }

    public QSampleBackupEntity(Path<? extends SampleBackupEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSampleBackupEntity(PathMetadata metadata) {
        super(SampleBackupEntity.class, metadata);
    }

}

