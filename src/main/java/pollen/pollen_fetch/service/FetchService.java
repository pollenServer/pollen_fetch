package pollen.pollen_fetch.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pollen.pollen_fetch.domain.Oak;
import pollen.pollen_fetch.domain.Pine;
import pollen.pollen_fetch.domain.Weeds;
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
    public List<String> areaList = new ArrayList<>();
    final String FILE_PATH = "src/main/resources/static/areacode.xlsx";

    @Autowired
    OakRepository oakRepository;

    @Autowired
    PineRepository pineRepository;

    @Autowired
    WeedsRepository weedsRepository;

    //    @Scheduled(cron = "0 05 06 * * ?", zone = "Asia/Seoul")    // 매일 06시 05분 실행
    public void fetch() throws IOException, ParseException {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int month = now.getMonthValue();
        String time = now.toString().replaceAll("-", "").concat("06");
        // 4~6월 => 소나무, 참나무
        if (4 <= month && month <= 6) {
            fetchOakPollen(time);
        }
        if (4 <= month && month <= 6) {
            fetchPinePollen(time);
        }
        // 8~10월 => 잡초류
        if (8 <= month && month <= 10) {
            fetchWeedsPollen(time);
        }
    }

    public void fetchOakPollen(String time) throws IOException, ParseException {
        List<Oak> findAll = oakRepository.findAll();

        if (findAll.size() == 0) {
            for (String areaNo : areaList) {
                JSONObject result = getData("getOakPollenRiskndxV3", areaNo, time);
                if (result != null) {
                    Oak oak = new Oak(result.get("areaNo").toString(), Integer.parseInt(result.get("today").toString()), Integer.parseInt(result.get("tomorrow").toString()), Integer.parseInt(result.get("dayaftertomorrow").toString()));
                    oakRepository.save(oak);
                }
            }
        } else {
            for (Oak oak : findAll) {
                JSONObject result = getData("getOakPollenRiskndxV3", oak.getAreaNo(), time);
                if (result != null) {
                    oak.setToday(Integer.parseInt(result.get("today").toString()));
                    oak.setTomorrow(Integer.parseInt(result.get("tomorrow").toString()));
                    oak.setDayaftertomorrow(Integer.parseInt(result.get("dayaftertomorrow").toString()));
                }
            }
        }
    }

    public void fetchPinePollen(String time) throws IOException, ParseException {
        List<Pine> findAll = pineRepository.findAll();

        if (findAll.size() == 0) {
            for (String areaNo : areaList) {
                JSONObject result = getData("getPinePollenRiskndxV3", areaNo, time);
                if (result != null) {
                    Pine pine = new Pine(result.get("areaNo").toString(), Integer.parseInt(result.get("today").toString()), Integer.parseInt(result.get("tomorrow").toString()), Integer.parseInt(result.get("dayaftertomorrow").toString()));
                    pineRepository.save(pine);
                }
            }
        } else {
            for (Pine pine : findAll) {
                JSONObject result = getData("getPinePollenRiskndxV3", pine.getAreaNo(), time);
                if (result != null) {
                    pine.setToday(Integer.parseInt(result.get("today").toString()));
                    pine.setTomorrow(Integer.parseInt(result.get("tomorrow").toString()));
                    pine.setDayaftertomorrow(Integer.parseInt(result.get("dayaftertomorrow").toString()));
                }
            }
        }
    }

    public void fetchWeedsPollen(String time) throws IOException, ParseException {
        List<Weeds> findAll = weedsRepository.findAll();

        if (findAll.size() == 0) {
            for (String areaNo : areaList) {
                JSONObject result = getData("getWeedsPollenRiskndxV3", areaNo, time);
                if (result != null) {
                    Weeds weeds = new Weeds(result.get("areaNo").toString(), Integer.parseInt(result.get("today").toString()), Integer.parseInt(result.get("tomorrow").toString()), Integer.parseInt(result.get("dayaftertomorrow").toString()));
                    weedsRepository.save(weeds);
                }
            }
        } else {
            for (Weeds weeds : findAll) {
                JSONObject result = getData("getWeedsPollenRiskndxV3", weeds.getAreaNo(), time);
                if (result != null) {
                    weeds.setToday(Integer.parseInt(result.get("today").toString()));
                    weeds.setTomorrow(Integer.parseInt(result.get("tomorrow").toString()));
                    weeds.setDayaftertomorrow(Integer.parseInt(result.get("dayaftertomorrow").toString()));
                }
            }
        }
    }

    public JSONObject getData(String url, String areaNo, String time) throws IOException, ParseException {
        String builtUrl = buildUrl(url, areaNo, time);
        JSONObject object = getJsonObject(builtUrl);
        JSONObject response = (JSONObject) object.get("response");
        JSONObject header = (JSONObject) response.get("header");
        if (header.get("resultCode").equals("00")) {
            JSONObject body = (JSONObject) response.get("body");
            JSONObject items = (JSONObject) body.get("items");
            JSONArray item = (JSONArray) items.get("item");

            return (JSONObject) item.get(0);
        }
        return null;
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
                String areaNo = "";
                switch (cell.getCellType()) {
                    case NUMERIC:
                        areaNo = String.valueOf(cell.getNumericCellValue());
                        break;
                    case STRING:
                        areaNo = cell.getStringCellValue().replaceAll(" ", "");
                        break;
                    case FORMULA:
                        areaNo = cell.getCellFormula().replaceAll(" ", "");
                    default:
                        break;
                }

                areaList.add(areaNo);
            }
        }
        opcPackage.close();
    }
}
