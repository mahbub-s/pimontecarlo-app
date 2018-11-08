package app;

import app.queue.LinkedList;
import app.queue.Queue;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//  extend the Application class of JavaFX to start GUI application
public class PiMonteCarloApp extends Application {

	//  define a class variable for PiMonteCarlo class
	PiMonteCarlo pimontecarlo;
	
	//  define a class variable for Animator class which extends AnimationTimer
	Animator animator;
	
	//  define a class variable for GraphicsContext which is initialized in start using canvas.getGraphicsContext2D()
	GraphicsContext gContext;
	
	//  define a class variable for Canvas which is JavaFX component and used to draw the dots
	Canvas canvas;
	
	Spinner<Long> threshold;
	
	Label status;

	Queue<double[]> points = new LinkedList<>();
	
	/**
	 * init none JavaFX options
	 */
	@Override
	public void init() throws Exception{
		//  call the super init method
		super.init();
		//  initialize the Animator
		animator = new Animator();
		//  initialize the PiMonteCarlo object
		pimontecarlo = new PiMonteCarlo();
	}
	
	/**
	 * create and connect components of JavaFX application
	 * @param primaryStage - the primary stage which to the scene can be set.
	 */
	public void start(Stage primaryStage) throws Exception{
		//  create a BorderPane layout to hold all components, generally the name contains root. ex, rootPane
		BorderPane rootPane = new BorderPane();
		
		//  create the top control Pane, it is a good idea to separate the creation in a helper method. ex, createController
		Pane controlPane = createController("Controls", 10000, 10000);
		
		//  create a JavaFX canvas and store it in your class variable. provide the constructor a size like 800 by 800
        canvas = new Canvas(800, 800);

		//  get the GraphicsContext from canvas and store it in your class variable
        gContext = canvas.getGraphicsContext2D();
        
		//  create a BorderPane layout to hold the canvas, this will allow up to easily color the canvas with no 
        BorderPane canvasPane = new BorderPane();
        
		//  setStyle for the new BorderLayout and assign it this color: "-fx-background-color: LightGray;"
        canvasPane.setStyle("-fx-background-color: LightGray;");
        
		//  set canvas to center of this BorderLayout
        canvasPane.setCenter(canvas);
		
		//  set the control pane created already as top of root BorderPane
		rootPane.setTop(controlPane);
		//  set the BorderPane with canvas in it as center of root BorderPane
		rootPane.setCenter(canvasPane);

		//  create a new scene object and assign it root BorderPane
        Scene scene = new Scene(rootPane, 800, 800);

		//  set the newly created scene to primaryStage
        primaryStage.setScene(scene);
        
		//  set the title of primaryStage
        primaryStage.setTitle("Monte Carlo");
                
		//  call show on primaryStage
        primaryStage.show();
		
		//lines below allow us to connect the size of canvas to the main root pane and update it every time the main window changes
		//  bind the widthProperty of canvas to widthProperty of root BorderPane
        canvas.widthProperty().bind(rootPane.widthProperty());
        
		//  bond the heightProperty of canvas to heightProperty of root BorderPane minus the height of control pane
        canvas.heightProperty().bind(rootPane.heightProperty().subtract(controlPane.heightProperty()));
        
		//  create a ChangeListener variable with generic type Number and assign it a lambda which will call clearCanvas help function
        ChangeListener<Number> cListener = (a,b,c) -> clearCanvas();
        
		//  add ChangeListener variable as Listener to widthProperty and heightProperty of canvas
        canvas.widthProperty().addListener(cListener);
        canvas.heightProperty().addListener(cListener);
        
		//  call clearCanvas function to draw the initial circle.
        clearCanvas();
	}

	/**
	 * called after the JavaFX GUI is closed
	 */
	public void stop() throws Exception{
		//  call the super stop method
		super.stop();
		//  stop the stopAnimator and Pi when PiMonteCarlo is created
		//  stop or shutdown PiMonteCarlo class thread
	}

	/**
	 * create the controller pane
	 * @param gridPane - layout to hold all the components
	 * @param name - name of the row
	 * @param maxSpinner - max value of spinner
	 * @param spinnerValue - starting value of spinner
	 * @return a Pane with all components added to it
	 */
	private Pane createController(String name, double maxSpinner, double spinnerValue){
		//  create a GridPane to hold all components of the controller
		GridPane controller = new GridPane();
		
		//  set Hgap and Vgap for GridPane.
		controller.setVgap(1);
		controller.setHgap(3);
		
		//  set Padding for GridPane with Insets
	    controller.setPadding(new Insets(3, 3, 3, 3)); 

		//  create a label to define the name of this row, first argument of method
	    Text labelController = new Text(name);
	    
		//  add the label to GridPane at row and col zero
	    controller.add(labelController, 0, 0);

		//  create a set of buttons for start, stop and clear
		Button startButton = new Button("Start");
		Button stopButton = new Button("Stop");
		Button clearButton = new Button("Clear");
		
		//  add each button to GridPane starting from column 1, all in row 0
		controller.add(startButton, 1, 0);
		controller.add(stopButton, 2,  0);
		controller.add(clearButton, 3, 0);

		//  create a label to define the name of spinner object, named "Threshold"
		Text spinnerObj = new Text("Threshold");
		//  add the label to GridPane at row zero and col 4
		controller.add(spinnerObj, 4, 0);

		//  create a Spinner JavaFX component with generic type of Integer with min of zero, max of maxSpinner and current of spinnerValue
		//https://news.kynosarges.org/2016/10/28/javafx-spinner-for-numbers/
		threshold = new Spinner<>(0, maxSpinner, spinnerValue);
		
		//  add the Spinner to GridPane at row zero and col 5
		controller.add(threshold, 5, 0);

		//  create a status Label for future details, set text to empty
		status = new Label();
	
		//  add the label to GridPane at row zero and col 7
		controller.add(status, 7, 0);
		
		//  add a listener to valueProperty to spinner
		//https://o7planning.org/en/11185/javafx-spinner-tutorial
		threshold.valueProperty().addListener(
				//  use lambda to create the listener, inside of lambda call clearCanvasAndStart and pass status label to it
				e-> clearCanvasAndStart()
			);
		
		//Create custom SpinnerValueFactory
		threshold.setValueFactory(
				new SpinnerValueFactory<Long>() {
					{
						setValue(10000L);
					}
					
					@Override
					public void increment(int steps) {
						setValue(getValue() + 10000);
					}
					
					@Override
					public void decrement(int steps) {
						//Setting threshold to 0 breaks the program
						if(getValue() > 10000)
							setValue(getValue() - 10000);
						else
							setValue(10000L);
					}
				}
			);
		
		//  add a listener to clear button
		//  use lambda to create the listener, inside of lambda call clearCanvas
		clearButton.setOnAction(e->clearCanvas());
		
		//  add a listener to start button
		//  use lambda to create the listener, inside of lambda call clearCanvasAndStart
		startButton.setOnAction(e->clearCanvasAndStart());
		
		//  add a listener to stop button	
		//  use lambda to create the listener, inside of lambda call stopAnimatorAndPi
		stopButton.setOnAction(e->stopAnimatorAndPi());
		
		//  return the created GridPane
		return controller;
	}

	/**
	 * clear the canvas and stop any active animation
	 */
	private void clearCanvas(){
		//  stop stopAnimator and PiMonteCarlo
		animator.stop();
		pimontecarlo.stop();
		
		if(gContext != null) {
			//  call clearRect on GraphicsContext class variable to erase content of canvas, use height and width of canvas
			gContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	
			//  call strokeOval on GraphicsContext class variable to draw the circle, use height and width of canvas
			gContext.strokeOval(0, 0, canvas.getWidth(), canvas.getHeight());
		}
	}

	/**
	 * clear the canvas first and start the animation
	 * @param label - output location to print animation details
	 */
	private void clearCanvasAndStart(){
		//  clear canvas
		clearCanvas();
		
		//  check of label is not null
		if (status != null) {
			//  if not null set text on the label with appropriate message
			status.setText("Empty");
		}
				
		//  start animator when PiMonteCarlo is created	
		animator.start();
		
		//  start the PiMonteCarlo thread by calling simulate
		pimontecarlo.simulate(threshold.getValue(), animator::addPoint);
	}

	/**
	 * stop the animator and PiMonteCarlo thread
	 */
	private void stopAnimatorAndPi(){
		//  call stop on animator when PiMonteCarlo is created
		animator.stop();
		//  stop PiMonteCarlo thread, if using executor service do not shutdown
		pimontecarlo.stop();
	}

	
	// Animation class to animate based on the content of handle method
	private class Animator extends AnimationTimer{
		//  create a function that will add points to the queue
		void addPoint(double[] point) {
			points.offer(point);
		}
		
		@Override
		public void handle(long now){
			for(int i = 0; i < 1000; i++) {
				if (points.peek() == null) {
					return;
				}
				double[] store = points.poll();
				if(store[2] == 1) 
					gContext.setFill(Color.RED);
				else
					gContext.setFill(Color.BLUE);
				gContext.fillOval(store[0] * canvas.getWidth(), store[1] * canvas.getHeight(), 2, 2);
			}
					
			status.setText(Double.toString(pimontecarlo.getPI()));
		}
	}

	/**
	 * map a number in range of A to B to a new range of C to D
	 * @param num - value to be converted in new range
	 * @param orgLow - low bound of original range
	 * @param orgHi - high bound of original range
	 * @param newLow - low bound of new range
	 * @param newHi - high bound of new range
	 * @return
	 */
	public static double map( double num, double orgLow, double orgHi, double newLow, double newHi){
		return (num - orgLow) * (newHi - newLow) / (orgHi - orgLow) + newLow;
	}

	public static void main(String[] args){
		//this method from Application class is called to start JavaFX application through main if JavaFX cannot be started directly
		launch(args);
	}
}

