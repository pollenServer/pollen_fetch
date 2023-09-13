package pollen.pollen_fetch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pollen.pollen_fetch.domain.Oak;
import pollen.pollen_fetch.domain.Pine;
import pollen.pollen_fetch.domain.Weeds;
import pollen.pollen_fetch.repository.OakRepository;
import pollen.pollen_fetch.repository.PineRepository;
import pollen.pollen_fetch.repository.WeedsRepository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FetchService {

    @Value("${spring.service.secret_key}")
    private String SERVICEKEY;
    private final String CHARSET = "UTF-8";
    public List<String> areaList = new ArrayList<>();
    final String FILE_PATH = "src/main/resources/static/areacode.xlsx";

    private final OakRepository oakRepository;
    private final PineRepository pineRepository;
    private final WeedsRepository weedsRepository;

    @Scheduled(cron = "0 05 06 * * ?", zone = "Asia/Seoul")    // 매일 06시 05분 실행
    public void fetch() throws IOException, ParseException {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int month = now.getMonthValue();
        String time = now.toString().replaceAll("-", "").concat("06");
        log.info("FetchService start time : {}", time);
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
        List<Oak> changedOak = new ArrayList<>();

        if (findAll.size() == 0) {
            for (String areaNo : areaList) {
                JSONObject result = getData("getOakPollenRiskndxV3", areaNo, time);
                Oak oak;
                if (result != null) {
                    if (result.get("today").toString().equals("")) {    // 전날 18시 데이터 응답 대비
                        oak = new Oak(result.get("areaNo").toString(), Integer.parseInt(result.get("tomorrow").toString()), Integer.parseInt(result.get("dayaftertomorrow").toString()), Integer.parseInt(result.get("twodaysaftertomorrow").toString()));
                    } else {
                        oak = new Oak(result.get("areaNo").toString(), Integer.parseInt(result.get("today").toString()), Integer.parseInt(result.get("tomorrow").toString()), Integer.parseInt(result.get("dayaftertomorrow").toString()));
                    }
                    changedOak.add(oak);
                } else {
                    oak = new Oak(areaNo);
                    changedOak.add(oak);
                }
                oakRepository.saveAll(changedOak);
            }
        } else {
            for (Oak oak : findAll) {
                JSONObject result = getData("getOakPollenRiskndxV3", oak.getAreaNo(), time);
                if (result != null) {
                    if (result.get("today").toString().equals("")) {    // 전날 18시 데이터 응답 대비
                        oak.setToday(Integer.parseInt(result.get("tomorrow").toString()));
                        oak.setTomorrow(Integer.parseInt(result.get("dayaftertomorrow").toString()));
                        oak.setDayaftertomorrow(Integer.parseInt(result.get("twodaysaftertomorrow").toString()));
                    } else {
                        oak.setToday(Integer.parseInt(result.get("today").toString()));
                        oak.setTomorrow(Integer.parseInt(result.get("tomorrow").toString()));
                        oak.setDayaftertomorrow(Integer.parseInt(result.get("dayaftertomorrow").toString()));
                    }
                }
            }
        }
    }

    public void fetchPinePollen(String time) throws IOException, ParseException {
        List<Pine> findAll = pineRepository.findAll();
        List<Pine> changedPine = new ArrayList<>();

        if (findAll.size() == 0) {
            for (String areaNo : areaList) {
                JSONObject result = getData("getPinePollenRiskndxV3", areaNo, time);
                Pine pine;
                if (result != null) {
                    if (result.get("today").toString().equals("")) {    // 전날 18시 데이터 응답 대비
                        pine = new Pine(result.get("areaNo").toString(), Integer.parseInt(result.get("tomorrow").toString()), Integer.parseInt(result.get("dayaftertomorrow").toString()), Integer.parseInt(result.get("twodaysaftertomorrow").toString()));
                    } else {
                        pine = new Pine(result.get("areaNo").toString(), Integer.parseInt(result.get("today").toString()), Integer.parseInt(result.get("tomorrow").toString()), Integer.parseInt(result.get("dayaftertomorrow").toString()));
                    }

                    changedPine.add(pine);
                } else {
                    pine = new Pine(areaNo);
                    changedPine.add(pine);
                }
                pineRepository.saveAll(changedPine);
            }
        } else {
            for (Pine pine : findAll) {
                JSONObject result = getData("getPinePollenRiskndxV3", pine.getAreaNo(), time);
                if (result != null) {
                    if (result.get("today").toString().equals("")) {    // 전날 18시 데이터 응답 대비
                        pine.setToday(Integer.parseInt(result.get("tomorrow").toString()));
                        pine.setTomorrow(Integer.parseInt(result.get("dayaftertomorrow").toString()));
                        pine.setDayaftertomorrow(Integer.parseInt(result.get("twodaysaftertomorrow").toString()));
                    } else {
                        pine.setToday(Integer.parseInt(result.get("today").toString()));
                        pine.setTomorrow(Integer.parseInt(result.get("tomorrow").toString()));
                        pine.setDayaftertomorrow(Integer.parseInt(result.get("dayaftertomorrow").toString()));
                    }
                }
            }
        }
    }

    public void fetchWeedsPollen(String time) throws IOException, ParseException {
        List<Weeds> findAll = weedsRepository.findAll();
        List<Weeds> changedWeeds = new ArrayList<>();

        if (findAll.size() == 0) {
            for (String areaNo : areaList) {
                JSONObject result = getData("getWeedsPollenRiskndxV3", areaNo, time);
                Weeds weeds;
                if (result != null) {
                    if (result.get("today").toString().equals("")) {    // 전날 18시 데이터 응답 대비
                        weeds = new Weeds(result.get("areaNo").toString(), Integer.parseInt(result.get("tomorrow").toString()), Integer.parseInt(result.get("dayaftertomorrow").toString()), Integer.parseInt(result.get("twodaysaftertomorrow").toString()));
                    } else {
                        weeds = new Weeds(result.get("areaNo").toString(), Integer.parseInt(result.get("today").toString()), Integer.parseInt(result.get("tomorrow").toString()), Integer.parseInt(result.get("dayaftertomorrow").toString()));
                    }

                    changedWeeds.add(weeds);
                } else {
                    weeds = new Weeds(areaNo);
                    changedWeeds.add(weeds);
                }
            }
            weedsRepository.saveAll(changedWeeds);
        } else {
            for (Weeds weeds : findAll) {
                JSONObject result = getData("getWeedsPollenRiskndxV3", weeds.getAreaNo(), time);
                if (result != null) {
                    if (result.get("today").toString().equals("")) {    // 전날 18시 데이터 응답 대비
                        weeds.setToday(Integer.parseInt(result.get("tomorrow").toString()));
                        weeds.setTomorrow(Integer.parseInt(result.get("dayaftertomorrow").toString()));
                        weeds.setDayaftertomorrow(Integer.parseInt(result.get("twodaysaftertomorrow").toString()));
                    } else {    // 당일 06시 데이터 응답
                        weeds.setToday(Integer.parseInt(result.get("today").toString()));
                        weeds.setTomorrow(Integer.parseInt(result.get("tomorrow").toString()));
                        weeds.setDayaftertomorrow(Integer.parseInt(result.get("dayaftertomorrow").toString()));
                    }
                }
            }
        }
    }

    public JSONObject getData(String url, String areaNo, String time) throws IOException, ParseException {
        String builtUrl = buildUrl(url, areaNo, time);
        JSONObject object = getJsonObject(builtUrl);
        if (object != null) {
            JSONObject response = (JSONObject) object.get("response");
            JSONObject header = (JSONObject) response.get("header");
            if (header.get("resultCode").equals("00")) {
                JSONObject body = (JSONObject) response.get("body");
                JSONObject items = (JSONObject) body.get("items");
                JSONArray item = (JSONArray) items.get("item");

                return (JSONObject) item.get(0);
            }
        }
        return null;
    }

    public JSONObject getJsonObject(String builtUrl) throws ParseException {
        try {
            URL url = new URL(builtUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json;utf-8");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), CHARSET));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                JSONParser jsonParser = new JSONParser();
                JSONObject object = (JSONObject) jsonParser.parse(sb.toString());
                conn.disconnect();

                return object;
            } else {
                conn.disconnect();
                return null;
            }
        } catch (IOException e) {
            return null;
        }
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
    public void ReadAreaCodeService() throws InvalidFormatException, IOException, ParseException {
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
        fetch();
    }
}
