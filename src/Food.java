import java.awt.*;

public class Food {
    double energy;
    double mass;

    double x;
    double y;

    double area;// == mass
    double radius;// = sqrt(cellArea/pi)

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //Temporary information for cells to store about this food
    double proximity;
    double eatWillingness;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    public Food(double energy, double mass, double x, double y) {
        this.energy = energy;
        this.mass = mass;
        this.radius = Math.sqrt(mass/Math.PI);

        this.x = x;
        this.y = y;

    }

    public void paint(Graphics2D g2d){
        g2d.setColor(Color.red);
        g2d.drawOval((int)(x-radius),(int)(y-radius),(int)(2*radius),(int)(2*radius));
    }
}
