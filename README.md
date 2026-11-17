# Show and Tell

Show off what you're holding, your inventory, your ender chest or your armor in chat.

Type a tag anywhere in a message and it turns into something people can hover:

| Tag | Aliases | Shows |
|---|---|---|
| `[item]` | `[i]` | The item in your main hand, with its full tooltip |
| `[offhand]` | `[oh]` | The item in your offhand |
| `[inv]` | `[inventory]` | Your inventory, hotbar at the bottom |
| `[ender]` | `[ec]`, `[enderchest]` | Your ender chest |
| `[armor]` | `[armour]` | Your armor, plus your offhand if it holds something |
| `[pos]` | `[loc]`, `[coords]` | Your coordinates and dimension; clicking it fills in a teleport command |
| `[backpack]` | `[bp]` | The backpack in your hand, or the one you're wearing ([Traveler's Backpack](https://modrinth.com/mod/travelersbackpack), or [Sophisticated Backpacks](https://modrinth.com/mod/sophisticated-backpacks) on NeoForge) |
| `[curios]` | `[curio]` | Your equipped [Curios](https://modrinth.com/mod/curios) (NeoForge, only when Curios is installed) |

Up to three tags per message are replaced. Write `\[item]` to send the tag as plain text.

Tags also work in `/msg`, `/me`, `/say` and `/teammsg`.

## Server and client

The server does the work, so it's the only side that needs the mod. Players without it still get the hover tooltips, since everything uses vanilla chat components. Inventories show up for them as a short list, the same way a shulker box tooltip does.

Players with the mod installed get a bit more:

- clicking a container tag opens it in a screen where every item shows its full tooltip
- inventories, ender chests and armor show as a grid of inventory slots instead of a list
- typing `[` in chat pops up the available tags

## Config

The server reads `config/showandtell.json`, written with defaults on first start. After editing it, run `/showandtell reload` or restart.

```jsonc
{
	// Tags players can't use, e.g. ["inv", "ender"]
	"disabled_tags": [],
	// Tags per message; extra ones stay as plain text
	"max_tags_per_message": 3,
	// Seconds a player waits between messages with tags, 0 = off
	"cooldown_seconds": 0
}
```

Disabled tags stay as typed text and don't show up in tag suggestions.

## Permissions

With a permissions mod such as LuckPerms, each tag has its own node:

| Node | Default |
|---|---|
| `showandtell.tag.<tag>` (`item`, `offhand`, `inv`, `ender`, `armor`, `pos`, `curios`, `backpack`) | everyone |
| `showandtell.bypass.cooldown` | ops |
| `showandtell.command.reload` | ops |

NeoForge has the permission API built in. On Fabric it needs [fabric-permissions-api](https://modrinth.com/mod/fabric-permissions-api). Without a permissions mod, the defaults above apply.

## Versions

Fabric and NeoForge, Minecraft 26.1 to 26.3. Fabric needs Fabric API.

## Building

Built with [Stonecutter](https://stonecutter.kikugie.dev/). Gradle needs Java 25:

```
./gradlew build
```

Jars end up in `versions/<minecraft>-<loader>/build/libs/`.

Add `-Psnapshots` to also build the current Minecraft snapshot (Fabric only), `-Pcurios` to put Curios in NeoForge dev runs, `-Pbackpacks` for both backpack mods, and `-Pluckperms` for LuckPerms (26.3 only).

## Credits

Icon from [pixelarticons](https://github.com/halfmage/pixelarticons) by Gerrit Halfmann (MIT).
