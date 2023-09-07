package pollen.pollen_fetch.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pollen.pollen_fetch.domain.Oak;
import pollen.pollen_fetch.repository.OakRepository;
import pollen.pollen_fetch.repository.PineRepository;
import pollen.pollen_fetch.repository.WeedsRepository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class FetchService {

    @Value("${spring.service.secret_key}")
    private String SERVICEKEY;
    private final String CHARSET = "UTF-8";
    public List<String> codeList = new ArrayList<>();
    final String FILE_PATH = "src/main/resources/static/areacode.xlsx";

    @Autowired
    OakRepository oakRepository;

    @Autowired
    PineRepository pineRepository;

    @Autowired
    WeedsRepository weedsRepository;

    public void fetch() throws IOException, ParseException {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int month = now.getMonthValue();
        // 4~6월 => 소나무, 참나무
        if (4 <= month && month <= 6) {
            fetchOakPollen("time");
        }
        if (4 <= month && month <= 6) {
            fetchPinePollen("time");
        }
        // 8~10월 => 잡초류
        if (8 <= month && month <= 10) {
            fetchWeedsPollen("time");
        }
    }

    public void fetchOakPollen(String time) throws IOException, ParseException {
        for (String area : codeList) {
            String builtUrl = buildUrl("getOakPollenRiskIdxV3", area, time);
            JSONObject object = getJsonObject(builtUrl);
            Oak oak = new Oak(object.get("areaCode").toString(), Integer.parseInt(object.get("today").toString()), Integer.parseInt(object.get("tomorrow").toString()), Integer.parseInt(object.get("dayaftertomorrow").toString()));
            oakRepository.save(oak);
        }
    }

    public void fetchPinePollen(String time) throws IOException, ParseException {
        for (String area : codeList) {
            String builtUrl = buildUrl("getPinePollenRiskIdxV3", area, time);
            JSONObject object = getJsonObject(builtUrl);
            Oak oak = new Oak(object.get("areaCode").toString(), Integer.parseInt(object.get("today").toString()), Integer.parseInt(object.get("tomorrow").toString()), Integer.parseInt(object.get("dayaftertomorrow").toString()));
            oakRepository.save(oak);
        }
    }

    public void fetchWeedsPollen(String time) throws IOException, ParseException {
        for (String area : codeList) {
            String builtUrl = buildUrl("getWeedsPollenRiskIdxV3", area, time);
            JSONObject object = getJsonObject(builtUrl);
            Oak oak = new Oak(object.get("areaCode").toString(), Integer.parseInt(object.get("today").toString()), Integer.parseInt(object.get("tomorrow").toString()), Integer.parseInt(object.get("dayaftertomorrow").toString()));
            oakRepository.save(oak);
        }
    }

    public JSONObject getJsonObject(String builtUrl) throws IOException, ParseException {
        URL url = new URL(builtUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        JSONParser jsonParser = new JSONParser();
        JSONObject object = (JSONObject) jsonParser.parse(new InputStreamReader(conn.getInputStream(), CHARSET));
        conn.disconnect();
        return object;
    }

    public String buildUrl(String url, String areaNo, String time) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/HealthWthrIdxServiceV3/" + url); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", CHARSET) + "=" + SERVICEKEY); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", CHARSET) + "=" + URLEncoder.encode("1", CHARSET)); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", CHARSET) + "=" + URLEncoder.encode("10", CHARSET)); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType", CHARSET) + "=" + URLEncoder.encode("JSON", CHARSET)); /*요청자료형식(XML/JSON)*/
        urlBuilder.append("&" + URLEncoder.encode("areaNo", CHARSET) + "=" + URLEncoder.encode(areaNo, CHARSET)); /*서울지점*/
        urlBuilder.append("&" + URLEncoder.encode("time", CHARSET) + "=" + URLEncoder.encode(time, CHARSET));    /*시간*/

        return urlBuilder.toString();
    }

    @PostConstruct
    public void ReadAreaCodeService() throws InvalidFormatException, IOException {
        OPCPackage opcPackage = OPCPackage.open(new File(FILE_PATH));

        XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();
        for (int i = 1; i < rows; i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                XSSFCell cell = row.getCell(1);
                String code ="";
                switch (cell.getCellType()) {
                    case NUMERIC:
                        code = String.valueOf(cell.getNumericCellValue());
                        break;
                    case STRING:
                        code = cell.getStringCellValue().replaceAll(" ", "");
                        break;
                    case FORMULA:
                        code = cell.getCellFormula().replaceAll(" ", "");
                    default:
                        break;
                }

                codeList.add(code);
            }
        }
    }
}
