ALTER TABLE wordcounts ADD CONSTRAINT harsh_word_unq UNIQUE (hash, word);

CREATE TABLE hashes AS
select seqnum, hash, commit_date, sum(count) totalcount from wordcounts group by seqnum, hash, commit_date;

CREATE INDEX h_seqnum_idx
    ON hashes (seqnum);

CREATE INDEX wc_word_idx
  ON wordcounts (word);

CREATE TABLE words AS
SELECT distinct word FROM wordcounts;


