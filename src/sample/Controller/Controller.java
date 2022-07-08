package sample.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Model.MyTableModel;
import sample.Utils.ModelProcessor;
import sample.Utils.PdfProcessor;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Initializable {

    Parent root;
    Stage stage;

    ModelProcessor modelProcessor = new ModelProcessor();

    public double dx, dy;

    private String pdfButtonState;
    private String xlsButtonState;
    private String txtButtonState;
    private String startButtonState;

    private Boolean isPaneExist = false;

    ObservableList<MyTableModel> ol = FXCollections.observableArrayList();

    //<editor-fold desc="GUI FXML">

    @FXML
    TextField textField;

    @FXML
    ImageView iv_reset;

    @FXML
    ImageView iv_pdf;

    @FXML
    ImageView iv_xls;

    @FXML
    ImageView iv_txt;

    @FXML
    ImageView iv_start;

    @FXML
    private Circle exitCircle;

    @FXML
    private AnchorPane rootView;

    @FXML
    private Label firstFileLabel;

    @FXML
    private Label secondFileLabel;

    @FXML
    TableView<MyTableModel> table;

    @FXML
    TableColumn<MyTableModel, String> firstNo;

    @FXML
    TableColumn<MyTableModel, String> secondNo;

    @FXML
    TableColumn<MyTableModel, String> firstDb;

    @FXML
    TableColumn<MyTableModel, String> firstRajzszam;

    @FXML
    TableColumn<MyTableModel, String> firstMertekegyseg;

    @FXML
    TableColumn<MyTableModel, String> secondRajzszam;

    @FXML
    TableColumn<MyTableModel, String> secondDb;

    @FXML
    TableColumn<MyTableModel, String> secondMertekegyseg;

    @FXML
    TableColumn<MyTableModel, String> secondStatus;

    @FXML
    TableColumn<MyTableModel, String> secondInfo;

    @FXML
    Pane pane;
    //</editor-fold>

    //<editor-fold desc="Move by window border">
    @FXML
    public void paneMouseClicked(MouseEvent event) {
        stage = (Stage) rootView.getScene().getWindow();
        dx = stage.getX() - event.getScreenX();
        dy = stage.getY() - event.getScreenY();
    }

    public void paneMouseDragged(MouseEvent event) {
        stage.setX(dx + event.getScreenX());
        stage.setY(dy + event.getScreenY());
    }

    //</editor-fold>

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDefaultState();
        firstNo.setCellValueFactory(new PropertyValueFactory<>("col_no"));
        firstRajzszam.setCellValueFactory(new PropertyValueFactory<>("col_rsz_pdf"));
        firstDb.setCellValueFactory(new PropertyValueFactory<>("col_db_pdf"));
        firstMertekegyseg.setCellValueFactory(new PropertyValueFactory<>("col_mert_pdf"));
        secondRajzszam.setCellValueFactory(new PropertyValueFactory<>("col_rsz_txt"));
        secondNo.setCellValueFactory(new PropertyValueFactory<>("col_no_txt"));
        secondDb.setCellValueFactory(new PropertyValueFactory<>("col_db_txt"));
        secondMertekegyseg.setCellValueFactory(new PropertyValueFactory<>("col_mert_txt"));
        secondStatus.setCellValueFactory(new PropertyValueFactory<>("col_stat"));
        secondInfo.setCellValueFactory(new PropertyValueFactory<>("col_info"));
        table.setItems(ol);
        isPaneExist = false;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setParentInController(Parent root) {
        this.root = root;
    }

    @FXML
    private void handleMouseClickOnTable() {
        table.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!ol.isEmpty()) {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        Node node = ((Node) event.getTarget()).getParent();
                        TableRow row;
                        removePopupPane();
                        if (node instanceof TableRow) {
                            row = (TableRow) node;
                        } else {
                            row = (TableRow) node.getParent();
                        }
                        if(row != null) {
                            MyTableModel t = (MyTableModel) row.getItem();
                            popUpWindow(event, t.getCol_no(), t.getCol_no_txt(), t.getCol_rsz_pdf(),
                                    t.getCol_rsz_txt(), t.getCol_db_pdf(), t.getCol_db_txt(), t.getCol_mert_pdf(),
                                    t.getCol_mert_txt());
                        }
                        else {
                            log("handleMouseClickOnTable" , "Null row error." );
                        }
                    }
                }
            }
        });
    }

    private void removePopupPane() {
        rootView.getChildren().remove(pane);
        isPaneExist = false;
    }

    @FXML
    public void handleMouseMovedAction(MouseEvent event) {
        if (isPaneExist) {
            Point buttonClickLocation = MouseInfo.getPointerInfo().getLocation();
            rootView.setOnMouseMoved(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if ((Math.abs(buttonClickLocation.x - event.getScreenX()) > 30
                            || (Math.abs(buttonClickLocation.y - event.getScreenY()) > 30))) {
                        removePopupPane();
                    }
                }
            });
        }
    }

    private void popUpWindow(MouseEvent event, String no1, String no2, String rsz1,
                             String rsz2, String db1, String db2, String mert1, String mert2) {

        setPane(event);

        //<editor-fold desc="Label table">

        Label l1 = makeNewTableLabel("Pdf/Xlsx", 4, 37);
        Label l2 = makeNewTableLabel("Txt", 22, 67);
        Label l3 = makeNewTableLabel("No", 65, 7);
        Label l4 = makeNewTableLabel("Rajzszám", 117, 7);
        Label l5 = makeNewTableLabel("db", 210, 7);
        Label l6 = makeNewTableLabel("Mért.", 236, 7);
        Label l7 = makeNewTableLabel(no1, 71, 37);
        Label l8 = makeNewTableLabel(no2, 71, 67);
        Label l9 = makeNewTableLabel(rsz1, 100, 37);
        if (!rsz1.equals(rsz2)) {
            l9.setTextFill(Color.RED);
        }
        Label l9a = makeNewTableLabel(rsz2, 100, 67);
        if (!rsz1.equals(rsz2)) {
            l9a.setTextFill(Color.RED);
        }
        Label l10 = makeNewTableLabel(db1, 222, 37);
        if (!db1.equals(db2)) {
            l10.setTextFill(Color.RED);
        }
        Label l11 = makeNewTableLabel(db2, 222, 67);
        if (!db1.equals(db2)) {
            l11.setTextFill(Color.RED);
        }
        Label l12 = makeNewTableLabel(mert1, 246, 37);
        Label l13 = makeNewTableLabel(mert2, 246, 67);

        //</editor-fold>

        pane.getChildren().addAll(l1, l2, l3, l4, l5, l6, l7, l8, l9, l9a, l10, l11, l12, l13);
        rootView.getChildren().addAll(pane);
        isPaneExist = true;
    }

    private Label makeNewTableLabel(String title, int x, int y) {
        Label label = new Label(title);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    private void setPane(MouseEvent event) {
        pane = new Pane();
        pane.setLayoutX(event.getSceneX() + 20);
        pane.setLayoutY(event.getSceneY() - 100);
        pane.setMinSize(285, 100);
        pane.prefHeight(100);
        pane.prefWidth(285);
        pane.setStyle("-fx-background-color: white; -fx-border-color: grey;");
    }

    private void setDefaultState() {

        iv_reset.setImage(new Image(getClass().getResource("/sample/Assets/reset.png").toExternalForm()));
        iv_reset.setDisable(false);
        iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdf.png").toExternalForm()));
        iv_pdf.setDisable(false);
        iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xls.png").toExternalForm()));
        iv_xls.setDisable(false);
        iv_txt.setImage(new Image(getClass().getResource("/sample/Assets/txt.png").toExternalForm()));
        iv_txt.setDisable(false);
        iv_start.setImage(new Image(getClass().getResource("/sample/Assets/help.png").toExternalForm()));
        iv_start.setDisable(true);
        iv_start.setVisible(false);
        xlsButtonState = "DEFAULT";
        txtButtonState = "DEFAULT";
        startButtonState = "DEFAULT";
        pdfButtonState = "DEFAULT";
    }

    //<editor-fold desc="OnClickListeners">

    public void pdfButtonClicked(MouseEvent event) {
        if (!isPdfButtonDisabled()) {
            if (isLeftButtonClicked(event)) {
                File choosedFile = getFileWithFilechooser("pdf files", "*.pdf", "pdf fajl kivalasztasa");

                if (choosedFile != null) {
                    if (isChoosedFilePdf(choosedFile)) {
                        setStateIfItsPdf(choosedFile);
                    } else {
                        if (!isFirstFileLabelEmpty()) {
                            pdfButtonToON();
                        } else {
                            setStateIfFileLabelIsEmpty();
                        }
                    }
                }

                else {
                    if (!firstFileLabel.getText().equals("")) {
                        pdfButtonToON();
                    }
                }
            }
        }

        if (isRightButtonClicked(event)) {
            setStateIfClickedRightOnPdf();
        }
    }


    //<editor-fold desc="PDF Button state flags">
    private void setStateIfClickedRightOnPdf() {
        pdfButtonToDEFAULT();
        xlsButtonToDEFAULT();
        startButtonToOFF();
        ol.clear();
        firstFileLabel.setText("");
    }

    private boolean isRightButtonClicked(MouseEvent event) {
        return event.getButton().equals(MouseButton.SECONDARY);
    }

    private boolean isLeftButtonClicked(MouseEvent event) {
        return event.getButton().equals(MouseButton.PRIMARY);
    }

    private boolean isPdfButtonDisabled() {
        return pdfButtonState.equals("DISABLED");
    }

    private boolean isFirstFileLabelEmpty() {
        return firstFileLabel.getText().equals("");
    }

    private void setStateIfItsPdf(File file) {
        pdfButtonToON();
        xlsButtonToDISABLE();
        firstFileLabel.setText(file.getPath());
        if (txtButtonState.equals("ON")) {
            startButtonToON();
        }
    }

    private boolean isChoosedFilePdf(File file) {
        return modelProcessor.isValidFile(file).equals("PDF");
    }
    //</editor-fold>

    public void xlsButtonClicked(MouseEvent event) {
        if (!isXlsButtonDisabled()) {
            if (isLeftButtonClicked(event)) {
                File chosedFile = getFileWithFilechooser("xlsx files", "*.xlsx", "xlsx fajl kivalasztasa");
                if (chosedFile != null) {
                    if (isChoosedFileXls(chosedFile)) {
                        setStateIfItsXls(chosedFile);
                    } else {
                        if (!isFirstFileLabelEmpty()) {
                            xlsButtonToON();
                        } else {
                            setStateIfFileLabelIsEmpty();
                        }
                    }
                }

                else {
                    if (!firstFileLabel.getText().equals("")) {
                        xlsButtonToON();
                    }
                }
            }
        }

        if (isRightButtonClicked(event)) {
            setStateIfClickedRightOnXls();
        }
    }
    //<editor-fold desc="XLS Button state flags">
    private void setStateIfClickedRightOnXls() {
        xlsButtonToDEFAULT();
        pdfButtonToDEFAULT();
        firstFileLabel.setText("");
        ol.clear();
    }

    private void setStateIfXlsFileIsNull() {
        xlsButtonToDEFAULT();
        pdfButtonToDEFAULT();
        startButtonToOFF();
        firstFileLabel.setText("");
    }

    private void setStateIfFileLabelIsEmpty() {
        xlsButtonToDEFAULT();
        pdfButtonToDEFAULT();
        startButtonToOFF();
    }

    private void setStateIfItsXls(File file) {
        xlsButtonToON();
        pdfButtonToDISABLE();
        firstFileLabel.setText(file.getPath());
        if (txtButtonState.equals("ON")) {
            startButtonToON();
        }
    }

    private boolean isChoosedFileXls(File file) {
        return modelProcessor.isValidFile(file).equals("XLSX");
    }

    private boolean isXlsButtonDisabled() {
        return xlsButtonState.equals("DISABLED");
    }
    //</editor-fold>

    public void txtButtonClicked(MouseEvent event) {

        if (!isTxtButtonDisabled()) {
            if (isLeftButtonClicked(event)) {
                File choosedFile = getFileWithFilechooser("txt files", "*.txt", "txt fajl kivalasztasa");

                if (choosedFile != null) {
                    if (isChoosedFileTxt(choosedFile)) {
                        setStateIfItsTxt(choosedFile);
                    } else {
                        if (!isSecondFileLabelEmpty()) {
                            txtButtonToON();
                        } else {
                            setStateIfFileLabelIsEmpty();
                        }
                    }
                } else {
                    if (!secondFileLabel.getText().equals("")) {
                        txtButtonToON();
                    }
                }
            }
        }

        if(isRightButtonClicked(event)) {
            setStateIfClickedRightOnTxt();
        }
    }
    //<editor-fold desc="TXT Button state flags">
    private void setStateIfClickedRightOnTxt() {
        txtButtonToDEFAULT();
        startButtonToOFF();
        secondFileLabel.setText("");
    }

    private void setStateIfTxtFileIsNull() {
        txtButtonToDEFAULT();
        startButtonToOFF();
        secondFileLabel.setText("");
        ol.clear();
    }

    private boolean isSecondFileLabelEmpty() {
        return secondFileLabel.getText().equals("");
    }

    private void setStateIfItsTxt(File file) {
        txtButtonToON();
        secondFileLabel.setText(file.getPath());
        if(pdfButtonState.equals("ON") || xlsButtonState.equals("ON")) {
            startButtonToON();
        }
    }

    private boolean isChoosedFileTxt(File file) {
        return modelProcessor.isValidFile(file).equals("TXT");
    }

    private boolean isTxtButtonDisabled() {
        return txtButtonState.equals("DISABLED");
    }
    //</editor-fold>

    public void resetButtonClicked(MouseEvent event) {
        modelProcessor.clearDataLists("f");
        modelProcessor.clearDataLists("s");
        ol.clear();
        startButtonToOFF();
        pdfButtonToDEFAULT();
        xlsButtonToDEFAULT();
        txtButtonToDEFAULT();
        firstFileLabel.setText("");
        secondFileLabel.setText("");
    }

    public void startButtonClicked(MouseEvent event) {
        List<String> rawList;
        if(isLeftButtonClicked(event)) {
            if(!isFirstFileLabelEmpty()) {
                rawList = modelProcessor.fillRawList(new File(firstFileLabel.getText()), modelProcessor.isValidFile(new File(firstFileLabel.getText())));
                modelProcessor.fillFlists(rawList);

            }
            if(!isSecondFileLabelEmpty()) {
                rawList = modelProcessor.fillRawList(new File(secondFileLabel.getText()), modelProcessor.isValidFile(new File(secondFileLabel.getText())));
                modelProcessor.fillSlists(rawList);
            }
            ol.clear();
            ol = fillOlList(modelProcessor.fillTheModelList());
        }
    }

    private ObservableList<MyTableModel> fillOlList(List<MyTableModel> fillTheModelList) {
        for (int i = 0; i < modelProcessor.findLargestListSize(); i++) {
            ol.add(new MyTableModel(modelProcessor.getfNo().get(i),
                    modelProcessor.getfRajzszam().get(i),
                    modelProcessor.getfDb().get(i),
                    modelProcessor.getfMertekegyseg().get(i),
                    modelProcessor.getsRajzszam().get(i),
                    modelProcessor.getsNo().get(i),
                    modelProcessor.getsDb().get(i),
                    modelProcessor.getsMertekegyseg().get(i),
                    modelProcessor.getStatus().get(i),
                    modelProcessor.getfInfo().get(i)
            ));
        }
        return ol;
    }

    //</editor-fold>

    //<editor-fold desc="Button States">
    private void pdfButtonToDISABLE() {
        pdfButtonState="DISABLED";
        iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdfGrey.png").toExternalForm()));
        iv_pdf.setDisable(true);
    }

    private void pdfButtonToDEFAULT() {
        pdfButtonState="DEFAULT";
        iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdf.png").toExternalForm()));
        iv_pdf.setDisable(false);
    }

    private void pdfButtonToON() {
        pdfButtonState="ON";
        iv_pdf.setImage(new Image(getClass().getResource("/sample/Assets/pdfGreen.png").toExternalForm()));
        iv_pdf.setDisable(false);
    }

    private void xlsButtonToDISABLE() {
        xlsButtonState="DISABLED";
        iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xlsGrey.png").toExternalForm()));
        iv_xls.setDisable(true);
    }

    private void xlsButtonToDEFAULT() {
        xlsButtonState="DEFAULT";
        iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xls.png").toExternalForm()));
        iv_xls.setDisable(false);
    }

    private void xlsButtonToON() {
        xlsButtonState="ON";
        iv_xls.setImage(new Image(getClass().getResource("/sample/Assets/xlsGreen.png").toExternalForm()));
        iv_xls.setDisable(false);
    }

    private void txtButtonToDISABLE() {
        txtButtonState="DISABLED";
        iv_txt.setImage(new Image(getClass().getResource("/sample/Assets/txtGrey.png").toExternalForm()));
        iv_txt.setDisable(true);
    }

    private void txtButtonToDEFAULT() {
        txtButtonState="DEFAULT";
        iv_txt.setImage(new Image(getClass().getResource("/sample/Assets/txt.png").toExternalForm()));
        iv_txt.setDisable(false);
    }

    private void txtButtonToON() {
        txtButtonState="ON";
        iv_txt.setImage(new Image(getClass().getResource("/sample/Assets/txtGreen.png").toExternalForm()));
        iv_txt.setDisable(false);
    }

    private void startButtonToON() {
        startButtonState ="ON";
        iv_start.setImage(new Image(getClass().getResource("/sample/Assets/help.png").toExternalForm()));
        iv_start.setDisable(false);
        iv_start.setVisible(true);
    }

    private void startButtonToOFF() {
        startButtonState ="OFF";
        iv_start.setImage(new Image(getClass().getResource("/sample/Assets/help.png").toExternalForm()));
        iv_start.setVisible(false);
        iv_start.setDisable(true);
    }
    //</editor-fold>

    private File getFileWithFilechooser(String filterText1, String filtertext2, String title) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterText1, filtertext2));
        fc.setTitle(title);
        return fc.showOpenDialog(stage);
    }

    public void onDragDraggedHereImage(DragEvent event) {
        List<File> tempFileList = event.getDragboard().getFiles();
        if (tempFileList.size()>1) {
            tempFileList = leaveOnlyFirstFile(tempFileList);
        }

        if (isPdfFile(tempFileList)) {
            if (modelProcessor.isValidFile(tempFileList.get(0)).equals("PDF") && !pdfButtonState.equals("DISABLED")) {
                File draggedFile;
                draggedFile = tempFileList.get(0);
                if (draggedFile!=null && firstFileLabel.getText().equals("")) {
                    setStateIfItsPdf(draggedFile);
                }
                else if (draggedFile!=null && !firstFileLabel.getText().equals("")) {
                    setStateIfItsPdf(draggedFile);
                }
            }
        }

        else if (isXlsFile(tempFileList)) {
            if (modelProcessor.isValidFile(tempFileList.get(0)).equals("XLSX") && !xlsButtonState.equals("DISABLED")) {
                File draggedFile;
                draggedFile = tempFileList.get(0);

                if (draggedFile!=null && firstFileLabel.getText().equals("")) {
                    setStateIfItsXls(draggedFile);
                }

                else if (draggedFile!=null && !firstFileLabel.getText().equals("")) {
                    setStateIfItsXls(draggedFile);
                }

                else if(isCancelClicked(draggedFile)) {
                    if (!firstFileLabel.getText().equals("")) {
                        xlsButtonToON();
                    }

                    else {
                        setStateIfItsXlsAndCancelClicked();
                    }
                }
            }
        }

        else if(modelProcessor.isValidTxt(tempFileList.get(0))) {

            if (modelProcessor.isValidFile(tempFileList.get(0)).equals("TXT")) {

                File draggedFile;
                draggedFile = tempFileList.get(0);

                if (draggedFile!=null && secondFileLabel.getText().equals("")) {
                    txtButtonToON();
                    secondFileLabel.setText(draggedFile.getPath());

                    if(xlsButtonState.equals("ON") || pdfButtonState.equals("ON")) {
                        startButtonToON();
                    }
                }

                else if (draggedFile!=null && !secondFileLabel.getText().equals("")) {
                    txtButtonToON();
                    secondFileLabel.setText(draggedFile.getPath());
                }

                else if(draggedFile==null) {
                    if (!secondFileLabel.getText().equals("")) {
                        txtButtonToON();
                    }
                    else {
                        txtButtonToDEFAULT();
                    }
                }
            }
        }

        else if(modelProcessor.isValidFile(tempFileList.get(0)).equals("UNKNOWN")) {
            showInvalidFileFormatDialog();

        }

    }

    private void showInvalidFileFormatDialog() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText("Invalid file format.");
        alert.show();
        log("showInvalidFileFormatDialog" , "Invalid file." );
    }

    private void setStateIfItsXlsAndCancelClicked() {
        xlsButtonToDEFAULT();
        pdfButtonToDEFAULT();
        startButtonToOFF();
    }

    private boolean isCancelClicked(File draggedFile) {
        return draggedFile==null;
    }

    private boolean isXlsFile(List<File> tempFileList) {
        return modelProcessor.isItaXlsFile(tempFileList.get(0));
    }

    private boolean isPdfFile(List<File> tempFileList) {
        return modelProcessor.isItaPdfFile(tempFileList.get(0));
    }

    private List<File> leaveOnlyFirstFile(List<File> tempFileList) {
        for (int i = 0; i < tempFileList.size(); i++) {
            if (i>0) {
                tempFileList.remove(i);
            }
        }
        return tempFileList;
    }

    public void onDragOverDragHereImage(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void exitCircleOnDrag() {
        exitCircle.setFill(Color.INDIANRED);
    }

    public void exitCircleOnDragExited() {
        exitCircle.setFill(Color.DODGERBLUE);
    }

    public void exitLabelOnExited() {
        exitCircle.setFill(Color.DODGERBLUE);
    }

    public void exitLabelOnMoved() {
        exitCircle.setFill(Color.INDIANRED);
    }

    public void startExitDialog(MouseEvent event) {

        Alert dg = new Alert(Alert.AlertType.CONFIRMATION);
        dg.setTitle("Exit");
        dg.setContentText("Are you sure you want to exit the application?");

        Optional<ButtonType> result = dg.showAndWait();

        if (result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    private void log(String methodName, String errorMessage) {
        Logger.getLogger(Controller.class.getName())
                .log(Level.INFO, "Controller/" + methodName + "  - " + errorMessage);
    }

}
