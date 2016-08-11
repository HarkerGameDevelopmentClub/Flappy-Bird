package main;

import java.awt.Dimension;
import java.awt.Toolkit;

import objects.Sprite;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application  {

	public double WIDTH;
	public double HEIGHT;
	
	int i = 0;
	
	GraphicsContext gc;
	Game game;
	Scene scene;
	
	public static void main(String[] args){
		launch(args);
	}
	
	public Scene getScene(){
		return scene;
	}
	
	@Override
    public void start(Stage stage)
    {
    	init(stage);
        
    	final long startNanoTime = System.nanoTime();
       
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
            	@SuppressWarnings("unused")
                double t = (currentNanoTime - startNanoTime)/1000000000.0; 
                i++;
                
                game.tick(i);
                
                game.draw(gc);
            }
        }.start();
        
        stage.show();
    }
	
	public void init(Stage stage){
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		WIDTH = screenSize.getWidth() / 1.0;
		HEIGHT = screenSize.getHeight()  / 1.0;
		
    	stage.setTitle("Flappy Bird");
    	

    	Group root = new Group();
        scene = new Scene(root);
        
        stage.setScene(scene);
        
        Canvas canvas = new Canvas(WIDTH,HEIGHT);
        root.getChildren().add(canvas);
     
        gc = canvas.getGraphicsContext2D();
        
        game = new Game(this);
	}
}
