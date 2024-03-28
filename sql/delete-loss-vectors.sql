# DELETE
# SELECT count(*)
SELECT *
FROM t_vector
WHERE rating < 350
# AND sample_count < 2
# AND avg_duration < 60
# OR (SAMPLE_COUNT < 2     AND avg_duration < 100)
# AND avg_duration = 62;
# WHERE rating < 40;
# WHERE (rating * t_vector.avg_duration * sample_count )  < 50000;
#   AND avg_duration < 5000;