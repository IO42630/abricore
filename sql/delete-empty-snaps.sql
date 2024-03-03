# DELETE
SELECT COUNT(*)
FROM t_snapshot
WHERE bid_price = 'null'
  AND price_range = 'null'
  AND traded_price = 'null'
  AND volume = 'null';

