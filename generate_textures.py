import os
import zipfile
from PIL import Image, ImageDraw, ImageFont, ImageFilter

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
    resized = img.resize((size, size), Image.Resampling.LANCZOS)
    return resized

def create_hud_status_icon(symbol_type="none", size=16):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    if symbol_type == "none":
        # Empty stone slot with dark metal frame & centered glowing question mark
        # Frame
        draw.rectangle([0, 0, 15, 15], fill=(30, 30, 35, 230), outline=(90, 90, 100, 255))
        draw.rectangle([1, 1, 14, 14], outline=(50, 50, 60, 255))
        # Inner dark slot
        draw.rectangle([3, 3, 12, 12], fill=(15, 15, 20, 240))
        # Centered bold question mark pixels
        qm_pixels = [
            (6, 4), (7, 4), (8, 4), (9, 4),
            (5, 5), (10, 5),
            (9, 6), (10, 6),
            (8, 7), (9, 7),
            (7, 8), (8, 8),
            (7, 10), (8, 10)
        ]
        for px, py in qm_pixels:
            draw.point((px, py), fill=(180, 180, 200, 255))

    elif symbol_type == "ready":
        # Custom ready badge
        draw.rectangle([0, 0, 15, 15], fill=(20, 100, 30, 230), outline=(50, 220, 80, 255))
        draw.rectangle([1, 1, 14, 14], outline=(30, 150, 50, 255))
        # Checkmark
        draw.polygon([(3, 8), (6, 11), (12, 4), (11, 3), (6, 9), (4, 7)], fill=(255, 255, 255, 255))

    return img

def create_cooldown_bar_segment(progress_percent, size_w=32, size_h=10):
    img = Image.new("RGBA", (size_w, size_h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Outer bar frame
    draw.rectangle([0, 1, size_w - 1, size_h - 2], fill=(20, 20, 25, 220), outline=(80, 80, 90, 255))
    draw.rectangle([1, 2, size_w - 2, size_h - 3], outline=(40, 40, 50, 255))

    # Inner fill
    max_fill_w = size_w - 4
    fill_w = int(max_fill_w * max(0.0, min(1.0, progress_percent)))

    if fill_w > 0:
        # Color gradient: Red (0%) -> Yellow (50%) -> Green (100%)
        if progress_percent < 0.5:
            r, g, b = 230, int(230 * (progress_percent * 2)), 30
        else:
            r, g, b = int(230 * (2.0 - progress_percent * 2)), 230, 30

        draw.rectangle([2, 3, 2 + fill_w, size_h - 4], fill=(r, g, b, 255))
        # Highlight line on top of bar fill
        draw.line([(2, 3), (2 + fill_w, 3)], fill=(min(255, r + 40), min(255, g + 40), min(255, b + 40), 255))

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

# Save status icons
create_hud_status_icon("none").save(os.path.join(TEXTURES_FONT_DIR, "question_mark.png"))
create_hud_status_icon("ready").save(os.path.join(TEXTURES_FONT_DIR, "ability_ready.png"))

# Create 10 smooth cooldown bar textures (0% to 100%)
for i in range(11):
    pct = i / 10.0
    bar_img = create_cooldown_bar_segment(pct)
    bar_img.save(os.path.join(TEXTURES_FONT_DIR, f"bar_{i * 10}.png"))

print("All stone textures, centered question mark icon, and custom cooldown bar textures generated!")

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
