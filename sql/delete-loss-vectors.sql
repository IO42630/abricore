# DELETE
SELECT count(*)
FROM t_vector
WHERE rating < 100
AND avg_duration = 62;
# AND sample_count < 2;
# WHERE t_vector.sample_count < 2;
#   AND avg_duration < 5000;