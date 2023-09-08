package pollen.pollen_fetch.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ReadAreaCodeServiceTest {

    @Autowired
    FetchService fetchService;

    @Test
    void  ReadAreaCodeServiceSuccess() {
        List<String> areaList = fetchService.areaList;

        Assertions.assertThat(areaList.size()).isEqualTo(269);
    }
}
