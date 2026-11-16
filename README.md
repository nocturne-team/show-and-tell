# Show and Tell

Show off what you're holding, your inventory, your ender chest or your armor in chat.

Type a tag anywhere in a message and it turns into something people can hover:

| Tag | Aliases | Shows |
|---|---|---|
| `[item]` | `[i]` | The item in your main hand, with its full tooltip |
| `[inv]` | `[inventory]` | Your inventory, hotbar at the bottom |
| `[ender]` | `[ec]`, `[enderchest]` | Your ender chest |
| `[armor]` | `[armour]` | Your armor, plus your offhand if it holds something |

Up to three tags per message are replaced. Write `\[item]` to send the tag as plain text.

Tags also work in `/msg`, `/me`, `/say` and `/teammsg`.

## Server and client

The server does the work, so it's the only side that needs the mod. Players without it still get the hover tooltips, since everything uses vanilla chat components. Inventories show up for them as a short list, the same way a shulker box tooltip does.

Players with the mod installed get a bit more:

- inventories, ender chests and armor show as a grid of inventory slots instead of a list
- typing `[` in chat pops up the available tags

## Versions

Fabric and NeoForge, Minecraft 26.1 to 26.3. Fabric needs Fabric API.

## Building

Built with [Stonecutter](https://stonecutter.kikugie.dev/). Gradle needs Java 25:

```
./gradlew build
```

Jars end up in `versions/<minecraft>-<loader>/build/libs/`.

## Credits

Icon from [pixelarticons](https://github.com/halfmage/pixelarticons) by Gerrit Halfmann (MIT).
