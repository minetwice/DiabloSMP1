# DiabloSMP

A **Custom Cinematic Abilities Plugin** for Minecraft 1.21+, powered by the [Aethelion](https://github.com/minetwice) engine.

## Features

- **15 unique abilities** — each with distinct visuals, mechanics, and elemental themes
- **Cinematic transformations** — Titan growth (up to 3.0x), Dwarf shrink (down to 0.2x), and asymmetric scaling
- **Custom animations** — 17 keyframe-based emote JSON files (arm raises, channels, slams, kneels)
- **Custom particles** — 11 JSON-defined particle auras + 40+ procedural code particle effects
- **No resource pack required** — all effects visible to all online players via Aethelion's display entity system
- **Right-click to cast** — abilities are items; right-click triggers the cast sequence
- **Cooldown system** — per-ability configurable cooldowns
- **Permission gated** — OP-only `/ability give`, all players can cast

## Requirements

| Dependency | Type | Version |
|---|---|---|
| Aethelion | Plugin (required) | Latest |
| Spigot/Paper | Server | 1.21+ |
| Java | Runtime | 21+ |

## Installation

1. Download and install [Aethelion](https://github.com/minetwice) in your server's `plugins/` folder
2. Drop `DiabloSMP.jar` into `plugins/`
3. Restart your server
4. Give yourself an ability: `/ability give inferno_ascension`
5. Right-click the Nether Star item to cast!

## Commands

| Command | Permission | Description |
|---|---|---|
| `/ability give <name>` | `diablosmp.ability.give` | Give yourself an ability item |
| `/abilities` | `diablosmp.ability.list` | List all available abilities |

## The 15 Abilities

| # | Name | Element | Core Mechanic |
|---|---|---|---|
| 01 | Inferno Ascension | Fire | Titan grow + fire aura + meteor rain |
| 02 | Frostfall Dominion | Ice | AoE freeze + ice crystal cage |
| 03 | Tempest Strike | Lightning | Dash + lightning barrage |
| 04 | Titan's Vigor | Earth | Max titan 3.0x + ground slam |
| 05 | Cyclone Aura | Wind | Levitate + cyclone knockback |
| 06 | Tidal Surge | Water | Water wave + knockback |
| 07 | Divine Aegis | Holy | Dwarf + golden dome shield |
| 08 | Umbral Eclipse | Shadow | Dwarf + vanish + teleport backstab |
| 09 | Arcane Overflow | Arcane | Head swell + rune circle + burst |
| 10 | Verdant Rebirth | Nature | Healing vines + pollen |
| 11 | Blood Covenant | Blood | Asymmetric arm + lifesteal |
| 12 | Void Collapse | Void | Min dwarf + implosion + explosion |
| 13 | Stormcaller's Fury | Storm | Titan arms + 7 lightning strikes |
| 14 | Ethereal Phase | Spirit | Dwarf + intangibility + ghost trail |
| 15 | Gravity Well | Gravity | Heavy arms + orbital debris + slam |

## Building

```bash
mvn clean package
```

Output: `target/DiabloSMP-1.0.0.jar`

## License

MIT License - see [LICENSE](LICENSE) file.

## Author

**Twicefear** — [GitHub](https://github.com/minetwice)
