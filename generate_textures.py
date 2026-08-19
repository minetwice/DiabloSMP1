import os
import zipfile
from PIL import Image, ImageDraw, ImageFont

STONES = [
    ("earth_smasher", (180, 100, 30)),
    ("flame_lord", (230, 40, 20)),
    ("void_walker", (120, 20, 200)),
    ("frost_monarch", (60, 200, 245)),
    ("lightning_overlord", (250, 230, 40)),
    ("shadow_reaper", (60, 60, 70)),
    ("venom_hydra", (30, 180, 50)),
    ("celestial_warden", (255, 215, 80)),
    ("wind_tempest", (220, 240, 255)),
    ("blood_berserker", (170, 0, 30)),
    ("gravity_master", (190, 80, 220)),
    ("time_weaver", (40, 110, 230)),
    ("phantom_assassin", (100, 110, 120)),
    ("iron_titan", (0, 160, 180)),
    ("chaos_archon", (240, 50, 180)),
]

BASE_DIR = "src/main/resources/resourcepack"
TEXTURES_ITEM_DIR = os.path.join(BASE_DIR, "assets/minecraft/textures/item")
TEXTURES_FONT_DIR = os.path.join(BASE_DIR, "assets/minecraft/textures/font")
MODELS_ITEM_DIR = os.path.join(BASE_DIR, "assets/minecraft/models/item")

os.makedirs(TEXTURES_ITEM_DIR, exist_ok=True)
os.makedirs(TEXTURES_FONT_DIR, exist_ok=True)
os.makedirs(MODELS_ITEM_DIR, exist_ok=True)

def create_gem_texture(color_rgb, size=16):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    r, g, b = color_rgb

    outline_color = (max(0, r - 80), max(0, g - 80), max(0, b - 80), 255)
    main_color = (r, g, b, 255)
    highlight = (min(255, r + 90), min(255, g + 90), min(255, b + 90), 255)
    shadow = (max(0, r - 50), max(0, g - 50), max(0, b - 50), 255)

    draw.polygon([(8, 1), (14, 7), (8, 14), (1, 7)], fill=outline_color)
    draw.polygon([(8, 2), (13, 7), (8, 13), (2, 7)], fill=main_color)
    draw.polygon([(8, 2), (13, 7), (8, 8)], fill=highlight)
    draw.polygon([(2, 7), (8, 8), (8, 13)], fill=shadow)
    draw.point((8, 3), fill=(255, 255, 255, 255))
    return img

def create_hud_icon(color_rgb, symbol_type="stone", size=16):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    if symbol_type == "none":
        draw.rectangle([2, 2, 13, 13], fill=(50, 50, 50, 220), outline=(100, 100, 100, 255))
        draw.text((4, 1), "?", fill=(180, 180, 180, 255))
    elif symbol_type == "ready":
        draw.rectangle([2, 2, 13, 13], fill=(20, 120, 30, 220), outline=(50, 220, 70, 255))
        draw.polygon([(4, 8), (7, 11), (12, 4)], outline=(255, 255, 255, 255), fill=(255, 255, 255, 255))
    else:
        r, g, b = color_rgb
        draw.rectangle([2, 2, 13, 13], fill=(r, g, b, 230), outline=(255, 255, 255, 255))
        draw.point((4, 4), fill=(255, 255, 255, 255))
        draw.point((5, 4), fill=(255, 255, 255, 255))
        draw.point((4, 5), fill=(255, 255, 255, 255))

    return img

# Clean old font textures
for f in os.listdir(TEXTURES_FONT_DIR):
    os.remove(os.path.join(TEXTURES_FONT_DIR, f))

# Generate textures
for id_name, rgb in STONES:
    # Item texture
    gem = create_gem_texture(rgb)
    gem.save(os.path.join(TEXTURES_ITEM_DIR, f"diablo_stone_{id_name}.png"))

    # Model JSON
    model_path = os.path.join(MODELS_ITEM_DIR, f"diablo_stone_{id_name}.json")
    model_json = f'''{{
  "parent": "item/generated",
  "textures": {{
    "layer0": "minecraft:item/diablo_stone_{id_name}"
  }}
}}'''
    with open(model_path, "w") as f:
        f.write(model_json)

    # Font texture (Must match assets/minecraft/font/default.json)
    hud = create_hud_icon(rgb, "stone")
    hud.save(os.path.join(TEXTURES_FONT_DIR, f"stone_{id_name}.png"))

# Generate static HUD icons matching default.json
create_hud_icon((0,0,0), "none").save(os.path.join(TEXTURES_FONT_DIR, "question_mark.png"))
create_hud_icon((0,0,0), "ready").save(os.path.join(TEXTURES_FONT_DIR, "ability_ready.png"))

print("Textures & Models successfully generated.")

# Zip resource pack
def zip_dir(path, zip_handle):
    for root, dirs, files in os.walk(path):
        for file in files:
            full_path = os.path.join(root, file)
            rel_path = os.path.relpath(full_path, path)
            zip_handle.write(full_path, rel_path)

zip_path = "src/main/resources/DiabloSMP-ResourcePack.zip"
with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as zip_f:
    zip_dir(BASE_DIR, zip_f)

with zipfile.ZipFile("DiabloSMP-ResourcePack.zip", 'w', zipfile.ZIP_DEFLATED) as zip_f:
    zip_dir(BASE_DIR, zip_f)

print("Resource pack zipped successfully.")
