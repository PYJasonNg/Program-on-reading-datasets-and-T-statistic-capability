package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*; 

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Test extends Application {
    static double averageGrownGlobal = 0;
    static double noSmallPECompaniesGlobal = 0;
    static double noLargePECompaniesGlobal = 0;

    private TextField getMeanOne = new TextField();
    private TextField getMeanTwo = new TextField();
    private TextField getSDOne = new TextField();
    private TextField getSDTwo = new TextField();
    private TextField getNoValuesOne = new TextField();
    private TextField getNoValuesTwo = new TextField();
    private TextField totalValues = new TextField();
    private Button getValues = new Button("Calculate"); 

    private TextField averageGrowthAllCompanies = new TextField();
    private TextField averageGrowthSmallPEGlobal = new TextField();
    private TextField averageGrowthLargePEGlobal = new TextField();

    public static double getAverageGrowth(String[][] dataset, int rows) {

        double averageGrowth = 0;

        for(int i = 0; i < rows; i++) {
            if (!dataset[i][7].equals("NA"))
                averageGrowth += Double.parseDouble(dataset[i][7].replaceAll("%", ""));
        }

        return averageGrowth / rows;
    }

    public static double countCompanies(String[][] dataset, int rows) {

        double noSmallPECompanies = 0;
        double noLargePECompanies = 0;

        double numberOfSmallPECompanies = 0;
        double numberOfLargePECompanies = 0;

        for (int i = 0; i < rows; i++) {
            if (!dataset[i][7].equals("NA"))
                if (Double.parseDouble(dataset[i][3]) > 20) {
                    noLargePECompanies += Double.parseDouble(dataset[i][7].replaceAll("%", ""));
                    numberOfLargePECompanies++;
                }
                else if (Double.parseDouble(dataset[i][3]) < 20) {
                    noSmallPECompanies += Double.parseDouble(dataset[i][7].replaceAll("%", ""));
                    numberOfSmallPECompanies++;
                }
        }

        return checkGrowth(noSmallPECompanies, noLargePECompanies, numberOfSmallPECompanies, numberOfLargePECompanies);
    }

    public static double checkGrowth(double numberOfSmallPECompanies, double numberOfLargePECompanies,
                                     double numberSmallPE, double numberLargePE) {

        double averageGrowthSmallPE = numberOfSmallPECompanies / numberSmallPE;
        double averageGrowthLargePE = numberOfLargePECompanies / numberLargePE;

       noLargePECompaniesGlobal += averageGrowthLargePE;
       noSmallPECompaniesGlobal += averageGrowthSmallPE;
       
        return noLargePECompaniesGlobal;
    }

    private void userTValue() {
        double meanOne = Double.parseDouble(getMeanOne.getText());
        double meanTwo = Double.parseDouble(getMeanTwo.getText());
        double SDOne = Double.parseDouble(getSDOne.getText());
        double SDTwo = Double.parseDouble(getSDTwo.getText());
        double noValuesOne = Double.parseDouble(getNoValuesOne.getText());
        double noValuesTwo = Double.parseDouble(getNoValuesTwo.getText());

        double standardError = Math.pow((Math.pow(SDOne, 2) / noValuesOne) + (Math.pow(SDTwo, 2) / noValuesTwo), 0.5);

        double tValue = (meanTwo - meanOne) / standardError;

        totalValues.setText(String.valueOf(tValue));
    }

    public static void read() throws IOException {
        java.io.File newFile = new java.io.File("data/pedata19.csv");

        Scanner input = new Scanner(newFile);

        // Counts lines
        BufferedReader reader = new BufferedReader(new FileReader(newFile));
        int rows = 0;
        while (reader.readLine() != null)
            rows++;
        reader.close();

        ArrayList<String> newList = new ArrayList<>();

        while (input.hasNext()) {
            String line = input.nextLine().replaceAll(",", "=");

            // Splits the lines in the dataset into seperate words
            String[] word = line.split("=");
            // Adds words onto a arrayList names newList
            for (String s : word) {
                newList.add(s);
            }
        }

        // Transforms the arrayList into an array
        String[] array = newList.toArray(new String[0]);

        // Guide: Column Title = index number
        // Industry Name = 0|
        // Number of firms = 1|
        // Current PE = 2|
        // Trailing PE = 3|
        // Forward PE = 4|
        // Aggregate Mkt Cap / Net Income (all firms) = 5|
        // Aggregate Mkt Cap / Trailing Net Income (only money making firms) = 6|
        // Expected growth - next 5 years = 7|
        // PEG Ratio = 8

        // 2-D array
        // 9 is the number of columns in the dataset
        String[][] newTable = new String[rows][9];

            // Transforms the 1-D array "array" into the 2-D array "newTable"
            int b = 0;
            for (int i = 0; i < rows; i++) {
                for (int o = 0; o < 9; o++) {
                    newTable[i][o] = array[b++];
                }
            }

        averageGrownGlobal += getAverageGrowth(newTable, rows);
        countCompanies(newTable, rows);

    }

    public void start(Stage primaryStage) throws IOException {
        // Initialises read method.
        read();

        averageGrowthAllCompanies.setText(String.valueOf(averageGrownGlobal));
        averageGrowthSmallPEGlobal.setText(String.valueOf(noSmallPECompaniesGlobal));
        averageGrowthLargePEGlobal.setText(String.valueOf(noLargePECompaniesGlobal));

        // Create UI for inputting values
        GridPane gridpane = new GridPane();
        gridpane.setVgap(10);
        gridpane.setHgap(10);
        gridpane.add(new Label("Average growth of all companies: "), 0, 0);
        gridpane.add(averageGrowthAllCompanies, 1, 0);
        gridpane.add(new Label("Average growth of small PE companies:"), 0, 1);
        gridpane.add(averageGrowthSmallPEGlobal, 1, 1);
        gridpane.add(new Label("Average growth of large PE companies:"), 0, 2);
        gridpane.add(averageGrowthLargePEGlobal, 1, 2);

        gridpane.add(new Label("Enter your first Mean: "), 0, 6);
        gridpane.add(getMeanOne, 1, 6);
        gridpane.add(new Label("Enter your first Standard Deviation: "), 0, 7);
        gridpane.add(getSDOne, 1, 7);
        gridpane.add(new Label("Enter your first number of values: "), 0, 8);
        gridpane.add(getNoValuesOne, 1, 8);
        gridpane.add(new Label("Enter your second Mean: "), 0, 9);
        gridpane.add(getMeanTwo, 1, 9);
        gridpane.add(new Label("Enter your second Standard Deviation: "), 0, 10);
        gridpane.add(getSDTwo, 1, 10);
        gridpane.add(new Label("Enter your second number of values: "), 0, 11);
        gridpane.add(getNoValuesTwo, 1, 11);
        gridpane.add(new Label("Results: "), 2, 6);
        gridpane.add(new Label("The t-test result is: "), 2, 7);
        gridpane.add(totalValues, 2, 8);
        gridpane.add(getValues, 1, 13);

        // Set UI
        gridpane.setAlignment(Pos.CENTER);
        averageGrowthAllCompanies.setAlignment(Pos.TOP_LEFT);
        averageGrowthAllCompanies.setEditable(false); 
        averageGrowthSmallPEGlobal.setAlignment(Pos.TOP_LEFT);
        averageGrowthSmallPEGlobal.setEditable(false);
        averageGrowthLargePEGlobal.setAlignment(Pos.TOP_LEFT);
        averageGrowthLargePEGlobal.setEditable(false);
        getMeanOne.setAlignment(Pos.BOTTOM_LEFT);
        getSDOne.setAlignment(Pos.BOTTOM_LEFT);
        getNoValuesOne.setAlignment(Pos.BOTTOM_RIGHT);
        getMeanTwo.setAlignment(Pos.BOTTOM_LEFT);
        getSDTwo.setAlignment(Pos.BOTTOM_LEFT);
        getNoValuesTwo.setAlignment(Pos.BOTTOM_RIGHT);
        totalValues.setEditable(false);
        GridPane.setHalignment(getValues, HPos.LEFT);

        // Process Events
        getValues.setOnAction(e -> userTValue());

        // Create a new scene
        Scene scene = new Scene(gridpane, 575, 500);
        primaryStage.setTitle("Working Model");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
