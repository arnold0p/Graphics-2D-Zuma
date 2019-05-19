package zuma;

import java.awt.Font;
import sprite.*;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Zuma extends Application {

    public static final double WINDOW_WIDTH = 1200;
    public static final double WINDOW_HEIGHT = 700;

    private AnimationTimer timer;

    private static Group root;
    private Moon moon;
    private Sun sun;

    private static List<Ball> balls = new ArrayList<>();
    private static List<Shot> shots = new ArrayList<>();

    private Scene scene;
    private long t = 0;
    private int sec = 0;
    private Text text;
    private static boolean end = false;
    private static int balls_cnt = 0;
    private Polygon z = null;
    
    @Override
    public void start(Stage primaryStage) {
        root = new Group();
        Background background = new Background(WINDOW_WIDTH, WINDOW_HEIGHT);
        sun = new Sun(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        moon = new Moon((3.0 / 4) * WINDOW_WIDTH, WINDOW_HEIGHT / 2);
        balls.add(new Ball());
        root.getChildren().addAll(background, sun, moon, balls.get(0));
        
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setOnMouseMoved(sun);
        scene.setOnMouseClicked(k -> {
            Zuma.makeShot(new Shot(sun));
            sun.setRandomMouthColor();
        });
        primaryStage.setTitle("Borba svetlosti");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();  
            }
        };
        timer.start();
    }

    public Polygon makeZ(){
        double x = moon.getX();
        double y = moon.getY();
        double top = y - 15;
        double bottom = y + 15;
        double left = x - 10;
        double right = x + 10;
        double offset = 5;
        Polygon z = new Polygon(left, top, 
                right , top, 
                right, top + offset, 
                left + offset, bottom  - offset, 
                right , bottom - offset, 
                right, bottom, 
                left, bottom, 
                left, bottom - offset,
                right - offset, top + offset, 
                left, top + offset);
        
        z.setFill(Color.WHITE);
        z.setStrokeWidth(2);
        z.setStroke(Color.BLACK);
        return z;
        
    }
    
    
    public void update() {
        if (end){
            for (int i = 0; i < shots.size(); i++) {
                Shot shot = shots.get(i);
                root.getChildren().remove(shot);
                shots.remove(shot);
            }
            return;
        }
        //adding time
        root.getChildren().remove(text);
        text = new Text();
        if (t==0){
            t = System.currentTimeMillis();
        }
        else {
            if (System.currentTimeMillis() - t > 1000){
                sec++;
                t = System.currentTimeMillis();
                
                // make Z
                if (Math.random()*100 > 75){
                    z = makeZ();
                  
                    RotateTransition rt = new RotateTransition(Duration.seconds(1), z);
                    double angle = Math.random()*80;
                    rt.setFromAngle(angle);
                    rt.setToAngle(-angle);
             
                    rt.setAutoReverse(true);
                    rt.setCycleCount(Animation.INDEFINITE);

                    TranslateTransition tt = new TranslateTransition(Duration.seconds(3), z);
                    tt.setByY(-WINDOW_HEIGHT/2 - 40);
                    tt.setInterpolator(Interpolator.LINEAR);
                    rt.play();
                    tt.play();
                    root.getChildren().add(z);
                }
            }
            
            
        }
      
        text.setText("Time: " + sec);
        text.setFill(Color.WHITE);
        text.setX(WINDOW_WIDTH/2);
        text.setY(20);
        root.getChildren().add(text);
        
        if (!balls.isEmpty() && balls.get(0).getBoundsInParent().intersects(moon.getBoundsInParent())) {
            //root.getChildren().remove(balls.get(0));
            //balls.remove(0);
            
            //delete shots and stop
            for (int i = 0; i < shots.size(); i++) {
                Shot shot = shots.get(i);
                root.getChildren().remove(shot);
                shots.remove(shot);
            }
            scene.setOnMouseClicked(null);
            end = true;
        }
        if (balls.get(balls.size() - 1).getTranslateY() >= Ball.getRadius() * 2) {
            //max 100 balls
            if (balls_cnt<100){
                Ball ball = new Ball();
                balls.add(ball);
                root.getChildren().add(ball);
                balls_cnt++;
            }
        }

        balls.forEach(ball -> ball.update());

        for (int i = 0; i < shots.size(); i++) {
            Shot shot = shots.get(i);
            shot.update();
            if (shot.getTranslateX() < 0 || shot.getTranslateX() > WINDOW_WIDTH
                    || shot.getTranslateY() < 0 || shot.getTranslateY() > WINDOW_HEIGHT) {
                root.getChildren().remove(shot);
                shots.remove(shot);
            } else {
                for (int j = 0; j < balls.size(); j++) {
                    if (shot.getBoundsInParent().intersects(balls.get(j).getBoundsInParent())) {
                        crashLogic(shot, j);
                        break;
                    }
                }
            }
        }
        
        //ako su sve kugle unistene ne moze da puca vise
        if (balls.size()==0 && balls_cnt == 100){
            end = true;
            
            //ne treba 
            scene.setOnMouseClicked(null);
        }
    }

    //logika za unistavanje pokretnih kugli - NE MENJATI!
    public void crashLogic(Shot shot, int j) {
        
        Color color = shot.getColor();
        Stop []stops = {
            new Stop(0,Color.WHITE),
            new Stop(1,color)
        };
        RadialGradient rg = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, stops);
        
        shot.getBody().setFill(rg);
        
        Ball hitBall = balls.get(j);
        Ball prevBall = (j - 1 >= 0) ? balls.get(j - 1) : null;
        Ball nextBall = (j + 1 < balls.size()) ? balls.get(j + 1) : null;
        if (nextBall != null && shot.getColor().equals(nextBall.getColor())) {
            j++;
            hitBall = nextBall;
            nextBall = (j + 1 < balls.size()) ? balls.get(j + 1) : null;
            prevBall = (j - 1 >= 0) ? balls.get(j - 1) : null;
        }
        if (shot.getColor().equals(hitBall.getColor())
                && ((prevBall != null && shot.getColor().equals(prevBall.getColor()))
                || (nextBall != null && shot.getColor().equals(nextBall.getColor())))) {
            root.getChildren().remove(shot);
            int sameColorCnt = 0, k;
            //next
            for (k = j + 1; k < balls.size() && balls.get(k).getColor().equals(shot.getColor()); sameColorCnt++) {
                root.getChildren().remove(balls.get(k));
                balls.remove(k);
            }
            //previous
            for (k = j; k >= 0 && balls.get(k).getColor().equals(shot.getColor()); k--, sameColorCnt++) {
                root.getChildren().remove(balls.get(k));
                balls.remove(k);
            }
            //reverse
            for (int m = k; m >= 0; m--) {
                balls.get(m).reverse(sameColorCnt);
            }
        } else {
            if (nextBall != null) {
                shot.becomeMoving(nextBall);
                balls.add(j + 1, shot);
                for (int k = j + 2; k < balls.size(); k++) {
                    Ball hit = balls.get(k);
                    hit.reverse(1);
                }
            } else {
                shot.becomeMoving(balls.get(balls.size() - 1));
                balls.add(balls.size(), shot);
                shot.reverse(1);
            }
        }
        shots.remove(shot);
    }

    public static void makeShot(Shot shot) {
        if (end)
            return;
        shots.add(shot);
        root.getChildren().add(shot);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
