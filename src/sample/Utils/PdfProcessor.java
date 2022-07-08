package sample.Utils;

import org.apache.commons.lang3.StringUtils;
import sample.interfaces.PdfProcessorInterface;
import static sample.Utils.ModelProcessor.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PdfProcessor implements PdfProcessorInterface {

    @Override
    public List<String> fillLists(List<String> rawList, int type) {
        List<String> result = new ArrayList<>();

        switch (type) {
            case PDFNO:
                result = getPdfNoList(rawList);
                break;
            case PDFRAJZSZAM:
                result = getPdfRajzszamList(rawList);
                break;
            case PDFDB:
                result = getPdfDbList(rawList);
                break;
            case PDFMERTEKEGYSEG:
                result = getPdfMertekegysegList(rawList);
                break;
            case PDFINFO:
                result = getPdfInfoList(rawList);
                break;
        }
        return result;
    }

    //<editor-fold desc="pdfList feltoltese">
    private List<String> getPdfNoList(List<String> rawList) {
        String temp, temp2,  substring;
        List<String> result = new ArrayList<>();

        for (int i = 0; i < rawList.size(); i++) {
            temp = rawList.get(i);

            if (checkNumberOfSeperators(temp)) {
                String[] tempStr = temp.split("÷");
                substring = tempStr[0];
                substring = StringUtils.deleteWhitespace(substring);
                substring = substring.replaceAll("[^0-9.]", "");

                if (!substring.isBlank() && !substring.isEmpty()) {
                    if (i < rawList.size()) {
                        if ((rawList.get(i+1).length() <4)) {
                            substring = substring + 0;
                        }
                    }
                    result.add(substring);
                }
            }
        }
        return result;
    }

    private List<String> getPdfRajzszamList(List<String> rawList) {
        String temp, substring;
        List<String> result = new ArrayList<>();

        for (int i = 0; i < rawList.size(); i++) {
            temp = rawList.get(i);
            if (checkNumberOfSeperators(temp)) {
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

    private List<String> getPdfDbList(List<String> rawList) {
        String temp, substring;
        List<String> result = new ArrayList<>();

        for (int i = 0; i < rawList.size(); i++) {
            temp = rawList.get(i);
            if (checkNumberOfSeperators(temp)) {
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

    private List<String> getPdfMertekegysegList(List<String> rawList) {
        List<String> result = new ArrayList<>();
        String temp;
        String substring;

        for (int i = 0; i < rawList.size(); i++) {
            temp = rawList.get(i);
            if (checkNumberOfSeperators(temp)) {
                List<String> tempStr = Arrays.asList(temp.split("÷"));
                substring =String.valueOf(tempStr.get(3));
                substring = StringUtils.deleteWhitespace(substring);
                if (!substring.isBlank() && !substring.isEmpty()) {
                    result.add(substring);
                }
            }
        }
        return result;
    }

    private List<String> getPdfInfoList( List<String> rawList) {

        List<String> result = new ArrayList<>();
        String temp;
        String substring;

        for (int i = 0; i < rawList.size(); i++) {
            temp = rawList.get(i);
            if (checkNumberOfSeperators(temp)) {
                substring = StringUtils.substring(temp, temp.length()-7, temp.length());
                substring = StringUtils.deleteWhitespace(substring);
                substring = substring.replaceAll("[÷]", "");
                if (!substring.isBlank() && !substring.isEmpty()) {
                    if (substring.contains("S") || substring.contains("I") || substring.contains("V") || substring.contains("E")) {
                        substring = (substring.replaceAll("-",""));
                    }
                    else {
                        substring = "";
                    }
                }
                result.add(substring);
            }
        }
        return result;
    }

    private boolean checkNumberOfSeperators(String temp) {
        int noOfSeparator = StringUtils.countMatches(temp, "÷");
        if (noOfSeparator>7) {
            return true;
        }
        else {
            return false;
        }
    }
    //</editor-fold>

}
