package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
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
        return ConfigurableSampleVo.builder()
                .type("A")
                .id(String.format("id-%d",i))
                .name(String.format("홍길동%s", i))
                .number(i)
                .longNumber((long)i)
                .doubleNumber(123.456 + i)
                .bigDecimal(new BigDecimal(i))
                .sqlDate(new java.sql.Date(System.currentTimeMillis()))
                .utilDate(new java.util.Date())
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                .localDate(LocalDate.now())
                .localDateTime(LocalDateTime.now().withNano(0))
                .lobText(String.format("가나다라동해물과백두산뷁읅읋흟갏궯ㄱㄴㄷㄹㄷㅄㅈ~~~%d",i))
                .cryptoText(String.format("가나다라동해물과백두산뷁읅읋흟갏궯ㄱㄴㄷㄹㄷㅄㅈ~~~%d",i))
                .build();
    }

    /**
     * create configurable sample item vo
     */
    private ConfigurableSampleItemVo createConfigurableSampleItemVo(String sampleId, int i) {
        return ConfigurableSampleItemVo.builder()
                .type("B")
                .sampleId(sampleId)
                .id(String.format("id-%d", i))
                .name(String.format("name-%s", i))
                .number(123+i)
                .localDate(LocalDate.now())
                .localDateTime(LocalDateTime.now())
                .build();
    }

}
