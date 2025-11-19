# 给tags表中的name字段加上unique约束
ALTER TABLE tags ADD UNIQUE (name);