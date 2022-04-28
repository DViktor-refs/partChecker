package sample.Utils;

import sample.interfaces.XlsProcessorInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import static sample.Utils.ModelProcessor.*;

public class XlsProcessor implements XlsProcessorInterface {

    //<editor-fold desc="Break Signs">
    private final static String BLANK = "÷BLANK÷BLANK";
    private final static String BREAK = "÷";
    private final static String XLSSTARTSIGN = "Megnevezés (ker. áru)";
    private final static String XLSENDSIGN = "ALU PROFIL";
    //</editor-fold>

    public List<String> fillLists(List<String> rawList, int type) {
        List<String> result = new ArrayList<>();

         switch (type) {
            case XLSDB:
                result = getXlsDbList( rawList);
                break;
            case XLSRAJZSZAM:
                result = getXlsRajzszamList( rawList);
                break;
        }
        return result;
    }

    private List<String> addCommercialProductionItems(int keraruStartIndex, int keraruEndIndex, List<String> result, List<String> rawList, int type) {

        switch (type) {
            case XLSDBCOMMERCIALPRODUCTION:
                for (int i = keraruStartIndex+1; i < keraruEndIndex; i++) {
                    String temp = rawList.get(i);
                    if(!temp.contains(BLANK) && temp.length() > 5) {
                        String[] t = temp.split(Pattern.quote(BREAK));
                        result.add(Arrays.asList(t).get(1));
                    }
                }
                break;

            case XLSRAJZSZAMCOMMERCIALPRODUCTION:
                for (int i = keraruStartIndex+1; i < keraruEndIndex; i++) {
                    String temp = rawList.get(i);
                    if(!temp.contains(BLANK) && temp.length() > 5) {
                        String[] t = temp.split(Pattern.quote(BREAK));
                        result.add(Arrays.asList(t).get(4));
                    }
                }
                break;
        }
        return result;
    }

    private List<String> addOwnProductionItems(int keraruStartIndex,  List<String> rawList, int type) {
        List<String> result = new ArrayList<>();

        switch (type) {
            case XLSDBOWNPRODUCTION:
                for (int i = 1; i < keraruStartIndex; i++) {
                    String temp = rawList.get(i);
                    if(!temp.contains(BLANK) && temp.length() > 5) {
                        String[] t = temp.split(Pattern.quote(BREAK));
                        result.add(Arrays.asList(t).get(1));
                    }
                }
                break;

            case XLSRAJZSZAMOWNPRODUCTION:
                for (int i = 1; i < keraruStartIndex; i++) {
                    String temp = rawList.get(i);
                    if(!temp.contains(BLANK) && temp.length() > 5) {
                        String[] t = temp.split(Pattern.quote(BREAK));
                        result.add(Arrays.asList(t).get(2));
                    }
                }
                break;
        }
        return result;
    }

    public List<String> getXlsRajzszamList(List<String> rawList) {
        List<String> result;
        int keraruStartIndex = findKeraruStartIndexInXls(rawList);
        int keraruEndIndex = findKeraruEndIndexInXls(rawList);
        result = addOwnProductionItems(keraruStartIndex,  rawList, XLSRAJZSZAMOWNPRODUCTION );
        return addCommercialProductionItems(keraruStartIndex, keraruEndIndex, result, rawList, XLSRAJZSZAMCOMMERCIALPRODUCTION );
    }

    public List<String> getXlsDbList(List<String> rawList) {
        List<String> result;
        int keraruStartIndex = findKeraruStartIndexInXls(rawList);
        int keraruEndIndex = findKeraruEndIndexInXls(rawList);
        result = addOwnProductionItems(keraruStartIndex, rawList, XLSDBOWNPRODUCTION);
        return addCommercialProductionItems(keraruStartIndex, keraruEndIndex, result, rawList, XLSDBCOMMERCIALPRODUCTION);
    }

    private int findKeraruStartIndexInXls(List<String> rawList) {
        int temp=-1;
        for (int i = 0; i < rawList.size(); i++) {
            if(rawList.get(i).contains(XLSSTARTSIGN)) {
                temp = i;
                break;
            }
        }
        return temp;
    }

    private int findKeraruEndIndexInXls(List<String> rawList) {
        int temp=-1;
        for (int i = 0; i < rawList.size(); i++) {
            if(rawList.get(i).contains(XLSENDSIGN)) {
                temp = i;
                break;
            }
        }
        return temp;
    }

}
