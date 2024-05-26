= Data
:toc:
:toclevels: 5
:stylesheet: ../../shared/adoc-styles.css

====
* Main link:../README.adoc[README.adoc].
====

== Overview
* For details see `com.olexyn.abricore.model`.

[cols="1,4"]
|===
| `UnderlyingAsset` | A physical or imaginary asset with price. Usually non-tradable.
| `Option` | Derivative of an underlying asset. Usually tradable.
| `SnapShot` | State of an asset a given instant. Usually aggregated into a series.
| `Position` | Amount of an asset held by the user in the present or past.
| `Trade` | Combines open and close transactions into one entity.
| `Strategy` | Set of rules represented by values and lambdas.
|===

{empty} +

== Option
* OptionStatus
** `FOUND`
** `KNOWN`
** `SELECTED_FOR_BRACE`
** `DEAD`

== SnapShot

=== T_SNAPSHOTS

This table holds the quote data of all the assets.
For historical data we aim to build a 1S grid.
Overall there are four sources of quote data:

[cols="1,3"]
|===
| SQ OBS | From observing quotes of options in SQ. +
Used mostly for sanity and delay testing.
| TW OBS | From observing quotes of underlying assets in TW. +
This data does not match the 1S grid, and is usually finer.
| TW HIST | From downloaded historical quotes of underlying assets in TW. +
This data matches the 1S grid, and is usually coarser.
| TW PATCH | If an asset has not been traded, there might be a 2S to 15S gap in the historical data. +
To build our 1S grid we patch this gap with the last known value.
|===

{empty} +

[%header,cols="1,1,1,1,1,2"]
|===
| COLUMN | SQ OBS | TW OBS | TW HIST | TW PATCH | DESCRIPTION
| ID 4+^| ✔️ |
| ASSET 4+^| ✔️ | Unique asset name.
| TIME 4+^| ✔️ |
| PRICE_TRADED | null 3+^| ✔️ (open) |
| PRICE_BID | ✔️ 3+^| null |
| PRICE_ASK | ✔️ 3+^| null |
| RANGE 2+^| null | ✔️ | 0 | high - low.
| VOLUME 2+^| null | ✔️ | 0 |
|===

== Position
* PositionStatus
** `OPEN` : currently not used.
** `CONFIRMED` : set when a  Positon is _fetched_.

## Trade

* we decided to group buy/sell into one obj _trade_ for tracebility.
* however we need some additonal rules:
    * if a position in an asset is open, no further position may be opened
    * if a position is closed, it must be closed in its entirety
* overall we will take `{ amount, asset }` as KEY
    * the remaining values are just metadata

* ID :
* TradeStatus
** `OPEN_PREPARED`
** `OPEN_ISSUED`
** `OPEN_PENDING`
** `OPEN_EXECUTED`
** `CANCEL_PREPARED`
** `CANCEL_ISSUED`
** `CANCEL_PENDING`
** `CANCEL_EXECUTED`
** `CLOSE_PREPARED`
** `CLOSE_ISSUED`
** `CLOSE_PENDING`
** `CLOSE_EXECUTED`

== Strategy


