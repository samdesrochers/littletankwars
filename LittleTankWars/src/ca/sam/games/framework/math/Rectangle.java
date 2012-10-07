package ca.sam.games.framework.math;

public class Rectangle {
    public final Vector2 lowerLeft;
    public float width, height;
    
    // Coordonn�es x,y : point en bas � gauche du d�but de rectangle
    public Rectangle(float x, float y, float width, float height) {
        this.lowerLeft = new Vector2(x,y);
        this.width = width;
        this.height = height;
    }
}
