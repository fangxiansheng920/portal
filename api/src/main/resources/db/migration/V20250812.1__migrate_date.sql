INSERT IGNORE INTO portal_tag (portal_id, tag_id, create_time)
SELECT id     AS portal_id,
       tag_id AS tag_id,
       NOW()  AS create_time
FROM portals
WHERE tag_id IS NOT NULL
  AND TRIM(tag_id) != '';