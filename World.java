import java.awt.Graphics2D;
import java.math.BigDecimal;
import java.util.ArrayList;

public class World {
    public static final BigDecimal GRAVITATIONAL_CONSTANT = new BigDecimal("6.674E-11");

    private ArrayList<Volume> volumes;

    public World() {
        volumes = new ArrayList<Volume>();
    }

    public void add(Volume volume) {
        volumes.add(volume);
    }

    public void draw(Graphics2D g2d) {
        for (Volume volume : volumes) {
            volume.draw(g2d);
        }
    }

    public Volume getVolume(int index) {
        return volumes.get(index);
    }

    public ArrayList<Volume> getVolumes() {
        return volumes;
    }

    public void setVolume(int index, Volume volume) {
        volumes.set(index, volume);
    }

    public void setVolumes(ArrayList<Volume> volumes) {
        this.volumes = volumes;
    }
}
