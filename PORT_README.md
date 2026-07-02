# Rats — Unofficial Minecraft 26.1 Port (100% AI-Generated)

> ## ⚠️ Full disclosure: every line of this port was written by AI.
> This port of the Rats mod to **Minecraft 26.1.2 / NeoForge 26.1.2.22-beta** was produced
> entirely by **Claude (Anthropic's AI assistant, model: Claude Fable 5)** working autonomously
> inside Claude Code. A human (JakeMalmrose) supervised, play-tested, and reported bugs — but
> did not write any of the code. Judge, review, and use it with that in mind.

## What this is

The beloved [Rats mod by AlexModGuy/Alexthe666](https://github.com/AlexModGuy/Rats) was last
officially released for Minecraft 1.20.1. A community fork
([eba67/Rats-Fix-Port--1.21.1](https://github.com/eba67/Rats-Fix-Port--1.21.1), building on work
by DearDanielr) carried it to 1.21.1. This branch carries it the rest of the way to **Minecraft
26.1.2** — across the most breaking stretch of API changes in modding history (the render-state /
submit rendering rewrite, data components, `ResourceLocation`→`Identifier`, registry freezing,
gamerule registries, the recipe/tag format overhauls, Java 25, and more).

It also required porting the **Citadel** library (a hard dependency) to 26.1 first — see
[Citadel-NeoForge 26.1 port](https://github.com/astryxion23/Citadel-NeoForge) lineage; the
companion AI-ported Citadel branch is published alongside this fork.

## Status

- Compiles clean; dedicated server boots to "Done" with zero log errors; client boots and plays.
- Verified in play: rats (wild/tamed/upgrades), Ratlantis dimension + portal round-trips, pirats
  and pirat boats, bosses (Rat King, Black Death), plague doctors, loot, recipes, gamerules
  (now namespaced: `/gamerule rats:do_rat_spawning`).
- ~1,400 files changed vs the 1.21.1 fork (3,905 compile errors driven to zero, then ~15 runtime
  crash/rendering bugs fixed through play-testing).

### Known cosmetic gaps
- Cheese/radius staff world overlays are stubbed (immediate-mode rendering was removed in 26.1).
- 35 formerly-emissive items (ratbow essences, vials, upgrade cores) render without fullbright.
- Day/night fog tint in Ratlantis is not reimplemented.
- Some hats may need per-hat seating tweaks on rats (pirat hat already tuned).

## Credits

- **Original mod**: [AlexModGuy (Alexthe666)](https://github.com/AlexModGuy) — all game design,
  art, models, textures, sounds, and the original codebase.
- **1.21.1 community port**: [eba67](https://github.com/eba67/Rats-Fix-Port--1.21.1) and
  [DearDanielr](https://github.com/DearDanielr).
- **26.1 port**: Claude (Anthropic) — 100% AI-generated, supervised by JakeMalmrose.

License follows the original repository's license. This is an unofficial port, published in the
spirit of keeping an abandoned mod playable; if the original author objects to its existence,
it will be taken down.
