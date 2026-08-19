# DiabloSMP Resource Pack

## Structure

```
resourcepack/
├── pack.mcmeta
├── assets/
│   └── diablosmp/
│       ├── font/
│       │   ├── cooldown.json          # Custom unicode for bars & icons
│       │   ├── cooldown_bar.png       # (you need to create)
│       │   ├── cooldown_bar_full.png
│       │   ├── stone_icon.png
│       │   └── question_mark.png
│       ├── models/
│       │   └── item/
│       │       ├── infernal_core.json
│       │       ├── earthquake_relic.json
│       │       └── ... (one per stone)
│       └── textures/
│           ├── item/
│           │   └── stones/
│           └── font/
```

## How cooldown bars work

In plugin we will send ActionBar with unicode:
- `\uE001` = empty/partial bar
- `\uE002` = full ready bar
- `\uE010` = current stone icon (middle)
- `\uE011` = question mark when no stone absorbed

Example ActionBar:
`\uE001 \uE001 \uE001 \uE010 \uE002 \uE002 \uE002`

Left side = Primary cooldown
Middle = Stone icon
Right side = Secondary cooldown

## Models

Each stone uses CustomModelData starting from 1000.
Create .json models + textures for beautiful 3D looking stones.

## Domain models (Earthquake secondary)

You can add custom block models for the 4 pillars using resource pack + ItemsAdder / Oraxen / or pure custom model data on armor stands.
