# DiabloSMP Plugin

**Author:** Twicefear  
**Minecraft:** 1.21 - 1.21.8 (Paper)  
**GitHub:** https://github.com/minetwice/DiabloSMP1

## Overview

DiabloSMP is a comprehensive Minecraft SMP plugin featuring 15 unique Diablo stones, each with two powerful abilities (primary = right click, secondary = shift + right click). The plugin includes a cinematic first-join reward system, stone absorption mechanics, custom cooldown bars, SMP grace period management, and a full resource pack.

## Features

### 15 Diablo Stones
Each stone has 2 unique abilities with complex particle animations:

| Stone | Primary (Right Click) | Secondary (Shift + Right Click) |
|-------|----------------------|-------------------------------|
| Earthquake | Lift blocks, cursor-controlled, launchable | 4 pillars -> shadow domain (10s) |
| Inferno | Homing fireball spiral | Fire tornado + eruption |
| Tempest | Cyclone pull + launch | Thunderstorm dome |
| Frostbite | Piercing ice shard | Ice sphere shield |
| Shadow | Shadow dash + afterimages | Shadow realm (invisible, bonus damage) |
| Holy | Smite pillar of light | Healing sanctuary ring |
| Void | Void rift implosion | Orbiting singularity |
| Nature | Entangling roots | Healing grove |
| Lightning | Chain lightning (5 jumps) | Lightning storm waves |
| Blood | Lifesteal projectile | Blood drain field |
| Gravity | Gravity well crush | Gravity inversion + crash |
| Soul | Soul rip + orbiting wisps | Soul harvest wave |
| Arcane | Homing arcane missile | Arcane nova silence |
| Plague | Plague cloud (spreads) | Pandemic zone |
| Chronos | Time slow aura | Temporal rewind (invulnerable) |

### Join Reward Cinematic
- Player levitates with complex particle animation for 8 seconds
- All 15 stone types pop up orbiting the player during the animation
- Player is invulnerable during the cinematic
- After the duration, player descends and receives a random stone

### Absorb System
- Hold a dormant stone and shift 3 times to open the absorb menu
- Place the stone in the single-slot menu
- Stone orbits the player and absorbs into their body with animation
- After absorption, abilities are unlocked

### Cooldown Display
- Custom resource-pack font characters render smooth cooldown bars
- Two separate bars: primary (left) and secondary (right)
- Stone icon displayed between the two bars
- Unknown stone shows a gray question mark
- Ready state shows "Ability Ready!" in green

### SMP Start System
- `/diablosmp start` opens a GUI for choosing grace period timer
- Boss bar displays SMP name and countdown
- World border shrinks to start size during grace
- On timer end: countdown -> announcements -> border expands
- `/diablosmp changename <name>` changes the SMP name live

### Commands
| Command | Description |
|---------|------------|
| `/diablosmp start` | Start SMP with grace timer GUI |
| `/diablosmp stop` | Stop the SMP |
| `/diablosmp changename <name>` | Change SMP name |
| `/diablosmp withdraw` | Withdraw absorbed stone to inventory |
| `/diablosmp reload` | Reload config |
| `/diablosmp status` | Show SMP status |
| `/diablosmp reset` | Clear all cooldowns |
| `/diablostone <stone> [player]` | Give a stone |
| `/diablostone list` | List all stones |

## Installation

1. Place `DiabloSMP-1.0.0.jar` in your server's `plugins/` folder
2. Place the resource pack in your server's resource pack directory
3. Restart the server
4. Configure `config.yml` as needed
5. Run `/diablosmp start` to begin the SMP

## Building

```bash
mvn clean package
```

The compiled JAR will be in `target/DiabloSMP-1.0.0.jar`.

## License

MIT License - Copyright (c) 2026 Twicefear
