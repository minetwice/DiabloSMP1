import os
import zipfile
from PIL import Image, ImageDraw, ImageFilter

MAPPING = {
    "earth_smasher": "terra.png",
    "flame_lord": "inferno.png",
    "void_walker": "void.png",
    "frost_monarch": "frost.png",
    "lightning_overlord": "thunder.png",
    "shadow_reaper": "shadow.png",
    "venom_hydra": "venom.png",
    "celestial_warden": "celestial.png",
    "wind_tempest": "tempest.png",
    "blood_berserker": "blood.png",
    "gravity_master": "gravity.png",
    "time_weaver": "solar.png",
    "phantom_assassin": "arcane.png",
    "iron_titan": "tidal.png",
    "chaos_archon": "nature.png"
}

ATTACHMENTS_DIR = "/tmp/file_attachments"
BASE_DIR = "src/main/resources/resourcepack"
TEXTURES_ITEM_DIR = os.path.join(BASE_DIR, "assets/minecraft/textures/item")
TEXTURES_FONT_DIR = os.path.join(BASE_DIR, "assets/minecraft/textures/font")
MODELS_ITEM_DIR = os.path.join(BASE_DIR, "assets/minecraft/models/item")

os.makedirs(TEXTURES_ITEM_DIR, exist_ok=True)
os.makedirs(TEXTURES_FONT_DIR, exist_ok=True)
os.makedirs(MODELS_ITEM_DIR, exist_ok=True)

# Clean old font textures
for f in os.listdir(TEXTURES_FONT_DIR):
    os.remove(os.path.join(TEXTURES_FONT_DIR, f))

def process_texture(src_filename):
    src_path = os.path.join(ATTACHMENTS_DIR, src_filename)
    if not os.path.exists(src_path):
        raise FileNotFoundError(f"Missing texture attachment: {src_filename}")

    img = Image.open(src_path).convert("RGBA")
    return img

def create_hud_icon_from_img(img, size=16):
    # Resize and sharpen for clean font HUD display
    resized = img.resize((size, size), Image.Resampling.LANCZOS)
    return resized

def create_hud_status_icon(symbol_type="none", size=16):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    if symbol_type == "none":
        draw.rectangle([2, 2, 13, 13], fill=(50, 50, 50, 220), outline=(100, 100, 100, 255))
        draw.text((4, 1), "?", fill=(180, 180, 180, 255))
    elif symbol_type == "ready":
        draw.rectangle([2, 2, 13, 13], fill=(20, 120, 30, 220), outline=(50, 220, 70, 255))
        draw.polygon([(4, 8), (7, 11), (12, 4)], outline=(255, 255, 255, 255), fill=(255, 255, 255, 255))
    return img

# Process and save textures
for id_name, filename in MAPPING.items():
    stone_img = process_texture(filename)

    # Save item texture
    item_path = os.path.join(TEXTURES_ITEM_DIR, f"diablo_stone_{id_name}.png")
    stone_img.save(item_path)

    # Save model JSON
    model_path = os.path.join(MODELS_ITEM_DIR, f"diablo_stone_{id_name}.json")
    model_json = f'''{{
  "parent": "item/generated",
  "textures": {{
    "layer0": "minecraft:item/diablo_stone_{id_name}"
  }}
}}'''
    with open(model_path, "w") as f:
        f.write(model_json)

    # Save font HUD icon
    font_path = os.path.join(TEXTURES_FONT_DIR, f"stone_{id_name}.png")
    hud_icon = create_hud_icon_from_img(stone_img, size=16)
    hud_icon.save(font_path)

# Save static status icons
create_hud_status_icon("none").save(os.path.join(TEXTURES_FONT_DIR, "question_mark.png"))
create_hud_status_icon("ready").save(os.path.join(TEXTURES_FONT_DIR, "ability_ready.png"))

print("All 15 stone textures and HUD icons successfully generated from attachments!")

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

print("Resource packs zipped successfully.")
