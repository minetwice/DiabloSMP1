# DiabloSMP

**Author:** Twicefear  
**Version:** 1.0.0  
**Platform:** Paper 1.21 – 1.21.8  
**Branch:** Grok

## Features

### 15 Legendary Diablo Stones
Each stone has **2 powerful abilities** (Right Click + Shift + Right Click) with complex particles, physics and domain effects.

| Stone                | Primary                  | Secondary              |
|----------------------|--------------------------|------------------------|
| Infernal Core        | Meteor Strike            | Hellfire Domain        |
| Abyssal Shard        | Abyss Pull               | Drowning Realm         |
| Earthquake Relic     | Rock Levitation + Launch | Pillar Domain          |
| Tempest Orb          | Tornado Pull             | Storm Prison           |
| Shadow Fang          | Shadow Dash              | Night Domain           |
| Radiant Prism        | Holy Beam                | Sanctuary Domain       |
| Frostbite Crystal    | Ice Spikes Wave          | Frozen Tomb            |
| Thunderbolt Core     | Chain Lightning          | Thunder Domain         |
| Bloodmoon Gem        | Life Steal Aura          | Blood Arena            |
| Nature's Wrath       | Vine Trap + Thorns       | Jungle Domain          |
| Phantom Echo         | Phase Shift              | Ghost Realm            |
| Chaos Fragment       | Chaos Burst              | Chaos Domain           |
| Dragonheart Scale    | Dragon Breath Cone       | Dragon Roost           |
| Voidwalker Stone     | Void Step                | Void Collapse          |
| Celestial Star       | Starfall Barrage         | Cosmic Prison          |

### Core Systems
- **First Join Animation** — 8 second levitation + complex particle rings + protection + random stone reward
- **Absorb System** — Hold stone → Shift 3 times → 1-slot GUI → absorb with orbiting animation
- **Dual Cooldown Bars** — ActionBar with unicode support (resource pack ready)
- **Stone Icon Display** — Shows current absorbed stone (or question mark if none)
- **SMP Start System** — `/diablosmp start` opens GUI for timer (minutes + seconds)
- **BossBar** — Grace period timer + SMP name
- **WorldBorder** — Starts small, expands after grace period (configable, supports infinite)
- **Withdraw** — `/diablosmp withdraw` to get stone back
- **Protection** — Configurable drop/store/death rules for stones

### Commands
| Command | Description |
|---------|-------------|
| `/diablosmp start` | Open start GUI (timer settings) |
| `/diablosmp stop` | Stop the SMP |
| `/diablosmp changename <name>` | Change SMP name |
| `/diablosmp withdraw` | Withdraw absorbed stone |
| `/diablosmp give <player> <stone>` | Give a stone |
| `/diablosmp list` | List all stones |
| `/diablosmp reload` | Reload config |
| `/diablosmp info` | Plugin info |

### Resource Pack
Located in `/resourcepack`
- Models for all 15 stones (CustomModelData 1000-1014)
- Font system for cooldown bars & icons
- See `resourcepack/HOW_TO_ADD_TEXTURES.md` for texture instructions

**Texture Style:** Glowing framed square icons (exactly like the purple eye / blue bubbles example)

### Building
```bash
./gradlew build
```
Artifact: `build/libs/DiabloSMP-1.0.0.jar`

GitHub Actions automatically builds on push to `Grok` branch.

## Author
**Twicefear**
