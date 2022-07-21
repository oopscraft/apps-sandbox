package org.oopscraft.apps.sandbox.batch.bak;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DbToDbJob {

//    public enum ReaderType { MYBATIS, QUERY_DSL, QUERY_DSL_PROJECTION }
//
//    public enum WriterType { MYBATIS, JPA }
//
//    public int chunkSize = 10;
//
//    public int forceToFailCount = -1;
//
//    @Autowired
//    private SampleErrorRepository sampleErrorRepository;
//
//    @Override
//    public void initialize(BatchContext batchContext) {
//
//        // 입력 패라미터 정의
//        Assert.notNull(batchContext.getJobParameter("readerType"), "readerType must not be null");
//        Assert.notNull(batchContext.getJobParameter("writerType"), "writerType must not be null");
//        Assert.notNull(batchContext.getJobParameter("limit"), "limit must not be null");
//        ReaderType readerType = ReaderType.valueOf(batchContext.getJobParameter("readerType"));
//        WriterType writerType = WriterType.valueOf(batchContext.getJobParameter("writerType"));
//        int limit = Integer.parseInt(batchContext.getJobParameter("limit"));
//
//        // optional 조건
//        chunkSize = Optional.ofNullable(batchContext.getJobParameter("chunkSize")).map(value -> Integer.valueOf(value)).orElse(chunkSize);
//        forceToFailCount = Optional.ofNullable(batchContext.getJobParameter("forceToFailCount")).map(value -> Integer.valueOf(value)).orElse(forceToFailCount);
//
//        // 1. 샘플 데이터 초기화
//        addTasklet(CreateSampleTasklet.builder()
//                .limit(limit)
//                .build());
//
//        // 2-A. Mybatis reader -> Mybatis writer 테스트
//        if(readerType == ReaderType.MYBATIS && writerType == WriterType.MYBATIS) {
//            addStep(copySampleToBackupWithMybatisToMybatisStep(limit));
//        }
//
//        // 2-B. Mybatis reader -> jpa writer 테스트
//        if(readerType == ReaderType.MYBATIS && writerType == WriterType.JPA) {
//            addStep(copySampleToBackupWithMybatisToJpaStep(limit));
//        }
//
//        // 2-C. QueryDsl reader -> Mybatis writer 테스트
//        if(readerType == ReaderType.QUERY_DSL && writerType == WriterType.MYBATIS) {
//            addStep(copySampleToBackupWithQueryDslToMybatisStep(limit));
//        }
//
//        // 2-D. QueryDsl reader -> Jpa writer 테스트
//        if(readerType == ReaderType.QUERY_DSL && writerType == WriterType.JPA) {
//            addStep(copySampleToBackupWithQueryDslToJpaStep(limit));
//        }
//
//        // 2-E. QueryDslProjection -> Mybatis writer 테스트
//        if(readerType == ReaderType.QUERY_DSL_PROJECTION && writerType == WriterType.MYBATIS) {
//            addStep(copySampleToBackupWithQueryDslProjectionToMybatisStep(limit));
//        }
//
//        // 2-F. QueryDslProjection -> Jpa writer 테스트
//        if(readerType == ReaderType.QUERY_DSL_PROJECTION && writerType == WriterType.JPA) {
//            addStep(copySampleToBackupWithQueryDslProjectionToJpaStep(limit));
//        }
//
//        // 3. 결과 검증
//        addTasklet(CompareSampleToBackupTasklet.builder()
//                .build());
//
//    }
//
//    /**
//     * copySampleToBackupWithMybatisToMybatis
//     * Mybatis reader 로 조회 -> Mybatis writer 로 처리
//     * @return
//     */
//    public Step copySampleToBackupWithMybatisToMybatisStep(int limit) {
//         return stepBuilderFactory.get("copySampleToBackupWithMybatisToMybatis")
//                .<SampleVo, SampleBackupVo>chunk(chunkSize)
//                .reader(sampleReaderWithMybatis(limit))
//                .processor(convertSampleVoToBackupVoProcessor())
//                .writer(sampleBackupMybatisWriter())
//                .listener(forceToFailProcessListener())
//                .build();
//    }
//
//    /**
//     * copySampleToBackupWithMybatisToJpaStep
//     * Mybatis reader 로 조회 -> Jpa writer 로 처리
//     * @param limit
//     * @return
//     */
//    public Step copySampleToBackupWithMybatisToJpaStep(int limit) {
//        return stepBuilderFactory.get("copySampleToBackupWithMybatisToJpa")
//                .<SampleVo, SampleBackupEntity>chunk(chunkSize)
//                .reader(sampleReaderWithMybatis(limit))
//                .processor(convertSampleVoToBackupEntityProcessor())
//                .writer(sampleBackupJpaWriter())
//                .listener(forceToFailProcessListener())
//                .build();
//    }
//
//    /**
//     * copySampleToBackupWithQueryDslToMybatisStep
//     * QueryDsl reader 로 조회 -> Mybatis writer 로 처리
//     * @return
//     */
//    public Step copySampleToBackupWithQueryDslToMybatisStep(int limit) {
//        return stepBuilderFactory.get("copySampleToBackupWithQueryDslToMybatisStep")
//                .<SampleEntity, SampleBackupVo>chunk(chunkSize)
//                .reader(sampleReaderWithQueryDsl(limit))
//                .processor(convertSampleEntityToBackupVoProcessor())
//                .writer(sampleBackupMybatisWriter())
//                .listener(forceToFailProcessListener())
//                .build();
//    }
//
//    /**
//     * copySampleToBackupWithQueryDslToJpaStep
//     * QueryDsl reader 로 조회 -> Jpa writer 로 처리
//     * @return
//     */
//    public Step copySampleToBackupWithQueryDslToJpaStep(int limit) {
//        return stepBuilderFactory.get("copySampleToBackupWithQueryDslToMybatisStep")
//                .<SampleEntity, SampleBackupEntity>chunk(chunkSize)
//                .reader(sampleReaderWithQueryDsl(limit))
//                .processor(convertSampleEntityToBackupEntityProcessor())
//                .writer(sampleBackupJpaWriter())
//                .listener(forceToFailProcessListener())
//                .build();
//    }
//
//    /**
//     * copySampleToBackupWithQueryDslProjectionToMybatisStep
//     * QueryDsl Projection reader 로 조회 -> Mybatis writer 로 처리
//     * @return
//     */
//    public Step copySampleToBackupWithQueryDslProjectionToMybatisStep(int limit) {
//        return stepBuilderFactory.get("copySampleToBackupWithQueryDslProjectionToMybatisStep")
//                .<SampleVo, SampleBackupVo>chunk(chunkSize)
//                .reader(sampleReaderWithQueryDslProjection(limit))
//                .processor(convertSampleVoToBackupVoProcessor())
//                .writer(sampleBackupMybatisWriter())
//                .listener(forceToFailProcessListener())
//                .build();
//    }
//
//    /**
//     * copySampleToBackupWithQueryDslProjectionToJpaStep
//     * QueryDsl Projection reader 로 조회 -> Jpa writer 로 처리
//     * @return
//     */
//    public Step copySampleToBackupWithQueryDslProjectionToJpaStep(int limit) {
//        return stepBuilderFactory.get("copySampleToBackupWithQueryDslProjectionStep")
//                .<SampleVo, SampleBackupEntity>chunk(10)
//                .reader(sampleReaderWithQueryDslProjection(limit))
//                .processor(convertSampleVoToBackupEntityProcessor())
//                .writer(sampleBackupJpaWriter())
//                .build();
//    }
//
//    /**
//     * sampleReaderWithMybatis
//     * MYBATIS cursor item reader 객체 생성
//     * @return
//     */
//    public ItemReader<SampleVo> sampleReaderWithMybatis(int limit) {
//        return createMybatisDbItemReaderBuilder(SampleVo.class)
//                .mapperClass(MybatisReaderToJpaWriterMapper.class)
//                .mapperMethod("selectSamples")
//                .parameter("limit", limit)
//                .build();
//    }
//
//    /**
//     * sampleReaderWithQueryDsl
//     * Query DSL cursor item reader 객체 생성
//     * @return
//     */
//    public ItemReader<SampleEntity> sampleReaderWithQueryDsl(int limit) {
//
//        // Query
//        QSampleEntity qSampleEntity = QSampleEntity.sampleEntity;
//        JPAQuery<SampleEntity> query = jpaQueryFactory.select(qSampleEntity)
//                .from(qSampleEntity)
//                .limit(limit);
//
//        // creates query dsl reader
//        return createQueryDslDbItemReader(SampleEntity.class)
//                .query(query)
//                .build();
//    }
//
//    /**
//     * sampleReaderWithQueryDslProjection
//     * Query DSL Projection 으로 직접 Value Object binding 하는 reader 객체 생성
//     * @return
//     */
//    public ItemReader<SampleVo> sampleReaderWithQueryDslProjection(int limit) {
//
//        // Query
//        QSampleEntity qSampleEntity = QSampleEntity.sampleEntity;
//        JPAQuery<SampleVo> query = new JPAQuery<>();
//        query.select(Projections.fields(SampleVo.class,
//                        qSampleEntity.id,
//                        qSampleEntity.name,
//                        qSampleEntity.number,
//                        qSampleEntity.longNumber,
//                        qSampleEntity.doubleNumber,
//                        qSampleEntity.bigDecimal,
//                        qSampleEntity.sqlDate,
//                        qSampleEntity.utilDate,
//                        qSampleEntity.timestamp,
//                        qSampleEntity.localDateTime,
//                        qSampleEntity.localDate,
//                        qSampleEntity.lobText
//                )).from(qSampleEntity)
//                .limit(limit);
//
//        // creates query dsl reader
//        return createQueryDslDbItemReader(SampleVo.class)
//                .query(query)
//                .build();
//    }
//
//    /**
//     * convertSampleVoToBackupVoProcessor
//     * SampleVo 객체를 SampleBackupVo 객체로 변환
//     * @return
//     */
//    public ItemProcessor<SampleVo,SampleBackupVo> convertSampleVoToBackupVoProcessor() {
//        return SampleVo -> {
//            try {
//                log.debug("SampleVo:{}", SampleVo);
//                return SampleBackupVo.builder()
//                        .id(SampleVo.getId())
//                        .name(SampleVo.getName())
//                        .number(SampleVo.getNumber())
//                        .longNumber(SampleVo.getLongNumber())
//                        .doubleNumber(SampleVo.getDoubleNumber())
//                        .bigDecimal(SampleVo.getBigDecimal())
//                        .sqlDate(SampleVo.getSqlDate())
//                        .utilDate(SampleVo.getUtilDate())
//                        .timestamp(SampleVo.getTimestamp())
//                        .localDateTime(SampleVo.getLocalDateTime())
//                        .localDate(SampleVo.getLocalDate())
//                        .data(SampleVo.getData())
//                        .build();
//            }catch(Exception e){
//                log.warn(e.getMessage());
//                saveError(e.getMessage());
//                return null;
//            }
//        };
//    }
//
//    /**
//     * convertSampleVoToBackupEntityProcessor
//     * SampleVo 객체를 SampleBackupEntity 객체로 변환
//     * @return
//     */
//    public ItemProcessor<SampleVo,SampleBackupEntity> convertSampleVoToBackupEntityProcessor() {
//        return SampleVo -> {
//            log.debug("SampleVo:{}", SampleVo);
//            return SampleBackupEntity.builder()
//                    .id(SampleVo.getId())
//                    .name(SampleVo.getName())
//                    .number(SampleVo.getNumber())
//                    .longNumber(SampleVo.getLongNumber())
//                    .doubleNumber(SampleVo.getDoubleNumber())
//                    .bigDecimal(SampleVo.getBigDecimal())
//                    .sqlDate(SampleVo.getSqlDate())
//                    .utilDate(SampleVo.getUtilDate())
//                    .timestamp(SampleVo.getTimestamp())
//                    .localDateTime(SampleVo.getLocalDateTime())
//                    .localDate(SampleVo.getLocalDate())
//                    .lobText(SampleVo.getData())
//                    .build();
//        };
//    }
//
//    /**
//     * convertSampleEntityToBackupVoProcessor
//     * SampleEntity 객체를 SampleBackupVo 객체로 변환
//     * @return
//     */
//    public ItemProcessor<SampleEntity,SampleBackupVo> convertSampleEntityToBackupVoProcessor() {
//        return SampleEntity -> {
//            log.debug("SampleEntity:{}", SampleEntity);
//            return SampleBackupVo.builder()
//                    .id(SampleEntity.getId())
//                    .name(SampleEntity.getName())
//                    .number(SampleEntity.getNumber())
//                    .longNumber(SampleEntity.getLongNumber())
//                    .doubleNumber(SampleEntity.getDoubleNumber())
//                    .bigDecimal(SampleEntity.getBigDecimal())
//                    .sqlDate(SampleEntity.getSqlDate())
//                    .utilDate(SampleEntity.getUtilDate())
//                    .timestamp(SampleEntity.getTimestamp())
//                    .localDateTime(SampleEntity.getLocalDateTime())
//                    .localDate(SampleEntity.getLocalDate())
//                    .lobText(SampleEntity.getLobText())
//                    .build();
//        };
//    }
//
//    /**
//     * convertSampleEntityToBackupEntityProcessor
//     * SampleEntity 객체를 SampleBackupEntity 객체로 변환
//     * @return
//     */
//    public ItemProcessor<SampleEntity,SampleBackupEntity> convertSampleEntityToBackupEntityProcessor() {
//        return SampleEntity -> {
//            log.debug("SampleEntity:{}", SampleEntity);
//            return SampleBackupEntity.builder()
//                    .id(SampleEntity.getId())
//                    .name(SampleEntity.getName())
//                    .number(SampleEntity.getNumber())
//                    .longNumber(SampleEntity.getLongNumber())
//                    .doubleNumber(SampleEntity.getDoubleNumber())
//                    .bigDecimal(SampleEntity.getBigDecimal())
//                    .sqlDate(SampleEntity.getSqlDate())
//                    .utilDate(SampleEntity.getUtilDate())
//                    .timestamp(SampleEntity.getTimestamp())
//                    .localDateTime(SampleEntity.getLocalDateTime())
//                    .localDate(SampleEntity.getLocalDate())
//                    .data(SampleEntity.getData())
//                    .build();
//        };
//    }
//
//    /**
//     * writer
//     * Mybatis 데이터 입력하는 writer
//     * @return
//     */
//    public ItemWriter<SampleBackupVo> sampleBackupMybatisWriter() {
//        return createMybatisDbItemWriterBuilder(SampleBackupVo.class)
//                .mapperClass(SampleBackupMapper.class)
//                .mapperMethod("insertSampleBackup")
//                .build();
//    }
//
//    /**
//     * sampleBackupJpaWriter
//     * JPA 데이터 입력하는 writer
//     * @return
//     */
//    public ItemWriter<SampleBackupEntity> sampleBackupJpaWriter() {
//        return createJpaDbItemWriterBuilder(SampleBackupEntity.class)
//                .build();
//    }
//
//    /**
//     * forceToFail 테스트 check listener
//     * @return ItemProcessListener
//     */
//    public ItemProcessListener forceToFailProcessListener() {
//        return new ItemProcessListener() {
//            int processCount = 0;
//            @Override
//            public void beforeProcess(Object item) {
//                log.debug("beforeProcess.item:{}", item);
//                processCount ++;
//            }
//            @Override
//            public void afterProcess(Object item, Object result) {
//                log.debug("afterProcess.item:{}", item);
//                if(forceToFailCount > -1) {
//                    if(processCount >= forceToFailCount) {
//                        throw new RuntimeException("forceToFailCount:" + forceToFailCount);
//                    }
//                }
//                log.debug("afterProcess.result:{}", result);
//            }
//            @Override
//            public void onProcessError(Object item, Exception e) {
//                log.debug("onProcessError.e:{}", e.getMessage());
//            }
//        };
//    }
//
//    public void saveError(String message){
//        SampleErrorEntity sampleErrorEntity = SampleErrorEntity.builder()
//                .id(UUID.randomUUID().toString().replaceAll("-",""))
//                .name(message)
//                .build();
//        sampleErrorRepository.saveAndFlush(sampleErrorEntity);
//
//    }

}
