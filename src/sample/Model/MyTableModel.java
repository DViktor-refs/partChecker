package sample.Model;

public class MyTableModel {
    private String col_no;
    private String col_rsz_pdf;
    private String col_db_pdf;
    private String col_mert_pdf;
    private String col_rsz_txt;
    private String col_db_txt;
    private String col_mert_txt;
    private String col_stat;
    private String col_info;
    private String col_no_txt;

    public MyTableModel() {
    }

    public MyTableModel(String col_no,
                        String col_rsz_pdf,
                        String col_db_pdf,
                        String col_mert_pdf,
                        String col_rsz_txt,
                        String col_no_txt,
                        String col_db_txt,
                        String col_mert_txt,
                        String col_stat,
                        String col_info) {

        this.col_no = col_no;
        this.col_rsz_pdf = col_rsz_pdf;
        this.col_db_pdf = col_db_pdf;
        this.col_mert_pdf = col_mert_pdf;
        this.col_rsz_txt = col_rsz_txt;
        this.col_db_txt = col_db_txt;
        this.col_mert_txt = col_mert_txt;
        this.col_stat = col_stat;
        this.col_info = col_info;
        this.col_no_txt = col_no_txt;
    }

    public String getCol_no() {
        return col_no;
    }

    public String getCol_rsz_pdf() {
        return col_rsz_pdf;
    }

    public String getCol_db_pdf() {
        return col_db_pdf;
    }

    public String getCol_mert_pdf() {
        return col_mert_pdf;
    }

    public String getCol_rsz_txt() {
        return col_rsz_txt;
    }

    public String getCol_db_txt() {
        return col_db_txt;
    }

    public String getCol_mert_txt() {
        return col_mert_txt;
    }

    public String getCol_stat() {
        return col_stat;
    }

    public String getCol_info() {
        return col_info;
    }

    public String getCol_no_txt() {
        return col_no_txt;
    }
}
