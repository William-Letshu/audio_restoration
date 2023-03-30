package com.example.project_1;

import java.util.Arrays;

public class KalmanFiltering {
    private double[] x;      // state estimate
    private double[] P;      // state covariance
    private double[] Q;      // process noise covariance
    private double[] R;      // measurement noise covariance
    private double[] K;      // Kalman gain
    public KalmanFiltering(int size) {
        this.K = new double[size];
        this.P= new double[size];
        this.Q = new double[size];
        this.R = new double[size];
        this.x = new double[size];

        Arrays.fill(x, 0.0);
        Arrays.fill(P, 1.0);
        Arrays.fill(Q, 0.001);
        Arrays.fill(R, 0.1);
    }

    public void update(double[] z) {
        // Predict
        for (int i = 0; i < x.length; i++) {
            x[i] = x[i];
            P[i] = P[i] + Q[i];
        }
        // Update
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = z[i] - x[i];
            K[i] = P[i] / (P[i] + R[i]);
            x[i] = x[i] + K[i] * y[i];
            P[i] = (1 - K[i]) * P[i];
        }
    }
    public double[] getState() {
        return x;
    }
}
