package Boids;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static java.lang.Math.sqrt;

public class Boids extends Application {
    final int range = 50;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Pane mainPane = new Pane();
        primaryStage.setTitle("Boids");
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

    class Boid{
        int x;
        int y;

        public Boid(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void move(Collection<Boid> allBoids){
            ArrayList<Boid> inRange = new ArrayList<>();
            for (Boid b :
                allBoids) {
                if(this.distance(b) <= range && !(b== this)){
                    inRange.add(b);
                }
            }
        }

        public double distance(Boid other){
            int dx = this.x - other.x;
            int dy = this.y - other.y;
            return sqrt((dx*dx) + (dy*dy));
        }
    }
}
