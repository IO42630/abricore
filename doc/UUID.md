# UUID

## `UUID` usage in _abricore_

* Each `Strategy` has an `UUID`.
* All `Job`(s) spawned from a `Strategy` inherit it's `UUID`.
    * => for each `Strategy` only one `Job` per `JobType` may run.
* `UUID` is used to fetch `Job` from `JobKeeper`.
