"""Draws the mod icon: the juice box in front of a big explosion. Run from the repo root:
python3 tools/icon.py [small-preview.png]

A 32x32 pixel-art grid, scaled up 8x with no smoothing. The juice box is the front texture from
textures.py at double size, so the icon looks like the block in game.
"""
import math
import os
import sys

sys.path.insert(0, os.path.dirname(__file__))
from textures import write_png, PAL, FRONT

BG = (44, 40, 72, 255)
BG_EDGE = (28, 24, 48, 255)
OUTLINE = (60, 34, 20, 255)
BLAST = [(255, 244, 150, 255), (255, 196, 60, 255), (240, 110, 30, 255), (200, 50, 30, 255)]


def icon():
    n = 32
    px = [(0, 0, 0, 0)] * (n * n)

    def put(x, y, c):
        if 0 <= x < n and 0 <= y < n:
            px[y * n + x] = c

    # rounded dark background
    for y in range(n):
        for x in range(n):
            corner = min(x, n - 1 - x) + min(y, n - 1 - y)
            if corner < 2:
                continue
            edge = x in (0, n - 1) or y in (0, n - 1) or corner == 2
            put(x, y, BG_EDGE if edge else BG)

    # a spiky explosion behind the box
    cx, cy = 15.5, 16.5
    for y in range(1, n - 1):
        for x in range(1, n - 1):
            dx, dy = x + 0.5 - cx, y + 0.5 - cy
            r = math.hypot(dx, dy)
            a = math.atan2(dy, dx)
            reach = 14.5 + 3.0 * math.cos(9 * a) + 1.0 * math.cos(4 * a + 1.0)
            if r <= reach:
                t = r / reach
                put(x, y, BLAST[0] if t < 0.35 else BLAST[1] if t < 0.6 else BLAST[2] if t < 0.85 else BLAST[3])

    # the juice box, front texture at 2x, with an outline
    bx, by = 8, 6
    for r, row in enumerate(FRONT):
        for c, ch in enumerate(row):
            for sy in range(2):
                for sx in range(2):
                    put(bx + c * 2 + sx, by + r * 2 + sy, PAL[ch])
    for y in range(by - 1, by + 25):
        put(bx - 1, y, OUTLINE)
        put(bx + 16, y, OUTLINE)
    for x in range(bx - 1, bx + 17):
        put(x, by - 1, OUTLINE)
        put(x, by + 24, OUTLINE)

    # the straw
    for i, y in enumerate(range(1, by - 1)):
        col = PAL["s"] if i % 2 == 0 else PAL["w"]
        put(18, y, col)
        put(19, y, col)
    return px, n


def scaled(px, n, s):
    return [px[(y // s) * n + x // s] for y in range(n * s) for x in range(n * s)], n * s


if __name__ == "__main__":
    here = os.path.dirname(__file__)
    px, n = icon()
    big, size = scaled(px, n, 8)
    write_png(os.path.join(here, "..", "src", "main", "resources", "assets", "icon.png"), big, size, size)
    if len(sys.argv) > 1:
        small, ssize = scaled(px, n, 2)
        write_png(sys.argv[1], small, ssize, ssize)
    print("done")
