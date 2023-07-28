public class ParticleGeneration {
    private int outerLayers;
    private int anglesToCalculate;

    public ParticleGeneration(int outerLayers, int anglesToCalculate) {
        this.outerLayers = outerLayers;
        this.anglesToCalculate = anglesToCalculate;
    }

    public int getOuterLayers() {
        return outerLayers;
    }

    public int getAnglesToCalculate() {
        return anglesToCalculate;
    }
}
