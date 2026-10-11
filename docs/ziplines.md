# Ziplines

## Using a zipline

The anchor keeps its original narrow post and two bands, with stripped bamboo on the post and green bamboo on the bands. Its shape, hitbox and ride behaviour are unchanged; existing anchors update their appearance automatically.

1. Place two **Zipline Anchor** blocks (`ltextras:zipline_anchor`). They are in the LTExtras creative tab. Two iron ingots with a stick between them in a vertical crafting column produce two anchors.
2. Right-click the first anchor with string, then the second anchor with the **same string stack**. This consumes one string in survival and connects the anchors with Minecraft's vanilla leash graphics. Clicking the first anchor again cancels the selection.
3. Right-click either anchor or any reachable part of the cable to ride. Use an empty hand or another item when clicking an anchor: string is reserved for linking.
4. An invisible carriage first tries the point on the cable you actually clicked, not the nearest point to your feet. Clicking a pole starts beside that pole. Boarding is at least half a block from the pole centre, increased just enough to clear the pole and the rider's width (about 0.55 blocks on a straight, level cable for a normal player). Clear clicks farther along the cable keep that starting point.
5. On cables **longer than five blocks**, clicking **less than three blocks from an endpoint** always travels away from that endpoint, regardless of your view. Otherwise, cable clicks follow your camera's look direction if it is within 45 degrees of either direction along the cable (including its slope); sideways clicks travel toward the farther anchor. Clicking a pole always travels toward the opposite pole. If endpoint zones overlap on a 5-6 block cable, the nearer pole wins. Midpoint ties travel toward the second anchor.
6. At the destination, you stay hanging at the end of the line for one second (20 ticks), then dismount and drop straight down. Press crouch at any time, including during that pause, to drop immediately. Dismounting never snaps you onto an anchor or searches for nearby ground.

If the clicked boarding position is blocked, the mod checks **0.5, 1, 1.5 and 2 blocks in the chosen travel direction**, then checks those same distances in the opposite direction. All forward options beat all backward options: a clear spot 1.5 blocks ahead wins over one 0.5 blocks behind. Distances are measured along the cable from the original click (or pole), not from an adjusted boarding position. Candidates must stay on the cable, retain pole clearance, fit the player's body, and remain within two blocks of the click. If none fit, boarding is refused. Searching never changes the direction selected at the click, even when it crosses the cable's midpoint or an endpoint-zone boundary.

Place anchors overhead: the rider hangs approximately 1.8 blocks below the cable. Only the selected boarding position must have room for the rider; an obstruction farther along the route does not prevent boarding. The carriage stops at solid blocks using the player's full standing collision box, without sliding off the cable or dismounting. It remembers the chosen direction and resumes automatically when the block is removed, even if you look the other way while waiting. Crouch still releases you immediately. Safe landing space is not required to ride; arrange the ground beneath your exits yourself. Normal gravity and fall damage apply after release.

Anchors need at least three blocks of horizontal separation and can be at most 64 blocks apart in total. Each anchor has one connection. Level and uphill rides travel at seven blocks per second at 20 TPS. Downhill rides gain an additive 3% per block of height difference between the two anchors, capped at twice the base speed (14 blocks per second). A ten-block drop gives a 30% boost; drops of 34 blocks or more reach the cap. This is a steady speed for that direction, including when boarding mid-cable, not a bonus compounded over time. Riding stops 1.25 blocks before the anchor to avoid colliding with it, then pauses in that exact hanging position before releasing you.

Break either anchor to remove its cable; a pickaxe recovers the anchor. The connection persists with the world. Linking requires the route to be loaded. Boarding loads both endpoints and starts temporary chunk tickets at the endpoints and cable midpoint. Like ender pearls, these tickets use radius two and a 40-tick timeout, loading and ticking nearby chunks. Occupied carriages refresh them, including while blocked or waiting at the destination. After the last rider leaves, tickets expire naturally instead of keeping unused cables loaded permanently. Separate zipline tickets do not interfere with actual ender pearls, and one rider dismounting cannot remove another rider's ticket.

Obstructions pause the carriage indefinitely; only reaching the actual destination starts the one-second release timer. If an anchor is removed during a ride, the carriage immediately releases you at your current position. Logging out also releases the carriage without relocating you. Saved carriages preserve their travel progress, destination (including while blocked), and any elapsed endpoint pause.

Travel checks actual block collision shapes, including thin and partial blocks. Mobs, players and other entities do not obstruct boarding or movement. Riders are unpushable, like entities on ladders or vines, and do not shove other mobs or players; normal entity pushing returns immediately on dismount. This does not disable solid-block collisions. Liquids and dangerous surfaces are not treated as solid walls unless their block collision shape actually intersects the rider. Travel pauses if chunks or cable entity data are still loading, rather than treating unloaded endpoints as broken; the chosen direction is retained. It also pauses before leaving world bounds. Normal player reach and block occlusion apply to cable clicks, and the server independently validates them and selects the direction using your look angle.

## Development and checks

From the LTExtras repository in PowerShell:

```powershell
.\gradlew.bat assemble
.\gradlew.bat runClient
```

In a creative test world:

```text
/give @s ltextras:zipline_anchor 2
/give @s minecraft:string 16
```

No local zipline test suite is included. Before deploying, play-test chunk loading at low view/simulation distances, downhill speed, entity push immunity, block obstructions, dismounting, leash appearance, crosshair targeting and ride smoothness with two clients. Compilation alone does not verify these in-game behaviours.
