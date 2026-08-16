# DiabloSMP

**DiabloSMP** is a production-ready, highly cinematic Paper plugin designed for Minecraft 1.21 through 1.26.2+.
It introduces **15 unique Diablo Stones**, each featuring mass-level animated boss abilities complete with particles, display entities, custom sounds, delayed keyframe sequences, multi-target damage, and persistent cooldown tracking.

---

## 🌟 Key Features

- **15 Cinematic Diablo Stones**: Custom boss-level animated skills (NOT potion effects!).
  1. `SHARD` (Diablo Shard) - Corrupted azure shards & target lock smash.
  2. `EMBER` (Diablo Ember) - Hellfire crimson ring & meteor fangs.
  3. `HALO` (Diablo Halo) - Solar rays & holy lightning pillars.
  4. `ROOT` (Diablo Root) - Corrupted nature vines trap & leaf storm.
  5. `VOID` (Diablo Void) - Void rift pull & implosion collapse.
  6. `FROST` (Diablo Frost) - Expanding frost nova & ice spikes trap.
  7. `STORM` (Diablo Storm) - Chained lightning storm arcs.
  8. `BLOOD` (Diablo Blood) - Swirling blood scythes & 30% lifesteal.
  9. `SERAPH` (Diablo Seraph) - 12-wing corrupted seraph & feather blades.
  10. `GRAVE` (Diablo Grave) - Heavy obsidian gravity core & eruption.
  11. `MIRAGE` (Diablo Mirage) - 4 phantom clones dash sequence.
  12. `LANCE` (Diablo Lance) - High-velocity piercing comet beam.
  13. `ABYSS` (Diablo Abyss) - Abyssal riptide tide & ocean surge.
  14. `CHRONO` (Diablo Chrono) - Time-field slowdown & time shatter.
  15. `OMEGA` (Diablo Omega) - Ultimate Diablo requiem with 15 light swords.

- **Dynamic HUD System**:
  - Live action-bar status displaying active stone cooldown progress.
  - Diablo Fragments status icons (`◆`/`◇`) showing owned and active stones.

- **Soulbound & Profile Protection**:
  - Soulbound item tag support preventing dropping, container storing, or trading.
  - Asynchronous saving to Flatfile JSON or SQLite database.
  - Persistent cooldowns across relogs and server restarts.

- **First-Join Starter & Admin Management**:
  - Optional starter GUI for new players to select their first stone.
  - Complete administrative command suite with full tab completion.

---

## 🚀 Installation

1. Download the compiled `DiabloSMP-1.0.0.jar` from the build output.
2. Place the jar file into your server's `plugins/` directory.
3. Start or restart your Paper 1.21+ server (Java 21 or Java 25 runtime).
4. Configure options in `plugins/DiabloSMP/config.yml` and `messages.yml` as desired.

---

## 📜 Commands & Permissions

### Player Commands
- `/diablo menu` - Opens the Diablo Stone equipment & selection menu (`diablosmp.use`)
- `/diablo select <stone>` - Sets active Diablo Stone (`diablosmp.use`)
- `/diablo starter` - Reopens first-join starter menu if unclaimed (`diablosmp.use`)
- `/diablo hud` - Toggles HUD display (`diablosmp.use`)
- `/diablo help` - Shows command help menu (`diablosmp.use`)

### Admin Commands (`diablosmp.admin`)
- `/diablo give <player> <stone|all>` - Grants stone(s) to a player
- `/diablo remove <player> <stone|all>` - Removes stone(s) from a player
- `/diablo resetcooldown <player> <stone|all>` - Resets stone cooldown(s)
- `/diablo reload` - Reloads configuration and messages
- `/diablo saveall` - Forces asynchronous save of all player profiles
- `/diablo cleanup` - Cleans up active tasks and leftover display entities

---

## 🎨 Resource Pack Support

The `resourcepack/` directory contains standard Minecraft overrides and font bitmaps for custom item models and custom Diablo Fragments HUD icons:
- `resourcepack/pack.mcmeta`
- `resourcepack/assets/minecraft/font/default.json`
- `resourcepack/assets/minecraft/models/item/amethyst_shard.json` (Custom Model Data 1001-1015)

---

## 🛠 Building from Source

```bash
gradle build
```
The output jar will be located in `build/libs/DiabloSMP-1.0.0.jar`.
