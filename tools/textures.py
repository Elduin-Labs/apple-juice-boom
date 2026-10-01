"""Draws the juice box textures. Run from the repo root: python3 tools/textures.py

Everything is drawn from scratch here (no Mojang art). Each face of the juice box only uses part
of its 16x16 texture, matching the "uv" boxes in models/block/apple_juice.json:
  front/back  x 4..12, y 4..16   (8 wide, 12 tall)
  sides       x 5..11, y 4..16   (6 wide, 12 tall)
  top/bottom  x 4..12, y 5..11
  straw       x 0..1,  y 0..4
"""
import os
import struct
import zlib

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "assets", "apple_juice_boom", "textures", "block")


def write_png(path, pixels, w, h):
    raw = b"".join(b"\x00" + bytes(c for px in pixels[y * w:(y + 1) * w] for c in px) for y in range(h))

    def chunk(kind, data):
        return struct.pack(">I", len(data)) + kind + data + struct.pack(">I", zlib.crc32(kind + data) & 0xFFFFFFFF)

    png = b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(raw, 9)) + chunk(b"IEND", b"")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "wb") as f:
        f.write(png)


PAL = {
    "y": (244, 201, 76, 255),    # carton yellow
    "Y": (226, 176, 52, 255),    # carton shadow
    "c": (250, 240, 214, 255),   # cream band
    "r": (214, 40, 40, 255),     # apple red
    "R": (160, 24, 30, 255),     # apple shadow
    "h": (255, 140, 130, 255),   # apple shine
    "g": (88, 168, 58, 255),     # leaf
    "b": (110, 70, 40, 255),     # stem
    "j": (232, 150, 40, 255),    # juice amber
    "J": (196, 112, 24, 255),    # juice dark
    "k": (60, 36, 20, 255),      # straw hole
    "w": (250, 250, 250, 255),   # straw white
    "s": (220, 40, 60, 255),     # straw stripe
}

# 8 wide, 12 tall
FRONT = [
    "cccccccc",
    "YccccccY",
    "yyyybyyy",
    "yyygbyyy",
    "yyrrhryy",
    "yrrrrhry",
    "yrrrrrry",
    "yrrrrrRy",
    "yyrRRRyy",
    "jjjjjjjj",
    "jJjjJjjJ",
    "JJJJJJJJ",
]
# 6 wide, 12 tall
SIDE = [
    "cccccc",
    "cccccc",
    "yyyyyY",
    "yyyyyY",
    "yyyyyY",
    "yyyyyY",
    "yyyyyY",
    "yyyyyY",
    "yyyyyY",
    "jjjjjj",
    "jJjjJj",
    "JJJJJJ",
]
# 8 wide, 6 tall; the straw goes in at block pixel (9, 7)
TOP = [
    "cccccccc",
    "cccccccc",
    "ccccckcc",
    "cccccccc",
    "cccccccc",
    "cccccccc",
]


def blank():
    return [(0, 0, 0, 0)] * 256


def paint(px, art, u, v):
    for r, row in enumerate(art):
        for c, ch in enumerate(row):
            if ch != ".":
                px[(v + r) * 16 + u + c] = PAL[ch]


if __name__ == "__main__":
    front = blank()
    paint(front, FRONT, 4, 4)
    side = blank()
    paint(side, SIDE, 5, 4)
    top = blank()
    paint(top, TOP, 4, 5)
    straw = blank()
    paint(straw, ["s", "w", "s", "w"], 0, 0)
    write_png(os.path.join(ROOT, "apple_juice_front.png"), front, 16, 16)
    write_png(os.path.join(ROOT, "apple_juice_side.png"), side, 16, 16)
    write_png(os.path.join(ROOT, "apple_juice_top.png"), top, 16, 16)
    write_png(os.path.join(ROOT, "apple_juice_straw.png"), straw, 16, 16)
    print("done")
