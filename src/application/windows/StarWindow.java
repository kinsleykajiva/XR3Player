/*
 * 
 */
package application.windows;

import java.io.IOException;

import application.tools.InfoTool;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The Class StarWindow.
 */
public class StarWindow extends GridPane {
	
	//--------------------------------
	
	@FXML
	private Button ok;
	
	@FXML
	private Canvas canvas;
	
	@FXML
	private Button close;
	
	@FXML
	private Label starsLabel;
	
	//-------------------------------------
	
	/** The window. */
	private Stage window;
	
	/** The gc. */
	private GraphicsContext gc;
	
	/** The stars position. */
	private int[] starsPosition = { 5 , 35 , 65 , 95 , 125 };
	
	/** The stars. */
	private DoubleProperty stars = new SimpleDoubleProperty();
	
	/** The accepted. */
	private boolean accepted;
	
	/** The no star. */
	private static final Image noStar = InfoTool.getImageFromResourcesFolder("noStar.png");
	
	/** The half star. */
	private static final Image halfStar = InfoTool.getImageFromResourcesFolder("halfStar.png");
	
	/** The star. */
	private static final Image star = InfoTool.getImageFromResourcesFolder("star.png");
	
	String[] labelText = { "No Stars" , "Very Bad" , "Bad" , "Very Bored" , "Bored" , "Almost Fine" , "Fine" , "Good" , "Very Good" , "Amazing" , "Excellent" };
	
	/**
	 * Constructor.
	 */
	public StarWindow() {
		
		// ----------------------------------FXMLLoader----------------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "StarWindow.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Called when .fxml is initialized
	 */
	@FXML
	private void initialize() {
		
		// Window
		window = new Stage();
		window.initStyle(StageStyle.TRANSPARENT);
		window.initModality(Modality.APPLICATION_MODAL);
		window.setAlwaysOnTop(true);
		
		// Graphics Context 2D
		gc = canvas.getGraphicsContext2D();
		
		// Canvas
		canvas.setOnMouseDragged(this::computeStars);
		canvas.setOnMouseReleased(this::computeStars);
		canvas.setOnMouseMoved(this::computeFakeStars);
		canvas.setOnMouseExited(m -> repaintStars(getStars()));
		
		// close
		close.setOnAction(a -> close(false));
		
		// OK
		ok.setOnAction(a -> close(true));
		
		// Scene
		window.setScene(new Scene(this, Color.TRANSPARENT));
		window.getScene().getStylesheets().add(getClass().getResource(InfoTool.STYLES + InfoTool.APPLICATIONCSS).toExternalForm());
		window.getScene().setOnKeyReleased(key -> {
			if (key.getCode() == KeyCode.ESCAPE)
				close(false);
		});
		
		// Repaint
		repaintStars(getStars());
		
	}
	
	/**
	 * Repaints the canvas with stars.
	 */
	private void repaintStars(double stars) {
		
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		// System.out.println("Stars--->" + stars);
		
		// paint half and whole stars
		if ( ( stars - 0.5 ) < (int) stars)
			for (int i = 0; i < stars; i++)
				gc.drawImage(star, starsPosition[i], 0);
			
		else {
			for (int i = 0; i < stars - 1; i++)
				gc.drawImage(star, starsPosition[i], 0);
			
			gc.drawImage(halfStar, starsPosition[(int) stars], 0);
		}
		
		// Paint unselected Stars
		if (stars != 5)
			for (int i = 4; i >= stars; i--)
				gc.drawImage(noStar, starsPosition[i], 0);
			
		//Label Text
		starsLabel.setText(labelText[(int) Math.round(stars * 2)]);
	}
	
	/**
	 * Stars must be one number from 0 to 10.
	 *
	 * @param newStars
	 *            the new stars
	 */
	private void setStars(double newStars) {
		if (getStars() == newStars)
			return;
		stars.set(newStars);
		repaintStars(stars.get());
	}
	
	/**
	 * Return the number of stars that have been selected.
	 *
	 * @return the stars
	 */
	public double getStars() {
		return stars.get();
	}
	
	/**
	 * Return Stars Property.
	 *
	 * @return the double property
	 */
	public DoubleProperty starsProperty() {
		return stars;
	}
	
	/**
	 * Was accepted.
	 *
	 * @return true, if successful
	 */
	public boolean wasAccepted() {
		return accepted;
	}
	
	/**
	 * Show.
	 *
	 * @param stars
	 *            the stars
	 * @param node
	 *            the node
	 */
	public void show(double stars , Node node) {
		// Auto Calculate the position
		Bounds bounds = node.localToScreen(node.getBoundsInLocal());
		show(stars, bounds.getMinX() + 5, bounds.getMaxY());
	}
	
	/**
	 * Show.
	 *
	 * @param stars
	 *            the stars
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void show(double stars , double x , double y) {
		setStars(stars);
		
		if (x <= -1 && y <= -1)
			window.centerOnScreen();
		else {
			
			if (x + getWidth() > InfoTool.getVisualScreenWidth())
				x = InfoTool.getVisualScreenWidth() - getWidth();
			else if (x < 0)
				x = 0;
			
			if (y + getHeight() > InfoTool.getVisualScreenHeight())
				y = InfoTool.getVisualScreenHeight() - getHeight();
			else if (y < 0)
				y = 0;
			
			window.setX(x);
			window.setY(y);
		}
		window.show();
	}
	
	/**
	 * Close the Window.
	 *
	 * @param accepted
	 *            True if accepted , False if not
	 */
	private void close(boolean accepted) {
		this.accepted = accepted;
		window.close();
	}
	
	/**
	 * Computes the stars based on the mouse position on screen
	 */
	private void computeStars(MouseEvent m) {
		int x = (int) m.getX();
		
		if (x <= 5)
			setStars(0);
		else if (x >= 144)
			setStars(5);
		else if (x >= 133)
			setStars(4.5);
		else if (x >= 115)
			setStars(4);
		else if (x >= 105)
			setStars(3.5);
		else if (x >= 85)
			setStars(3);
		else if (x >= 74)
			setStars(2.5);
		else if (x >= 55)
			setStars(2);
		else if (x >= 45)
			setStars(1.5);
		else if (x >= 25)
			setStars(1);
		else if (x >= 12)
			setStars(0.5);
	}
	
	/**
	 * Computes the stars based on the mouse position on screen
	 */
	private void computeFakeStars(MouseEvent m) {
		int x = (int) m.getX();
		
		if (x <= 5)
			repaintStars(0);
		else if (x >= 144)
			repaintStars(5);
		else if (x >= 133)
			repaintStars(4.5);
		else if (x >= 115)
			repaintStars(4);
		else if (x >= 105)
			repaintStars(3.5);
		else if (x >= 85)
			repaintStars(3);
		else if (x >= 74)
			repaintStars(2.5);
		else if (x >= 55)
			repaintStars(2);
		else if (x >= 45)
			repaintStars(1.5);
		else if (x >= 25)
			repaintStars(1);
		else if (x >= 12)
			repaintStars(0.5);
	}
	
	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}
	
}
