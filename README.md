# Silicon Spatula – Restaurant Tycoon
**Term Project – CSCI 1202 Programming Principles II · Spring 2026**

A JavaFX simulation game where you manage a fast-food restaurant. Orders arrive automatically every few seconds, you cook them using available ingredients, restock when supplies run low, and keep the cash flowing.

---

## Requirements

- **Java 21 or higher** — check with `java -version`
- **Maven 3.8 or higher** — check with `mvn -version`

If you don't have Maven installed, see the installation steps below.

---

## Installing Maven

### macOS

The easiest way is via Homebrew:

```bash
brew install maven
```

If you don't have Homebrew, install it first from [https://brew.sh](https://brew.sh), then run the command above.

### Windows

1. Download the **Binary zip archive** from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
2. Extract it somewhere like `C:\Program Files\Maven`
3. Add the `bin` folder to your system `PATH`:
   - Search for **"Edit the system environment variables"** in the Start menu
   - Click **Environment Variables**
   - Under **System variables**, find `Path` → click **Edit** → **New**
   - Add: `C:\Program Files\Maven\apache-maven-3.x.x\bin`
4. Restart your terminal and verify with `mvn -version`

---

## How to Run

### Step 1 — Clone the repository

```bash
git clone https://github.com/ADA-SITE-CSCI1202-Spring-2026/term-project-team-7.git
cd teamprojectPP2
```

### Step 2 — Run the application

**macOS / Linux:**
```bash
mvn javafx:run
```

**Windows:**
```bash
mvn javafx:run
```

The command is the same on all platforms. Maven will automatically download JavaFX the first time — this may take a minute on the first run. After that it uses the cached version and starts instantly.

> **Note:** Do not open individual `.java` files and try to run them. The project must be launched through Maven from the project root (the folder containing `pom.xml`).

---

## Project Structure

```
teamprojectPP2/
├── pom.xml                                     # Maven config – manages JavaFX dependency
├── README.md
└── src/
    └── appliance/
    │   ├── AirPot.java                         
    │   ├── DrinkDispenser.java                 
    │   ├── Grill.java                          
    │   ├── IAppliance.java                     
    │   ├── Oven.java                           
    │   └── SauceDispenser.java                 
    ├── engine/
    │   └── RestaurantEngine.java               
    ├── fileio/
    │   └── FileManager.java                    
    ├── manager/
    │   ├── InsufficientFundsException.java
    │   ├── InsufficientIngredientsException.java
    │   └── InventoryManager.java               
    ├── model/
    │   ├── menu/
    │   │   ├── Beverage.java                   
    │   │   ├── Dessert.java                    
    │   │   ├── HotFood.java                    
    │   │   └── Sauce.java                      
    │   ├── ApplianceType.java                  
    │   ├── Ingredient.java                     
    │   ├── MenuItem.java                       
    │   ├── MenuItemFactory.java                
    │   └── Order.java                          
    └── ui/
        └── DashboardUI.java                    
```

---

## The Four UI Panels

### 1. Orders Queue
Shows all pending customer orders in a live list. A new order is automatically generated every few seconds. Click **Cook Next Order** to process the order at the front of the queue. Successfully cooked orders earn you cash; failed orders (due to missing ingredients) are rejected and logged.

### 2. Inventory
Displays your current cash balance and the stock count of every ingredient. Updates immediately after every cook, restock, or file load.

### 3. Restock
Pick an ingredient from the dropdown and click **Buy Ingredient** to purchase 10 units and spend the matching amount of cash. If you don't have enough cash, the purchase is rejected and logged.

### 4. System Log
A scrolling live feed of everything happening in the restaurant — new orders, successful cooks, failed cooks with the specific missing ingredient, restocks, saves, and loads.

---

## Ingredients & Recipes

| Ingredient      | Restock cost (per 10) | Used in                                          |
|-----------------|-----------------------|--------------------------------------------------|
| Bun             | $2.50                 | Burger (×2)                                      |
| Beef Patty      | $8.00                 | Burger (×1)                                      |
| Chicken Strip   | $6.50                 | Chicken Strips (×3), Twister (×2)                |
| Lettuce         | $1.50                 | Burger (×1), Twister (×1)                        |
| Tomato          | $2.00                 | Burger (×1), Twister (×1)                        |
| Cheese          | $3.50                 | Burger (×1), Cheesecake (×1)                     |
| Pickle          | $1.50                 | Burger (×1)                                      |
| Lavash          | $2.00                 | Twister (×1)                                     |
| Potato          | $1.50                 | Fries (×2)                                       |
| Oil             | $3.00                 | Fries (×1)                                       |
| Ketchup         | $1.50                 | Ketchup Sauce (×1)                               |
| Mayo            | $1.50                 | Mayo Sauce (×1), Twister (×1)                    |
| Barbecue        | $2.00                 | Barbecue Sauce (×1)                              |
| Heavy Cream     | $3.50                 | Muffin Cake (×1), Cheesecake (×1)                |
| Dark Chocolate  | $4.50                 | Muffin Cake (×1)                                 |
| Butter          | $2.50                 | Muffin Cake (×1), Cheesecake (×1)                |
| Sugar           | $1.00                 | Muffin Cake (×1), Cheesecake (×2), Americano (×1)|
| Cola Syrup      | $3.00                 | Cola (×1)                                        |
| Cola Zero Syrup | $3.00                 | Cola Zero (×1)                                   |
| Sprite Syrup    | $3.00                 | Sprite (×1)                                      |
| Coffee Bean     | $5.50                 | Latte (×1), Americano (×1)                       |
| Tea Leaf        | $2.50                 | Tea (×1)                                         |
| Water           | $0.50                 | Latte (×1), Americano (×1), Tea (×1), Cola (×1), Cola Zero (×1), Sprite (×1), Water (×1) |
| Milk            | $2.00                 | Latte (×1), Americano (×1)                       |

**Menu prices:** Burger $8.50 · Chicken Strips $7.00 · Twister Wrap $9.00 · Fries $4.00 · Muffin Cake $5.50 · Cheesecake $6.00 · Latte $4.00 · Americano $3.50 · Tea $2.50 · Cola $2.00 · Cola Zero $2.00 · Sprite $2.00 · Water $1.00 · Ketchup $0.50 · Mayo $0.50 · Barbecue Sauce $0.75

**Starting state:** 10 of every ingredient · $120.00 cash

---

## Save / Load

Use the **Save** and **Load** buttons in the UI. The game state is written to `silicon_spatula_save.txt` in the project root directory. The file is plain text and saves:

- All ingredient quantities
- Current cash balance
- The full pending order queue (in order, with item types preserved)

Loading restores everything exactly as it was so you can resume the simulation where you left off.
