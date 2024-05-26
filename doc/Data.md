# Data

## Overview

* For details see `com.olexyn.abricore.model`.


| Data              | Desc                                                                    |
|-------------------|-------------------------------------------------------------------------|
| `UnderlyingAsset` | A physical or imaginary asset with price. Usually non-tradable.         |
| `Option`          | Derivative of an underlying asset. Usually tradable.                    |
| `SnapShot`        | State of an asset at a given instant. Usually aggregated into a series. |
| `Position`        | Amount of an asset held by the user in the present or past.             |
| `Trade`           | Combines open and close transactions into one entity.                   |
| `Strategy`        | Set of rules represented by values and lambdas.                         |

## Option

* OptionStatus
    * `FOUND`
    * `KNOWN`
    * `SELECTED_FOR_BRACE`
    * `DEAD`

## SnapShot

### T_SNAPSHOTS

This table holds the quote data of all the assets.
For historical data we aim to build a 1S grid.
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


## Position

* PositionStatus
* `OPEN` : currently not used.
    * `CONFIRMED` : set when a Positon is _fetched_.

## Trade

* we decided to group buy/sell into one obj _trade_ for tracebility.
* however we need some additonal rules:
    * if a position in an asset is open, no further position may be opened
    * if a position is closed, it must be closed in its entirety
* overall we will take `{ amount, asset }` as KEY (see _ABC-1_)
    * the remaining values are just metadata
* if there are preexisting positions, we add a trade with status `OPEN_EXECUTED` for them (see _ABC-2_)

### TradeStatus

* `OPEN_PREPARED`
* `OPEN_ISSUED`
* `OPEN_PENDING`
* `OPEN_EXECUTED`
* `CANCEL_PREPARED`
* `CANCEL_ISSUED`
* `CANCEL_PENDING`
* `CANCEL_EXECUTED`
* `CLOSE_PREPARED`
* `CLOSE_ISSUED`
* `CLOSE_PENDING`
* `CLOSE_EXECUTED`

## Strategy


