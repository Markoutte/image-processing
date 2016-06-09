package me.markoutte.image;

public final class Edge implements Comparable<Edge> {

    private int a;
    private int b;
    private double weight;

    public Edge(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public Edge(int a, int b, double weight) {
        this.a = a;
        this.b = b;
        this.weight = weight;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge e) {
        if (this.weight < e.weight)
            return -1;
        if (this.weight > e.weight)
            return 1;
        return 0;
    }
}

