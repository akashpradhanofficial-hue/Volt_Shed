enum StorageTier {
    TIER_1_CRITICAL(1),  
    TIER_2_MANAGED(2),   
    TIER_3_ELASTIC(3);   

    private final int priorityValue;
    StorageTier(int value) { this.priorityValue = value; }
    public int getPriorityValue() { return priorityValue; }
}

class Feeder implements Comparable<Feeder> {
    private String id;
    private String name;
    private double currentLoadMW; 
    private StorageTier tier;
    private boolean isEnergized;

    public Feeder(String id, String name, double currentLoadMW, StorageTier tier) {
        this.id = id;
        this.name = name;
        this.currentLoadMW = currentLoadMW;
        this.tier = tier;
        this.isEnergized = true; 
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getCurrentLoadMW() { return currentLoadMW; }
    public void setCurrentLoadMW(double load) { this.currentLoadMW = load; }
    public StorageTier getTier() { return tier; }
    public boolean isEnergized() { return isEnergized; }
    public void setEnergized(boolean energized) { this.isEnergized = energized; }

    @Override
    public int compareTo(Feeder other) {
        if (this.tier != other.tier) {
            return Integer.compare(other.tier.getPriorityValue(), this.tier.getPriorityValue());
        }
        return Double.compare(other.currentLoadMW, this.currentLoadMW);
    }

    @Override
    public String toString() {
        return String.format("[%s] (ID: %s) %s - %.2f MW (%s)", 
            isEnergized ? "ACTIVE" : "SHED", id, name, currentLoadMW, tier);
    }
}

class FeederFactory {
    public static Feeder createFeeder(String id, String name, double loadMW, int tierLevel) {
        StorageTier selectedTier;
        switch (tierLevel) {
            case 1: selectedTier = StorageTier.TIER_1_CRITICAL; break;
            case 2: selectedTier = StorageTier.TIER_2_MANAGED; break;
            case 3: selectedTier = StorageTier.TIER_3_ELASTIC; break;
            default: throw new IllegalArgumentException("Invalid Tier!");
        }
        return new Feeder(id, name, loadMW, selectedTier);
    }
}

