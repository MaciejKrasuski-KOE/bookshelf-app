ALTER TABLE books DROP COLUMN author;
ALTER TABLE books ADD COLUMN original_title VARCHAR(255);
ALTER TABLE books ADD COLUMN series_id VARCHAR(36);
ALTER TABLE books ADD COLUMN sub_series_id VARCHAR(36);
ALTER TABLE books ADD COLUMN series_order INT;
ALTER TABLE books ADD COLUMN sub_series_order INT;

ALTER TABLE books ADD CONSTRAINT fk_books_series
    FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE SET NULL;

ALTER TABLE books ADD CONSTRAINT fk_books_sub_series
    FOREIGN KEY (sub_series_id) REFERENCES sub_series(id) ON DELETE SET NULL;
