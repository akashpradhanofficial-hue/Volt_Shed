# Real-Time Automated Hierarchical Grid Load-Shedding Matrix Engine (ADMS)

An enterprise-inspired, real-time **Automated Demand Management Scheme (ADMS)** modeled on the **Odisha Power Transmission Corporation Limited (OPTCL)** network operations. The system complies with the **Indian Electricity Grid Code (IEGC)** and state regulations (**Odisha Electricity Grid Code - OEGC**) to protect transmission infrastructure (such as the Mancheswar EHV Substation) and prevent catastrophic cascading grid failures during acute power deficits [sldcorissa.org.in/].

## 🚀 Final Project Specifications & What It Does

This system operates as a live command center dashboard running natively inside a terminal. It features several key operations:

* **Live Internet SCADA Integration**: A dedicated background thread executes HTTP network requests targeting the live public portal of the **State Load Despatch Centre (SLDC) Odisha** [sldcorissa.org.in/]. It extracts real-time metrics for **State Demand (MW)** and **Grid Operating Frequency (Hz)**, then mathematically scales them down to model a 350 MW regional substation profile.
* **In-Memory Register Caching**: Bypasses slow, disk-bound relational SQL databases to entirely eliminate transactional latency blocks during frequency crises. Deploys a thread-safe `ConcurrentHashMap` caching layer to store and manage active feeder metrics at sub-millisecond execution speeds.
* **Logarithmic Prioritization Processing**: Leverages a custom `Comparable` architecture fed into a **Max-Heap based PriorityQueue Collection** structure. This automatically floats high-capacity, non-essential lines to the top of the shedding list, resolving systemic power deficits in $O(\log N)$ computational efficiency.
* **Color-Coded Structural UI Grid**: Implements a strict, fixed-width columnar terminal interface. Uses ANSI escape codes to map utility statuses instantly: **Yellow** forces absolute immutability on Tier 1 emergency networks; **Green** highlights active, balanced feeders; and **Red** provides immediate visibility when the engine sheds an overloaded line.
* **Automated Stress-Test & Custom Exception Handling**: Simulates a severe grid crisis every 30 seconds (such as a massive consumer cooling surge). When the deficit exceeds all available Tier 2 and Tier 3 capacities, the system encounters an unfixable threshold. It blocks accidental drops to hospitals, triggers a custom checked `GridCollapseException`, shifts the display message to a red alert state, and locks into an interactive manual menu prompt (`1 -> Reset Trips`, `2 -> Exit`) to demonstrate robust error recovery loops.

---

## 🛠️ Technical Stack Profile

* **Core Language**: Java (Advanced Object-Oriented Design)
* **Concurrency Foundations**: Multi-threading Engine Models & Synchronized Blocks
* **Network Infrastructure**: Java Built-in HTTP Resource Client Streams (`java.net.HttpURLConnection`)
* **Data Structures**: Max-Heap Priority Queues, Concurrent Maps (`ConcurrentHashMap`), Stream Arrays, custom Object Sorting Comparators

---

## 📦 3-Tier Priority Feeder Configuration Matrix

The system dynamically processes six realistic high-voltage feeder channels mapped to the local geography of the Mancheswar electrical network:

* **Tier 1: Zero-Failure Critical Lifelines (Forced Yellow - Immune to Automated Shedding Loops)**
  * `F1` (30.00 MW): KIMS and Capital Medical Emergency Feeder (Direct human life threat)
  * `F2` (110.00 MW): Mancheswar Railway Workshop Traction Corridor (Mass public mobility sector)
* **Tier 2: Managed Consumer Sectors (Green / Red - Fluctuates based on live internet data surges)**
  * `F3` (70.00 MW Base): Saheed Nagar High-Density Residential Area
  * `F4` (60.00 MW Base): Vani Vihar and Utkal Varsity Residential Blocks
* **Tier 3: Elastic High-Capacity Industrial Loads (Green / Red - Selected for immediate shed execution)**
  * `F5` (80.00 MW): Chandaka Industrial Estate - Sector A
  * `F6` (90.00 MW): Rasulgarh Heavy Alloy Manufacturing Unit

---

## 📂 System Architecture File Layout

1. **`Feeder.java`**: Domain Model representing high-voltage transmission branches. Encapsulates numerical load values, functional priority attributes, an object-creational **Factory Design Pattern**, and multi-parameter boundary comparisons to drive the priority queue.
2. **`GridSubstation.java`**: Substation Core Engine managing power ceilings and caching registries. Houses the thread-safe `optimizeGridLoad()` math block that tracks cumulative consumption against capacity limits and explicitly throws the checked exception when boundaries collapse.
3. **`Main.java`**: System Orchestrator. Coordinates the background internet scraping routine, executes the automated 30-second stress-test intervals, handles active terminal clear parameters (`cls`), formats the ANSI color-coding, and intercepts system exceptions to present interactive operational inputs.
