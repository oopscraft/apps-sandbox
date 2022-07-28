package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.item.file.DelimiterFileItemWriter;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemReader;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemWriter;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.oopscraft.apps.sandbox.core.sample.entity.*;
import org.springframework.batch.item.ExecutionContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Builder
public class CreateSampleFileTasklet extends AbstractTasklet {

    private final long size;

    public enum FileType { TSV, FLD }

    private FileType fileType;

    private String filePath;

    @Override
    public void doExecute(BatchContext batchContext, ExecutionContext executionContext) throws Exception {
        switch(fileType){
            case TSV :
                createDelimiterSampleFile(executionContext);
                break;
            case FLD :
                createFixedLengthSampleFile(executionContext);
                break;
            default:
                throw new RuntimeException("invalid fileType");
        }
    }

    /**
     * createDelimiterSampleFile
     * @param executionContext
     */
    public void createDelimiterSampleFile(ExecutionContext executionContext) throws Exception {
        DelimiterFileItemWriter<SampleVo> sampleFileWriter = null;
        try {
            sampleFileWriter = createDelimiterFileItemWriterBuilder(SampleVo.class)
                    .filePath(filePath)
                    .build();
            sampleFileWriter.open(executionContext);
            for(int i = 0; i < size; i ++) {
                SampleVo sampleVo = createSampleVo(i);
                sampleFileWriter.write(sampleVo);
            }
        }finally{
            sampleFileWriter.close();
        }
    }

    /**
     * compareDelimiterFileToSampleDb
     * @param executionContext
     */
    public void createFixedLengthSampleFile(ExecutionContext executionContext) throws Exception {
        FixedLengthFileItemWriter<SampleVo> sampleFileWriter = null;
        try {
            sampleFileWriter = createFixedLengthFileItemWriterBuilder(SampleVo.class)
                    .filePath(filePath)
                    .build();
            sampleFileWriter.open(executionContext);
            for(int i = 0; i < size; i++){
                SampleVo sampleVo = createSampleVo(i);
                sampleFileWriter.write(sampleVo);
            }
        } finally{
            sampleFileWriter.close();
        }
    }

    /**
     * create sample vo
     * @param i
     * @return
     */
    private SampleVo createSampleVo(int i) {
        Faker faker = new Faker(new Locale("ko"), new Random(i));
        return SampleVo.builder()
                .id(UUID.randomUUID().toString())
                .name(faker.name().lastName() + faker.name().firstName())
                .number(faker.number().numberBetween(-100,100))
                .longNumber(faker.number().numberBetween(-1000_000_000,1000_000_000))
                .doubleNumber(faker.number().randomDouble(4,-100000,100000))
                .bigDecimal(BigDecimal.valueOf(faker.number().randomDouble(4,-1234567890, 1234567890)))
                .sqlDate(new java.sql.Date(System.currentTimeMillis() + faker.number().numberBetween(0,1234)))
                .utilDate(new java.util.Date(System.currentTimeMillis() + faker.number().numberBetween(0,1234)))
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis() + faker.number().numberBetween(0,1234)))
                .localDate(LocalDate.now().plusDays(faker.number().numberBetween(0,123)))
                .localDateTime(LocalDateTime.now().withNano(0).plusSeconds(faker.number().numberBetween(0,1234)))
                .lobText(faker.address().fullAddress())
                .cryptoText(faker.business().creditCardNumber())
                .build();
    }

}
