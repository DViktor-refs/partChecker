package sample.Utils;

import org.apache.commons.lang3.StringUtils;
import sample.Controller.Controller;
import sample.interfaces.PdfProcessorInterface;
import static sample.Utils.ModelProcessor.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PdfProcessor implements PdfProcessorInterface {

    @Override
    public List<String> fillLists(List<String> rawList, int type) {
        List<String> result = new ArrayList<>();
        List<Integer> startIndex = findStartIndexInPdf(rawList);
        List<Integer> endIndex = findEndIndexInPdf(rawList);
        isStartIndexEndIndexEquals(startIndex, endIndex, "fillLists");

        switch (type) {
            case PDFNO:
                result = getPdfNoList(startIndex, endIndex, rawList);
                break;
            case PDFRAJZSZAM:
                result = getPdfRajzszamList(startIndex, endIndex, rawList);
                break;
            case PDFDB:
                result = getPdfDbList(startIndex, endIndex, rawList);
                break;
            case PDFMERTEKEGYSEG:
                result = getPdfMertekegysegList(startIndex, endIndex, rawList);
                break;
            case PDFINFO:
                result = getPdfInfoList(startIndex, endIndex, rawList);
                break;
        }
        return result;
    }

    //<editor-fold desc="pdfList feltoltese">
    private List<String> getPdfNoList(List<Integer> startIndex, List<Integer> endIndex, List<String> rawList) {
        String temp, substring;
        List<String> result = new ArrayList<>();
        isStartIndexEndIndexEquals(startIndex, endIndex, "getPdfNoList");

        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = rawList.get(j);
                String[] tempStr = temp.split("÷");
                substring = tempStr[0];
                substring = StringUtils.deleteWhitespace(substring);
                substring = substring.replaceAll("[^0-9.]", "");
                if (!substring.isBlank() && !substring.isEmpty()) {
                    result.add(substring);
                }
            }
        }
        return result;
    }

    private List<String> getPdfRajzszamList(List<Integer> startIndex, List<Integer> endIndex, List<String> rawList) {
        String temp, substring;
        List<String> result = new ArrayList<>();
        isStartIndexEndIndexEquals(startIndex, endIndex, "getPdfRajzszamList");

        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = rawList.get(j);
                List<String> tempStr = Arrays.asList(temp.split("÷"));
                substring =String.valueOf(tempStr.get(1));
                substring = StringUtils.deleteWhitespace(substring);
                if (!substring.isBlank() && !substring.isEmpty()) {
                    result.add(substring);
                }
            }
        }
        return result;
    }

    private List<String> getPdfDbList(List<Integer> startIndex, List<Integer> endIndex, List<String> rawList) {
        String temp, substring;
        List<String> result = new ArrayList<>();
        isStartIndexEndIndexEquals(startIndex, endIndex, "getPdfDbList");

        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = rawList.get(j);
                String[] tempStr = temp.split("÷");
                substring = tempStr[2];
                substring = StringUtils.deleteWhitespace(substring);
                String subFirstHalf = substring.split(",")[0];
                String subSecondHalf = substring.split(",")[1];
                if (subSecondHalf.equals("000")) {
                    substring = subFirstHalf;
                }
                else {
                    subSecondHalf = subSecondHalf.replaceAll("0","");
                    substring = subFirstHalf+","+subSecondHalf;
                }

                if (!substring.isBlank() && !substring.isEmpty()) {
                    result.add(substring);
                }
            }
        }
        return result;
    }

    private List<String> getPdfMertekegysegList(List<Integer> startIndex, List<Integer> endIndex, List<String> rawList) {
        List<String> result = new ArrayList<>();
        isStartIndexEndIndexEquals(startIndex, endIndex, "getPdfMertekegysegList");

        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                result.add(getPdfMertItem(j, rawList));
            }
        }
        return result;
    }

    private List<String> getPdfInfoList(List<Integer> startIndex, List<Integer> endIndex, List<String> rawList) {

        List<String> result = new ArrayList<>();
        isStartIndexEndIndexEquals(startIndex, endIndex, "getPdfInfoList");

        if (startIndex.size() != endIndex.size()) {
            Logger.getLogger(Controller.class.getName())
                    .log(Level.INFO, "PdfProcessor/getPdfInfoList - pdf parser hiba. startIndex és endIndex eltér");
        }
        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                result.add(getPdfInfoItem(j, rawList));
            }
        }
        return result;
    }

    private List<Integer> findStartIndexInPdf(List<String> rawList) {
        List<Integer> pdfStartIndex = new ArrayList<>();
        for (int i = 0; i < rawList.size(); i++) {
            if(rawList.get(i).contains("Pos.Artikel")) {
                pdfStartIndex.add(i+1);
            }
        }
        return pdfStartIndex;
    }

    private List<Integer> findEndIndexInPdf(List<String> rawList) {
        List<Integer> pdfEndIndex = new ArrayList<>();
        for (int i = 0; i < rawList.size(); i++) {
            if(rawList.get(i).contains("Seite") && rawList.get(i).contains("machinery")) {
                pdfEndIndex.add(i);
            }
        }
        return pdfEndIndex;
    }

    private String getPdfInfoItem(int currentLoopIndex, List<String> rawList) {
        String temp = rawList.get(currentLoopIndex);
        String substring;
        String result="";

        substring = StringUtils.substring(temp, temp.length()-7, temp.length());
        substring = StringUtils.deleteWhitespace(substring);
        substring = substring.replaceAll("[÷]", "");
        if (!substring.isBlank() && !substring.isEmpty()) {
            if (substring.contains("S") || substring.contains("I") || substring.contains("V") || substring.contains("E")) {
                result = (substring.replaceAll("-",""));
            }
        }
        return result;
    }

    private String getPdfMertItem(int currentLoopIndex, List<String> rawList) {
        String temp = rawList.get(currentLoopIndex);
        String substring;
        String result="";
        String[] tempStr = temp.split("÷");
        substring = tempStr[3];
        substring = StringUtils.deleteWhitespace(substring);

        if (!substring.isBlank() && !substring.isEmpty()) {
            result = (substring);
        }
        return result;
    }
    //</editor-fold>

    private void isStartIndexEndIndexEquals(List<Integer> startIndex, List<Integer> endIndex, String methodName) {
        if (startIndex.size() != endIndex.size()) {
            Logger.getLogger(Controller.class.getName())
                    .log(Level.INFO, "PdfProcessor/" + methodName +"  - pdf parser hiba. startIndex és endIndex eltér");
        }
    }
}
