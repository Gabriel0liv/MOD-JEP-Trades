# Just Enough Trades

Forge-only JEI addon for Minecraft 1.20.1.

## Requirements

- Minecraft 1.20.1
- Forge 47.1.3
- JEI 15.20.0.133 or compatible 1.20.1 release

## What it does

Just Enough Trades adds JEI categories that show villager trades by profession level:

- Villager Trades: Novice
- Villager Trades: Apprentice
- Villager Trades: Journeyman
- Villager Trades: Expert
- Villager Trades: Master

Each entry shows the villager profession plus input A, optional input B, and output.

## Limitations

- Dynamic trades are shown as a generated sample.
- Some modded trades may be skipped if they require a real entity context or fail when sampled safely.

## Client-side behavior

This mod is client-side. It shows villager trades known to the local client, including trades from Forge mods installed on the client.

It cannot detect trades that are added only on a remote server by server-side mods, plugins, datapacks, or scripts not present on the client.

## Testing in dev

Run:

```bat
.\gradlew.bat runClient
```

JEI is included as a runtime dependency.

To test with extra Forge mods, put their jars in:

```text
run/mods/
```
