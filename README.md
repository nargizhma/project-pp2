# 🍔 The Silicon Spatula — Restaurant Tycoon
### CSCI 1202 Programming Principles II — Team 7

---

## JavaFX 26 Setup on macOS with VS Code

### Step 1 – Locate your JavaFX SDK

You said JavaFX 26 is already downloaded. Find the folder — it should look like:

```
/Users/<your-name>/javafx-sdk-26/
```

Inside it you should see a `lib/` folder containing `.jar` files like
`javafx.controls.jar`, `javafx.graphics.jar`, etc.

---

### Step 2 – Add JARs to the `lib/` folder of this project

1. Create a `lib/` folder in the **root** of this project (next to `src/`).
2. Copy **all `.jar` files** from your JavaFX SDK `lib/` folder into `lib/`.

```bash
cp /Users/<your-name>/javafx-sdk-26/lib/*.jar lib/
```

> VS Code's `settings.json` already tells it to pick up `lib/**/*.jar`.

---

### Step 3 – Edit `.vscode/launch.json`

Open `.vscode/launch.json` and replace the placeholder path:

```
--module-path /path/to/javafx-sdk-26/lib
```

with the **actual absolute path** to your JavaFX SDK lib folder, for example:

```
--module-path /Users/anar/javafx-sdk-26/lib
```

Save the file.

---

### Step 4 – Install the Java Extension Pack in VS Code

If not already installed, open VS Code and install:
- **Extension Pack for Java** (by Microsoft)

---

### Step 5 – Run the project

- Open the `SiliconSpatula` folder in VS Code.
- Press **F5** (or go to Run → Start Debugging).
- Select **"🍔 Run Silicon Spatula"** configuration.
- The restaurant dashboard window should appear!

---

## Project Structure

```
SiliconSpatula/
├── src/main/java/com/siliconSpatula/
│   ├── model/
│   │   ├── Ingredient.java        ← enum of all ingredients
│   │   ├── ApplianceType.java     ← enum of appliance types
│   │   ├── MenuItem.java          ← abstract base class
│   │   ├── MenuItemFactory.java   ← factory for save/load reconstruction
│   │   ├── Order.java             ← wraps a MenuItem with an ID
│   │   └── menu/
│   │       ├── HotFood.java       ← abstract + Burger, ChickenStrips, Twister, Fries
│   │       ├── Dessert.java       ← abstract + MuffinCake, Cheesecake
│   │       ├── Beverage.java      ← abstract + Coffee, Tea, Cola, Water
│   │       └── Sauce.java         ← abstract + KetchupSauce, MayoSauce, BarbecueSauce
│   ├── appliance/
│   │   ├── IAppliance.java        ← interface: canProcess(), process()
│   │   ├── Grill.java
│   │   ├── AirPot.java
│   │   ├── DrinkDispenser.java
│   │   └── SauceDispenser.java
│   ├── manager/
│   │   ├── InventoryManager.java  ← encapsulated HashMap<Ingredient,Integer>
│   │   └── InsufficientIngredientsException.java
│   ├── engine/
│   │   └── RestaurantEngine.java  ← simulation engine + Timeline
│   ├── fileio/
│   │   └── FileManager.java       ← save/load to silicon_spatula_save.txt
│   └── ui/
│       └── DashboardUI.java       ← JavaFX Application (4 panels)
├── lib/                           ← place JavaFX JARs here
├── .vscode/
│   ├── settings.json
│   └── launch.json
└── README.md
```

---

## How to Play

| Action | Description |
|--------|-------------|
| **Cook Next Order** | Polls the top order from the queue. Checks ingredients, cooks with a delay, then delivers. |
| **Buy Ingredient** | Select ingredient from dropdown → click Buy. Costs $5.00, adds 10 units. |
| **Save Game** | Writes cash, all ingredient quantities, and pending queue to `silicon_spatula_save.txt`. |
| **Load Game** | Restores everything from the save file. |
| **Stop & Save** | Stops the order generation timeline safely, then saves. **Use this before closing the window** to avoid file corruption. |

---

## Save File Format

```
# Silicon Spatula Save File
MONEY,120.00
INGREDIENT,BUN,10
INGREDIENT,BEEF_PATTY,5
...
ORDER,1,BURGER
ORDER,2,COLA
```
