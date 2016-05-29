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
    double afRatio = 14;
    double myafRatio;

    public void powerBandMaker(double[] powerBand) {
        int j = minRPM;
        for (int i = minRPM; j < powerBandStart; i = (int) Math.pow(j, 1.3) / 700 + 100) {
            powerBand[j] = i;
            //System.out.println(i);
            j++;
        }
        for (int i = powerBandStart; j < powerBandEnd; i = (int) Math.pow(j / 120 - 52, 2) * -1 + 350) {
            powerBand[j] = i;
            //System.out.println(i);
            j++;
        }
        for (int i = powerBandEnd; j < maxRPM; i = (int) (j * -0.237113402062 + 2012.57)) {
            powerBand[j] = i;
            //System.out.println(i);
            j++;
        }

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

        });

        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Number of Month");
        //creating the chart
        final LineChart<Number, Number> lineChart
                = new LineChart<Number, Number>(xAxis, yAxis);

        lineChart.setTitle("Power Output");
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");
        //populating the series with data
        series.getData().add(new XYChart.Data(1, 23));
        series.getData().add(new XYChart.Data(2, 14));
        series.getData().add(new XYChart.Data(3, 15));
        series.getData().add(new XYChart.Data(4, 24));
        series.getData().add(new XYChart.Data(5, 34));
        series.getData().add(new XYChart.Data(6, 36));
        series.getData().add(new XYChart.Data(7, 22));
        series.getData().add(new XYChart.Data(8, 45));
        series.getData().add(new XYChart.Data(9, 43));
        series.getData().add(new XYChart.Data(10, 17));
        series.getData().add(new XYChart.Data(11, 29));
        series.getData().add(new XYChart.Data(12, 25));

        Scene scene1 = new Scene(grid, 800, 450);
        Scene scene2 = new Scene(lineChart, 800, 450);
        primaryStage.setScene(scene1);
        primaryStage.setScene(scene2);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
