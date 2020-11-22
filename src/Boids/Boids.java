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
    final int speed = 4;
    final double desired_separation = 15;
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
        double[] orientation;

        public Boid(int x, int y) {
            this.x = x;
            this.y = y;
            this.orientation = new double[]{0,1};
        }

        public void move(Collection<Boid> allBoids){
            ArrayList<Boid> inRange = new ArrayList<>();
            ArrayList<Boid> withinSep = new ArrayList<>();
            for (Boid b :
                allBoids) {
                if(b == this){
                    break;
                }
                double dist = this.distance(b);
                if (dist <= range) {
                    inRange.add(b);
                    if(dist <= desired_separation){
                        withinSep.add(b);
                    }
                }
            }

            double[] separation_vector = separation(withinSep);
            double[] alignment_vector = alignment(inRange);
            double[] cohesion_vector = cohesion(inRange);

            double vx = separation_vector[0] + alignment_vector[0] + cohesion_vector[0];
            double vy = separation_vector[1] + alignment_vector[1] + cohesion_vector[1];

            this.orientation = normalize(new double[]{vx, vy});
            this.x += speed * this.orientation[0];
            this.y += speed * this.orientation[1];
        }

        private double[] separation(ArrayList<Boids.Boids.Boid> boids) {
            double avg_x = 0;
            double avg_y = 0;
            for (Boid b: boids) {
                avg_x += b.x;
                avg_y += b.y;
            }
            avg_x /= boids.size();
            avg_y /= boids.size();
            double[] vector = new double[2];
            vector[0] = this.x - avg_x;
            vector[1] = this.y - avg_y;
            return normalize(vector);
        }

        private double[] alignment(ArrayList<Boids.Boids.Boid> boids) {
            double[] vector = new double[]{0,0};
            for(Boid b : boids){
                vector[0] += b.x;
                vector[1] += b.y;
            }
            return normalize(vector);
        }

        private double[] cohesion(ArrayList<Boids.Boids.Boid> boids) {
            double avg_x = 0;
            double avg_y = 0;
            for (Boid b: boids) {
                avg_x += b.x;
                avg_y += b.y;
            }
            avg_x /= boids.size();
            avg_y /= boids.size();
            double[] vector = new double[2];
            vector[0] = avg_x - this.x;
            vector[1] = avg_y - this.y;
            return normalize(vector);
        }

        public double distance(Boid other){
            int dx = this.x - other.x;
            int dy = this.y - other.y;
            return sqrt((dx*dx) + (dy*dy));
        }

        public double[] normalize(double[] vector){
            double sum_of_squares = 0;
            for (int i = 0; i < vector.length; i++) {
                sum_of_squares += vector[i]*vector[i];
            }
            double magnitude = Math.sqrt(sum_of_squares);
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= magnitude;
            }
            return vector;
        }
    }
}
