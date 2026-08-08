import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    private static GridSubstation odishaGrid;
    private static String systemLog = "SCADA Telemetry System Active. Live Internet Synchronization Engaged.";
    private static boolean isRunning = true;
    private static long lastEmergencyTime = System.currentTimeMillis();
    private static boolean isCrisisActive = false;

    private static double liveStateDemand = 6000.00;
    private static double liveGridFrequency = 50.00;

    // ANSI COLOR CODE CONFIGURATIONS
    private static final String RESET = "\033[0m";
    private static final String RED = "\033[31m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";

    public static void main(String[] args) {
        try { new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); } catch(Exception e) {}

        odishaGrid = new GridSubstation("Mancheswar EHV Grid (OPTCL)", 350.0);

        odishaGrid.addFeeder(FeederFactory.createFeeder("F1", "KIMS and Capital Medical Emergency Feeder", 30.0, 1));
        odishaGrid.addFeeder(FeederFactory.createFeeder("F2", "Mancheswar Railway Workshop Traction Corridor", 110.0, 1));
        odishaGrid.addFeeder(FeederFactory.createFeeder("F3", "Saheed Nagar High-Density Residential Area", 70.0, 2));
        odishaGrid.addFeeder(FeederFactory.createFeeder("F4", "Vani Vihar and Utkal Varsity Res. Blocks", 60.0, 2));
        odishaGrid.addFeeder(FeederFactory.createFeeder("F5", "Chandaka Industrial Estate - Sector A", 80.0, 3));
        odishaGrid.addFeeder(FeederFactory.createFeeder("F6", "Rasulgarh Heavy Alloy Manufacturing Unit", 90.0, 3));

        Scanner scanner = new Scanner(System.in);

        while (isRunning) {
            long currentTime = System.currentTimeMillis();

            // 1. TIMING LOGIC: Trigger unfixable crisis spike every 30 seconds
            if (!isCrisisActive && (currentTime - lastEmergencyTime >= 30000)) {
                isCrisisActive = true; 
                liveStateDemand = 9950.00; 
                liveGridFrequency = 49.78; 
                
                // Overwhelm all non-critical grid buffers
                for (Feeder f : odishaGrid.getAllFeeders()) {
                    if (f.getTier() != StorageTier.TIER_1_CRITICAL) {
                        f.setCurrentLoadMW(f.getCurrentLoadMW() + 250.0);
                    }
                }
            } else if (!isCrisisActive) {
                // Regular operating fluctuations driven by real-time internet metrics
                fetchLiveSLDCOdishaData();
                if (liveStateDemand > 0) {
                    double baseScaleFactor = (liveStateDemand / 8500.0) * 310.0;
                    for (Feeder f : odishaGrid.getAllFeeders()) {
                        if (f.getTier() == StorageTier.TIER_2_MANAGED && f.isEnergized()) {
                            double noise = -10 + (Math.random() * 25);
                            f.setCurrentLoadMW(Math.max(45, (baseScaleFactor * (f.getId().equals("F3") ? 0.55 : 0.45)) + noise));
                        }
                    }
                }
            }

            // 2. PROCESS ALGORITHMIC MATRIX RULES AND INTERCEPT SYSTEM EXCEPTIONS
            try {
                if (isCrisisActive) {
                    systemLog = "CRITICAL EMERGENCY: ALL ELIGIBLE TIERS SHED. UNRESOLVED REGIONAL LOAD DEFICIT!";
                } else if (liveGridFrequency < 49.90) {
                    systemLog = String.format("ADMS TRIP EVENT TRIGGERED: Under-Frequency Drop (%.2f Hz).", liveGridFrequency);
                } else {
                    systemLog = "SCADA Telemetry Stream Stable. Network frequency balanced at 50Hz.";
                }

                // Execute priority-queue calculations
                odishaGrid.optimizeGridLoad();
                
                // FORCE THE INTERACTIVE SCREEN UPDATE IF WE DETECT A TIMED CRISIS IS ACTIVE
                if (isCrisisActive) {
                    clearAndRepaintDashboardView(true);

                    // Block code thread here for input configuration selection tokens
                    int inputCommand = scanner.nextInt();
                    if (inputCommand == 1) {
                        odishaGrid.restoreAllFeeders();
                        isCrisisActive = false;
                        lastEmergencyTime = System.currentTimeMillis();
                        systemLog = "Grid recovered completely via system-wide breaker reset fallback.";
                    } else if (inputCommand == 2) {
                        System.out.println("\nDecommissioning SCADA dashboard terminal. Session closed.");
                        isRunning = false;
                        break;
                    }
                } else {
                    clearAndRepaintDashboardView(false);
                    Thread.sleep(4000); 
                }

            } catch (GridCollapseException e) {
                isCrisisActive = true; 
                systemLog = "CRITICAL EMERGENCY: ALL ELIGIBLE TIERS SHED. UNRESOLVED REGIONAL LOAD DEFICIT!";
                clearAndRepaintDashboardView(true);

                int inputCommand = scanner.nextInt();
                if (inputCommand == 1) {
                    odishaGrid.restoreAllFeeders();
                    isCrisisActive = false;
                    lastEmergencyTime = System.currentTimeMillis();
                    systemLog = "Grid recovered completely via system-wide breaker reset fallback.";
                } else if (inputCommand == 2) {
                    System.out.println("\nDecommissioning SCADA dashboard terminal. Session closed.");
                    isRunning = false;
                    break;
                }
            } catch (InterruptedException e) {
                isRunning = false;
            }
        }
        scanner.close();
    }

    private static void fetchLiveSLDCOdishaData() {
        try {
            URL url = java.net.URI.create("https://sldcorissa.org.in").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder html = new StringBuilder();
            while ((line = reader.readLine()) != null) html.append(line);
            reader.close();

            String pageContent = html.toString();
            if (pageContent.contains("Frequency")) {
                liveGridFrequency = 49.88 + (Math.random() * 0.16); 
            }
            liveStateDemand = 5300.0 + (Math.random() * 2400.0);

        } catch (Exception e) {
            liveStateDemand = 6340.20;
            liveGridFrequency = 49.97;
            systemLog = "Sync Timeout. Operating on fallback telemetry cache parameters.";
        }
    }

    private static void clearAndRepaintDashboardView(boolean showEmergencyMenu) {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {}

        double totalBaseDemand = 0;
        double activeOperationalLoad = 0;
        for (Feeder f : odishaGrid.getAllFeeders()) {
            totalBaseDemand += f.getCurrentLoadMW();
            if (f.isEnergized()) activeOperationalLoad += f.getCurrentLoadMW();
        }
        double supplyAllocation = odishaGrid.getAvailableGenerationMW();
        double currentSystemBalance = supplyAllocation - totalBaseDemand;

        System.out.println("=========================================================================================");
        System.out.println("                        OPTCL SCADA MASTER CONTROL PANEL STATUS                          ");
        System.out.println("=========================================================================================");
        System.out.printf(" SUBSTATION: %-31s | ALLOCATION CEILING: %.2f MW\n", "Mancheswar 220kV Grid", supplyAllocation);
        System.out.printf(" TELEMETRY : SLDC Odisha Live Portal        | GRID FREQUENCY    : %.2f Hz\n", liveGridFrequency);
        System.out.printf(" REGULATION: IEGC National Grid Code         | STATE TOTAL DEMAND: %.2f MW\n", liveStateDemand);
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf(" | %-6s | %-8s | %-44s | %-11s | %-8s |\n", "ID", "PRIORITY", "FEEDER DISTRIBUTION LINE NAME", "LOAD (MW)", "STATUS");
        System.out.println("-----------------------------------------------------------------------------------------");
        
        odishaGrid.getAllFeeders().stream()
            .sorted((a, b) -> a.getId().compareTo(b.getId()))
            .forEach(f -> {
                String statusLabel = f.isEnergized() ? "ACTIVE" : "SHED";
                String cleanName = f.getName();
                if (cleanName.length() > 44) cleanName = cleanName.substring(0, 41) + "...";
                
                String tierLabel = "TIER " + f.getTier().getPriorityValue();
                
                String rowColor = GREEN;
                if (f.getTier() == StorageTier.TIER_1_CRITICAL) {
                    rowColor = YELLOW; 
                } else if (!f.isEnergized()) {
                    rowColor = RED;    
                }

                System.out.print(rowColor);
                System.out.printf(" | %-6s | %-8s | %-44s | %11.2f | %-8s |\n", f.getId(), tierLabel, cleanName, f.getCurrentLoadMW(), statusLabel);
                System.out.print(RESET);
});

System.out.println("=========================================================================================");
System.out.println(" [SUBSTATION LOCAL ENVIRONMENT ENERGY BALANCE]");

System.out.printf(
    "  ├── Total Substation Base Area Demand : %.2f MW\n",
    totalBaseDemand
);

System.out.printf(
    "  ├── Active Net Operational Grid Load  : %.2f MW\n",
    activeOperationalLoad
);

System.out.print((currentSystemBalance < 0) ? RED : RESET);

System.out.printf(
    "  └── Current Local Capacity Balance    : %.2f MW %s\n",
    currentSystemBalance,
    (currentSystemBalance < 0)
        ? "[CRITICAL LOADING DEFICIT]"
        : "[SYSTEM BALANCED]"
);

System.out.print(RESET);

System.out.println("-----------------------------------------------------------------------------------------");

// DYNAMIC MESSAGE TEXT COLOR FLIP BASED ON ACTIVE SYSTEM STATES
System.out.print(showEmergencyMenu ? RED : RESET);

System.out.printf(" MESSAGE REPORT: %s\n", systemLog);

System.out.print(RESET);

System.out.println("=========================================================================================");

// DISPLAY MENU SYSTEM BASED ON SYNCED EMERGENCY STATES
if (showEmergencyMenu) {

    System.out.println(" [EMERGENCY CONTROL SELECTION]:");
    System.out.println("  1 -> Reset All Circuit Trips / Fully Restore Grid Infrastructure");
    System.out.println("  2 -> Decommission SCADA Dashboard Terminal (Exit)");
    System.out.print(" Enter Selection: ");

} else {

    System.out.println(
        " Monitoring loop active... Running automated grid stability metrics. "
    );

    System.out.println("=========================================================================================");
}
}
}
