# SnapShot


## Lifecycle

[](img/snap-lifecycle.svg)

* In a way Dto mimics Entity.
* `SnapshotEntity` has Unique Index on Instant/Asset.
* Therefore, SnapShotDto is hashed by Instant/Asset.
    * We insist of working with Set<SnapShotDto>.
    * This implies data loss (by pushing over an existing hash).
    * Thus encouraging the usage of SnapShotDto.merge(SnapShotDto).
* The runtime data holder is a `TreeMap<Instant, SnapShotDto>`.
    * It is filled via `put(SnapShotDto)`.
    * To do so, we:
        * Check if "newly pushed" SnapShotDto exists.
        * If yes we merge "newly pushed" into existing SnapShot.
        * If no we simply put.


## T_SNAPSHOTS

This table holds the quote data of all the assets.
For historical data we aim to build a _1S_ grid.
Overall there are four sources of quote data:


| SQ OBS   | From observing quotes of options in SQ. Used mostly for sanity and delay testing.                                                                         |
|----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| TW OBS   | From observing quotes of underlying assets in TW. This data does not match the 1S grid, and is usually finer.                                             |
| TW HIST  | From downloaded historical quotes of underlying assets in TW.    This data matches the 1S grid, and is usually coarser.                                   |
| TW PATCH | If an asset has not been traded, there might be a 2S to 15S gap in the historical data. To build our 1S grid we patch this gap with the last known value. |


| COLUMN       | SQ OBS | TW OBS | TW HIST | TW PATCH | DESCRIPTION                  |
|--------------|--------|--------|---------|----------|------------------------------|
| ID           | OK     | OK     | OK      | OK       |                              |
| ASSET        | OK     | OK     | OK      | OK       | Unique asset name.           |
| TIME         | OK     | OK     | OK      | OK       |                              |
| PRICE_TRADED | null   | OK     | OK      | OK       | open, i.e. the opening price |
| PRICE_BID    | OK     | null   | null    | null     |                              |
| PRICE_ASK    | OK     | null   | null    | null     |                              |
| RANGE        | null   | null   | OK      | 0        | high - low.                  |
| VOLUME       | null   | null   | OK      | 0        |                              |
