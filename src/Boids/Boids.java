package Boids;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Math.signum;
import static java.lang.Math.sqrt;

public class Boids extends Application {
    final int range = 200;
    final int speed = 2;
    final double desired_separation = 50;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Pane mainPane = new Pane();
        Canvas mainCanvas = new Canvas(500, 500);
        mainPane.getChildren().add(mainCanvas);
        primaryStage.setTitle("Boids");
        Scene mainScene = new Scene(mainPane);
        primaryStage.setScene(mainScene);
        ArrayList<Boid> boids = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            boids.add(new Boid((int) (Math.random()*500),(int) (Math.random()*500)));
        }

        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        primaryStage.show();

        new AnimationTimer() {
            @Override public void handle(long currentNanoTime) {
                gc.clearRect(0,0,500,500);
                for(Boid b : boids){
                    double v_angle = Math.atan2(-b.orientation[1], b.orientation[0])*180/Math.PI - 202.5;
                    gc.fillArc(b.x-20, b.y-20, 40, 40, v_angle, 45, ArcType.ROUND);
                }
                for(Boid b : boids) {
                    b.reorient(boids);
                    b.move(speed);
                }
            }
        }.start();
    }

    class Boid{
        double x;
        double y;
        double[] orientation;
        final double maxTurn = 0.05;

        public Boid(int x, int y) {
            this.x = x;
            this.y = y;
            this.orientation = normalize(new double[]{Math.random()*2-1, Math.random()*2-1});
        }

        public void move(double speed){
            double temp_x = this.x + (speed * orientation[0]);
            double temp_y = this.y + (speed * orientation[1]);
            if(temp_x < 0){
                temp_x += 500;
            } else if(temp_x > 500){
                temp_x -= 500;
            }
            if(temp_y < 0){
                temp_y += 500;
            } else if(temp_y > 500) {
                temp_y -= 500;
            }
            this.x = temp_x;
            this.y = temp_y;
        }

        public void reorient(Collection<Boid> allBoids){
            ArrayList<Boid> inRange = new ArrayList<>();
            ArrayList<Boid> withinSep = new ArrayList<>();
            for (Boid b :
                allBoids) {
                double dist = this.distance(b);
                if (dist <= range && b != this) {
                    inRange.add(b);
                    if(dist <= desired_separation){
                        withinSep.add(b);
                    }
                }
            }
            if(inRange.size() > 0) {
                double[] separation_vector = separation(withinSep);
                double[] alignment_vector = alignment(inRange);
                double[] cohesion_vector = cohesion(inRange);

                double vx = separation_vector[0] + alignment_vector[0] + cohesion_vector[0];
                double vy = separation_vector[1] + alignment_vector[1] + cohesion_vector[1];

                double[] orient = normalize(new double[]{vx, vy});
                double turnAngle =Math.atan2(this.orientation[1] - orient[1], this.orientation[0] - orient[0]);
                if(Math.abs(turnAngle) >= this.maxTurn){
                    turnAngle = signum(turnAngle) * maxTurn;
                    vx = Math.cos(turnAngle) * this.orientation[0] - Math.sin(turnAngle) * this.orientation[1];
                    vy = Math.sin(turnAngle) * this.orientation[0] + Math.cos(turnAngle) * this.orientation[1];
                    orient = normalize(new double[]{vx, vy});
                }
                this.orientation = orient;
            }
        }

        private double[] separation(ArrayList<Boid> boids) {
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

        private double[] alignment(ArrayList<Boid> boids) {
            double[] vector = new double[]{0,0};
            for(Boid b : boids){
                vector[0] += b.x;
                vector[1] += b.y;
            }
            return normalize(vector);
        }

        private double[] cohesion(ArrayList<Boid> boids) {
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
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            double dist =  sqrt((dx*dx) + (dy*dy));
            if(dist > 0.5 * 500){
                dist = 500 - dist;
            }
            return dist;
        }

        public double[] normalize(double[] vector){
            if(Double.isNaN(vector[0]) || Double.isNaN(vector[1])){
                return new double[]{0, 0};
            }
            double sum_of_squares = 0;
            for (double v : vector) {
                sum_of_squares += v * v;
            }
            double magnitude = Math.sqrt(sum_of_squares);
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= magnitude;
            }
            return vector;
        }
    }
}
