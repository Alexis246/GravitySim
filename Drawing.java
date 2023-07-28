import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;

public class Drawing extends Canvas {
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        World world = new World();

        ParticleGeneration pg = new ParticleGeneration(1, 100);
        Ellipse circle = new Ellipse(200, 200, 300, 115, new BigDecimal(100), new BigDecimal("0.5"), pg);
        world.add(circle);

        g2d.setStroke(new BasicStroke(0));
        world.draw(g2d);

        System.out.println(circle.getParticlesByX().get(13).get(0));
        System.out.println(circle.getParticlesByX().get(36).get(0));

        System.out.println(circle);
    }
}