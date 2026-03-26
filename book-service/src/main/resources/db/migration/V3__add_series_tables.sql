CREATE TABLE series (
    id   VARCHAR(36)  PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE sub_series (
    id        VARCHAR(36)  PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    series_id VARCHAR(36)  NOT NULL REFERENCES series(id) ON DELETE CASCADE
);

CREATE INDEX idx_sub_series_series_id ON sub_series (series_id);
