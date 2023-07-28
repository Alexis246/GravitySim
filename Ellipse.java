import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;

public class Ellipse implements Volume {
    private BigDecimal x, y;
    private BigDecimal width, height;
    private BigDecimal halfWidth, halfHeight;
    private BigDecimal mass;
    private BigDecimal particleRadius;
    private BigDecimal particleDiameter;
    private ParticleGeneration particleGeneration;
    private ArrayList<ArrayList<Particle>> xParticles, yParticles;

    public static final MathContext MATH_CONTEXT = new MathContext(100, RoundingMode.HALF_UP);

    // (x, y) is the center of the ellipse
    public Ellipse(BigDecimal x, BigDecimal y, BigDecimal width, BigDecimal height, BigDecimal mass, BigDecimal particleRadius, ParticleGeneration pg) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.halfWidth = width.divide(new BigDecimal(2));
        this.height = height;
        this.halfHeight = height.divide(new BigDecimal(2));
        this.particleRadius = particleRadius;
        this.particleDiameter = particleRadius.multiply(new BigDecimal(2));
        this.mass = mass;
        this.particleGeneration = pg;
        createParticles(particleGeneration);
    }

    public Ellipse(int x, int y, int width, int height, BigDecimal mass, BigDecimal particleRadius, ParticleGeneration pg) {
        this(new BigDecimal(x), new BigDecimal(y), new BigDecimal(width), new BigDecimal(height), mass, particleRadius, pg);
    }

    public Ellipse(BigDecimal x, BigDecimal y, BigDecimal diameter, BigDecimal mass, BigDecimal particleRadius, ParticleGeneration pg) {
        this(x, y, diameter, diameter, mass, particleRadius, pg);
    }

    public Ellipse(int x, int y, int diameter, BigDecimal mass, BigDecimal particleRadius, ParticleGeneration pg) {
        this(new BigDecimal(x), new BigDecimal(y), new BigDecimal(diameter), new BigDecimal(diameter), mass, particleRadius, pg);
    }

    @Override
    public void draw(Graphics2D g2d) {
        //draws background
        Shape background = new Ellipse2D.Double(x.subtract(halfWidth).doubleValue(),
                y.subtract(halfHeight).doubleValue(), width.doubleValue(), height.doubleValue());

        g2d.setColor(Color.GRAY);
        g2d.draw(background);
        g2d.fill(background);

        //draws particles

        g2d.setColor(Color.CYAN);
        int length = yParticles.size();
        for (int i = 0; i < length; i++) {
            ArrayList<Particle> arrL = yParticles.get(i);
            for (int j = 0; j < arrL.size(); j++) {
                Shape shape = arrL.get(j).getShape(particleRadius);
                g2d.draw(shape);
                g2d.fill(shape);
            }
        }
    }

    @Override
    public void createParticles(ParticleGeneration pg) {
        xParticles = new ArrayList<ArrayList<Particle>>();
        yParticles = new ArrayList<ArrayList<Particle>>();
        BigDecimal maxDX = width.subtract(particleDiameter).divide(new BigDecimal(2));
        BigDecimal maxDY = height.subtract(particleDiameter).divide(new BigDecimal(2));

        for (BigDecimal dX = maxDX.subtract(maxDX.remainder(particleDiameter)).negate(); dX
                .compareTo(maxDX) <= 0; dX = dX.add(particleDiameter)) {
            for (BigDecimal dY = maxDY.subtract(maxDY.remainder(particleDiameter)).negate(); dY
                    .compareTo(maxDY) <= 0; dY = dY.add(particleDiameter)) {

                //Doesn't create particle if center is outside of ellipse
                if (dX.divide(halfWidth, MATH_CONTEXT).pow(2).add(dY.divide(halfHeight, MATH_CONTEXT).pow(2))
                        .compareTo(BigDecimal.ONE) > 0) {
                    continue;
                }

                Particle newParticle = new Particle(x.add(dX), y.add(dY), BigDecimal.ZERO);
                addParticle(newParticle);
            }
        }

        // //Looks for particles 2 particles deep into the ellipse from both horizontals and verticals(outer particles)
        int outerLayers = pg.getOuterLayers();
        ArrayList<Particle> checkParticles = new ArrayList<Particle>();

        for (int i = 0; i < xParticles.size(); i++) {
            ArrayList<Particle> particles = xParticles.get(i);
            int length = particles.size();
            int space = length < outerLayers ? 0 : length - outerLayers;

            for (int fromTop = 0; fromTop < length - space; fromTop++) {
                checkParticles.add(particles.get(fromTop));
            }
            for (int fromBottom = 0; fromBottom < (space < outerLayers ? space
                    : outerLayers); fromBottom++) {
                checkParticles.add(particles.get(length - 1 - fromBottom));
            }
        }

        for (int i = 0; i < yParticles.size(); i++) {
            ArrayList<Particle> particles = yParticles.get(i);
            int length = particles.size();
            int space = length < outerLayers ? 0 : length - outerLayers;

            for (int fromLeft = 0; fromLeft < length - space; fromLeft++) {
                checkParticles.add(particles.get(fromLeft));
            }
            for (int fromRight = 0; fromRight < (space < outerLayers ? space : outerLayers); fromRight++) {
                checkParticles.add(particles.get(length - 1 - fromRight));
            }
        }

        //Checks if points on circle are inside of circle
        final double radius = particleRadius.doubleValue();
        final int angles = pg.getAnglesToCalculate();
        final double angleMeasure = 2 * (Math.PI) / angles;

        for (Particle particle : checkParticles) {
            for (int i = 0; i < angles; i++) {
                final double angle = angleMeasure * i;
                final double dX = Math.cos(angle) * radius;
                final double dY = Math.sin(angle) * radius;
                final BigDecimal xTerm = particle.getX().subtract(this.x).add(new BigDecimal(dX)).divide(halfWidth, MATH_CONTEXT).pow(2);
                final BigDecimal yTerm = particle.getY().subtract(this.y).add(new BigDecimal(dY)).divide(halfHeight, MATH_CONTEXT).pow(2);

                if (xTerm.add(yTerm).compareTo(BigDecimal.ONE) > 0) {
                    removeParticle(particle);
                }
            }
        }

        setMass(mass);
    }

    public void createParticle(BigDecimal x, BigDecimal y, BigDecimal mass) {
        addParticle(new Particle(x, y, mass));
    }

    public void addParticle(Particle particle) {
        //ArrayList.get(yPosition).get(xPosition)
        int row = 0;

        //gets row to add to
        int length1 = yParticles.size();
        for (int i = 0; i < length1; i++) {
            int comparison = particle.getY().compareTo(yParticles.get(i).get(0).getY());
            if (comparison == 0) {
                row = i;
                break;
            } else if (comparison < 0) {
                yParticles.add(i, new ArrayList<Particle>());
                row = i;
                break;
            } else if (i == length1 - 1) {
                yParticles.add(new ArrayList<Particle>());
                row = length1;
            }
        }

        if (yParticles.size() == 0) {
            yParticles.add(new ArrayList<Particle>());
        }

        //gets column to add to
        ArrayList<Particle> rowArrL = yParticles.get(row);
        int col = 0;
        int length2 = rowArrL.size();

        for (int i = 0; i < length2; i++) {
            int comparison = particle.getX().compareTo(rowArrL.get(i).getX());
            if (comparison < 0) {
                col = i;
                break;
            } else if (i == length2 - 1) {
                col = length2;
            }
        }
        rowArrL.add(col, particle);

        //ArrayList.get(xPosition).get(yPosition)
        row = 0;

        //gets row to add to
        length1 = xParticles.size();
        for (int i = 0; i < length1; i++) {
            int comparison = particle.getX().compareTo(xParticles.get(i).get(0).getX());
            if (comparison == 0) {
                row = i;
                break;
            } else if (comparison < 0) {
                xParticles.add(i, new ArrayList<Particle>());
                row = i;
                break;
            } else if (i == length1 - 1) {
                xParticles.add(new ArrayList<Particle>());
                row = length1;
            }
        }

        if (xParticles.size() == 0) {
            xParticles.add(new ArrayList<Particle>());
        }

        //gets column to add to
        ArrayList<Particle> colArrL = xParticles.get(row);
        col = 0;
        length2 = colArrL.size();

        for (int i = 0; i < length2; i++) {
            int comparison = particle.getX().compareTo(colArrL.get(i).getX());
            if (comparison < 0) {
                col = i;
                break;
            } else if (i == length2 - 1) {
                col = length2;
            }
        }
        colArrL.add(col, particle);
    }

    public void removeParticle(Particle particle) {
        xParticles.forEach(particles -> particles.remove(particle));
        yParticles.forEach(particles -> particles.remove(particle));
    }

    public void removeParticleByX(int row, int column) {
        xParticles.get(row).remove(column);
        if (yParticles.get(row).size() == 0) {
            yParticles.remove(row);
        }
    }

    public void removeParticleByY(int row, int column) {
        yParticles.get(row).remove(column);
        if (yParticles.get(row).size() == 0) {
            yParticles.remove(row);
        }
    }

    public void removeParticles(Collection<Particle> c) {
        xParticles.forEach(particles -> particles.removeAll(c));
        yParticles.forEach(particles -> particles.removeAll(c));
    }

    public void setParticleRadius(BigDecimal radius) {
        this.particleRadius = radius;
        this.particleDiameter = radius.multiply(new BigDecimal(2));
    }

    public void setParticleDiameter(BigDecimal diameter) {
        this.particleDiameter = diameter;
        this.particleRadius = diameter.divide(new BigDecimal(2));
    }

    public void setParticles(ArrayList<Particle> particles) {
        xParticles.clear();
        yParticles.clear();
        for (Particle particle : particles) {
            addParticle(particle);
        }
    }

    public void setParticleGeneration(ParticleGeneration pg) {
        particleGeneration = pg;
    }

    /**
     * Equally sets each particle's mass to (mass / particleCount)
     * @param mass
     */
    public void setMass(BigDecimal mass) {
        this.mass = mass;
        int particleCount = 0;
        for (ArrayList<Particle> particleArrL : xParticles) {
            particleCount += particleArrL.size();
        }
        for (ArrayList<Particle> particleArrL : xParticles) {
            for (Particle particle : particleArrL) {
                particle.setMass(mass.divide(new BigDecimal(particleCount), MATH_CONTEXT));
            }
        }
    }
    
    public void setX(BigDecimal x) {
        this.x = x;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }

    public void setPosition(BigDecimal x, BigDecimal y) {
        this.x = x;
        this.y = y;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
        halfWidth = width.divide(new BigDecimal(2));
        createParticles(particleGeneration);
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
        halfHeight = height.divide(new BigDecimal(2));
        createParticles(particleGeneration);
    }

    public ArrayList<Particle> getParticles() {
        ArrayList<Particle> particleArrL = new ArrayList<Particle>();

        for (ArrayList<Particle> arrL : yParticles) {
            for (Particle particle : arrL) {
                particleArrL.add(particle);
            }
        }
        return particleArrL;
    }

    public ArrayList<ArrayList<Particle>> getParticlesByX() {
        return xParticles;
    }

    public ArrayList<ArrayList<Particle>> getParticlesByY() {
        return yParticles;
    }

    public ParticleGeneration getParticleGeneration() {
        return particleGeneration;
    }

    public BigDecimal getMass() {
        return mass;
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public BigDecimal getHalfWidth() {
        return halfWidth;
    }
    
    public BigDecimal getHeight() {
        return height;
    }

    public BigDecimal getHalfHeight() {
        return halfHeight;
    }

    public int getParticleCount() {
        int sum = 0;
        for (ArrayList<Particle> particles : yParticles) {
            sum += particles.size();
        }
        return sum;
    }

    @Override
    public String toString() {
        return "ELLIPSE (" + x + ", " + y + ") width: " + width + " height: " + height + " particle count: " + getParticleCount() + " mass: " + mass;
    }
}