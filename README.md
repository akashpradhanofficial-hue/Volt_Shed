# Real Time Automated Hierarchical Grid Load-Shedding Matrix Engine

An enterprise-inspired, real-time **Automated Demand Management Scheme (ADMS)** modeled on the **Odisha Power Transmission Corporation Limited (OPTCL)** network operations. The system complies with the **Indian Electricity Grid Code (IEGC)** and state regulations (**Odisha Electricity Grid Code (OEGC)**) to protect transmission infrastructure (such as the Mancheswar EHV Substation) and prevent catastrophic cascading grid failures during acute power deficits.

---

## Specifications & What It Does

This system operates as a live command center dashboard running natively inside a terminal. It features several key operations:

- **Live Internet SCADA Integration**: A dedicated background thread executes HTTP network requests targeting the live public portal of the **State Load Despatch Centre (SLDC) Odisha**. It extracts real-time metrics for **State Demand (MW)** and **Grid Operating Frequency (Hz)**, then mathematically scales them down to model a 350 MW regional substation profile.

- **In-Memory Register Caching**: Deploys a thread-safe `ConcurrentHashMap` caching layer to store and manage active feeder metrics at sub-millisecond execution speeds.

- **Logarithmic Prioritization Processing**: Leverages a custom `Comparable` architecture fed into a **Max-Heap based PriorityQueue Collection** structure. This automatically floats high-capacity, non-essential lines to the top of the shedding list, resolving systemic power deficits in $O(\log N)$ computational efficiency.

- **Color-Coded Structural UI Grid**: Implements a strict, fixed-width columnar terminal interface. Uses ANSI escape codes to map utility statuses instantly: **Yellow** forces absolute immutability on Tier 1 emergency networks; **Green** highlights active, balanced feeders; and **Red** provides immediate visibility when the engine sheds an overloaded line.

- **Automated Stress-Test & Custom Exception Handling**: Simulates a severe grid crisis every 30 seconds (such as a massive consumer cooling surge). When the deficit exceeds all available Tier 2 and Tier 3 capacities, the system encounters an unfixable threshold. It blocks accidental drops to hospitals, triggers a custom checked `GridCollapseException`, shifts the display message to a red alert state, and locks into an interactive manual menu prompt (`1 -> Reset Trips`, `2 -> Exit`) to demonstrate robust error recovery loops.

---

## 📊 Live Execution Results

The following screenshots show the system progressing through its operational states.

### Normal Automated Grid Monitoring
<p align="center">
   <img width="850" alt="normal-operation" src="https://github.com/user-attachments/assets/66321d4f-1999-499c-bea9-99095f67deca" />
</p>
<p align="center"><em>Tier 2 and Tier 3 feeders in green as long as they're active and turn red when shed while Tier 1 remains protected denoted by yellow.</em></p>

### Critical Emergency & Automated Load Shedding

<p align="center">
  <img src="results/critical-emergency.png" alt="Critical OPTCL SCADA load-shedding state" width="850">
</p>

<p align="center"><em>Critical emergency state — Tier 2 and Tier 3 feeders shed while Tier 1 critical feeders remain protected.</em></p>

### Controlled Session Termination

<p align="center">
  <img src="results/session-termination.png" alt="OPTCL SCADA dashboard session termination" width="850">
</p>

<p align="center"><em>Controlled SCADA dashboard decommissioning after selecting the Exit option.</em></p>

---

## 🛠️ Technical Stack Profile

- **Core Language**: Java (Advanced Object-Oriented Design)
- **Concurrency Foundations**: Multi-threading Engine Models & Synchronized Blocks
- **Network Infrastructure**: Java Built-in HTTP Resource Client Streams (`java.net.HttpURLConnection`)
- **Data Structures**: Max-Heap Priority Queues, Concurrent Maps (`ConcurrentHashMap`), Stream Arrays, custom Object Sorting Comparators

---

## 📦 3-Tier Priority Feeder Configuration Matrix

The system dynamically processes six realistic high-voltage feeder channels mapped to the local geography of the Mancheswar electrical network:

### Tier 1: Zero-Failure Critical Lifelines

**Forced Yellow — Immune to Automated Shedding Loops**

- `F1` (30.00 MW): KIMS and Capital Medical Emergency Feeder (Direct human life threat)
- `F2` (110.00 MW): Mancheswar Railway Workshop Traction Corridor (Mass public mobility sector)

### Tier 2: Managed Consumer Sectors

**Green / Red — Fluctuates based on live internet data surges**

- `F3` (70.00 MW Base): Saheed Nagar High-Density Residential Area
- `F4` (60.00 MW Base): Vani Vihar and Utkal Varsity Residential Blocks

### Tier 3: Elastic High-Capacity Industrial Loads

**Green / Red — Selected for immediate shed execution**

- `F5` (80.00 MW): Chandaka Industrial Estate - Sector A
- `F6` (90.00 MW): Rasulgarh Heavy Alloy Manufacturing Unit

---

## 📂 System Architecture File Layout

```text
VoltShed2/
├── Main.java
├── Feeder.java
├── GridSubstation.java
├── results/
│   ├── normal-operation.png
│   ├── critical-emergency.png
│   └── session-termination.png
└── README.md
```

1. **`Feeder.java`**: Domain Model representing high-voltage transmission branches. Encapsulates numerical load values, functional priority attributes, an object-creational **Factory Design Pattern**, and multi-parameter boundary comparisons to drive the priority queue.

2. **`GridSubstation.java`**: Substation Core Engine managing power ceilings and caching registries. Houses the thread-safe `optimizeGridLoad()` math block that tracks cumulative consumption against capacity limits and explicitly throws the checked exception when boundaries collapse.

3. **`Main.java`**: System Orchestrator. Coordinates the background internet scraping routine, executes the automated 30-second stress-test intervals, handles active terminal clear parameters (`cls`), formats the ANSI color-coding, and intercepts system exceptions to present interactive operational inputs.

---

## 🔄 Operational Flow

```text
Live SLDC Telemetry
        ↓
Feeder Load Update
        ↓
Substation Capacity Evaluation
        ↓
Power Deficit Detected?
      /       \
    NO         YES
    │           │
    │           ↓
    │     Priority Queue
    │           ↓
    │     Tier-Based Shedding
    │           ↓
    │     Tier 1 Protected
    │           ↓
    │     Tier 2 / Tier 3 Shed
    │           ↓
    │     Deficit Resolved?
    │        /       \
    │      YES        NO
    │       │          │
    │       │          ↓
    │       │   GridCollapseException
    │       │          ↓
    │       │   Emergency Control Menu
    │       │       /           \
    │       │      /             \
    │       ▼                    ▼
    │     Resume                 Exit
    │       │                    │
    └───────┴────────────────────┘
             Monitoring
```

---

## 🚨 Emergency Control & Exception Handling

When the deficit exceeds all available Tier 2 and Tier 3 capacities, the system triggers the custom checked `GridCollapseException`.

The emergency interface provides:

```text
[EMERGENCY CONTROL SELECTION]:

1 -> Reset All Circuit Trips / Fully Restore Grid Infrastructure
2 -> Decommission SCADA Dashboard Terminal (Exit)
```

### Option 1 — Reset Trips

Selecting `1` restores all feeders and resumes the monitoring loop. The same emergency stress-test cycle continues until another critical event occurs.

### Option 2 — Exit

Selecting `2` decommissions the SCADA dashboard terminal and closes the session.

---

## 🎯 Key Engineering Concepts Demonstrated

- Advanced Java OOP
- Factory Design Pattern
- `Comparable` interface
- Max-Heap based `PriorityQueue`
- `ConcurrentHashMap`
- Multi-threading and synchronized blocks
- Java Streams
- HTTP networking using `HttpURLConnection`
- Custom checked exception handling
- Automated grid stress testing
- Hierarchical load-shedding logic
- ANSI color-coded terminal UI
- Interactive emergency recovery
- Power-demand and capacity calculations

---

## 🏁 Project Outcome

The project demonstrates an enterprise-inspired Java implementation of an automated hierarchical load-shedding system. It combines real-time telemetry handling, concurrent data management, priority-based decision making, exception-driven emergency handling and an interactive terminal SCADA interface into a single simulation.

The execution screenshots demonstrate the complete operational lifecycle:

**Normal Monitoring → Automated Load Shedding → Critical Exception → Operator Control → Grid Restoration / Controlled Shutdown**
