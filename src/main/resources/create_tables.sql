﻿DROP TABLE wordcounts;

CREATE TABLE wordcounts(seqnum bigint NOT NULL, hash text NOT NULL, word text NOT NULL, count bigint NOT NULL);
