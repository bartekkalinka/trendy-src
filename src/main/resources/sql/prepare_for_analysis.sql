ALTER TABLE wordcounts RENAME TO wordcounts_1;

CREATE INDEX wc_hash_idx
  ON wordcounts_1 (hash);

CREATE INDEX wc_word_idx
  ON wordcounts_1 (word);

ALTER TABLE wordcounts_1 ADD CONSTRAINT harsh_word_unq UNIQUE (hash, word);