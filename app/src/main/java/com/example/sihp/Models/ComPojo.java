package com.example.sihp.Models;

public class ComPojo {

    String problem;
    public double lattitude;
    double longitude;

    public ComPojo() {

    }

    public ComPojo(String problem, double lattitude, double longitude) {
        this.problem = problem;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
