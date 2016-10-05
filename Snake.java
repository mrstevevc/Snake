/*
 * Stephen Lester
 *
 * Based on the user's input at the command
 * line, either classic snake is played, or a modified version where if 
 * the user hits their tail the game does not end, but every part of the snake
 * after where it hit itself is removed instead. Also in the extra mode, 
 * every rectangle the snake hits stays the same color when it is added to the
 * snake. There is also a pause function for both versions, and a hard mode
 * where the game's speed increases for every rectangle the snake hits.
 * There is a slow motion button that is available if the player has collected
 * enough rectangles, but this reduces the snake's size to one rectangle.
 * When the player loses, their score is displayed, along with a button that
 * let's them replay the game.
 *
 */
	

import javafx.application.Application; //Imports
import javafx.stage.Stage;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.Parent;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

import java.lang.Math;
import java.util.List;
import java.lang.InterruptedException;

public class Snake extends Application{ //Inheritance for GUI usage
   
   public enum Direction{ //Enumeration for direction of the onscreen snake
	    UP, RIGHT, DOWN, LEFT  
	 }

	 private static final int BLOCK = 40; //Constants
	 private static final int WIDTH = 20 * BLOCK;
	 private static final int HEIGHT = 15 * BLOCK;
	 private static final int SCORE_MULTIPLIER = 10;
	 private static final int SIZE_FACTOR = 7;
	 private static final int TEXT_X = WIDTH/2 - 100;
	 private static final int TEXT_Y = HEIGHT/2;
	 private static final int BUTTON_X = WIDTH/2 - 30;
	 private static final int BUTTON_Y = HEIGHT/2 + 60;
   
	 private static final double COLOR_ONE = 0.2;
	 private static final double COLOR_TWO = 0.4;
	 private static final double COLOR_THREE = 0.6;
	 private static final double COLOR_FOUR = 0.8;
    private static final double DURATION = 0.08;
    private static final double SPEED_INCREMENT = 0.2;

    private int score = 0;
    private double speed = 1.0;
	 private boolean hard = false;

	 private Direction direction = Direction.RIGHT; //Snake starts out moving
	                                                //right

	 private boolean moved = false; //Boolean values for running the game
	 private boolean running = false;

	 private Timeline timeline = new Timeline(); //Used so game can be played
	                                             //indefinitely 

	 private ObservableList<Node> snake; //Snake displayed onscreen
 
    public static String argVal; //Used to determine whether to play 
	                              //regular snake or modified snake
   
	 Pane root = new Pane(); //GUI used for start method

	 private Parent createScreen(){ //Creates the GUI
         root.setPrefSize(WIDTH, HEIGHT); //Sets size of GUI display

			Group snakeBody = new Group();  //Used so Rectangle object can be added
			snake = snakeBody.getChildren();//to the snake

         ColoredRect rectangle = new ColoredRect();//Creating a randomly colored
			Rectangle food = rectangle.getRect();     //Rectangle object
			if(!(argVal.equals("extra"))){
         food.setFill(Color.ORANGE); //Sets the Rectangle object's color to 
				                             //orange for regular snake mode
			}
			//Puts the Rectangle object at a random location in the GUI
			food.setTranslateX((int)(Math.random() * (WIDTH)) / BLOCK*BLOCK);
			food.setTranslateY((int)(Math.random() * (HEIGHT)) / BLOCK*BLOCK);
			
			//Updates game every 0.08 seconds
         KeyFrame frame = new KeyFrame(Duration.seconds(DURATION), event ->{
            if(!running){
               return;
				 }
				 //Used for the ternary operator
				 boolean toRemove = snake.size() > 1;
             //Ternary operator to cycle through the snake's body as it moves
			 Node tail = toRemove ? snake.remove(snake.size() - 1) : snake.get(0);
        
				 //Finds end of the snake, the location where the snake's length is 
				 //increased
				 double tailX = tail.getTranslateX();
				 double tailY = tail.getTranslateY();

         //Switch used to move the snake's body in the direction the snake moves
				 switch(direction){
             case UP:
						    tail.setTranslateX(snake.get(0).getTranslateX());
							 tail.setTranslateY(snake.get(0).getTranslateY() - BLOCK);
							 break;
			    case RIGHT:
						    tail.setTranslateX(snake.get(0).getTranslateX() + BLOCK);
							 tail.setTranslateY(snake.get(0).getTranslateY());
							 break;
				 case DOWN:
						    tail.setTranslateX(snake.get(0).getTranslateX());
							 tail.setTranslateY(snake.get(0).getTranslateY() + BLOCK);
							 break;
				 case LEFT:
							 tail.setTranslateX(snake.get(0).getTranslateX() - BLOCK);
							 tail.setTranslateY(snake.get(0).getTranslateY());
							 break;
				 }
         
             moved = true; //For user input switch in start method

				 if(toRemove){ //Cycling through snake's body as it moves
               snake.add(0, tail);
				 }

				 for(Node rect : snake){  //Checking to see if the snake hits itself
              if(rect != tail && tail.getTranslateX() == rect.getTranslateX() 
								&& tail.getTranslateY() == rect.getTranslateY()){
                     int rectLoc = snake.indexOf(rect);
							int tailLoc = snake.size();
						  	
							if(argVal.equals("extra")){ //Cuts off snake body parts in 
								                          //extra mode
						       snake.remove(rectLoc, tailLoc);
								 score = (snake.size() - 1) * SCORE_MULTIPLIER;
							}
							else{
								 gameOver(); //Ends game in regular mode
						       break;
							}	
						}
				 }

            if(tail.getTranslateX() < 0 || tail.getTranslateX() >= WIDTH 
						 || tail.getTranslateY() < 0 || tail.getTranslateY() >= HEIGHT){
				gameOver(); //Checking to see if the snake hits the GUI boundaries
			}

            if(tail.getTranslateX() == food.getTranslateX() &&  
			      tail.getTranslateY() == food.getTranslateY()){
               food.setTranslateX((int)(Math.random() * (WIDTH - BLOCK)) / BLOCK 
								* BLOCK); //Setting Rectangle at a new location
				  food.setTranslateY((int)(Math.random() * (HEIGHT - BLOCK)) / BLOCK
							* BLOCK);

				   Rectangle rect = new Rectangle(BLOCK, BLOCK);
				   rect.setTranslateX(tailX); //Setting newly acquired Rectangle
				   rect.setTranslateY(tailY); //at the tail of the snake
				   if(argVal.equals("extra")){ //Setting snake color
                  rect.setFill(food.getFill()); 
			      }
			      else{
			         rect.setFill(Color.GREEN);
			      }
			      snake.add(rect);
			      score = (snake.size() - 1) * SCORE_MULTIPLIER;
			      timeline.setRate(1); //Accounts for speed change

		         if(hard == true){ //Changes game's speed and background color for
                  speed += SPEED_INCREMENT;  //hard mode

				      timeline.setRate(speed);
				      root.setStyle("-fx-background-color: black;");
			      }
			      //Setting colors randomly for the rectangles
			      double colorChance = Math.random();
			      if(argVal.equals("extra")){
                  if(colorChance <= COLOR_ONE){
			            food.setFill(Color.BLUE);
			         }
			         else if(colorChance <= COLOR_TWO){
                     food.setFill(Color.MAGENTA);
			         }
			         else if(colorChance <= COLOR_THREE){
                     food.setFill(Color.YELLOW);
			         }
			         else if(colorChance <= COLOR_FOUR){
                     food.setFill(Color.ORANGE);
			         }
			         else{
			            food.setFill(Color.RED);
			         }
               }

		      }
			});
			
	      timeline.getKeyFrames().add(frame); //Runs timeline at frame's rate
	      timeline.setCycleCount(Timeline.INDEFINITE); //Runs game indefinitely

	      root.getChildren().addAll(food, snakeBody); //Adds objects to GUI
	      return root; //Returns root, used in the start method 
   }

	 //Stops, then starts game
   private void restartGame(){
      stopGame();
	   timeline.setRate(1);
		startGame();
	}
    
   private void startGame(){
      direction = Direction.RIGHT; //Starts the snake moving right
		Rectangle head = new Rectangle(BLOCK, BLOCK);
		head.setFill(Color.GREEN);
		snake.add(head); //Creates a green head Rectangle for the snake
		timeline.play(); //Starts the game
		running = true;  
	}

	private void stopGame(){
      running = false; //Stops game from running
		timeline.stop(); 
		snake.clear(); //Removes snake from GUI
	}
 
	private void gameOver(){
		stopGame(); //Stops game
		speed = 1;
		hard = false;
      Text box = new Text(TEXT_X, TEXT_Y, 
			   "GAME OVER\nyour score is " + score); //Sets game over text and 
			                                         //score
		box.setFill(Color.BLACK);
		box.setStyle("-fx-font: 36 sans;"); //Set color, size, and font style
			                                 //of text

	   root.getChildren().add(box); //Prints text to the screen

		Button restart = new Button(); //Creates a new Button object
		restart.setLayoutX(BUTTON_X); //Sets the Button's location
		restart.setLayoutY(BUTTON_Y);
		restart.setText("Play again?"); //Sets text inside the Button
		root.getChildren().add(restart); //Adds button to the GUI

		root.setStyle("-fx-background-color: white;"); //This is here to keep
                                                     //the background from  
		                                               //turning to a red shade

		//Action executed when the restart button is pressed
		restart.setOnAction(new EventHandler<ActionEvent>(){  
        @Override
   	  public void handle(ActionEvent event){ //Overriding the abstract
	  	                                        //handle method
           restartGame(); //Restarts game
			  root.getChildren().remove(restart); //Removes the text and button
			  root.getChildren().remove(box);     //from the GUI
			  score = 0; //Resets the score
		  }
		});
	}

   //Overriding the abstract start method 
   @Override
	public void start(Stage primaryStage) throws Exception{
      Scene scene = new Scene(createScreen()); //Creates Scene object out of 
			                                      //Pane object 

		scene.setOnKeyPressed(event -> { //Lambda expression for using the 
					                        //machine's keyboard
	      if(moved){
			   switch(event.getCode()){ //Switch for moving the snake 
			      case UP:
					case W:
					   if(direction != Direction.DOWN){
                     direction = Direction.UP;
						}
						break;
					case RIGHT:
					case D:
						if(direction != Direction.LEFT){
							direction = Direction.RIGHT;
						}
						break;	
					case DOWN:	
					case S: 
						if(direction != Direction.UP){
                     direction = Direction.DOWN;
						}
					   break;
					case LEFT:
					case A:
					   if(direction != Direction.RIGHT){
                     direction = Direction.LEFT;
						}
						break;
					case N: //This acts as a double edged sword for the player,
							  //this removes all but the snake's head and slows the  
						     //game, but reduces the player's score.
							  //This also sets the background to white if the player
							  //was in hard mode

					   if(snake.size() >= SIZE_FACTOR){
                     snake.remove(1, snake.size());
					      score = (snake.size() - 1) * SCORE_MULTIPLIER;
						   timeline.setRate(0.5);
						   speed = 1;
						   hard = false;
						   root.setStyle("-fx-background-color: white;");
				      }
					   break;
					case H: //Runs hard mode
                  hard = true;
					   break;
               case P: //Pauses the game
					   if(timeline.getStatus().equals(Animation.Status.RUNNING)){
                     timeline.pause();
						}
						else if(timeline.getStatus().equals(Animation.Status.PAUSED)){
                     timeline.play();
						}
						break;
				}
			}  
		});

		primaryStage.setTitle("SNAKE"); //Sets the title for the GUI
		primaryStage.setScene(scene); //Sets and displays the GUI
		primaryStage.show();
	  	startGame(); //Starts the game
   } 

   //Main method
	public static void main(String[] args){
      if(args.length > 1){ //Error checking
         System.out.println("ERROR: Input must be java Snake SomeArgument");
		   System.exit(0);
		}
	   argVal = args[0]; //Sets the argVal to determine what version of Snake
			               //to play

		launch(args); //Launch the program
   }

}
