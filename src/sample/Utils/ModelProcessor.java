package sample.Utils;

import javafx.scene.control.Alert;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Model.MyTableModel;
import sample.interfaces.FileProcessorInterface;
import sample.interfaces.JFXTableInterface;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ModelProcessor implements FileProcessorInterface, JFXTableInterface {

    public static final CharSequence XLSIDENTIFIERLINE = "[Content_Types].xml";
    public static final CharSequence ITEMSIDENTIFIERLINE = "Artikel:";
    public static final CharSequence PDFHEADER = "%PDF";
    public static final CharSequence CONSTRUCTIONSTAGEIDENTIFIERLINE = "Baustufe.:" ;
    public static final String INFO = "I";
    public static final int PDFNO = 1;
    public static final int PDFRAJZSZAM = 2;
    public static final int PDFDB = 3;
    public static final int PDFMERTEKEGYSEG = 4;
    public static final int PDFINFO = 5;
    public static final int XLSDB = 1;
    public static final int XLSRAJZSZAM = 2;
    public static final int TXTNO = 1;
    public static final int TXTRAJZSZAM = 2;
    public static final int TXTDB = 3;
    public static final int TXTMERTEKEGYSEG = 4;
    public static final int XLSDBOWNPRODUCTION = 1;
    public static final int XLSRAJZSZAMOWNPRODUCTION = 2;
    public static final int XLSDBCOMMERCIALPRODUCTION = 3;
    public static final int XLSRAJZSZAMCOMMERCIALPRODUCTION = 4;

    PdfProcessor pdfProcessor = new PdfProcessor();
    TxtProcessor txtProcessor = new TxtProcessor();
    XlsProcessor xlsProcessor = new XlsProcessor();
    List<Integer> differentLineNumbers = new ArrayList<>();
    List<String> status = new ArrayList<>();
    List<String> fDb= new ArrayList<>();
    List<String> fInfo= new ArrayList<>();
    List<String> fRajzszam= new ArrayList<>();
    List<String> fNo= new ArrayList<>();
    List<String> fMertekegyseg= new ArrayList<>();
    List<String> sDb= new ArrayList<>();
    List<String> sNo= new ArrayList<>();
    List<String> sMertekegyseg= new ArrayList<>();
    List<String> sRajzszam= new ArrayList<>();

    @Override
    public List<MyTableModel> fillTheModelList() {
        int largestListSize = findLargestListSize();
        fillStatusList(findErrorsInColumns());
        fillEmptyHoles(largestListSize);
        return fill(largestListSize);
    }

    public List<String> fillRawList(File file, String type) {

        List<String> result = new ArrayList<>();

        switch (type) {
            case "TXT" -> result = txt2list(file);

            case "XLSX" -> result = xls2list(file);

            case "PDF" -> result = pdf2list(file);
        }
        return result;
    }

    @Override
    public void fillFlists(List<String> rawList) {
        clearDataLists("f");
        if (pdfProcessor.fillLists(rawList, PDFNO).size() > 1) {
            fNo = pdfProcessor.fillLists(rawList, PDFNO);
            fDb = pdfProcessor.fillLists(rawList, PDFDB);
            fRajzszam = pdfProcessor.fillLists(rawList, PDFRAJZSZAM);
            fInfo = pdfProcessor.fillLists(rawList, PDFINFO);
            fMertekegyseg = pdfProcessor.fillLists(rawList, PDFMERTEKEGYSEG);
        } else {
            fDb = xlsProcessor.fillLists(rawList, XLSDB);
            fRajzszam = xlsProcessor.fillLists(rawList, XLSRAJZSZAM);
        }
    }

    @Override
    public void fillSlists(List<String> rawList) {
        clearDataLists("s");
        sNo = txtProcessor.fillLists(rawList, TXTNO);
        sDb = txtProcessor.fillLists(rawList, TXTDB);
        sMertekegyseg = txtProcessor.fillLists(rawList, TXTMERTEKEGYSEG);
        sRajzszam = txtProcessor.fillLists(rawList, TXTRAJZSZAM);
    }

    //<editor-fold desc="file validator methods">
    public boolean isValidPdf(File file) {
        boolean result=false;
        if (isItaPdfFile(file)) {
            if (isValidPdfContent(file)) {
                log("isValidPdf", "isValidPdf - it is a VALID pdf file.");
                result = true;

            }
            else {
                showInvalidFileFormatDialog();
                log("isValidPdf", "it NOT A VALID pdf file.");
            }
        }
        return result;
    }

    private boolean isValidPdfContent(File file) {
        PDFTextStripper stripper;
        String tempPdf;
        try {
            stripper = new PDFTextStripper();
            PDDocument doc = PDDocument.load(file);
            tempPdf = stripper.getText(doc);
            doc.close();
        } catch (IOException e) {
            tempPdf="";
            log("isValidPdfContent", "isValidPdfContent - IOException, " + e);
        }
        return tempPdf.contains(ITEMSIDENTIFIERLINE);
    }

    public boolean isItaPdfFile(File file) {
        String temp;
        boolean result = false;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            while ((temp = br.readLine()) != null) {
                if (temp.contains(PDFHEADER)) {
                    result = true;
                }
            }
        } catch (IOException e) {
            log("isItaPdfFile", "it's not a pdf file, " + e);
        }
        return result;
    }

    public boolean isValidXls(File file) {

        boolean result=false;
        if (isItaXlsFile(file)) {
            if(setCell(file).getStringCellValue().equals("Db szám")) {
                log("isValidXls", "ModelProcessor/isValidXls - It is a VALID excel file.");
                result = true;
            }
            else {
                log("isValidXls", "ModelProcessor/isValidXls - Non compatible excel fil.");
            }
        }
        return result;
    }

    private Cell setCell(File file) {
        FileInputStream fis;
        XSSFWorkbook wb;
        XSSFSheet sheet;
        Row row;
        Cell cell = null;
        try {
            fis = new FileInputStream(file);
            wb = new XSSFWorkbook(fis);
            sheet = wb.getSheetAt(0);
            row = sheet.getRow(0);
            cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        } catch (IOException e) {
            log("setCell", "ModelProcessor/setCell - IOException, "+e);
        }
        return cell;
    }

    public boolean isItaXlsFile(File file) {
        boolean result=false;
        String temp;

        try {
            BufferedReader br=new BufferedReader(new FileReader(file));
            while((temp=br.readLine())!=null) {
                if(temp.contains(XLSIDENTIFIERLINE)) {
                    result = true;
                }
            }
        } catch (IOException e) {
            log("isItaXlsFile", "IOException, "+e);
        }
        return result;
    }

    public boolean isValidTxt(File file) {

        BufferedReader reader;
        FileReader fileReader;
        boolean result = false;

        try {
            String temp;
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
            while ((temp=reader.readLine()) != null) {
                if (temp.contains(CONSTRUCTIONSTAGEIDENTIFIERLINE)) {
                    log("isValidTxt", "It is a VALID txt file.");
                    result = true;
                    break;
                }
            }
        }

        catch (IOException e) {
            log("isValidTxt", "IOException, "+e);
        }
        return result;
    }

    public String isValidFile(File file) {

        if (isValidPdf(file)) {
            return "PDF";
        }
        else if(isValidXls(file)) {
            return "XLSX";
        }
        else if(isValidTxt(file)) {
            return "TXT";
        }
        else {
            return "UNKNOWN";
        }
    }
    //</editor-fold>

    public void clearDataLists(String type) {
        if (type.equals("f")) {
            if (fDb != null) {  fDb.clear();   }
            if (fInfo!= null) {  fInfo.clear();  }
            if (fRajzszam != null) {  fRajzszam.clear();   }
            if (fNo != null) {  fNo.clear();   }
            if (fMertekegyseg != null) {  fMertekegyseg.clear();   }
        }
        if (type.equals("s")) {
            if (sDb != null) {  sDb.clear();  }
            if (sNo != null) {  sNo.clear();  }
            if (sMertekegyseg != null) {  sMertekegyseg.clear();  }
            if (sRajzszam != null) {  sRajzszam.clear();  }
            if (status != null) { status.clear();  }
        }
    }

    public int findLargestListSize() {

        int[] findMaxArray = {
                fDb.size(),
                fInfo.size(),
                fRajzszam.size(),
                fNo.size(),
                fMertekegyseg.size(),
                sDb.size(),
                sNo.size(),
                sMertekegyseg.size(),
                sRajzszam.size()
        };
        return Arrays.stream(findMaxArray).max().getAsInt();
    }

    private List<String> pdf2list(File file) {
        PDFTextStripper stripper;
        List<String> result = null;
        try {
            stripper = new PDFTextStripper();
            PDDocument doc = PDDocument.load(file);
            stripper.setSortByPosition(true);
            stripper.setWordSeparator("÷");
            String temp = stripper.getText(doc);
            result = new ArrayList<>(Arrays.asList(temp.split("\\n")));
        } catch (IOException e) {
            log("pdf2list", "IOException, "+e);
        }
        return result;
    }

    private List<String> xls2list(File file) {
        List<String> result = new ArrayList<>();
        FileInputStream fis;
        XSSFWorkbook wb;
        XSSFSheet sheet;
        try {
            fis = new FileInputStream(file);
            wb = new XSSFWorkbook(fis);
            sheet = wb.getSheetAt(0);
            Row row;
            Cell cell;

            for (int i = 0; i < sheet.getLastRowNum(); i++) {
                StringBuilder appendStr = new StringBuilder();
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                for (int j = 0; j < 4; j++) {
                    cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    addCellTypeFlags(cell, appendStr);
                }
                result.add(appendStr.toString());
            }
        } catch (IOException e) {
            log("xls2list", "IOException, "+e);
        }
        return result;
    }

    private List<String> txt2list(File file) {
        BufferedReader br;
        String temp;
        List<String> result = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(file));
            while ((temp = br.readLine()) != null) {
                result.add(temp);
            }
        } catch (IOException e) {
            log("txt2list", "IOException, "+e);
        }
        return result;
    }

    private void addCellTypeFlags(Cell cell, StringBuilder appendStr) {
        if (cell.getCellType() == CellType.BLANK) {
            appendStr.append("÷BLANK");
        } else if (cell.getCellType() == CellType._NONE) {
            appendStr.append("÷NONE");
        } else if (cell.getCellType() == CellType.NUMERIC) {
            appendStr.append("÷").append(NumberToTextConverter.toText(cell.getNumericCellValue()));
        } else if (cell.getCellType() == CellType.STRING) {
            appendStr.append("÷").append(cell.getStringCellValue());
        }
    }

    private List<MyTableModel> fill(int largestListSize) {
        List<MyTableModel> result = new ArrayList<>();
        for (int i = 0; i < largestListSize; i++) {
            result.add(new MyTableModel(
                    fNo.get(i),
                    fRajzszam.get(i),
                    fDb.get(i),
                    fMertekegyseg.get(i),
                    sRajzszam.get(i),
                    sNo.get(i),
                    sDb.get(i),
                    sMertekegyseg.get(i),
                    status.get(i),
                    fInfo.get(i)));
        }
        return result;
    }

    private void fillEmptyHoles(int largestListSize) {
        for (int i = fNo.size(); i < largestListSize; i++) {
            fNo.add("x");
        }

        for (int i = fRajzszam.size(); i < largestListSize; i++) {
            fRajzszam.add("x");
        }

        for (int i = fDb.size(); i < largestListSize; i++) {
            fDb.add("x");
        }

        for (int i = fMertekegyseg.size(); i < largestListSize; i++) {
            fMertekegyseg.add("x");
        }

        for (int i = sRajzszam.size(); i < largestListSize; i++) {
            sRajzszam.add("x");
        }

        for (int i = sNo.size(); i < largestListSize; i++) {
            sNo.add("x");
        }

        for (int i = sDb.size(); i < largestListSize; i++) {
            sDb.add("x");
        }

        for (int i = sMertekegyseg.size(); i < largestListSize; i++) {
            sMertekegyseg.add("x");
        }

        for (int i = fInfo.size(); i < largestListSize; i++) {
            fInfo.add("");
        }

        for (int i = status.size(); i < largestListSize; i++) {
            status.add("");
        }
    }

    private void fillStatusList(List<Integer> differentLineNumbers) {
        status.clear();
        for (int i = 0; i < findLargestListSize(); i++) {
            status.add("");
        }

        for (Integer differentLineNumber : differentLineNumbers) {
            status.add(differentLineNumber, "Error/Info");
        }
    }

    private List<Integer> findErrorsInColumns() {
        List<Integer> tempList;
        if (differentLineNumbers != null) {
            differentLineNumbers.clear();
        }
        int maxDbLength = setMaxDbLength();
        int maxRajzszamLength = setMaxRajzszamLength();
        checkDifferencesInDbLists(differentLineNumbers, maxDbLength);
        checkDifferencesInRajzszamLists(differentLineNumbers, maxRajzszamLength);
        checkDifferencesInInfoLists(differentLineNumbers);
        Collections.sort(differentLineNumbers);
        tempList = differentLineNumbers;

        return tempList.stream().distinct().collect(Collectors.toList());
    }

    private void checkDifferencesInInfoLists(List<Integer> differentLineNumbers) {
        for (int i = 0; i < fInfo.size(); i++) {
            if(fInfo.get(i).equals(INFO)) {
                differentLineNumbers.add(i);
            }
        }
    }

    private void checkDifferencesInRajzszamLists(List<Integer> differentLineNumbers, int maxDbLength) {
        for (int i = 0; i < maxDbLength; i++) {
            if (fRajzszam.size()>i && sRajzszam.size()>i) {
                if (!fRajzszam.get(i).equals(sRajzszam.get(i))) {
                    differentLineNumbers.add(i);
                }
            } else if (fRajzszam.size()>i) {
                differentLineNumbers.add(i);
            } else if (sRajzszam.size()>i) {
                differentLineNumbers.add(i);
            }
        }
    }

    private void checkDifferencesInDbLists(List<Integer> differentLineNumbers, int maxDbLength) {
        for (int i = 0; i < maxDbLength; i++) {
            if (fDb.size()>i && sDb.size()>i) {
                if (!fDb.get(i).equals(sDb.get(i))) {
                    differentLineNumbers.add(i);
                }
            } else if (fDb.size()>i) {
                differentLineNumbers.add(i);
            } else if (sDb.size()>i) {
                differentLineNumbers.add(i);
            }
        }
    }

    private int setMaxRajzszamLength() {
        int result;
        if (fRajzszam.size()>sRajzszam.size()) {
            result = fRajzszam.size()+1;
        }
        else {
            result = sRajzszam.size()+1;
        }
        return result;
    }

    private int setMaxDbLength() {
        int result;
        if (fDb.size() > sDb.size()) {
             result = fDb.size()+1;
        }
        else {
            result = sDb.size()+1;
        }
        return result;
    }

    private void showInvalidFileFormatDialog() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText("Invalid file format.");
        alert.show();
        log("showInvalidFileFormatDialog", "Invalid file format.");
    }

    private void log(String methodeName, String message) {
        Logger.getLogger(ModelProcessor.class.getName())
                .log(Level.INFO, "ModelProcessor/" + methodeName + " - " + message);
    }

    //<editor-fold desc="Getters">
    public List<String> getfDb() {
        return fDb;
    }

    public List<String> getfInfo() {
        return fInfo;
    }

    public List<String> getfRajzszam() {
        return fRajzszam;
    }

    public List<String> getfNo() {
        return fNo;
    }

    public List<String> getfMertekegyseg() {
        return fMertekegyseg;
    }

    public List<String> getsDb() {
        return sDb;
    }

    public List<String> getsNo() {
        return sNo;
    }

    public List<String> getsMertekegyseg() {
        return sMertekegyseg;
    }

    public List<String> getsRajzszam() {
        return sRajzszam;
    }

    public List<String> getStatus() {
        return status;

    }
    //</editor-fold>
}

