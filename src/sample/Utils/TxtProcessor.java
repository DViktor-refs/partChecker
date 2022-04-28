package sample.Utils;

import org.apache.commons.lang3.StringUtils;
import sample.Controller.Controller;
import sample.interfaces.TxtProcessorInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sample.Utils.ModelProcessor.*;

public class TxtProcessor implements TxtProcessorInterface {

    @Override
    public List<String> fillLists(List<String> rawList, int type) {
        List<String> result = new ArrayList<>();

        switch (type) {
            case TXTNO:
                result = getTxtNoList(rawList);
                break;
            case TXTRAJZSZAM:
                result = getTxtRajzszamList(rawList);
                break;
            case TXTDB:
                result = getTxtDbList(rawList);
                break;
            case TXTMERTEKEGYSEG:
                result = getTxtMertekegysegList(rawList);
                break;
        }
        return result;
    }

    private List<String> getTxtRajzszamList(List<String> rawList) {
        List<Integer> startIndex = findStartIndexInTxt(rawList);
        List<Integer> endIndex = findEndIndexInTxt(rawList);
        isStartIndexEndIndexEquals(startIndex, endIndex, "getTxtRajzszamList");

        return getTxtRajzszamItems(startIndex, endIndex, rawList);
    }

    private List<String> getTxtDbList(List<String> rawList) {

        List<Integer> startIndex = findStartIndexInTxt(rawList);
        List<Integer> endIndex = findEndIndexInTxt(rawList);
        isStartIndexEndIndexEquals(startIndex, endIndex, "getTxtDbList");

        return getTxtDbItems(startIndex, endIndex, rawList);
    }

    private List<String> getTxtMertekegysegList(List<String> rawList) {
        List<Integer> startIndex = findStartIndexInTxt(rawList);
        List<Integer> endIndex = findEndIndexInTxt(rawList);
        isStartIndexEndIndexEquals(startIndex, endIndex, "getTxtMertekegysegList");

        return getTxtMertekegysegItems(startIndex, endIndex, rawList);
    }

    private List<String> getTxtNoList(List<String> rawList) {
        List<Integer> startIndex = findStartIndexInTxt(rawList);
        List<Integer> endIndex = findEndIndexInTxt(rawList);
        isStartIndexEndIndexEquals(startIndex, endIndex, "getTxtNoList");

        return getTxtNoItems(rawList);
    }

    private List<Integer> findStartIndexInTxt(List<String> rawList) {
        List<Integer> startIndex = new ArrayList<>();
        for (int i = 0; i < rawList.size(); i++) {
            if(rawList.get(i).contains("Jk.Rf.")) {
                startIndex.add(i+2);
            }
        }
        return startIndex;
    }

    private List<Integer> findEndIndexInTxt(List<String> rawList) {
        List<Integer> endIndex = new ArrayList<>();
        for (int i = 0; i < rawList.size(); i++) {
            if(rawList.get(i).equals("\f")) {
                endIndex.add(i-5);
            }
        }
        return endIndex;
    }

    //<editor-fold desc="Getters">
    private List<String> getTxtRajzszamItems(List<Integer> startIndex, List<Integer> endIndex, List<String> rawList) {
        String temp;
        String substring;
        List<String> result = new ArrayList<>();
        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = rawList.get(j);

                if (!temp.contains("\f") && temp.length() > 51) {
                    substring = temp.substring(30,51);
                    substring = StringUtils.deleteWhitespace(substring);
                    if (!substring.isBlank() && !substring.isEmpty()) {
                        result.add(substring);
                    }
                }
            }
        }
        return result;
    }

    private List<String> getTxtNoItems( List<String> rawList) {
        String substring;
        List<String> result = new ArrayList<>();

        for (String s : rawList) {
            if (!s.contains("\f") && s.length() > 4) {
                substring = s.substring(0, 4);
                substring = StringUtils.deleteWhitespace(substring);
                if (StringUtils.isNumeric(substring)) {
                    result.add(substring);
                }
            }
        }
        return result;
    }

    private List<String> getTxtDbItems(List<Integer> startIndex, List<Integer> endIndex, List<String> rawLis) {
        String temp;
        String substring;
        List<String> result = new ArrayList<>();

        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = rawLis.get(j);
                if(!temp.contains("\f") && temp.length() > 57 && !temp.startsWith("    ")) {
                    substring = temp.substring(50,57);
                    substring = StringUtils.deleteWhitespace(substring);
                    result.add(substring);
                }
            }
        }
        return result;
    }

    private List<String> getTxtMertekegysegItems(List<Integer> startIndex, List<Integer> endIndex, List<String> rawLis) {
        String temp;
        String substring;
        List<String> result = new ArrayList<>();
        for (int i = 0; i < startIndex.size(); i++) {
            for (int j = startIndex.get(i); j < endIndex.get(i); j++) {
                temp = rawLis.get(j);
                if(!temp.contains("\f") && temp.length() > 62 && !temp.startsWith("    ")) {
                    substring = temp.substring(58,62);
                    substring = StringUtils.deleteWhitespace(substring);
                    result.add(substring);
                }
            }
        }
        return result;
    }
    //</editor-fold>

    private void isStartIndexEndIndexEquals(List<Integer> startIndex, List<Integer> endIndex, String methodName) {
        if (startIndex.size() != endIndex.size()) {
            Logger.getLogger(Controller.class.getName())
                    .log(Level.INFO, "TxtProocessor/" + methodName +"  - pdf parser hiba. startIndex és endIndex eltér");
        }
    }

}
