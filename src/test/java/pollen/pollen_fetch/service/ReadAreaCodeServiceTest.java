package pollen.pollen_fetch.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class ReadAreaCodeServiceTest {

    @Autowired
    FetchService fetchService;

    @Test
    void  ReadAreaCodeServiceSuccess() throws IOException, InvalidFormatException {
        List<String> codeList = fetchService.codeList;

        Assertions.assertThat(codeList.size()).isEqualTo(269);
    }
}
