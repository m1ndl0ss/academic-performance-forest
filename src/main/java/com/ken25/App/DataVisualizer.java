package com.ken25.App;

import java.io.File;
import java.io.FileReader;

import com.ken25.Modules.Astro;
import com.ken25.Modules.Bio;
import com.ken25.Modules.FileDisplayer;
import com.ken25.Modules.PropertyAnalyzer;
import com.ken25.Modules.PropertyReduction;
import com.ken25.Modules.Psionic;
import com.ken25.Modules.Quantum;
import com.ken25.Modules.Symbiotic;
import com.ken25.Modules.Utility;
import com.opencsv.CSVReader;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import java.util.*;

public class DataVisualizer extends Application {

    private TableView<List<String>> tableView = new TableView<>();
    private BorderPane root = new BorderPane();
    private VBox controlPane = new VBox(10);
    private VBox rightPane;
    private VBox leftPane;
    private ComboBox<String> analysisSelector = new ComboBox<>();
    private ComboBox<String> analysisCourseCompletion = new ComboBox<>();
    private ComboBox<String> analysisGradeDistribution = new ComboBox<>();
    private BarChart<String, Number> chart;
    private List<String[]> csvData;
    private ComboBox<String> courseFilter;
    private ObservableList<List<String>> masterData = FXCollections.observableArrayList();
    private FilteredList<List<String>> filteredData;
    private TextField minGradeField;
    private TextField maxGradeField;
    private ComboBox<String> passFailFilter;
    private Node[] extraControls;
    

    private File dataFile;
    private String[][] data;
    private String[] courses;
    private String[][] graduateGradesData = null;
    private String[][] studentInfoData = null;


    private ComboBox<String> courseSelector = new ComboBox<>();
    private Label courseLabel = new Label("Target Course:");
    private TextField studentIdField = new TextField("312804"); 
    private Label studentIdLabel = new Label("Target Student ID:");

    @Override
    public void start(Stage stage) {

        // --- Top Controls --- //
        Button selectFileButton = new Button("Select CSV File");
        selectFileButton.setOnAction(e -> loadCSV(stage));

        analysisSelector.getItems().addAll("Course Completion", "Performance Correlation", "Grade Prediction by Property", "Graduation Eligibility", "Grade Distribution", "Attribute-Based Performance");
        analysisSelector.setValue("Select analysis");

        analysisCourseCompletion. getItems().addAll("Bar Graph", "Histogram");
        analysisCourseCompletion.setValue("Select chart");

        analysisGradeDistribution.getItems().addAll("Box and Whiskers", "Histogram", "Violin Plot");
        analysisGradeDistribution.setValue("Select Chart");

        analysisSelector.setOnAction(e -> {
            boolean isPrediction = "Grade Prediction by Property".equals(analysisSelector.getValue());
            courseSelector.setVisible(isPrediction);
            courseLabel.setVisible(isPrediction);
            studentIdField.setVisible(isPrediction);
            studentIdLabel.setVisible(isPrediction);

            boolean isCourseCompletion = "Course Completion".equals(analysisSelector.getValue());
            analysisCourseCompletion.setVisible(isCourseCompletion);
            analysisCourseCompletion.setManaged(isCourseCompletion);

            boolean isGradeDistribution = "Grade Distribution".equals(analysisSelector.getValue());
            analysisGradeDistribution.setVisible(isGradeDistribution);
            analysisGradeDistribution.setManaged(isGradeDistribution);
        
        });

        courseSelector.setVisible(false);
        courseLabel.setVisible(false);
        studentIdField.setVisible(false);
        studentIdLabel.setVisible(false);
        analysisCourseCompletion.setVisible(false);
        analysisCourseCompletion.setManaged(false);
        analysisGradeDistribution.setVisible(false);
        analysisGradeDistribution.setManaged(false);

        Button analyseButton = new Button("Analyse");
        analyseButton.setOnAction(e -> performAnalysis());

        Label rowCountLabel = new Label("Student Count: 0");

        
        // --- Filter --- //
        courseFilter = new ComboBox<>();
        courseFilter.getItems().add("All Courses");
        courseFilter.setValue("All Courses");

        minGradeField = new TextField();
        minGradeField.setPromptText("Min");

        maxGradeField = new TextField();
        maxGradeField.setPromptText("Max");

        passFailFilter = new ComboBox<>();
        passFailFilter.getItems().addAll("All", "Passing Only", "Failing Only");
        passFailFilter.setValue("All");

        HBox filterBar = new HBox(10, courseFilter, minGradeField, maxGradeField, passFailFilter);
        filterBar.setPadding(new Insets(10, 0, 10, 0));

        HBox extraBar1 = new HBox(10, courseLabel, courseSelector);
        filterBar.setPadding(new Insets(10, 0, 10, 0));
        
        HBox extraBar2 = new HBox(10, studentIdLabel, studentIdField);
        filterBar.setPadding(new Insets(10, 0, 10, 0));

        StackPane secondarySelector = new StackPane();
        secondarySelector.getChildren().addAll(analysisCourseCompletion, analysisGradeDistribution);    

        HBox selectors = new HBox(10, analysisSelector, secondarySelector);
        
        Separator separator = new Separator();
        
        controlPane.getChildren().addAll(selectFileButton, selectors, analyseButton, filterBar, rowCountLabel, separator, extraBar1, extraBar2);
        controlPane.setStyle("-fx-padding: 10;");


        extraControls = new Node[] {
            separator,
            courseLabel,
            courseSelector,
            studentIdLabel,
            studentIdField
        };

        analysisSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean shouldShow = "Grade Prediction by Property".equals(newVal);
            showExtraControls(shouldShow);
        });

        showExtraControls(false);

        // --- Table (Bottom Left) --- //
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        ScrollPane tableScroll = new ScrollPane(tableView);
        tableScroll.setFitToWidth(true);
        tableScroll.setFitToHeight(true);

        leftPane = new VBox(10, controlPane, tableScroll);
        VBox.setVgrow(tableScroll, Priority.ALWAYS);
        leftPane.setStyle("-fx-padding: 10;");

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double staticWidth = newVal.doubleValue() * 0.5; // 50% of window
            leftPane.setPrefWidth(staticWidth);
            tableView.setPrefWidth(staticWidth - 20);
        });
        
        root.setLeft(leftPane);

        // --- Right Chart Placeholder ---
        rightPane = new VBox(10);
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Data Analysis");

        root.setCenter(rightPane);

        chart.prefWidthProperty().bind(rightPane.widthProperty());
        chart.prefHeightProperty().bind(rightPane.heightProperty());
        rightPane.getChildren().add(chart);

        // --- Scene Setup --- //
        Scene scene = new Scene(root, 1000, 600);

        // --- Handler for key events --- //
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case F11:
                    stage.setFullScreen(!stage.isFullScreen());
                    break;
                default:
                    break;
            }
        });

        // --- Filter Handler --- //
        Runnable updateFilter = () -> {

            int selectedCourseIndex = courseFilter.getSelectionModel().getSelectedIndex();

            // +1 because column 0 = student ID, column 1 = first course, etc.
            int realColIndex = selectedCourseIndex <= 0 ? -1 : selectedCourseIndex;

            filteredData.setPredicate(row -> {

                // --- Course filter --- //
                if (realColIndex != -1) {
                    String gradeStr = row.get(realColIndex);
                    if (gradeStr == null || gradeStr.isEmpty()) return false;
                }

                Double gradeValue = null;
                if (realColIndex != -1) {
                    try {
                        gradeValue = Double.parseDouble(row.get(realColIndex));
                    } catch (Exception e) {
                        return false;
                    }
                }

                // --- Min grade filter --- //
                if (!minGradeField.getText().isEmpty() && gradeValue != null) {
                    try {
                        double min = Double.parseDouble(minGradeField.getText());
                        if (gradeValue < min) return false;
                    } catch (Exception ignored) {}
                }

                // --- Max grade filter --- //
                if (!maxGradeField.getText().isEmpty() && gradeValue != null) {
                    try {
                        double max = Double.parseDouble(maxGradeField.getText());
                        if (gradeValue > max) return false;
                    } catch (Exception ignored) {}
                }

                // --- Pass/Fail filter --- //
                if (passFailFilter.getValue().equals("Passing Only") && gradeValue != null) {
                    if (gradeValue < 5.5) return false;
                }

                if (passFailFilter.getValue().equals("Failing Only") && gradeValue != null) {
                    if (gradeValue >= 5.5) return false;
                }

                return true;
            });

            rowCountLabel.setText("Student Count: " + filteredData.size());
        };

        courseFilter.setOnAction(e -> updateTableColumns());
        minGradeField.textProperty().addListener((obs, oldV, newV) -> updateFilter.run());
        maxGradeField.textProperty().addListener((obs, oldV, newV) -> updateFilter.run());
        passFailFilter.setOnAction(e -> updateFilter.run());

        stage.setScene(scene);
        stage.setTitle("Data Visualizer");
        stage.setResizable(true);
        stage.show();
    }

    private void showExtraControls(boolean show) {
        for (Node n : extraControls) {
            n.setVisible(show);
            n.setManaged(show); 
        }
    }

    private void loadCSV(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(stage);
        dataFile = file;
        data = Utility.fillArray(dataFile.getAbsolutePath());
        courses = Arrays.copyOfRange(data[0], 1, data[0].length);
        courseSelector.getItems().clear();
        courseSelector.getItems().addAll(courses);
        if (courses.length > 0) courseSelector.getSelectionModel().select(0);
        courseFilter.getItems().addAll(courses); 

        Map<String, Integer> courseToIndex = new HashMap<>();
        for (int i = 0; i < courses.length; i++) {
            courseToIndex.put(courses[i], i);
        }
        if (file != null) {
            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                csvData = reader.readAll();
                displayCSV();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void displayCSV() {
        tableView.getColumns().clear();
        masterData.clear();

        if (csvData == null || csvData.isEmpty()) return;

        String[] headers = csvData.get(0);
        for (int i = 0; i < headers.length; i++) {
            final int colIndex = i;
            TableColumn<List<String>, String> col = new TableColumn<>(headers[i]);

            col.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().get(colIndex))
            );

            tableView.getColumns().add(col);
        }

        for (int i = 1; i < csvData.size(); i++) {
            masterData.add(Arrays.asList(csvData.get(i)));
        }

        // ----- Create filtered list ----- //
        filteredData = new FilteredList<>(masterData, p -> true);
        tableView.setItems(filteredData);
    }

    private void updateTableColumns() {
        tableView.getColumns().clear();

        int selectedIndex = courseFilter.getSelectionModel().getSelectedIndex();

        // Always show StudentID
        TableColumn<List<String>, String> idCol = new TableColumn<>("StudentID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        tableView.getColumns().add(idCol);

        if (selectedIndex > 0) {
            int colIndex = selectedIndex; // because row.get(0) = StudentID
            TableColumn<List<String>, String> courseCol = new TableColumn<>(courses[selectedIndex-1]);
            courseCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(colIndex)));
            tableView.getColumns().add(courseCol);
        } else {
            // All courses
            for (int i = 0; i < courses.length; i++) {
                final int colIndex = i + 1;
                TableColumn<List<String>, String> col = new TableColumn<>(courses[i]);
                col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(colIndex)));
                tableView.getColumns().add(col);
            }
        }

        tableView.setItems(filteredData);
    }

    private void performAnalysis() {
        if (csvData == null || csvData.size() < 2) return;

        // Clear previous chart
        rightPane.getChildren().clear();

        String analysisType = analysisSelector.getValue();
        String analysisChartCC = analysisCourseCompletion.getValue();
        String analysisChartGD = analysisGradeDistribution.getValue();
        Region newChart = null;

        switch (analysisType) {
            case "Course Completion":
                if (analysisChartCC.equals("Bar Graph")) newChart = courseProgression();
                else if (analysisChartCC.equals("Histogram")) newChart = courseProgressionHistogram();
                break;
            case "Performance Correlation":
                newChart = performanceCorrelationChart();
                break;
            case "Graduation Eligibility":
                newChart = EligibleForGraduation();
                break;
            case "Grade Distribution":
                if (analysisChartGD.equals("Box and Whiskers")) newChart = gradesDistribution();
                else if (analysisChartGD.equals("Histogram")) newChart = gradesDistributionHistogram();
                else if (analysisChartGD.equals("Violin Plot")) newChart = gradesDistributionViolin();
                break;
            case "Grade Prediction by Property":
                if (studentInfoData == null) {
                    File defaultFile = new File("data/StudentInfo.csv");
                    if (defaultFile.exists()) {
                        studentInfoData = Utility.fillArray(defaultFile.getAbsolutePath());
                    } else {
                        FileChooser fc = new FileChooser();
                        File siFile = fc.showOpenDialog(new Stage());
                        if (siFile != null) {
                            studentInfoData = Utility.fillArray(siFile.getAbsolutePath());
                        } else {
                            return; 
                        }
                    }
                }
                newChart = gradePredictionChart();
                break;
            case "Attribute-Based Performance":
                // Load StudentInfo if not already loaded
                if (studentInfoData == null) {
                    File defaultFile = new File("data/StudentInfo.csv");
                    if (defaultFile.exists()) {
                        studentInfoData = Utility.fillArray(defaultFile.getAbsolutePath());
                    } else {
                        FileChooser fc = new FileChooser();
                        File siFile = fc.showOpenDialog(new Stage());
                        if (siFile != null) {
                            studentInfoData = Utility.fillArray(siFile.getAbsolutePath());
                        } else {
                            return;
                        }
                    }
                }
                // Load GraduateGrades if not already loaded
                if (graduateGradesData == null) {
                    File defaultFile = new File("data/GraduateGrades.csv");
                    if (defaultFile.exists()) {
                        graduateGradesData = Utility.fillArray(defaultFile.getAbsolutePath());
                    }
                }

                 // Swarm plot has its own ScrollPane
                VBox swarmPlot = createSwarmPlotWithControls();
                swarmPlot.prefWidthProperty().bind(rightPane.widthProperty());
                swarmPlot.prefHeightProperty().bind(rightPane.heightProperty());
                rightPane.getChildren().add(swarmPlot);
                return;  // Exit here to skip the ScrollPane wrapping below
            default:
                break;
        }

        if (newChart == null) return;

        // Wrap chart in a ScrollPane
        ScrollPane scrollPane;
        scrollPane = new ScrollPane();
        scrollPane.setContent(newChart);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPadding(new Insets(5));

        // Make ScrollPane fill rightPane
        scrollPane.prefWidthProperty().bind(rightPane.widthProperty());
        scrollPane.prefHeightProperty().bind(rightPane.heightProperty());

        // Add the new chart to the pane
        rightPane.getChildren().add(scrollPane);
    }
    
    private BarChart<String, Number> courseProgression() {
        // X-axis is categorical (course names)
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        
        xAxis.setLabel("Course (" + courses.length + ")");
        yAxis.setLabel("Completion %");
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Course Completion Progress");
        
        // Add data points
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Completion Rates");
        
        double[] completionRates = Analyzer.completionRates(data);

        for (int i = 0; i < courses.length; i++) {
            double rate = completionRates[i];
            series.getData().add(new XYChart.Data<>(courses[i], rate));
        }
        
        barChart.getData().add(series);

        // Chart config
        int courseCount = courses.length;
        double maxBarWidth = 20;
        double minBarWidth = 10;
        double gap = 5;             // gap between bars
        double minWidth = 400;      // minimum chart width

        double barWidth = Math.max(minBarWidth, Math.min(maxBarWidth, 1000.0 / courseCount));
        double totalWidth = Math.max(minWidth, courseCount * (barWidth + gap));
        barChart.setPrefWidth(totalWidth);

        return barChart;
    }

    private BarChart<String, Number> courseProgressionHistogram() {
        // --- Axes ---
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Completion Rate (%)");
        yAxis.setLabel("Number of Courses");

        
        yAxis.setMinorTickVisible(false);
        
        BarChart<String, Number> histogram = new BarChart<>(xAxis, yAxis);
        histogram.setTitle("Distribution of Course Completion Rates");
        
        // --- Data ---
        double[] completionRates = Analyzer.completionRates(data);
        
        int binSize = 10; // 0–10, 10–20, ..., 90–100
        int binCount = 100 / binSize;
        
        int[] bins = new int[binCount];
        
        // Fill bins
        for (double rate : completionRates) {
            int binIndex = Math.min((int) (rate / binSize), binCount - 1);
            bins[binIndex]++;
        }

        int maxCount = 0;
        for (int count : bins) {
            maxCount = Math.max(maxCount, count);
        }
       
        // Force integer ticks
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(maxCount + 1); // important
        yAxis.setTickUnit(1);

        // --- Series ---
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Courses per Range");

        for (int i = 0; i < binCount; i++) {
            int lower = i * binSize;
            int upper = lower + binSize;
            String label = lower + "–" + upper;

            series.getData().add(new XYChart.Data<>(label, bins[i]));
        }

        histogram.getData().add(series);

        // --- Styling / layout ---
        histogram.setLegendVisible(false);
        histogram.setCategoryGap(2);
        histogram.setBarGap(0);
        histogram.setPrefWidth(600);

        return histogram;
    }

    private ScatterChart<Number, Number> performanceCorrelationChart() {
        // Axes
        NumberAxis xAxis = new NumberAxis(3, 10, 1);
        NumberAxis yAxis = new NumberAxis(3, 10, 1);
        xAxis.setLabel("Average Early Course Grade (per student)");
        yAxis.setLabel("Average Late Course Grade (per student)");
        
        double[] rates = FileDisplayer.completionRates(data);
        List<Integer> earlyCourses = new ArrayList<>();
        List<Integer> lateCourses  = new ArrayList<>();
        for (int i = 0; i < rates.length; i++) {
            double rate = rates[i];
            if (rate >= 90) {
                earlyCourses.add(i + 1);
            } else if (rate < 50 && rate > 0) {
                lateCourses.add(i + 1);
            }
        }
        int[] earlyCols = earlyCourses.stream().mapToInt(Integer::intValue).toArray();
        int[] lateCols  = lateCourses.stream().mapToInt(Integer::intValue).toArray();
        double[] earlyAvg = Utility.studentAverage(data, earlyCols);
        double[] lateAvg  = Utility.studentAverage(data, lateCols);

        // Filter out students with NaN in either array
        List<Double> validEarly = new ArrayList<>();
        List<Double> validLate = new ArrayList<>();

        for (int i = 0; i < earlyAvg.length; i++) {
            if (!Double.isNaN(earlyAvg[i]) && !Double.isNaN(lateAvg[i])) {
                validEarly.add(earlyAvg[i]);
                validLate.add(lateAvg[i]);
            }
        }

        double[] earlyFiltered = validEarly.stream().mapToDouble(Double::doubleValue).toArray();
        double[] lateFiltered = validLate.stream().mapToDouble(Double::doubleValue).toArray();
        
        ScatterChart<Number, Number> scatter = new ScatterChart<>(xAxis, yAxis);
        
        // Calculate regression for title
        double[] coeffs = Utility.linearRegression(earlyFiltered, lateFiltered);
        scatter.setTitle(String.format(
            "Performance Correlation: Early vs Late Courses\n(r = %.3f, y = %.2fx + %.2f)", 
            Utility.correlation(earlyFiltered, lateFiltered),
            coeffs[0], 
            coeffs[1]
        ));
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Students");
        
        for (int i = 0; i < earlyFiltered.length; i++) {
            series.getData().add(new XYChart.Data<>(earlyFiltered[i], lateFiltered[i]));
        }
        
        for (XYChart.Data<Number, Number> d : series.getData()) {
            Circle dot = new Circle(3);
            dot.setStyle("-fx-fill: rgba(31, 119, 180, 0.6);");
            d.setNode(dot);
        }
        
        scatter.getData().add(series);
        
        // Add trend line after the chart is rendered
        addTrendLine(scatter, xAxis, yAxis, coeffs);
        
        return scatter;
    }

    private void addTrendLine(ScatterChart<Number, Number> scatter, 
                            NumberAxis xAxis, NumberAxis yAxis, double[] coeffs) {
        Line trendLine = new Line();
        trendLine.setStroke(Color.web("#ff7f0e"));
        trendLine.setStrokeWidth(2);
        trendLine.setMouseTransparent(true);
        
        // Update line position whenever the chart is laid out
        scatter.needsLayoutProperty().addListener((obs, oldVal, newVal) -> {
            updateTrendLine(trendLine, scatter, xAxis, yAxis, coeffs);
        });
        
        // Also update after the chart is shown
        scatter.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    updateTrendLine(trendLine, scatter, xAxis, yAxis, coeffs);
                    
                    // Add the line to the plot area
                    Pane plotArea = (Pane) scatter.lookup(".chart-plot-background").getParent();
                    if (!plotArea.getChildren().contains(trendLine)) {
                        plotArea.getChildren().add(trendLine);
                    }
                });
            }
        });
    }

    private void updateTrendLine(Line line, ScatterChart<Number, Number> scatter,
                             NumberAxis xAxis, NumberAxis yAxis, double[] coeffs) {
        double xMin = xAxis.getLowerBound();
        double xMax = xAxis.getUpperBound();
        double yAtXMin = coeffs[0] * xMin + coeffs[1];
        double yAtXMax = coeffs[0] * xMax + coeffs[1];
        
        // Clamp y values to axis bounds
        double yLower = yAxis.getLowerBound();
        double yUpper = yAxis.getUpperBound();
        
        if (yAtXMin < yLower) {
            yAtXMin = yLower;
            xMin = (yAtXMin - coeffs[1]) / coeffs[0];
        } else if (yAtXMin > yUpper) {
            yAtXMin = yUpper;
            xMin = (yAtXMin - coeffs[1]) / coeffs[0];
        }
        
        if (yAtXMax < yLower) {
            yAtXMax = yLower;
            xMax = (yAtXMax - coeffs[1]) / coeffs[0];
        } else if (yAtXMax > yUpper) {
            yAtXMax = yUpper;
            xMax = (yAtXMax - coeffs[1]) / coeffs[0];
        }
        
        // Get the plot background to find correct offset
        Node plotBackground = scatter.lookup(".chart-plot-background");
        if (plotBackground == null) return;
        
        Bounds plotBounds = plotBackground.getBoundsInParent();
        
        // Convert data coordinates to pixel coordinates relative to plot area
        double startX = plotBounds.getMinX() + xAxis.getDisplayPosition(xMin);
        double startY = plotBounds.getMinY() + yAxis.getDisplayPosition(yAtXMin);
        double endX = plotBounds.getMinX() + xAxis.getDisplayPosition(xMax);
        double endY = plotBounds.getMinY() + yAxis.getDisplayPosition(yAtXMax);
        
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);
    }

    private PieChart EligibleForGraduation(){

        String[] eligibility = Analyzer.EligibleForGraduation(data);

        int countNotEligible = 0;
        int countEligible = 0;

        //counts eligible vs not eligible students
        for(int i = 0; i < eligibility.length; i++){
            if(eligibility[i] == null)
                continue;
            if(eligibility[i].equals("NE"))
                countNotEligible++;
            else countEligible++;
        }

        PieChart.Data sliceEligible = new PieChart.Data("Eligible", countEligible);
        PieChart.Data sliceNotEligible = new PieChart.Data("Not Eligible", countNotEligible);
        PieChart pieChart = new PieChart();
        pieChart.getData().addAll(sliceEligible, sliceNotEligible);
        double total = countEligible + countNotEligible;
        pieChart.setTitle("Graduation Eligibility");
                pieChart.getData().forEach(data -> {double percent = data.getPieValue()/total * 100;
            String percentage = String.format("%.2f%%", percent);
            Tooltip toolTip = new Tooltip(percentage);
            Tooltip.install(data.getNode(), toolTip);
        });



        return pieChart;

    }
   
    private BarChart<String, Number> gradePredictionChart() {
        
        String selectedCourseName = courseSelector.getValue();
        String studentIdText = studentIdField.getText();

        int targetStudentId = Integer.parseInt(studentIdText.trim());
        

        int courseNumber = -1;
        for(int i = 1; i < data[0].length; i++) {
            if(data[0][i].equals(selectedCourseName)) {
                courseNumber = i;
                break;
            }
        }
        if(courseNumber == -1) return null;

        int[] studentIds = new int[studentInfoData.length];
        for(int i = 1; i < studentInfoData.length; i++){
                studentIds[i] = Integer.parseInt(studentInfoData[i][0].trim());
            } 

        PropertyAnalyzer quantumA = new Quantum();
        PropertyAnalyzer astroA = new Astro();
        PropertyAnalyzer bioA = new Bio();
        PropertyAnalyzer psionicA = new Psionic();
        PropertyAnalyzer symbioticA = new Symbiotic();

        double[][] quantumRes = quantumA.analyze(studentInfoData, data, courseNumber, studentIds);
        double[][] astroRes = astroA.analyze(studentInfoData, data, courseNumber, studentIds);
        double[][] bioRes = bioA.analyze(studentInfoData, data, courseNumber, studentIds);
        double[][] psionicRes = psionicA.analyze(studentInfoData, data, courseNumber, studentIds);
        double[][] symbioticRes = symbioticA.analyze(studentInfoData, data, courseNumber, studentIds);

        double[] currentGradesScores = new double[studentInfoData.length];
        java.util.Arrays.fill(currentGradesScores, -1.0);
        for (int i = 1; i < studentInfoData.length; i++) {
           
                int sID = studentIds[i];
                int gradeRow = PropertyAnalyzer.findRowById(data, sID);
                if(gradeRow != -1 && data[gradeRow][courseNumber] != null) {
                    double g = Double.parseDouble(data[gradeRow][courseNumber]);
                    if(g > 0) currentGradesScores[i] = g;
                }
            }
        PropertyReduction verify = new PropertyReduction();
        String bestProp = verify.bestProperty(quantumRes, astroRes, bioRes, psionicRes, symbioticRes, currentGradesScores);
        
        double[][] bestResultData = null;
        String[] categories = null;
        int targetCategoryIndex = -1;
        int studentRowIndex = PropertyAnalyzer.findRowById(studentInfoData, targetStudentId);

        String title = "Prediction: " + selectedCourseName;

       
        if(studentRowIndex != -1) {
           
                switch (bestProp.toLowerCase()) {
                    case "quantum": 
                        bestResultData = quantumRes; 
                        categories = new String[]{"Stable","Fractured","Chaotic","Coherent","Resonant"};
                        title += " (Quantum)";
                        targetCategoryIndex = findCategoryIndex(studentInfoData[studentRowIndex][1], categories);
                        break;
                    case "astro": 
                        bestResultData = astroRes; 
                        categories = new String[]{"1 ns/mc","2 ns/mc","3 ns/mc"};
                        title += " (Astro)";
                        targetCategoryIndex = findCategoryIndex(studentInfoData[studentRowIndex][3], categories);
                        break;
                    case "bio": 
                        bestResultData = bioRes; 
                        categories = new String[]{"Silver","Turquiose","White-Blue", "Crimson", "Violet"};
                        title += " (Bio)";
                        targetCategoryIndex = findCategoryIndex(studentInfoData[studentRowIndex][5], categories);
                        break;
                    case "psionic": 
                        bestResultData = psionicRes; 
                        categories = new String[]{"Low", "Mid-Low", "Mid-High", "High"};
                        title += " (Psionic)";
                        double val = Double.parseDouble(studentInfoData[studentRowIndex][4]);
                        targetCategoryIndex = getPsionicRange(val);
                        break;
                    case "symbiotic": 
                        bestResultData = symbioticRes; 
                        categories = new String[]{"None", "Harmonized"};
                        title += " (Symbiotic)";
                        targetCategoryIndex = findCategoryIndex(studentInfoData[studentRowIndex][2], categories);
                        break;
                }
            
        }

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Average Grade");
        xAxis.setLabel("Property Category");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(title);
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for(int j=0; j<bestResultData.length; j++) {
            double sum = 0;
            int count = 0;
            for(int k=0; k<bestResultData[j].length; k++) {
                if(bestResultData[j][k] > 0) {
                    sum += bestResultData[j][k];
                    count++;
                }
            }
            
            double avg;

            if (count > 0) {
                avg = sum / count;
            } else {
                avg = 0;
            }

            String categoryName;
            if (categories != null && j < categories.length) {
                categoryName = categories[j];
            } else {
                categoryName = "Category " + j;
            }

            String label = categoryName + "\n(n=" + count + ")";
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(label, avg);
            series.getData().add(dataPoint);

            
            final boolean isStudentCategory = (j == targetCategoryIndex);
            
            dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    if (isStudentCategory) {
                        newNode.setStyle("-fx-bar-fill: #ff9800; -fx-border-color: black; -fx-border-width: 2px;");
                    } else {
                        newNode.setStyle("-fx-bar-fill: #2196f3;");
                    }
                }
            });
        }

        barChart.getData().add(series);
        return barChart;
    }

    private int findCategoryIndex(String value, String[] categories) {
        if(value == null) return -1;
        value = value.trim();
        for(int i=0; i<categories.length; i++) {
            if(value.equalsIgnoreCase(categories[i])) return i;
        }
        return -1;
    }

    private int getPsionicRange(double value){
        if(value > -1 && value <= -0.5) return 0; // Low
        if(value > -0.5 && value <= 0) return 1;  // Mid-Low
        if(value > 0 && value <= 0.5) return 2;   // Mid-High
        if(value > 0.5 && value <= 1) return 3;   // High
        return -1;
    }

    private Region gradesDistribution() {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        double[][] grades = Utility.getGrades(data);
        
        for (int c = 0; c < courses.length; c++) {
            List<Double> courseGrades = new ArrayList<>();
            
            for (int s = 0; s < grades.length; s++) {
                if (!Double.isNaN(grades[s][c])) {
                    courseGrades.add(grades[s][c]);
                }
            }
            
            // Only add if there are valid grades
            if (!courseGrades.isEmpty()) {
                dataset.add(courseGrades, "Grades", courses[c]);
            }
        }
        
        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
            "Grade Distribution per Course",
            "Course",
            "Grade",
            dataset,
            true
        );
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLowerBound(3.0);  // Lowered since CurrentGrades might have lower grades
        
        org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        
        ChartViewer viewer = new ChartViewer(chart);
        viewer.setMouseTransparent(true);
        viewer.setMinSize(800, 600);
        viewer.setPrefSize(Math.max(800, courses.length * 30), 600);
        viewer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        StackPane container = new StackPane(viewer);
        container.setPadding(new Insets(20));
        container.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        
        return container;
    }

    /**
     * Creates a swarm plot with dynamic parameters
     * @param gradesData the grades data (CurrentGrades or GraduateGrades)
     * @param studentInfoData the student info data
     * @param attributeColumn the column index for the attribute (1-5)
     * @param attributeName display name for the attribute
     * @param minWidth plot width
     * @param height plot height
     */
    private Pane createSwarmPlot(String[][] gradesData, String[][] studentInfoData,
                             int attributeColumn, String attributeName,
                             double minWidth, double height) {

        // Get student attributes
        Map<String, String> attributes = Utility.getStudentAttribute(studentInfoData, attributeColumn);

        // Calculate average grade for each student (all courses)
        int[] allCols = new int[gradesData[0].length - 1];
        for (int i = 0; i < allCols.length; i++) {
            allCols[i] = i + 1;
        }
        double[] avgGrades = Utility.studentAverage(gradesData, allCols);

        // Group students by category
        Map<String, List<Double>> categoryGrades = new LinkedHashMap<>();

        for (int r = 1; r < gradesData.length; r++) {
            String studentId = gradesData[r][0];
            double avg = avgGrades[r - 1];

            if (Double.isNaN(avg)) continue;

            String category = attributes.get(studentId);
            if (category == null || category.isEmpty()) continue;

            categoryGrades.computeIfAbsent(category, k -> new ArrayList<>()).add(avg);
        }

        // Sort categories for consistent display
        List<String> categories = new ArrayList<>(categoryGrades.keySet());
        Collections.sort(categories);

        // Calculate dynamic width based on number of categories
        double categoryMinWidth = 150; // Minimum width per category
        double calculatedWidth = Math.max(minWidth, categories.size() * categoryMinWidth + 120);

        // Create the plot
        Pane plotPane = new Pane();
        plotPane.setPrefSize(calculatedWidth, height);
        plotPane.setMinSize(calculatedWidth, height);

        // Increased left margin for y-axis label
        double leftMargin = 90;
        double rightMargin = 30;
        double topMargin = 50;
        double bottomMargin = 80;
        double plotWidth = calculatedWidth - leftMargin - rightMargin;
        double plotHeight = height - topMargin - bottomMargin;

        // Calculate grade range from data
        double minGrade = categoryGrades.values().stream()
                .flatMap(List::stream)
                .mapToDouble(Double::doubleValue)
                .min().orElse(3.0);
        double maxGrade = categoryGrades.values().stream()
                .flatMap(List::stream)
                .mapToDouble(Double::doubleValue)
                .max().orElse(10.0);

        // Add padding to range
        minGrade = Math.floor(minGrade) - 0.5;
        maxGrade = Math.ceil(maxGrade) + 0.5;

        // Draw background
        Rectangle background = new Rectangle(leftMargin, topMargin, plotWidth, plotHeight);
        background.setFill(Color.web("#f5f5f5"));
        background.setStroke(Color.web("#cccccc"));
        plotPane.getChildren().add(background);

        // Draw horizontal grid lines and y-axis labels
        for (int grade = (int) Math.ceil(minGrade); grade <= (int) maxGrade; grade++) {
            double y = topMargin + plotHeight - ((grade - minGrade) / (maxGrade - minGrade) * plotHeight);

            Line gridLine = new Line(leftMargin, y, leftMargin + plotWidth, y);
            gridLine.setStroke(Color.web("#dddddd"));
            plotPane.getChildren().add(gridLine);

            Text label = new Text(String.valueOf(grade));
            label.setX(leftMargin - 25);
            label.setY(y + 4);
            label.setFont(Font.font(11));
            plotPane.getChildren().add(label);
        }

        // Y-axis title - positioned further left to avoid overlap
        Text yTitle = new Text("Average Grade");
        yTitle.setFont(Font.font(12));
        
        // Create a Group to rotate, makes positioning easier
        Group yTitleGroup = new Group(yTitle);
        yTitleGroup.setRotate(-90);
        yTitleGroup.setLayoutX(5);
        yTitleGroup.setLayoutY(topMargin + plotHeight / 2);
        plotPane.getChildren().add(yTitleGroup);

        // Calculate category positions
        double categoryWidth = plotWidth / categories.size();

        // Dynamic dot radius based on plot size and data density
        int maxCategorySize = categoryGrades.values().stream()
                .mapToInt(List::size)
                .max().orElse(1);
        double dotRadius = Math.max(2, Math.min(4, categoryWidth / (maxCategorySize * 0.15)));
        double jitterStep = dotRadius * 2.2;

        // Draw swarm for each category
        for (int catIndex = 0; catIndex < categories.size(); catIndex++) {
            String category = categories.get(catIndex);
            List<Double> grades = categoryGrades.get(category);

            double centerX = leftMargin + categoryWidth * catIndex + categoryWidth / 2;

            // X-axis label - full text, centered
            Text catLabel = new Text(category);
            catLabel.setFont(Font.font(10));
            double labelWidth = catLabel.getLayoutBounds().getWidth();
            catLabel.setX(centerX - labelWidth / 2);
            catLabel.setY(topMargin + plotHeight + 20);
            plotPane.getChildren().add(catLabel);

            // Sort grades for better swarm layout
            Collections.sort(grades);

            // Track placed dots for collision detection
            List<double[]> placedDots = new ArrayList<>();

            for (Double grade : grades) {
                double y = topMargin + plotHeight - ((grade - minGrade) / (maxGrade - minGrade) * plotHeight);

                // Find x position that doesn't collide
                double x = centerX;
                int direction = 1;
                int step = 0;

                while (collides(x, y, placedDots, dotRadius * 2)) {
                    step++;
                    x = centerX + direction * step * jitterStep;
                    direction *= -1;

                    if (step > 50 || Math.abs(x - centerX) > categoryWidth / 2 - dotRadius) break;
                }

                placedDots.add(new double[]{x, y});

                Circle dot = new Circle(x, y, dotRadius);
                dot.setFill(Color.web("#1f77b4", 0.7));
                dot.setStroke(Color.web("#1f77b4"));
                dot.setStrokeWidth(0.5);

                Tooltip tooltip = new Tooltip(String.format("%s: %.2f", category, grade));
                Tooltip.install(dot, tooltip);

                plotPane.getChildren().add(dot);
            }

            // Draw category mean line
            double mean = grades.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double meanY = topMargin + plotHeight - ((mean - minGrade) / (maxGrade - minGrade) * plotHeight);
            Line meanLine = new Line(centerX - 20, meanY, centerX + 20, meanY);
            meanLine.setStroke(Color.web("#d62728"));
            meanLine.setStrokeWidth(2);
            plotPane.getChildren().add(meanLine);
        }

        // Title
        Text title = new Text("Grade by " + attributeName);
        title.setFont(Font.font("System", FontWeight.BOLD, 14));
        double titleWidth = title.getLayoutBounds().getWidth();
        title.setX(leftMargin + (plotWidth - titleWidth) / 2);
        title.setY(30);
        plotPane.getChildren().add(title);

        // X-axis title
        Text xTitle = new Text(attributeName);
        xTitle.setFont(Font.font(12));
        double xTitleWidth = xTitle.getLayoutBounds().getWidth();
        xTitle.setX(leftMargin + (plotWidth - xTitleWidth) / 2);
        xTitle.setY(topMargin + plotHeight + 55);
        plotPane.getChildren().add(xTitle);

        // Legend for mean line
        Line legendLine = new Line(calculatedWidth - 80, 25, calculatedWidth - 50, 25);
        legendLine.setStroke(Color.web("#d62728"));
        legendLine.setStrokeWidth(2);
        plotPane.getChildren().add(legendLine);

        Text legendText = new Text("Mean");
        legendText.setX(calculatedWidth - 45);
        legendText.setY(29);
        legendText.setFont(Font.font(10));
        plotPane.getChildren().add(legendText);

        return plotPane;
    }

    private VBox createSwarmPlotWithControls() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));

        // Attribute selector
        Label attributeLabel = new Label("Select Student Attribute:");
        ComboBox<String> attributeSelector = new ComboBox<>();
        attributeSelector.getItems().addAll(
            "Quantum Coherence Threshold",
            "Symbiotic Network Compatibility",
            "Astro-Temporal Drift Resistance",
            "Bio-Luminal Transmission"
        );
        attributeSelector.setValue("Quantum Coherence Threshold");

        Map<String, Integer> attributeColumns = new HashMap<>();
        attributeColumns.put("Quantum Coherence Threshold", 1);
        attributeColumns.put("Symbiotic Network Compatibility", 2);
        attributeColumns.put("Astro-Temporal Drift Resistance", 3);
        attributeColumns.put("Bio-Luminal Transmission", 5);

        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.getChildren().addAll(
            attributeLabel, attributeSelector
        );

        // Use ScrollPane for the plot to enable horizontal scrolling
        ScrollPane plotScrollPane = new ScrollPane();
        plotScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        plotScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        plotScrollPane.setFitToHeight(true);
        plotScrollPane.setFitToWidth(false);  // Important: allow horizontal scrolling
        plotScrollPane.setPrefHeight(550);

        // Initial plot
        Pane initialPlot = createSwarmPlot(
            data, studentInfoData,
            attributeColumns.get(attributeSelector.getValue()),
            attributeSelector.getValue(),
            800, 500
        );
        plotScrollPane.setContent(initialPlot);

        // Update plot when selection changes
        Runnable updatePlot = () -> {
            String[][] selectedData = data;

            Pane newPlot = createSwarmPlot(
                selectedData, studentInfoData,
                attributeColumns.get(attributeSelector.getValue()),
                attributeSelector.getValue(),
                800, 500
            );
            plotScrollPane.setContent(newPlot);
        };

        attributeSelector.setOnAction(e -> updatePlot.run());

        container.getChildren().addAll(controls, plotScrollPane);
        VBox.setVgrow(plotScrollPane, Priority.ALWAYS);

        return container;
    }

    private VBox gradesDistributionHistogram() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        
        // Course selector
        Label courseLabel = new Label("Select Course:");
        ComboBox<String> courseSelector = new ComboBox<>();
        courseSelector.getItems().add("All Courses");
        courseSelector.getItems().addAll(Arrays.asList(courses));
        courseSelector.setValue("All Courses");
        
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.getChildren().addAll(courseLabel, courseSelector);
        
        // Chart container
        StackPane chartContainer = new StackPane();
        chartContainer.setPrefSize(800, 500);
        
        // Initial chart
        JFreeChart initialChart = createHistogramChart("All Courses", -1);
        ChartViewer initialViewer = new ChartViewer(initialChart);
        initialViewer.setMouseTransparent(true);
        chartContainer.getChildren().add(initialViewer);
        
        // Update chart when selection changes
        courseSelector.setOnAction(e -> {
            String selected = courseSelector.getValue();
            int courseIndex = -1; // -1 means all courses
            
            if (!selected.equals("All Courses")) {
                courseIndex = Arrays.asList(courses).indexOf(selected);
            }
            
            JFreeChart newChart = createHistogramChart(selected, courseIndex);
            ChartViewer newViewer = new ChartViewer(newChart);
            newViewer.setMouseTransparent(true);
            chartContainer.getChildren().setAll(newViewer);
        });
        
        container.getChildren().addAll(controls, chartContainer);
        VBox.setVgrow(chartContainer, Priority.ALWAYS);
        
        return container;
    }

    private JFreeChart createHistogramChart(String title, int courseIndex) {
        double[][] grades = Utility.getGrades(data);
        
        // Collect grades
        List<Double> gradeList = new ArrayList<>();
        
        if (courseIndex == -1) {
            // All courses
            for (int s = 0; s < grades.length; s++) {
                for (int c = 0; c < grades[s].length; c++) {
                    if (!Double.isNaN(grades[s][c])) {
                        gradeList.add(grades[s][c]);
                    }
                }
            }
        } else {
            // Specific course
            for (int s = 0; s < grades.length; s++) {
                if (!Double.isNaN(grades[s][courseIndex])) {
                    gradeList.add(grades[s][courseIndex]);
                }
            }
        }
        
        // Convert to array for histogram
        double[] gradeArray = gradeList.stream().mapToDouble(Double::doubleValue).toArray();
        
        // Create dataset with bins for each grade (3-10)
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.FREQUENCY);
        // 8 bins for the range 2.5 to 10.5 (each bin covers exactly 1 grade)
        dataset.addSeries("Grades", gradeArray, 8, 2.5, 10.5);
        
        // Create chart
        JFreeChart chart = ChartFactory.createHistogram(
            "Grade Distribution: " + title,
            "Grade",
            "Frequency",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Customize appearance
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setDomainGridlinePaint(java.awt.Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(java.awt.Color.LIGHT_GRAY);
        
        // Set x-axis range
        org.jfree.chart.axis.NumberAxis domainAxis = (org.jfree.chart.axis.NumberAxis) plot.getDomainAxis();
        domainAxis.setRange(2.5, 10.5);
        domainAxis.setTickUnit(new NumberTickUnit(1));
        
        // Bar color
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new java.awt.Color(31, 119, 180)); // Blue color
        renderer.setBarPainter(new StandardXYBarPainter()); // Flat bars (no gradient)
        renderer.setShadowVisible(false);
        renderer.setMargin(0.0);
        
        // Add statistics to subtitle
        if (!gradeList.isEmpty()) {
            double mean = gradeList.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double stdDev = calculateStdDev(gradeList, mean);
            
            chart.addSubtitle(new TextTitle(
                String.format("n = %d, Mean = %.2f, Std Dev = %.2f", gradeList.size(), mean, stdDev)
            ));
        }
        
        return chart;
    }

    private double calculateStdDev(List<Double> values, double mean) {
        double sumSquaredDiff = 0;
        for (Double value : values) {
            sumSquaredDiff += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sumSquaredDiff / values.size());
    }

    private boolean collides(double x, double y, List<double[]> placed, double minDist) {
        for (double[] pos : placed) {
            double dx = x - pos[0];
            double dy = y - pos[1];
            if (Math.sqrt(dx * dx + dy * dy) < minDist) {
                return true;
            }
        }
        return false;
    }
    

    private VBox gradesDistributionViolin() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        
        // Course selector - show all courses or subset
        Label viewLabel = new Label("View:");
        ComboBox<String> viewSelector = new ComboBox<>();
        viewSelector.getItems().addAll("All Courses", "First 10 Courses", "Last 10 Courses");
        viewSelector.setValue("All Courses");
        
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.getChildren().addAll(viewLabel, viewSelector);
        
        // ScrollPane for the plot
        ScrollPane plotScrollPane = new ScrollPane();
        plotScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        plotScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        plotScrollPane.setFitToHeight(true);
        plotScrollPane.setFitToWidth(false);
        plotScrollPane.setPrefHeight(620);
        
        // Initial plot
        Pane initialPlot = createViolinPlot(0, courses.length);
        plotScrollPane.setContent(initialPlot);
        
        // Update plot when selection changes
        viewSelector.setOnAction(e -> {
            String selected = viewSelector.getValue();
            int start = 0;
            int end = courses.length;
            
            if (selected.equals("First 10 Courses")) {
                end = Math.min(10, courses.length);
            } else if (selected.equals("Last 10 Courses")) {
                start = Math.max(0, courses.length - 10);
            }
            
            Pane newPlot = createViolinPlot(start, end);
            plotScrollPane.setContent(newPlot);
        });
        
        container.getChildren().addAll(controls, plotScrollPane);
        VBox.setVgrow(plotScrollPane, Priority.ALWAYS);
        
        return container;
    }

    private Pane createViolinPlot(int startCourse, int endCourse) {
        double[][] grades = Utility.getGrades(data);
        int numCourses = endCourse - startCourse;
        
        // Calculate dimensions
        double violinWidth = 80;
        double violinSpacing = 100;
        double leftMargin = 90;
        double rightMargin = 30;
        double topMargin = 50;
        double bottomMargin = 130;
        
        double plotWidth = leftMargin + rightMargin + numCourses * violinSpacing;
        double plotHeight = 600;
        double chartHeight = plotHeight - topMargin - bottomMargin;
        
        Pane plotPane = new Pane();
        plotPane.setPrefSize(plotWidth, plotHeight);
        plotPane.setMinSize(plotWidth, plotHeight);
        
        // Grade range
        double minGrade = 3.0;
        double maxGrade = 10.0;
        
        // Draw background
        Rectangle background = new Rectangle(leftMargin, topMargin, 
                plotWidth - leftMargin - rightMargin, chartHeight);
        background.setFill(Color.web("#f9f9f9"));
        background.setStroke(Color.web("#cccccc"));
        plotPane.getChildren().add(background);
        
        // Draw horizontal grid lines and y-axis labels
        for (int grade = (int) minGrade; grade <= (int) maxGrade; grade++) {
            double y = topMargin + chartHeight - ((grade - minGrade) / (maxGrade - minGrade) * chartHeight);
            
            Line gridLine = new Line(leftMargin, y, plotWidth - rightMargin, y);
            gridLine.setStroke(Color.web("#e0e0e0"));
            plotPane.getChildren().add(gridLine);
            
            Text label = new Text(String.valueOf(grade));
            label.setX(leftMargin - 25);
            label.setY(y + 4);
            label.setFont(Font.font(11));
            plotPane.getChildren().add(label);
        }
        
        // Y-axis title
        Text yTitle = new Text("Grade");
        yTitle.setFont(Font.font(12));
        Group yTitleGroup = new Group(yTitle);
        yTitleGroup.setRotate(-90);
        yTitleGroup.setLayoutX(20);
        yTitleGroup.setLayoutY(topMargin + chartHeight / 2 + yTitle.getLayoutBounds().getWidth() / 2);
        plotPane.getChildren().add(yTitleGroup);
        
        // Draw violin for each course
        for (int i = 0; i < numCourses; i++) {
            int courseIndex = startCourse + i;
            
            // Collect grades for this course
            List<Double> courseGrades = new ArrayList<>();
            for (int s = 0; s < grades.length; s++) {
                if (!Double.isNaN(grades[s][courseIndex])) {
                    courseGrades.add(grades[s][courseIndex]);
                }
            }
            
            if (courseGrades.isEmpty()) continue;
            
            double centerX = leftMargin + i * violinSpacing + violinSpacing / 2;
            
            // Calculate kernel density estimation
            double[] density = calculateKernelDensity(courseGrades, minGrade, maxGrade, 50);
            double maxDensity = Arrays.stream(density).max().orElse(1);
            
            // Normalize density to fit within violin width
            double halfWidth = violinWidth / 2;
            
            // Create violin shape (polygon)
            Polygon violin = new Polygon();
            violin.setFill(Color.web("#1f77b4", 0.6));
            violin.setStroke(Color.web("#1f77b4"));
            violin.setStrokeWidth(1);
            
            // Left side (top to bottom)
            for (int j = density.length - 1; j >= 0; j--) {
                double gradeValue = minGrade + (maxGrade - minGrade) * j / (density.length - 1);
                double y = topMargin + chartHeight - ((gradeValue - minGrade) / (maxGrade - minGrade) * chartHeight);
                double x = centerX - (density[j] / maxDensity) * halfWidth;
                violin.getPoints().addAll(x, y);
            }
            
            // Right side (bottom to top)
            for (int j = 0; j < density.length; j++) {
                double gradeValue = minGrade + (maxGrade - minGrade) * j / (density.length - 1);
                double y = topMargin + chartHeight - ((gradeValue - minGrade) / (maxGrade - minGrade) * chartHeight);
                double x = centerX + (density[j] / maxDensity) * halfWidth;
                violin.getPoints().addAll(x, y);
            }
            
            plotPane.getChildren().add(violin);
            
            // Add box plot inside violin
            Collections.sort(courseGrades);
            double median = getPercentile(courseGrades, 50);
            double q1 = getPercentile(courseGrades, 25);
            double q3 = getPercentile(courseGrades, 75);
            double min = courseGrades.get(0);
            double max = courseGrades.get(courseGrades.size() - 1);
            
            // Inner box (Q1 to Q3)
            double boxWidth = 10;
            double q1Y = topMargin + chartHeight - ((q1 - minGrade) / (maxGrade - minGrade) * chartHeight);
            double q3Y = topMargin + chartHeight - ((q3 - minGrade) / (maxGrade - minGrade) * chartHeight);
            
            Rectangle box = new Rectangle(centerX - boxWidth / 2, q3Y, boxWidth, q1Y - q3Y);
            box.setFill(Color.WHITE);
            box.setStroke(Color.BLACK);
            box.setStrokeWidth(1);
            plotPane.getChildren().add(box);
            
            // Median line
            double medianY = topMargin + chartHeight - ((median - minGrade) / (maxGrade - minGrade) * chartHeight);
            Line medianLine = new Line(centerX - boxWidth / 2, medianY, centerX + boxWidth / 2, medianY);
            medianLine.setStroke(Color.BLACK);
            medianLine.setStrokeWidth(2);
            plotPane.getChildren().add(medianLine);
            
            // Whiskers
            double minY = topMargin + chartHeight - ((min - minGrade) / (maxGrade - minGrade) * chartHeight);
            double maxY = topMargin + chartHeight - ((max - minGrade) / (maxGrade - minGrade) * chartHeight);
            
            Line lowerWhisker = new Line(centerX, q1Y, centerX, minY);
            lowerWhisker.setStroke(Color.BLACK);
            plotPane.getChildren().add(lowerWhisker);
            
            Line upperWhisker = new Line(centerX, q3Y, centerX, maxY);
            upperWhisker.setStroke(Color.BLACK);
            plotPane.getChildren().add(upperWhisker);
            
            // X-axis label (course name) - fully vertical
            String courseName = courses[courseIndex];
            String displayName = courseName;
            Text courseLabel = new Text(displayName);
            courseLabel.setFont(Font.font(9));

            // Get the text width before rotation (this becomes height after rotation)
            double textWidth = courseLabel.getLayoutBounds().getWidth();

            // Set rotation pivot to the start of the text
            courseLabel.setRotate(-90);

            // Position: centerX for horizontal, and fixed Y position
            // The text rotates around its center, so we need to offset by half the text width
            courseLabel.setX(centerX - courseLabel.getLayoutBounds().getHeight() / 2 - 20);
            courseLabel.setY(topMargin + chartHeight + 15 + textWidth / 2);

            Tooltip tooltip = new Tooltip(courseName);
            Tooltip.install(courseLabel, tooltip);

            plotPane.getChildren().add(courseLabel);
        }
        
        // Title
        Text title = new Text("Grade Distribution by Course (Violin Plot)");
        title.setFont(Font.font("System", FontWeight.BOLD, 14));
        title.setX(leftMargin + (plotWidth - leftMargin - rightMargin) / 2 - title.getLayoutBounds().getWidth() / 2);
        title.setY(30);
        plotPane.getChildren().add(title);
        
        return plotPane;
    }

    private double[] calculateKernelDensity(List<Double> data, double min, double max, int numPoints) {
        double[] density = new double[numPoints];
        double bandwidth = 0.4; // Smoothing parameter
        
        for (int i = 0; i < numPoints; i++) {
            double x = min + (max - min) * i / (numPoints - 1);
            double sum = 0;
            
            for (Double value : data) {
                // Gaussian kernel
                double u = (x - value) / bandwidth;
                sum += Math.exp(-0.5 * u * u) / Math.sqrt(2 * Math.PI);
            }
            
            density[i] = sum / (data.size() * bandwidth);
        }
        
        return density;
    }

    private double getPercentile(List<Double> sortedData, double percentile) {
        if (sortedData.isEmpty()) return 0;
        
        double index = (percentile / 100) * (sortedData.size() - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);
        
        if (lower == upper) {
            return sortedData.get(lower);
        }
        
        double fraction = index - lower;
        return sortedData.get(lower) * (1 - fraction) + sortedData.get(upper) * fraction;
    }

    public static void main(String[] args) {
        launch();
    }
}
