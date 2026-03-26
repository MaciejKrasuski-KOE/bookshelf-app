CREATE TABLE authors (
    id   VARCHAR(36)  PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE book_authors (
    book_id   VARCHAR(36) NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    author_id VARCHAR(36) NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);

CREATE INDEX idx_book_authors_author_id ON book_authors (author_id);
