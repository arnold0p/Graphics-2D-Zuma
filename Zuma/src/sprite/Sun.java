/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprite;

import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

/**
 *
 * @author km183142m
 */
public class Sun extends Sprite implements EventHandler<MouseEvent> {

    public static final double SUN_RADIUS = 70;

    public static final Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

    private Circle body;
    private Circle mouth,mouthBack;
    private Color mouthColor, mounthColorBack;
    private int i=0;
    
    public Sun(double x, double y) {
        body = new Circle(SUN_RADIUS);
        body.setFill(Color.GOLD);
        
        body.setStrokeWidth(1);
        body.setStroke(Color.ORANGE);
        mouth = new Circle(SUN_RADIUS / 3);
        mouthBack = new Circle(SUN_RADIUS / 3);
        
        mouthColor = colors[(int) (Math.random() * colors.length)];
        mounthColorBack = colors[(int) (Math.random() * colors.length)];
        
        mouth.setFill(mouthColor);
        mouth.setTranslateY(SUN_RADIUS / 4);
        
        
        mouthBack.setTranslateY(SUN_RADIUS / 4);
        mouthBack.setFill(null);
        mouthBack.setStrokeWidth(10);
        mouthBack.setStroke(mounthColorBack);
        
        
        Arc left_eye = new Arc(-SUN_RADIUS/3.3 + 3,-SUN_RADIUS/2 -5,10,25,180,180);
        Arc right_eye = new Arc(SUN_RADIUS/3.3 - 3,-SUN_RADIUS/2 -5,10,25,180,180);
      
        Circle left = new Circle(-SUN_RADIUS/3.3 + 3,-SUN_RADIUS/2 + 15,5);
        Circle right = new Circle(SUN_RADIUS/3.3 - 3,-SUN_RADIUS/2 + 15,5);
        
        left_eye.setStroke(Color.BLACK);
        left_eye.setFill(Color.WHITE);
        left_eye.setType(ArcType.CHORD);
        left_eye.setRotate(180);
        
        right_eye.setStroke(Color.BLACK);
        right_eye.setFill(Color.WHITE);
        right_eye.setType(ArcType.CHORD);
        right_eye.setRotate(180);
        
        Rectangle left_o = new Rectangle( -SUN_RADIUS/2 - 5 - 2, -7,15 , 10);
        left_o.setFill(Color.ORANGE);
        left_o.setArcHeight(5);
        left_o.setArcWidth(5);
        
        Rectangle right_o = new Rectangle(SUN_RADIUS/2 - 5, -7, 15 , 10 );
        right_o.setFill(Color.ORANGE);
        right_o.setArcHeight(5);
        right_o.setArcWidth(5);
        
        
        
        
        Rectangle rec1 = new Rectangle(-SUN_RADIUS, -SUN_RADIUS, SUN_RADIUS*2,SUN_RADIUS*2);
        rec1.setFill(Color.YELLOW);
        Rectangle rec2 = new Rectangle(-SUN_RADIUS, -SUN_RADIUS, SUN_RADIUS*2,SUN_RADIUS*2);
        rec2.setFill(Color.YELLOW);
        rec2.setRotate(45);
        
        Group recs = new Group();
        
        recs.getChildren().addAll(rec1,rec2);
        
        Duration t = Duration.seconds(1);
        ScaleTransition st1 = new ScaleTransition(t,recs);
        st1.setFromX(1);
        st1.setToX(1.1);
        st1.setFromY(1);
        st1.setToY(1.1);
        st1.setAutoReverse(true);
        st1.setCycleCount(Timeline.INDEFINITE);
        st1.play();
        
        getChildren().addAll(recs,body, mouthBack, mouth, left_eye,right_eye,left,right,right_o,left_o);
        setTranslateX(x);
        setTranslateY(y);
    }

    public void setRandomMouthColor() {
        mouthColor = mounthColorBack;
        mounthColorBack = colors[(int) (Math.random() * colors.length)];
        
        mouth.setFill(mouthColor);
        mouthBack.setStroke(mounthColorBack);
    }

    public Color getMouthColor() {
        return mouthColor;
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void handle(MouseEvent event) {
        double x = event.getX(), y = event.getY();
        double dx = getTranslateX() - x, dy = y - getTranslateY();
        double alpha = 90 - Math.toDegrees(Math.atan(dy / dx));
        if (x > getTranslateX()) {
            alpha -= 180;
        }
        setRotate(alpha);
    }

}
