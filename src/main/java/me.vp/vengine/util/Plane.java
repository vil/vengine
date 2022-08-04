package me.vp.vengine.util;


/*
 *
 * @Author Vp (https://github.com/herravp)
 *
 */

public class Plane {
    private final Vector r1;
    private final Vector r2;

    public Vector p;
    public Vector normal;

    public Plane(Polygon p) {
        this.p = p.getCentre();
        this.r1 = p.vertexes[1].subtract(p.vertexes[0]).normalise();
        this.r2 = p.vertexes[2].subtract(p.vertexes[0]).normalise();
        this.normal = this.r1.cross(this.r2);
    }

    public Plane(Vector p, Vector r1, Vector r2) {
        this.p = new Vector(p);
        this.r1 = new Vector(r1).normalise();
        this.r2 = new Vector(r2).normalise();
        this.normal = this.r1.cross(this.r2);
    }
}