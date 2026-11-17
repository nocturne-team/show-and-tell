# show and tell

[modrinth](https://modrinth.com/mod/show-and-tell) | [issues](https://github.com/nocturne-team/show-and-tell/issues)

## overview

show off what you're holding, your inventory, your ender chest and more in chat! type a tag anywhere in a message and it turns into something people can hover over.

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

up to three tags per message get replaced. write `\[item]` to send a tag as plain text. tags also work in `/msg`, `/me`, `/say` and `/teammsg`.

## features

- only the server needs it! players without the mod still get the hover tooltips, since everything is vanilla chat components
- with the mod on your client, containers show as a grid of slots instead of a list
- clicking a container tag opens it in a screen where every item has its full tooltip
- typing `[` in chat suggests the tags you can use
- per-tag permissions, a cooldown, and a tag limit per message

## dependencies

- fabric api (on fabric)

## mod integrations

- curios (`[accessories]` on neoforge)
- trinkets updated (`[accessories]` on fabric)
- traveler's backpack (`[backpack]`)
- sophisticated backpacks (`[backpack]` on neoforge)
- fabric permissions api / luckperms (per-tag permissions)

## config

the server reads `config/showandtell.json`, written with defaults on first start. after editing it, run `/showandtell reload` or restart.

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

disabled tags stay as typed text and don't show up in suggestions.

## permissions

with a permissions mod such as luckperms, each tag gets its own node:

| node | default |
|---|---|
| `showandtell.tag.<tag>` (`item`, `offhand`, `inv`, `ender`, `armor`, `pos`, `accessories`, `backpack`) | everyone |
| `showandtell.bypass.cooldown` | ops |
| `showandtell.command.reload` | ops |

neoforge has a permission api built in. on fabric it needs [fabric-permissions-api](https://modrinth.com/mod/fabric-permissions-api). without one, the defaults above apply.

## versions

fabric and neoforge, minecraft 26.1 to 26.3.

## building

built with [stonecutter](https://stonecutter.kikugie.dev/). gradle needs java 25:

```
./gradlew build
```

jars end up in `versions/<minecraft>-<loader>/build/libs/`.

dev run flags: `-Psnapshots` also builds the current snapshot (fabric only), `-Pcurios` adds curios to neoforge runs, `-Ptrinkets` adds trinkets updated to fabric runs, `-Pbackpacks` adds both backpack mods, and `-Pluckperms` adds luckperms (26.3 only).

## credits

thank you to:

- Gerrit Halfmann ([pixelarticons](https://github.com/halfmage/pixelarticons), MIT) for the icon
