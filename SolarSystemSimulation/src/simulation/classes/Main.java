package simulation.classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Scanner;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Main extends Application {
	private static boolean ENABLED_TRACES = false;
	private static boolean ENABLED_LABELS = false;
	private static boolean ENABLED_LINES_TO_SUN = false;

	public static void main(String[] args) {
		launch(args);
	}

	public static ArrayList<String> getColours() {

		// Create a list of Color objects
		Field[] fields = Color.class.getFields();
		ArrayList<String> colourNames = new ArrayList<String>();

		for (Field field : fields) {
			if (field.getType() == Color.class) {
				colourNames.add(
						field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1).toLowerCase());
			}
		}

		return colourNames;
	}

	@Override
	public void start(Stage primaryStage) {

		try {

			// Get a list of all Color objects
			ObservableList<String> colourKeyList = FXCollections.observableList(Main.getColours());
			Collections.sort(colourKeyList);

			ArrayList<Planet> planets = new ArrayList<Planet>();
			ArrayList<Planet> planetsStatic = new ArrayList<Planet>();

			// Retrieve system's monitor size
			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

			// Configure primary stage to fit on the screen
			primaryStage.setTitle("Solar System Simulation");
			primaryStage.setResizable(false);
			primaryStage.setWidth(screenBounds.getWidth() / 1.8);
			primaryStage.setHeight(screenBounds.getHeight() / 1.2);

			// Declare Pane objects for holding GUI components
			GridPane layout = new GridPane();
			GridPane menu = new GridPane();
			GridPane buttons = new GridPane();
			GridPane sliders = new GridPane();
			Pane canvasContainer = new Pane();

			// Configure GridPane objects
			layout.setAlignment(Pos.TOP_CENTER);
			layout.setHgap(0);
			layout.setVgap(0);
			layout.setPadding(new Insets(0));

			menu.setPadding(new Insets(15, 10, 10, 10));
			menu.setHgap(0);
			menu.setAlignment(Pos.BOTTOM_CENTER);

			buttons.setHgap(10);
			buttons.setVgap(5);
			buttons.setPadding(new Insets(0));
			buttons.setAlignment(Pos.BOTTOM_LEFT);

			sliders.setHgap(10);
			sliders.setVgap(5);
			sliders.setPadding(new Insets(0));
			sliders.setAlignment(Pos.BOTTOM_RIGHT);

			// Define column constraints to display menu items
			ColumnConstraints buttonsColumn = new ColumnConstraints();
			ColumnConstraints slidersColumn = new ColumnConstraints();

			buttonsColumn.setPercentWidth(50);
			slidersColumn.setPercentWidth(50);

			menu.getColumnConstraints().addAll(buttonsColumn, slidersColumn);

			// Declare necessary GUI components
			ToggleButton startPauseSim = new ToggleButton("Start/Pause Simulation");
			Button resetSim = new Button("Reset Simulation");
			Button drawTraces = new Button("Draw Planets' Paths");
			Button showLabels = new Button("Display Planet Info");
			Button drawLinesToSun = new Button("Draw Lines to Sun");
			Button changePlanetProperties = new Button("Make Changes to Planets");

			MenuBar menuBar = new MenuBar();
			Menu menuFile = new Menu("File");
			Menu menuEdit = new Menu("Edit");
			Menu menuView = new Menu("View");
			menuBar.getMenus().addAll(menuFile, menuEdit, menuView);

			MenuItem loadDefSys = new MenuItem("Load default system");
			MenuItem createSys = new MenuItem("Create new system");
			MenuItem saveSys = new MenuItem("Save system");
			MenuItem loadSys = new MenuItem("Load system");

			if (planets.isEmpty()) {
				saveSys.setDisable(true);
			}

			menuFile.getItems().addAll(loadDefSys, createSys, saveSys, loadSys);

			Label accuracySliderLabel = new Label(
					"Simulation Accuracy (Timestep used in calculation)\n<One day - One month>");
			Label speedSliderLabel = new Label("Calculation Frequency\n(How many times a second)");
			Slider accuracySlider = new Slider(0, Planet.ONE_YEAR / 12, Planet.ONE_DAY);
			Slider speedSlider = new Slider(0, 2000, 50);

			Canvas mainCanvas = new Canvas();
			Canvas traceCanvas = new Canvas();

			GraphicsContext gcMain = mainCanvas.getGraphicsContext2D();
			GraphicsContext gcTrace = traceCanvas.getGraphicsContext2D();
			canvasContainer = new Pane(traceCanvas, mainCanvas);

			mainCanvas.setWidth(screenBounds.getWidth());
			mainCanvas.setHeight(screenBounds.getHeight());
			traceCanvas.setWidth(screenBounds.getWidth());
			traceCanvas.setHeight(screenBounds.getHeight());

			// Settings for GUI components
			startPauseSim.setMaxWidth(Double.MAX_VALUE);
			resetSim.setMaxWidth(Double.MAX_VALUE);
			drawTraces.setMaxWidth(Double.MAX_VALUE);
			showLabels.setMaxWidth(Double.MAX_VALUE);
			drawLinesToSun.setMaxWidth(Double.MAX_VALUE);
			changePlanetProperties.setMaxWidth(Double.MAX_VALUE);

			accuracySlider.setBlockIncrement(Planet.ONE_DAY * 7);
			accuracySlider.setShowTickMarks(true);
			accuracySlider.setMajorTickUnit(Planet.ONE_DAY * 30);

			speedSlider.setBlockIncrement(200);
			speedSlider.setShowTickMarks(true);
			speedSlider.setShowTickLabels(true);
			speedSlider.setMajorTickUnit(200);

			buttons.setMaxWidth(Double.MAX_VALUE);
			sliders.setMaxWidth(Double.MAX_VALUE);

			canvasContainer.setStyle("-fx-background-color: black");
			menu.setStyle("-fx-background-color: white");

			// menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
			gcTrace.setLineWidth(0.3);

			// Populate GridPanes with GUI components
			layout.add(menuBar, 0, 0);
			layout.add(canvasContainer, 0, 1);
			layout.add(menu, 0, 2);

			menu.add(buttons, 0, 0);
			menu.add(sliders, 1, 0);
			buttons.add(startPauseSim, 0, 0);
			buttons.add(resetSim, 0, 1);
			buttons.add(drawTraces, 1, 0);
			buttons.add(showLabels, 1, 2);
			buttons.add(changePlanetProperties, 0, 2);
			buttons.add(drawLinesToSun, 1, 1);
			sliders.add(accuracySliderLabel, 0, 0);
			sliders.add(accuracySlider, 0, 1);
			sliders.add(speedSliderLabel, 1, 0);
			sliders.add(speedSlider, 1, 1);

			// Declare an instance of Brute Force Simulation class
			SimulationBF sim = new SimulationBF();

			// Populate starting screen with planets
			drawPlanets(planets, gcMain, gcTrace);

			// Create a Timeline object that will periodically re-draw planets as their
			// positions change
			Timeline timeline = new Timeline(
					new KeyFrame(Duration.seconds(1 / speedSlider.getValue()), new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent e) {
							e.consume();
							sim.checkForces(planets);

							gcMain.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
							drawPlanets(planets, gcMain, gcTrace);
						}
					}));

			// Set the Timeline to repeat forever
			timeline.setCycleCount(Animation.INDEFINITE);

			// Set the scene and display it
			Scene scene = new Scene(layout, primaryStage.getWidth(), primaryStage.getHeight());
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			primaryStage.show();

			// ------------------------------------------------- ACTION LISTENERS
			// -------------------------------------------------

			loadDefSys.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {

					e.consume();

					ButtonType ok = ButtonType.OK;
					Optional<ButtonType> result = Optional.of(ok);

					if (!planets.isEmpty()) {
						Alert warning = new Alert(AlertType.CONFIRMATION);
						warning.setTitle("Are you sure?");
						warning.setHeaderText(null);
						warning.setContentText("Loading the default system will remove the current system!"
								+ "\nDo you want to continue?");

						result = warning.showAndWait();

					}

					if (result.get() == ButtonType.OK) {

						// Stop the animation if play button is selected
						if (startPauseSim.isSelected()) {
							timeline.stop();
							startPauseSim.setSelected(false);
						}

						// Empty the planet ArrayList and clear the screen
						planets.clear();
						gcMain.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());

						planets.add(
								new Planet("Sun", 500.0, 365.0, 0.0, 0.0, 0.0, 0.0, Planet.SUN_MASS, Color.YELLOW, 6));

						planets.add(new Planet("Mercury", 505.85, 365.0, 0.0, 9.9921636, 0.0, 0.0, Planet.MERCURY_MASS,
								Color.RED, 3));

						planets.add(new Planet("Venus", 510.86, 365.0, 0.0, 7.3781799, 0.0, 0.0, Planet.VENUS_MASS,
								Color.SADDLEBROWN, 3));

						planets.add(new Planet("Earth", 515, 365.0, 0.0, 6.283, 0.0, 0.0, Planet.EARTH_MASS, Color.BLUE,
								3));

						planets.add(new Planet("Mars", 522.86, 365.0, 0.0, 5.0804039, 0.0, 0.0, Planet.MARS_MASS,
								Color.FIREBRICK, 3));

						planets.add(new Planet("Jupiter", 578.045, 365.0, 0.0, 2.7615473, 0.0, 0.0, Planet.MARS_MASS,
								Color.ROSYBROWN, 4));

						planets.add(new Planet("Saturn", 642.95, 365.0, 0.0, 2.023729, 0.0, 0.0, Planet.MARS_MASS,
								Color.GOLD, 4));

						planets.add(new Planet("Uranus", 787.7, 365.0, 0.0, 1.433475, 0.0, 0.0, Planet.MARS_MASS,
								Color.AQUAMARINE, 4));

						planets.add(new Planet("Neptune", 950.9, 365.0, 0.0, 1.138348, 0.0, 0.0, Planet.MARS_MASS,
								Color.DODGERBLUE, 4));

						for(Planet item:planets) {
							planetsStatic.add(item);
						}

						drawPlanets(planets, gcMain, gcTrace);

						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setHeaderText(null);
						alert.setContentText("Default system loaded!");
						alert.showAndWait();

						if (!planets.isEmpty()) {
							saveSys.setDisable(false);
						}

					}

				}
			});

			createSys.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {

					e.consume();

					Optional<ButtonType> result = Optional.of(ButtonType.OK);

					if (!planets.isEmpty()) {
						Alert warning = new Alert(AlertType.WARNING);
						warning.setTitle("Are you sure?");
						warning.setHeaderText(null);
						warning.setContentText("Choosing to create a new system will remove the current system!"
								+ "\nDo you want to continue?");

						result = warning.showAndWait();
					}

					if (result.get() == ButtonType.OK) {

						// Stop the animation if play button is selected
						if (startPauseSim.isSelected()) {
							timeline.stop();
							startPauseSim.setSelected(false);
						}

						// Empty the planet ArrayList and clear the screen
						planets.clear();
						gcMain.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());

						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Adding new Planets");
						alert.setHeaderText(null);
						alert.setContentText("Click on a spot where you'd like to place a new planet"
								+ "\nRepeat doing so until all desired planets are placed");
						alert.initModality(Modality.WINDOW_MODAL);
						alert.show();
					}


				}
			});

			saveSys.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) throws NullPointerException{

					//Consume event
					e.consume();

					//Check if the simulation is running, stop if it is
					if(startPauseSim.isSelected()) {
						timeline.stop();
						startPauseSim.setSelected(false);
					}

					//Create necessary objects for saving the file
					FileChooser fileChooser = new FileChooser();
					PrintWriter output = null;

					//Set extra options to objects
					fileChooser.setTitle("Save System");
					fileChooser.setInitialDirectory(new File("/home/bullseye/"));
					fileChooser.setInitialFileName(".csv");
					fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV files (*.csv)","*.csv"));

					try{

						//Display FileChooser
						File file = fileChooser.showSaveDialog(primaryStage);
						String filePath = file.getPath();

						//If a file is successfully chosen
						if(file != null) {

							//If a chosen file doesn't end with ".csv"
							if(!filePath.toLowerCase().endsWith(".csv")){
								file = new File(filePath + ".csv");
							}

							//Initiate file writer
							output = new PrintWriter(file);

							//Write planets' info to a file
							for(Planet planet:planets) {
								output.write(planet.getName() + ",");
								output.write(planet.getPosX() + ",");
								output.write(planet.getPosY() + ",");
								output.write(planet.getVelX() + ",");
								output.write(planet.getVelY() + ",");
								output.write(planet.getForceX() + ",");
								output.write(planet.getForceY() + ",");
								output.write(planet.getMass() + ",");
								output.write(planet.getColor() + ",");

								//If it's the last planet to be saved
								if(planets.indexOf(planet) == planets.size()) {
									output.write(planet.getSize() + "");
								}else {
									output.write(planet.getSize() + "\n");
								}
							}

							//Display a "success" message
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Save System");
							alert.setHeaderText(null);
							alert.setContentText("System saved successfully!");
							alert.show();

						}

					}catch(FileNotFoundException ex) {
						System.out.println("Error encountered!: File Not Found");
					}finally {
						output.close();
					}
				}
			});

			loadSys.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					
					//Consume event
					e.consume();

					//Check if the simulation is running, stop if it is
					if(startPauseSim.isSelected()) {
						timeline.stop();
						startPauseSim.setSelected(false);
					}

					//Define objects for file reading
					FileChooser fileChooser = new FileChooser();
					Scanner input = null;
					Scanner lineInput = null;

					//Set extra options to objects
					fileChooser.setTitle("Load System");
					fileChooser.setInitialDirectory(new File("/home/bullseye/"));

					try {
						//Clear the current list of planets
						planets.clear();
						planetsStatic.clear();
						
						File file = fileChooser.showOpenDialog(primaryStage);
						input = new Scanner(file);

						//For every line of input in a file
						while(input.hasNextLine()) {

							//Take in each line and add its information to a new planet object
							String line = input.nextLine();
							lineInput = new Scanner(line);
							lineInput.useDelimiter(",");
							
							String name = lineInput.next();
							Double posX = lineInput.nextDouble();
							Double posY = lineInput.nextDouble();
							Double velX = lineInput.nextDouble();
							Double velY = lineInput.nextDouble();
							Double forceX = lineInput.nextDouble();
							Double forceY = lineInput.nextDouble();
							Double mass = lineInput.nextDouble();
							Color color = Color.valueOf(lineInput.next().toLowerCase());
							Double size = lineInput.nextDouble();

							planets.add(new Planet(name, posX, posY, velX, velY, forceX, forceY, mass, color, size));
							planetsStatic.add(new Planet(name, posX, posY, velX, velY, forceX, forceY, mass, color, size));

						}
						
						//Draw the new list of planets
						drawPlanets(planets, gcMain, gcTrace);

					}catch(FileNotFoundException ex) {
						ex.printStackTrace();
						System.out.println("Error encountered!: File Not Found");
					}finally {
						input.close();
					}
					
					//Display a "success" message
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Load System");
					alert.setHeaderText(null);
					alert.setContentText("System loaded successfully!");
					alert.show();

				}

			});

			startPauseSim.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					e.consume();

					if (startPauseSim.isSelected()) {
						timeline.play();
					} else {
						timeline.pause();
					}

				}
			});

			resetSim.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					e.consume();

					timeline.stop();

					gcMain.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
					planets.clear();

					while (planets.size() > 9) {
						planets.remove(planets.size() - 1);
					}

					for (Planet planet : planetsStatic) {
						gcMain.setFill(planet.getColor());
						gcMain.fillOval(planet.getOriPosX() - planet.getSize() / 2,
								planet.getOriPosY() - planet.getSize() / 2, planet.getSize(), planet.getSize());

						gcMain.setFill(Color.WHITE);
						gcMain.fillText(planet.getName(), planet.getOriPosX() + planet.getSize(),
								planet.getOriPosY() + planet.getSize());
						planet.setPosX(planet.getOriPosX());
						planet.setPosY(planet.getOriPosY());
						planet.setVelX(planet.getOriVelX());
						planet.setVelY(planet.getOriVelY());
						planet.resetForces();

						planets.add(planet);
					}

					if (startPauseSim.isSelected()) {
						timeline.play();
					}

				}
			});

			drawTraces.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					e.consume();

					if (Main.ENABLED_TRACES) {
						Main.ENABLED_TRACES = false;
						gcTrace.clearRect(0, 0, traceCanvas.getWidth(), traceCanvas.getHeight());
					} else {
						Main.ENABLED_TRACES = true;
					}

				}
			});

			showLabels.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					e.consume();

					if (Main.ENABLED_LABELS) {
						Main.ENABLED_LABELS = false;
					} else {
						Main.ENABLED_LABELS = true;
					}

				}
			});

			drawLinesToSun.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					e.consume();

					if (Main.ENABLED_LINES_TO_SUN) {
						Main.ENABLED_LINES_TO_SUN = false;
					} else {
						Main.ENABLED_LINES_TO_SUN = true;
					}

				}
			});

			changePlanetProperties.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					e.consume();

					if (planets.isEmpty()) {

						Alert warning = new Alert(AlertType.WARNING);
						warning.setTitle("No Planets Present");
						warning.setHeaderText(null);
						warning.setContentText("There are no planets present!");
						warning.showAndWait();

					} else {
						timeline.stop();

						ArrayList<Button> planetButtons = new ArrayList<Button>();
						ArrayList<Button> deleteButtons = new ArrayList<Button>();

						Image deleteImg = new Image("Cross.png");

						Dialog<ButtonType> newDialog = new Dialog<>();
						newDialog.setTitle("Planet Properties");
						newDialog.setHeaderText("Please select which planet\n you'd like to change:");

						newDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

						GridPane list = new GridPane();

						list.setHgap(10);
						list.setVgap(5);

						newDialog.getDialogPane().setContent(list);

						EventHandler<ActionEvent> planetClick = event -> {
							event.consume();

							Object source = event.getSource();

							for (Button button : planetButtons) {
								if (button.equals(source)) {

									String name = button.getText();

									Dialog<String[]> planetDialog = new Dialog<>();
									planetDialog.setTitle("Planet Properties");
									planetDialog.setHeaderText("Change Planet Properties: " + name);

									ButtonType changeButton = new ButtonType("Change", ButtonData.OK_DONE);

									planetDialog.getDialogPane().getButtonTypes().addAll(changeButton,
											ButtonType.CANCEL);

									GridPane dataGrid = new GridPane();
									dataGrid.setHgap(10);
									dataGrid.setVgap(10);
									dataGrid.setPadding(new Insets(20, 150, 10, 10));

									TextField posX = new TextField();
									TextField posY = new TextField();
									TextField velX = new TextField();
									TextField velY = new TextField();
									TextField mass = new TextField();
									ComboBox<String> colours = new ComboBox<String>(colourKeyList);

									for (Planet planet : planets) {
										if (planet.getName().equals(name)) {

											posX.setText(Double.toString(planet.getPosX()));
											posY.setText(Double.toString(planet.getPosY()));
											velX.setText(Double.toString(planet.getVelX()));
											velY.setText(Double.toString(planet.getVelY()));
											mass.setText(Double.toString(planet.getMass()));
											colours.setValue("Blue");
											break;

										}

									}

									dataGrid.add(new Label("Position along X-axis  (0 to 1000) :"), 0, 0);
									dataGrid.add(posX, 1, 0);
									dataGrid.add(new Label("Position along Y-axis  (0 to 735) :"), 0, 1);
									dataGrid.add(posY, 1, 1);
									dataGrid.add(new Label("Velocity along X-axis  (Try between -6 and 6) :"), 0, 2);
									dataGrid.add(velX, 1, 2);
									dataGrid.add(new Label("Velocity along Y-axis  (Try between -6 and 6) :"), 0, 3);
									dataGrid.add(velY, 1, 3);
									dataGrid.add(new Label("Mass  (Sun's mass = 1) :"), 0, 4);
									dataGrid.add(mass, 1, 4);
									dataGrid.add(new Label("Planet's color:"), 0, 5);
									dataGrid.add(colours, 1, 5);

									planetDialog.getDialogPane().setContent(dataGrid);

									planetDialog.setResultConverter(dialogButton -> {
										if (dialogButton == changeButton) {
											String[] res = { posX.getText(), posY.getText(), velX.getText(),
													velY.getText(), mass.getText(), colours.getValue() };
											return res;
										} else {
											planetDialog.close();
										}
										return null;
									});

									Optional<String[]> planetResult = planetDialog.showAndWait();

									planetResult.ifPresent(newData -> {

										for (Planet planet : planets) {
											if (planet.getName().equals(name)) {
												planet.setPosX(Double.parseDouble(newData[0]));
												planet.setPosY(Double.parseDouble(newData[1]));
												planet.setVelX(Double.parseDouble(newData[2]));
												planet.setVelY(Double.parseDouble(newData[3]));
												planet.setMass(Double.parseDouble(newData[4]));
												planet.setColor(Color.valueOf(newData[5].toUpperCase()));
												break;
											}
										}

										newDialog.close();

										if (!planets.isEmpty()) {
											saveSys.setDisable(false);
										}

										timeline.play();

									});

								}
							}
						};

						EventHandler<ActionEvent> delClick = event -> {
							event.consume();

							Object source = event.getSource();

							for (int x = 0; x < planets.size(); x++) {
								if (deleteButtons.get(x).equals(source)) {

									Alert alert = new Alert(AlertType.INFORMATION);
									alert.setHeaderText(null);
									alert.setContentText(
											"Planet \"" + planets.get(x).getName() + "\" has been deleted!");
									alert.showAndWait();

									planets.remove(x);
									planetButtons.remove(x);
									deleteButtons.remove(x);

									gcMain.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
									drawPlanets(planets, gcMain, gcTrace);

									newDialog.close();

								}
							}
						};

						for (int x = 0; x < planets.size(); x++) {

							if(x>29) {
								Alert warning = new Alert(AlertType.WARNING);
								warning.setTitle("Limit Reached");
								warning.setHeaderText(null);
								warning.setContentText("Number of present planets reached maximum (30)!"+
										"Only first 30 planets will be loaded");
								warning.showAndWait();

								break;
							}

							planetButtons.add(x, new Button(planets.get(x).getName()));
							planetButtons.get(x).setMaxWidth(Double.MAX_VALUE);
							planetButtons.get(x).setOnAction(planetClick);

							deleteButtons.add(x, new Button());
							deleteButtons.get(x).setGraphic(new ImageView(deleteImg));
							deleteButtons.get(x).setMaxWidth(Double.MAX_VALUE);
							deleteButtons.get(x).setOnAction(delClick);

							if(x>19) {
								list.add(planetButtons.get(x), 4, x-20);
								list.add(deleteButtons.get(x), 5, x-20);
							}else if(x>9) {
								list.add(planetButtons.get(x), 2, x-10);
								list.add(deleteButtons.get(x), 3, x-10);
							}else {
								list.add(planetButtons.get(x), 0, x);
								list.add(deleteButtons.get(x), 1, x);
							}

						}

						Optional<ButtonType> result = newDialog.showAndWait();

						if (result.get() == ButtonType.CANCEL) {
							newDialog.close();
							if (startPauseSim.isSelected()) {
								timeline.play();
							}
						}

					}
				}
			});

			speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {

					KeyFrame newKeyFrame = new KeyFrame(Duration.seconds(1 / (double) newVal),
							new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {

							sim.checkForces(planets);

							gcMain.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
							drawPlanets(planets, gcMain, gcTrace);
						}
					});

					timeline.stop();
					timeline.getKeyFrames().setAll(newKeyFrame);

					if (startPauseSim.isSelected())
						timeline.play();
				}
			});

			accuracySlider.valueProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {

					timeline.stop();

					SimulationBF.timestep = (double) newVal;

					timeline.play();
				}
			});

			// Create new planet dialog
			canvasContainer.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					mouseEvent.consume();

					timeline.stop();

					if(planets.size()<30) {

						Dialog<String[]> newDialog = new Dialog<>();
						newDialog.setTitle("Add a new Planet");
						newDialog.setHeaderText("Enter new planet's data:");

						ButtonType createButton = new ButtonType("Create", ButtonData.OK_DONE);

						newDialog.getDialogPane().getButtonTypes().addAll(createButton, ButtonType.CANCEL);

						GridPane grid = new GridPane();
						grid.setHgap(10);
						grid.setVgap(10);
						grid.setPadding(new Insets(20, 150, 10, 10));

						TextField name = new TextField();
						TextField velX = new TextField();
						TextField velY = new TextField();
						TextField mass = new TextField();
						TextField size = new TextField();
						ComboBox<String> colours = new ComboBox<String>(colourKeyList);

						name.setPromptText("Name");
						velX.setPromptText("X-velocity");
						velY.setPromptText("Y-velocity");
						mass.setPromptText("Mass");
						size.setPromptText("Size");
						colours.setValue("Blue");

						grid.add(new Label("Name:"), 0, 0);
						grid.add(name, 1, 0);
						grid.add(new Label("Velocity along X-axis  (Try between -6 and 6) :"), 0, 1);
						grid.add(velX, 1, 1);
						grid.add(new Label("Velocity along Y-axis  (Try between -6 and 6) :"), 0, 2);
						grid.add(velY, 1, 2);
						grid.add(new Label("Mass  (Sun's mass = 1) :"), 0, 3);
						grid.add(mass, 1, 3);
						grid.add(new Label("Size  (Sun's size = 6, Planet's = 3-4) :"), 0, 4);
						grid.add(size, 1, 4);
						grid.add(new Label("Planet's color:"), 0, 5);
						grid.add(colours, 1, 5);

						newDialog.getDialogPane().setContent(grid);

						newDialog.setResultConverter(dialogButton -> {
							if (dialogButton == createButton) {
								String[] res = { name.getText(), velX.getText(), velY.getText(), mass.getText(),
										size.getText(), colours.getValue() };

								return res;
							} else {

								if (startPauseSim.isSelected()) {
									timeline.play();
								}
								newDialog.close();
							}
							return null;

						});

						Optional<String[]> result = newDialog.showAndWait();

						result.ifPresent(planetDetails -> {
							planets.add(new Planet(planetDetails[0], mouseEvent.getSceneX(), mouseEvent.getSceneY(),
									Double.parseDouble(planetDetails[1]), Double.parseDouble(planetDetails[2]), 0.0, 0.0,
									Double.parseDouble(planetDetails[3]), Color.valueOf(planetDetails[5].toUpperCase()),
									Double.parseDouble(planetDetails[4])));

							if (startPauseSim.isSelected()) {
								timeline.play();
							}

							drawPlanets(planets, gcMain, gcTrace);

							if (!planets.isEmpty()) {
								saveSys.setDisable(false);
							}

						});
					}else {
						Alert warning = new Alert(AlertType.WARNING);
						warning.setTitle("Limit Reached	");
						warning.setHeaderText(null);
						warning.setContentText("Maximum number of planets reached!"+
								"Can't add any more planets");

						warning.showAndWait();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ------------------------------------------------- OTHER METHODS
	// -------------------------------------------------

	private void drawPlanets(ArrayList<Planet> planets, GraphicsContext main, GraphicsContext trace) {
		for (Planet planet : planets) {

			if (Main.ENABLED_LINES_TO_SUN && planets.indexOf(planet) != 0) {
				main.setStroke(planet.getColor());
				main.setLineWidth(0.3);
				main.strokeLine(planet.getPosX(), planet.getPosY(), planets.get(0).getPosX(), planets.get(0).getPosY());
			}

			main.setFill(planet.getColor());
			main.fillOval(planet.getPosX() - planet.getSize() / 2, planet.getPosY() - planet.getSize() / 2,
					planet.getSize(), planet.getSize());

			if (Main.ENABLED_LABELS) {
				String formattedPositionX = String.format("%.2f", planet.getPosX());
				String formattedPositionY = String.format("%.2f", planet.getPosY());
				String formattedVelocityX = String.format("%.2f", planet.getVelX());
				String formattedVelocityY = String.format("%.2f", planet.getVelY());
				String formattedDistanceToSun = String.format("%.2f", planet.getDistance(planets.get(0)));

				String display = (planet.getName() + "\nPos- X: " + formattedPositionX + " Y: " + formattedPositionY
						+ "\nVel- X: " + formattedVelocityX + " Y: " + formattedVelocityY + "  AU/year"
						+ "\nDistance to Sun: " + formattedDistanceToSun + " AU");

				main.setFill(Color.WHITE);

				switch (planets.indexOf(planet)) {
				case 0:
					main.fillText(planet.getName(), planet.getPosX() + planet.getSize(),
							planet.getPosY() + planet.getSize());
					break;
				case 1:
					main.fillText(display, 330, 15);
					main.fillText(planet.getName(), planet.getPosX() + planet.getSize(),
							planet.getPosY() + planet.getSize());
					break;
				case 2:
					main.fillText(display, 330, 85);
					main.fillText(planet.getName(), planet.getPosX() + planet.getSize(),
							planet.getPosY() + planet.getSize());
					break;
				case 3:
					main.fillText(display, 515, 15);
					main.fillText(planet.getName(), planet.getPosX() + planet.getSize(),
							planet.getPosY() + planet.getSize());
					break;
				case 4:
					main.fillText(display, 515, 85);
					main.fillText(planet.getName(), planet.getPosX() + planet.getSize(),
							planet.getPosY() + planet.getSize());
					break;
				default:
					main.fillText(display, planet.getPosX() + planet.getSize(), planet.getPosY() + planet.getSize());
				}

			} else {
				main.setFill(Color.WHITE);
				main.fillText(planet.getName(), planet.getPosX() + planet.getSize(),
						planet.getPosY() + planet.getSize());
			}

			if (Main.ENABLED_TRACES) {

				trace.setStroke(planet.getColor());
				trace.strokeLine(planet.getPrevPosX(), planet.getPrevPosY(), planet.getPosX(), planet.getPosY());
			}

		}
	}
}