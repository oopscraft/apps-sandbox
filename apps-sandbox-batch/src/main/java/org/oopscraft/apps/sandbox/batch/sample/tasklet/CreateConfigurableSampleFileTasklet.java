package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.item.file.DelimiterFileItemWriter;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemWriter;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.ConfigurableSampleItemVo;
import org.oopscraft.apps.sandbox.batch.sample.vo.ConfigurableSampleVo;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleItemVo;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.springframework.batch.item.ExecutionContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Builder
public class CreateConfigurableSampleFileTasklet extends AbstractTasklet {

    private final long size;

    public enum FileType { TSV, FLD }

    private FileType fileType;

    private String filePath;

    /**
     * doExecute
     * @param batchContext
     * @param executionContext
     * @throws Exception
     */
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
        DelimiterFileItemWriter<SampleVo> fileItemWriter = createDelimiterFileItemWriterBuilder(SampleVo.class)
                .filePath(filePath)
                .withHeader(false)
                .build();
        try {
            fileItemWriter.open(executionContext);
            for (int i = 0; i < size; i++) {
                ConfigurableSampleVo sampleVo = createConfigurableSampleVo(i);
                fileItemWriter.write(sampleVo);
                for (int ii = 0; ii < 3; ii++) {
                    ConfigurableSampleItemVo sampleItemVo = createConfigurableSampleItemVo(sampleVo.getId(), ii);
                    fileItemWriter.write(sampleItemVo);
                }
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw e;
        }finally{
            fileItemWriter.close();
        }
    }

    /**
     * compareDelimiterFileToSampleDb
     * @param executionContext
     */
    public void createFixedLengthSampleFile(ExecutionContext executionContext) throws Exception {
        FixedLengthFileItemWriter<SampleVo> fileItemWriter = createFixedLengthFileItemWriterBuilder(SampleVo.class)
                .filePath(filePath)
                .withHeader(false)
                .build();
        try {
            fileItemWriter.open(executionContext);
            for(int i = 0; i < size; i++){
                ConfigurableSampleVo sampleVo = createConfigurableSampleVo(i);
                fileItemWriter.write(sampleVo);
                for (int ii = 0; ii < 3; ii++) {
                    ConfigurableSampleItemVo sampleItemVo = createConfigurableSampleItemVo(sampleVo.getId(), ii);
                    fileItemWriter.write(sampleItemVo);
                }
            }
        } finally{
            fileItemWriter.close();
        }
    }

    /**
     * create configurable sample vo
     * @param i
     * @return
     */
    private ConfigurableSampleVo createConfigurableSampleVo(int i) {
        Faker faker = new Faker(new Locale("ko"), new Random(i));
        return ConfigurableSampleVo.builder()
                .type("A")
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

    /**
     * create configurable sample item vo
     */
    private ConfigurableSampleItemVo createConfigurableSampleItemVo(String sampleId, int i) {
        Faker faker = new Faker(new Locale("ko"), new Random(i));
        return ConfigurableSampleItemVo.builder()
                .type("B")
                .sampleId(sampleId)
                .id(UUID.randomUUID().toString())
                .name(faker.name().lastName() + faker.name().firstName())
                .number(faker.number().numberBetween(-100,100))
                .localDate(LocalDate.now().plusDays(faker.number().numberBetween(-123,123)))
                .localDateTime(LocalDateTime.now().plusDays(faker.number().numberBetween(-123,123)))
                .build();
    }

}
