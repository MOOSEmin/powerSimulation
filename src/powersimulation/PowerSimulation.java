package powersimulation;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author jtyler
 */
public class PowerSimulation extends Application {

    int airValue;
    int fuelValue;
    int rpmValue;
    //static variables
    int gearCount = 1;
    int maxRPM = 8450;
    int minRPM = 1000;
    int powerBandStart = 5000;
    int powerBandEnd = 7800;
    double[] powerBand = new double[maxRPM];
    double[] optimalPower = new double[maxRPM];
    double afRatio = 14;
    double myafRatio;

    //problem is that on the first loop of each for loop, powerBand[j] is set to i, it should be set to powerBand[j - 1]
    public void powerBandMaker(double[] powerBand) {
        int j = minRPM;
        for (double i = (double) Math.pow(j, 1.4) / 700 + 13; j <= powerBandStart; i = (double) Math.pow(j, 1.4) / 700 + 13) {
            powerBand[j] = i;
            //System.out.println(i);
            j++;
        }
        for (double i = (double) Math.pow(j / 135 - 46, 2) * -1 + 320; j <= powerBandEnd; i = (double) Math.pow(j / 135 - 46, 2) * -1 + 320) {
            powerBand[j] = i;
            //System.out.println(i);
            j++;
        }
        for (double i = (double) (j * -0.237113402062 + 2022.57); j < maxRPM; i = (double) (j * -0.237113402062 + 2022.57)) {
            powerBand[j] = i;
            //System.out.println(i);
            j++;
        }
        optimalPower = powerBand;
        return;
    }

    public void numOnly(TextField field) {
        field.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = field.getText().charAt(oldValue.intValue());
                    if (!(ch >= '0' && ch <= '9')) {
                        field.setText(field.getText().substring(0, field.getText().length() - 1));
                    }
                }
            }

        });
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Welcome");

        // HBox
        HBox hb = new HBox();
        hb.setPadding(new Insets(15, 12, 15, 12));
        hb.setSpacing(10);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(15);
        grid.setVgap(25);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Inputs");
        sceneTitle.setFont(Font.font("Roboto", FontWeight.THIN, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        grid.setGridLinesVisible(false);

        Label air = new Label("Air");
        grid.add(air, 0, 1);

        TextField airTextField = new TextField();
        grid.add(airTextField, 1, 1);
        numOnly(airTextField);

        Label fuel = new Label("Fuel");
        grid.add(fuel, 0, 2);

        TextField fuelTextField = new TextField();
        grid.add(fuelTextField, 1, 2);
        numOnly(fuelTextField);

        Label shiftRPM = new Label("Shift RPM");
        grid.add(shiftRPM, 0, 3);

        TextField shiftRPMTextField = new TextField();
        grid.add(shiftRPMTextField, 1, 3);
        numOnly(shiftRPMTextField);

        Button btn = new Button("Launch");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 4);

        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("HP");
        xAxis.setLabel("RPM");
        //creating the chart
        final LineChart<Number, Number> lineChart
                = new LineChart<Number, Number>(xAxis, yAxis);

        lineChart.setTitle("Power Output");

        //defining a series
        btn.setOnAction(e -> {
            airValue = Integer.parseInt(airTextField.getText());
            fuelValue = Integer.parseInt(fuelTextField.getText());
            rpmValue = Integer.parseInt(shiftRPMTextField.getText());
            System.out.println("air : " + airValue);
            System.out.println("fuel : " + fuelValue);
            System.out.println("rpm : " + rpmValue);

            myafRatio = airValue / fuelValue;
            System.out.println(afRatio - myafRatio);

            double diff = 1 - 1 / Math.abs(afRatio - myafRatio);

            powerBandMaker(powerBand);

            System.out.println("my diff ratio : " + myafRatio);
            System.out.println("diff multiplier : " + diff);

            for (int i = 0; i < powerBand.length; i++) {
                if (diff == Double.NEGATIVE_INFINITY) {
                    diff = 0;
                }
                powerBand[i] = powerBand[i] * (1 - diff);
                System.out.println(powerBand[i]);
            }

            //populating the series with data
            XYChart.Series series1 = new XYChart.Series();
            series1.setName("Horse Power @ RPM");
            for (int i = 0; i < maxRPM; i++) {
                series1.getData().add(new XYChart.Data(i, powerBand[i]));
            }
            
            XYChart.Series series2 = new XYChart.Series();
            series2.setName("Optimal Power Output");
            for (int i = 0; i < maxRPM; i++) {
                series2.getData().add(new XYChart.Data(i, optimalPower[i]));
            }

            lineChart.getData().addAll(series1, series2);
        });

        Scene scene1 = new Scene(hb, 800, 450);
        hb.getChildren().add(grid);
        hb.getChildren().add(lineChart);
        primaryStage.setScene(scene1);
        primaryStage.show();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
