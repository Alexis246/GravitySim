import java.awt.Graphics2D;

public interface Volume {
    void draw(Graphics2D g2d);

    void createParticles(ParticleGeneration pg);
}
