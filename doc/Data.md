# Data

## Overview


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




## Position

* PositionStatus
* `OPEN` : currently not used.
    * `CONFIRMED` : set when a Positon is _fetched_.





