CREATE TABLE shelves (
    id          VARCHAR(36)  PRIMARY KEY,
    user_id     VARCHAR(36)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_shelf_user_name UNIQUE (user_id, name)
);

CREATE TABLE shelf_books (
    id          VARCHAR(36)  PRIMARY KEY,
    shelf_id    VARCHAR(36)  NOT NULL REFERENCES shelves (id) ON DELETE CASCADE,
    book_id     VARCHAR(100) NOT NULL,
    added_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_shelf_book UNIQUE (shelf_id, book_id)
);

CREATE INDEX idx_shelves_user_id      ON shelves (user_id);
CREATE INDEX idx_shelf_books_shelf_id ON shelf_books (shelf_id);
