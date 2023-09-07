package pollen.pollen_fetch.service;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class FetchServiceTest {

    @Autowired
    FetchService fetchService;

    @Test
    void FetchServiceSuccessTest() throws IOException, ParseException {
        fetchService.fetch();
    }
}
