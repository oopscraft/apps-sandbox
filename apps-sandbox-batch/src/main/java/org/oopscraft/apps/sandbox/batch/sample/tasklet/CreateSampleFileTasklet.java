package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return SampleVo.builder()
                .id(String.format("id-%d",i))
                .name(String.format("홍길동%s", i))
                .number(i)
                .longNumber((long)i)
                .doubleNumber(Double.valueOf(123.456 + 0.5))
                .bigDecimal(new BigDecimal(i).setScale(2))
                .sqlDate(new java.sql.Date(System.currentTimeMillis() + (1000*i)))
                .utilDate(new java.util.Date(System.currentTimeMillis() + (1000*i)))
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis() + (1000*i)))
                .localDate(LocalDate.now().plusDays(i))
                .localDateTime(LocalDateTime.now().withNano(0).plusSeconds(i))
                .lobText(String.format("가나다라동해물과백두산뷁읅읋흟갏궯ㄱㄴㄷㄹㄷㅄㅈ~~~%d",i))
                .cryptoText(String.format("가나다라동해물과백두산뷁읅읋흟갏궯ㄱㄴㄷㄹㄷㅄㅈ~~~%d",i))
                .build();
    }

}
