[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

# InvSync

This is a fabric mod for 1.16.x that Allows you to sync player inventories, health, food level, experience and score across multiple servers! This is accomplished using an sqlite database (that is automatically generated and set up for you). Most mods should have no problem running along side, and modded items will also be sync'd.

## Requirements

- This mod requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) 
- You can get the mod on [Modrith](https://modrinth.com/mod/invsync) or on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/invsync-fabric)

## Getting Started

Setup is very simple. Drop the mod into your mods folder on all the servers you want to sync. Starting a server will generate a config with the following contents:

- SQLite Settings:
  - Database Name: The name of the sqlite database you want generated or your existing database.
  - Database Table Name: The name of the table that will store player data in your database.
  - Database Directory: The directory where you want the database to be created or where your existing one already lives.
  - Enable WAL Mode: This will increase read/write speed to the database and help with task concurrency, however will cause problems when using a proxy such as velocity or bungee cord. [Learn more about WAL.](https://sqlite.org/wal.html)

- Sync Settings: 
  - Sync Inv: Sync player inventory between servers. 
  - Sync Armour: Sync player armour between servers.
  - Sync eChest: Sync player ender chest between servers.
  - Sync Xp: Sync player experience between servers.
  - Sync Score: Sync player score between servers.
  - Sync Health: Sync player health between servers.
  - Sync Food Level: Sync player food level between servers.

## IMPORTANT NOTES 

- Configs MUST be identical between all participating servers in order for the mod to function correctly.
- Due to the way the mod loads and saves player data, the mod will clear player inventories upon first join. This is not a problem when the mod is set up on a server from the get go, however will erase player inventories on servers with existing worlds and progress.

## Issues And Requests

If you find any bugs or glitches, be sure to make a bug report under issues and I will do my best to fix it! Just as well if you have a cool idea for something that I should add, let me know and I will consider adding it!

