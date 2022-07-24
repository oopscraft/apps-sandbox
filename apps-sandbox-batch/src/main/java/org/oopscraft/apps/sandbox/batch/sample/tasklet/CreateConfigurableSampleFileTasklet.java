package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.item.file.DelimiterFileItemWriter;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemWriter;
import org.oopscraft.apps.batch.job.AbstractTasklet;
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
        return SampleVo.builder()
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

}
