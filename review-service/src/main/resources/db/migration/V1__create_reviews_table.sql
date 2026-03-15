CREATE TABLE reviews (
    id              VARCHAR(36)  PRIMARY KEY,
    user_id         VARCHAR(36)  NOT NULL,
    book_id         VARCHAR(100) NOT NULL,
    rating          INTEGER      NOT NULL CHECK (rating BETWEEN 1 AND 5),
    content         TEXT,
    verified_reader BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_review_user_book UNIQUE (user_id, book_id)
);

CREATE INDEX idx_reviews_book_id ON reviews (book_id);
CREATE INDEX idx_reviews_user_id ON reviews (user_id);
