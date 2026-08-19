# DiabloSMP

**Author:** Twicefear  
**Version:** 1.0.0  
**Platform:** Paper 1.21 - 1.21.8  

## Features

- **15 Legendary Diablo Stones** each with 2 powerful abilities
- Complex first-join animation with particles + levitation + protection
- Absorb system (Shift 3 times while holding stone)
- Dual cooldown system with ActionBar (ready for resource pack unicode bars)
- `/diablosmp start` with grace period + BossBar + WorldBorder control
- Withdraw, give, list, reload commands
- Configurable protection (drop, store, death)
- Massive particle & physics based abilities (Earthquake Relic fully implemented as example)

## Commands

| Command | Description |
|---------|-------------|
| `/diablosmp start [seconds]` | Start SMP with grace period |
| `/diablosmp stop` | Stop SMP |
| `/diablosmp changename <name>` | Change SMP name |
| `/diablosmp withdraw` | Withdraw absorbed stone |
| `/diablosmp give <player> <stone>` | Give a stone |
| `/diablosmp list` | List all stones |
| `/diablosmp reload` | Reload config |
| `/diablosmp info` | Plugin info |

## Building

```bash
./gradlew build
```

Artifact will be in `build/libs/`

## Resource Pack

Resource pack is under development (custom models for stones, cooldown bars using custom fonts, domain models).

## License

All rights reserved - Twicefear
