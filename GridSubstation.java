import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class GridCollapseException extends Exception {
    public GridCollapseException(String message) { super(message); }
}

// In Spring Boot, this would be tagged with @Service for business logic
class GridSubstation {
    private String substationName;
    private double availableGenerationMW;
    
    // INDUSTRY INSIGHT: Replacing slow SQL Database with a high-performance, thread-safe ConcurrentHashMap
    private Map<String, Feeder> feederRegistry; 

    public GridSubstation(String substationName, double availableGenerationMW) {
        this.substationName = substationName;
        this.availableGenerationMW = availableGenerationMW;
        this.feederRegistry = new ConcurrentHashMap<>(); 
    }

    public void setAvailableGenerationMW(double supply) { this.availableGenerationMW = supply; }
    public double getAvailableGenerationMW() { return availableGenerationMW; }
    public Collection<Feeder> getAllFeeders() { return feederRegistry.values(); }

    public void addFeeder(Feeder feeder) { 
        feederRegistry.put(feeder.getId(), feeder); 
    }

    public synchronized void restoreAllFeeders() {
        for (Feeder f : feederRegistry.values()) f.setEnergized(true);
        System.out.println("🔄 [SCADA] All grid breakers reset. Power fully restored.");
    }

    // Thread-safe optimization engine to handle live data modifications
public synchronized void optimizeGridLoad() throws GridCollapseException {
    double totalDemand = feederRegistry.values().stream()
                                       .filter(Feeder::isEnergized)
                                       .mapToDouble(Feeder::getCurrentLoadMW)
                                       .sum();

    if (totalDemand <= availableGenerationMW) {
        return; // Grid is safe, exit early
    }

    // 1. DECLARE BOTH DEFICIT AND CLEARED LOAD VARIABLES HERE SO THEY ARE VISIBLE EVERYWHERE Below
    double deficit = totalDemand - availableGenerationMW;
    double clearedLoad = 0;

    // 2. Build the Priority Queue for Shedding
    PriorityQueue<Feeder> sheddingQueue = new PriorityQueue<>();
    for (Feeder f : feederRegistry.values()) {
        if (f.isEnergized()) {
            sheddingQueue.add(f);
        }
    }

    // 3. Process line drops until deficit is cleared or queue is empty
    while (clearedLoad < deficit && !sheddingQueue.isEmpty()) {
        Feeder candidate = sheddingQueue.poll();

        if (candidate.getTier() == StorageTier.TIER_1_CRITICAL) {
            continue; // Skip vital infrastructure
        }

        candidate.setEnergized(false);
        clearedLoad += candidate.getCurrentLoadMW();
    }

    // 4. CRITICAL HARD FAILURE CHECK: This will now see 'clearedLoad' and 'deficit' perfectly!
    if (clearedLoad < deficit) {
        throw new GridCollapseException("CRITICAL EMERGENCY: ALL ELIGIBLE TIERS SHED. UNRESOLVED REGIONAL LOAD DEFICIT!");
    }
}


    public void displayGridReport() {
        System.out.println("\n================= LIVE SCADA CACHE REPORT =================");
        feederRegistry.values().forEach(System.out::println);
        System.out.println("===========================================================");
    }
}
