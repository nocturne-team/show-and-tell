[github](https://github.com/nocturne-team/show-and-tell) | [issues](https://github.com/nocturne-team/show-and-tell/issues)

## overview

show and tell lets you show off what you're holding, your inventory, your ender chest and more in chat! type a tag anywhere in a message and it turns into something people can hover over.

## tags

| tag | aliases | shows |
|---|---|---|
| `[item]` | `[i]` | the item in your main hand, with its full tooltip |
| `[offhand]` | `[oh]` | the item in your offhand |
| `[inv]` | `[inventory]` | your inventory, hotbar at the bottom |
| `[ender]` | `[ec]`, `[enderchest]` | your ender chest |
| `[armor]` | `[armour]` | your armor, plus your offhand if it holds something |
| `[pos]` | `[loc]`, `[coords]` | your coordinates and dimension; click it to fill in a teleport command |
| `[backpack]` | `[bp]` | the backpack in your hand, or the one you're wearing |
| `[accessories]` | `[acc]`, `[curios]`, `[trinkets]` | your equipped accessories |

## features

- only the server needs it! players without the mod still get the hover tooltips, since everything is vanilla chat components
- with the mod on your client, containers show as a grid of slots instead of a list
- clicking a container tag opens it in a screen where every item has its full tooltip
- typing `[` in chat suggests the tags you can use
- per-tag permissions, a cooldown, and a tag limit per message, all set in `config/showandtell.json`

## dependencies

- [fabric api](https://modrinth.com/mod/fabric-api) (on fabric)

## mod integrations

- [curios](https://modrinth.com/mod/curios) (`[accessories]` on neoforge)
- [trinkets updated](https://modrinth.com/mod/trinkets-updated) (`[accessories]` on fabric)
- [traveler's backpack](https://modrinth.com/mod/travelersbackpack) (`[backpack]`)
- [sophisticated backpacks](https://modrinth.com/mod/sophisticated-backpacks) (`[backpack]` on neoforge)
- [fabric permissions api](https://modrinth.com/mod/fabric-permissions-api) / [luckperms](https://modrinth.com/plugin/luckperms) (per-tag permissions, `showandtell.tag.<tag>`)

## credits

thank you to:

- Gerrit Halfmann ([pixelarticons](https://github.com/halfmage/pixelarticons)) for the icon
