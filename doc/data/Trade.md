# Trade

* we decided to group buy/sell into one obj _trade_ for tracebility.
* however we need some additonal rules:
    * if a position in an asset is open, no further position may be opened
    * if a position is closed, it must be closed in its entirety
* overall we will take `{ amount, asset }` as KEY (see _ABC-1_)
    * the remaining values are just metadata
* if there are preexisting positions, we add a trade with status `OPEN_EXECUTED` for them (see _ABC-2_)



### Trade States

* see _enum_.

[](img/trade-states.svg)




## TODO

* add _issuedInstant_
    * if issued some time ago, and not executed, then cancel
