import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;

public class Particle {
    private BigDecimal x, y;
    private BigDecimal mass;

    public Particle(BigDecimal x, BigDecimal y, BigDecimal mass) {
        this.x = x;
        this.y = y;
        this.mass = mass;
    }

    /**
     * @return gravity between two particles
     */
    public BigDecimal getGravity(Particle particle) {
        BigDecimal mass2 = mass.multiply(particle.getMass());
        BigDecimal distance2 = x.subtract(particle.getX()).pow(2).add(y.subtract(particle.getY()).pow(2));
        return World.GRAVITATIONAL_CONSTANT.multiply(mass2).divide(distance2);
    }

    public boolean equals(Particle particle) {
        return x.equals(particle.getX()) && y.equals(particle.getY()) && mass.equals(particle.getMass());
    }

    public void setX(BigDecimal x) {
        this.x = x;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }

    public void setMass(BigDecimal mass) {
        this.mass = mass;
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    public BigDecimal getMass() {
        return mass;
    }

    public Shape getShape(BigDecimal radius) {
        return new Ellipse2D.Double(x.subtract(radius).doubleValue(), y.subtract(radius).doubleValue(), radius.doubleValue() * 2, radius.doubleValue() * 2);
    }

    @Override
    public String toString() {
        return "PARTICLE (" + x + ", " + y + ") mass: " + mass;
    }
}
