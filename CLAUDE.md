# Apple Juice Boom

A juice box block. Shift-click it and it fizzes, the whole sky explodes, and your game crashes for real.

This file is read automatically whenever Claude Code is opened in this folder.
Everything below is specific to this one mod. The general rules about how to
work with Elduin live in `~/.claude/CLAUDE.md`.

## Facts about this mod

    mod id            apple_juice_boom      (underscores — never change this)
    slug              apple-juice-boom      (repo name and Modrinth slug)
    package           com.elduin.apple_juice_boom
    loader            fabric                (only fabric — see below)
    minecraft         1.21.11, 26.2
    primary version   1.21.11               (the one he plays)
    java              21 for 1.21.x, 25 for 26.x — Gradle picks this per version

## How it works

- `AppleJuiceBlock` is a small juice-box block with a `shaken` state. Shift +
  right-click with an empty hand sets `shaken`, plays the TNT fuse sound and
  schedules a tick 50 ticks later (`animateTick` sprays juice meanwhile).
  A plain right-click just shows a hint above the hotbar.
- When it goes off (`boom`): the block is removed, the server saves everything
  and waits for the disk (`saveEverything(true, true, true)`), a power-12 TNT
  explosion goes off, and a `BoomPayload` is sent to the player who shook it
  (or the nearest player within 32 blocks).
- **The crash is on purpose and real.** `client/Apocalypse` gets the payload,
  spends 60 ticks spawning explosion emitters all around the player and
  shaking the camera, then calls `Minecraft.delayCrash` with a
  `TooMuchAppleJuiceException`. `delayCrash` exits without an emergency save,
  so in single player the explosion itself usually isn't kept — the save just
  before it is. On a dedicated server only the shaker's game crashes; the
  server keeps running and keeps the crater.
- Crafting: apple + sugar + glass bottle (shapeless). It has its own creative
  tab, "Apple Juice Bomb", and is also in Food & Drinks.
- Textures and the icon are drawn by `tools/textures.py` and `tools/icon.py`
  (no Mojang art). Edit those and re-run; don't hand-edit the PNGs. The block
  model only uses part of each 16x16 texture — the uv boxes are listed at the
  top of `textures.py`.
- Version differences live in `Compat.java` (payload registry rename in 26.2,
  creative tab builder and action-bar message in 26) and
  `FabricEventSubscriber` (`ItemGroupEvents` → `CreativeModeTabEvents`).

The mod id is baked into save files. Once a world has been played with this mod,
**changing the mod id breaks that world.** Rename the display name freely;
never rename the mod id.

## Layout

Multi-version is handled by [Stonecutter](https://plugins.gradle.org/plugin/dev.kikugie.stonecutter):
one source tree, version-conditional comments, many outputs.

    src/main/java/<package>/                the mod
    src/main/resources/                     assets, textures, mixins, lang
    versions/<mcversion>-fabric/build/libs/ built jars land here
    stonecutter.properties.toml             mod id, name, version, dependencies
    settings.gradle.kts                     the Minecraft version list
    .github/workflows/release.yml           builds and publishes on a version tag

There is **no `fabric.mod.json` file** — it is generated at build time from
`stonecutter.properties.toml` by the code in `build-logic/`. Editing mod
metadata means editing the `.toml`, not a json file. Same for `mod.version`:
there is no `mod_version` in `gradle.properties`.

Stonecutter subprojects are named `<mcversion>-fabric`, so the 1.21.11 jar is in
`versions/1.21.11-fabric/build/libs/`. That `-fabric` suffix is easy to forget.

Do **not** add a branch or a repo for a new Minecraft version. Add it to the
list in `settings.gradle.kts`, add a matching `[fabric."<version>"]` block in
`stonecutter.properties.toml`, add it to the matrix in
`.github/workflows/release.yml`, and fix whatever stops compiling.

## Fabric only

This template builds Fabric and nothing else. NeoForge and Forge were removed on
purpose: each extra loader is another full copy of Minecraft to decompile, and
this is an 8 GB machine. Do not add them back.

For the same reason `gradle.properties` sets `org.gradle.parallel=false`.
Leave it off. Turning it on with more than one Minecraft version in the list
will exhaust memory and take the whole machine down.

## Commands

    ./gradlew "Set active project to 1.21.11-fabric"   switch versions first
    ./gradlew "1.21.11-fabric:build"                   build just that version
    ./gradlew build                                    build every version
    ./gradlew runActiveClient                          launch a dev client

Switching rewrites the shared source tree into that version's form. It is **not**
required before building — each version subproject regenerates its own sources,
so the jars are correct either way. Switch to keep the working tree in the
version you're reading, not because the build needs it.

Never hand-edit `.sc_active_version`. Stonecutter records what form the shared
sources are currently in, and editing that file behind its back desyncs the
bookkeeping — you get `cannot find symbol` errors on classes that plainly exist.
Use the task above and nothing else.

## Access wideners

Optional and absent by default. If you need one, create
`src/main/resources/aw/<mcversion>.accesswidener` and Loom picks it up
automatically; without the file the step is skipped entirely. An *empty*
placeholder file does not work — it fails the build on 1.21.11+.

## Conventions for this repo

- Textures are 16x16 unless there's a reason. Keep the pixel-art style consistent
  with the rest of the mod.
- Every new block, item and mob needs an entry in the language file
  (`assets/<mod_id>/lang/en_us.json`) or it shows up in-game as a raw id, which
  reads to him as "broken".
- Anything a player can tune goes in the config, not hardcoded.
- Keep it dependency-free where possible. If a library is genuinely needed, it
  has to be one that's available for every Minecraft version in the list above.

## Releasing

Handled by the **share-it** skill. Short version: bump `mod.version` in
`stonecutter.properties.toml`, update `CHANGELOG.md` in plain words, push a
`v<version>` tag, and the workflow publishes to Modrinth using the org's
`MODRINTH_TOKEN`.
