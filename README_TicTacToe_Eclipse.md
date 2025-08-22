
# Little Luminaries — **TicTacToe** (JavaFX 24)  
## Eclipse 2025‑09 Run Guide — **No Modules** (no `module-info.java`)

**App:** TicTacToe (JavaFX)  
**Author:** Tennie White  
**Tested With:** JDK 24, JavaFX SDK 24, Eclipse 2025‑09  
**Build Tool:** None (plain Eclipse; **no Maven/Gradle**, **no modules**)

---

## 1) What this app is
A kid‑friendly **two‑player** Tic‑Tac‑Toe using **dolphin vs. whale** icons and simple sounds. It’s a single JavaFX application you can run directly from Eclipse without modules.

---

## 2) Requirements
- **JDK 24** installed and selected in Eclipse  
  (Eclipse → *Window* → *Preferences* → **Java** → **Installed JREs** → add/select JDK 24)
- **JavaFX SDK 24** downloaded and unzipped (you need the path to its `lib` folder)
- **Eclipse 2025‑09** (or newer)

> No `module-info.java`. We’ll run non‑modular with the correct **VM arguments**.

---

## 3) Recommended Project Layout
Make sure your **package** line matches the folder path.

```
TicTacToe/                         ← Eclipse project root
├─ src/
│  └─ com/littleluminaries/
│     └─ TicTacToe.java            ← main class
├─ resources/
│  ├─ images/
│  │  ├─ dolphin.png
│  │  └─ whale.png
│  └─ sounds/                      ← optional, if your code plays sounds
│     ├─ click.mp3
│     └─ win.mp3
└─ lib/                            ← optional (any extra jars you use)
```

**Important path note**: If your code uses `new Image("file:resources/images/dolphin.png")`, then the `resources/` folder should sit at the **project root** (same level as `src/`).

---

## 4) Import the Project into Eclipse
**File → Import… → General → Existing Projects into Workspace → Next**  
- **Select root directory**: choose your project folder (or use **Select archive file** if you have a .zip)  
- Ensure the project is found → **Finish**

Verify: `src/com/littleluminaries/TicTacToe.java` exists and begins with:  
```java
package com.littleluminaries;
```

---

## 5) Add JavaFX 24 JARs (Non‑modular project)
JavaFX isn’t part of the JDK; add it to your **Build Path** and pass VM args at runtime.

1) Download **JavaFX SDK 24** and note its `lib` folder path. Examples:  
- Windows: `C:\javafx-sdk-24\lib`  
- macOS: `/Library/Java/javafx-sdk-24/lib` (or wherever you unzipped it)  
- Homebrew macOS: `/opt/homebrew/opt/javafx/lib`

2) In Eclipse: **Project → Properties → Java Build Path → Libraries**  
- Click **Modulepath** (preferred even without modules) or **Classpath** (both will work here).  
- Click **Add External JARs…** and add **all JARs inside** your `javafx-sdk-24/lib` folder.  
- **Apply and Close**.

> Tip: Create a **User Library** named `JavaFX24` pointing to `…/lib`, then add that library to your project. It’s reusable across projects.

---

## 6) Create a Run Configuration
**Run → Run Configurations… → Java Application → New**

**Main tab**  
- **Project**: select your project  
- **Main class**: `com.littleluminaries.TicTacToe`

**Arguments tab → VM arguments** (single line — **no carets `^`**):
- **Windows example:**
  ```
  --module-path "C:\javafx-sdk-24\lib" --add-modules=javafx.controls,javafx.graphics,javafx.media --enable-native-access=javafx.graphics
  ```
- **macOS/Linux example (adjust the path):**
  ```
  --module-path "/Library/Java/javafx-sdk-24/lib" --add-modules=javafx.controls,javafx.graphics,javafx.media --enable-native-access=javafx.graphics
  ```

Click **Apply** → **Run**.

**Why these flags?**  
- `--module-path` and `--add-modules` tell the JVM where to find JavaFX at runtime (even though our project is non‑modular).  
- `--enable-native-access=javafx.graphics` silences Java 24’s “restricted method” warning in JavaFX.

---

## 7) First Run Checklist
- A window opens (e.g., “TicTacToe”).  
- Dolphin/whale images render.  
- Clicking places icons on the grid.  
- If sounds are coded, simple sounds play on moves/wins.

---

## 8) Troubleshooting (quick fixes)

**`Could not find or load main class com.littleluminaries.TicTacToe`**  
- Check **Run Config → Main class** exactly matches the package + class name.  
- Ensure the file path matches package: `src/com/littleluminaries/TicTacToe.java` with `package com.littleluminaries;`.

**`ClassNotFoundException: ^`**  
- You pasted VM args with Windows line‑continuation `^`. In Eclipse, keep **one single line**; **remove `^`**.

**`NoClassDefFoundError: javafx/application/Application`**  
- JavaFX not on runtime path. Re‑check **VM arguments** and that JavaFX JARs were added to the project.

**Images don’t show**  
- Confirm files exist at the exact path your code uses.  
- For `new Image("file:resources/images/dolphin.png")`, the `resources/images/` folder must be at the project root.  
- Console will show URL/IO errors if a file is missing.

**Audio doesn’t play**  
- Verify file format (MP3/WAV) and path.  
- Check Console for `MediaPlayer` errors.

**Build path confusion**  
- **Project → Clean…** then rebuild.  
- Make sure only one JDK is active (JDK 24).

---

## 9) Known‑Good VM Arguments (copy/paste)

**Windows:**
```
--module-path "C:\javafx-sdk-24\lib" --add-modules=javafx.controls,javafx.graphics,javafx.media --enable-native-access=javafx.graphics
```

**macOS (Homebrew JavaFX):**
```
--module-path "/opt/homebrew/opt/javafx/lib" --add-modules=javafx.controls,javafx.graphics,javafx.media --enable-native-access=javafx.graphics
```

**Linux (example):**
```
--module-path "/usr/lib/javafx/lib" --add-modules=javafx.controls,javafx.graphics,javafx.media --enable-native-access=javafx.graphics
```

---

## 10) Tips
- Keep **VM args** on a single line in Eclipse.  
- You may keep JavaFX jars on **Modulepath** or **Classpath** for non‑modular runs; both work when you pass the VM args.  
- Consider a **User Library** to avoid re‑adding JavaFX jars for every project.  
- If you later add a `module-info.java`, move JavaFX jars to **Modulepath** and keep the same VM args.

---

### You’re all set!
Run the **TicTacToe** configuration and have fun. 🐬 vs 🐋
