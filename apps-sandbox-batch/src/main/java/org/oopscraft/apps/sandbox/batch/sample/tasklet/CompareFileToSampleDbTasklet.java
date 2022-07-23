package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.item.file.DelimiterFileItemReader;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemReader;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleEntity;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneOffset;
import java.util.Optional;

@Slf4j
@Builder
public class CompareFileToSampleDbTasklet extends AbstractTasklet {

    public enum FileType { TSV, FLD }

    private FileType fileType;

    private String filePath;

    @Autowired
    private SampleRepository sampleRepository;

    @Override
    public void doExecute(BatchContext batchContext, ExecutionContext executionContext) throws Exception {
        switch(fileType){
            case TSV :
                compareDelimiterFileToDb(executionContext);
                break;
            case FLD :
                compareFixedLengthFileToDb(executionContext);
                break;
            default:
                throw new RuntimeException("invalid fileType");
        }
    }

    /**
     * compareDelimiterFileToSampleDb
     * @param executionContext
     */
    public void compareDelimiterFileToDb(ExecutionContext executionContext) throws Exception {
        DelimiterFileItemReader<SampleVo> sampleFileReader = null;
        try {
           sampleFileReader = createDelimiterFileItemReaderBuilder(SampleVo.class)
                   .filePath(filePath)
                   .build();
           sampleFileReader.open(executionContext);
            for(SampleVo sampleVo = sampleFileReader.read(); sampleVo != null; sampleVo = sampleFileReader.read()){
                SampleEntity sampleEntity = sampleRepository.findById(sampleVo.getId()).orElseThrow();
                compareSampleVoToSampleEntity(sampleVo, sampleEntity);
            }
        }finally{
            sampleFileReader.close();
        }
    }

    /**
     * compareDelimiterFileToSampleDb
     * @param executionContext
     */
    public void compareFixedLengthFileToDb(ExecutionContext executionContext) throws Exception {
        FixedLengthFileItemReader<SampleVo> sampleFileReader = null;
        try {
            sampleFileReader = createFixedLengthFileItemReaderBuilder(SampleVo.class)
                .filePath(filePath)
                .build();
            sampleFileReader.open(executionContext);
            for(SampleVo sampleVo = sampleFileReader.read(); sampleVo != null; sampleVo = sampleFileReader.read()){
                SampleEntity sampleEntity = sampleRepository.findById(sampleVo.getId()).orElseThrow();
                compareSampleVoToSampleEntity(sampleVo, sampleEntity);
            }
        } finally{
            sampleFileReader.close();
        }
    }

    /**
     * compareSampleVoToSampleEntity
     * @param sampleVo
     * @param sampleEntity
     */
    private void compareSampleVoToSampleEntity(SampleVo sampleVo, SampleEntity sampleEntity) {
        int result = new CompareToBuilder()
                .append(sampleVo.getId(), sampleEntity.getId())
                .append(sampleVo.getName(), sampleEntity.getName())
                .append(sampleVo.getNumber(), sampleEntity.getNumber())
                .append(sampleVo.getBigDecimal(), sampleEntity.getBigDecimal())
                .append(sampleVo.getLongNumber(), sampleEntity.getLongNumber())
                .append(sampleVo.getDoubleNumber(), sampleEntity.getDoubleNumber())
                .append(sampleVo.getSqlDate(), sampleEntity.getSqlDate())
                .append(Optional.ofNullable(sampleVo.getUtilDate()).map(v->v.toInstant().getEpochSecond()).orElse(null),
                        Optional.ofNullable(sampleEntity.getUtilDate()).map(v->v.toInstant().getEpochSecond()).orElse(null))
                .append(sampleVo.getLocalDate(), sampleEntity.getLocalDate())
                .append(Optional.ofNullable(sampleVo.getLocalDateTime()).map(v->v.toEpochSecond(ZoneOffset.UTC)).orElse(null),
                        Optional.ofNullable(sampleEntity.getLocalDateTime()).map(v->v.toEpochSecond(ZoneOffset.UTC)).orElse(null))
                .append(sampleVo.getLobText(), sampleEntity.getLobText())
                .append(sampleVo.getCryptoText(), sampleEntity.getCryptoText())
                .toComparison();
        if(result != 0) {
            log.info("{}", sampleVo);
            log.info("{}", sampleEntity);
            throw new RuntimeException("data mismatch");
        }
    }
}
