﻿select * from wordcounts;

select count(1) from wordcounts;

select * from hashes order by seqnum;

select count(1) from hashes;

-- highest counts for single hashes
select seqnum, hash, '.' || word || '.', count from wordcounts order by count desc, hash, word;

-- highest total counts
select word, sum(count) from wordcounts group by word order by sum(count) desc;

select hash, word, count(1) from wordcounts group by hash, word having count(1) > 1;

-- highest differences in count
WITH agr AS (select word, max(count) maxcnt, min(count) mincnt from wordcounts group by word)
select word, maxcnt, mincnt from agr order by maxcnt - mincnt desc;

select count(distinct hash) from wordcounts;

select min(seqnum), max(seqnum) from wordcounts;

select distinct seqnum, hash, commit_date from wordcounts order by seqnum, hash, commit_date;

select seqnum, hash, commit_date, count(distinct word), sum(count) from wordcounts group by seqnum, hash, commit_date order by seqnum;

-- histogram of given word percentage in relation to total wordcount
-- interesting words: map, Int, val, def, case, import, Array, tilePixels, state, player, Seq, akka, should, Option
select h.seqnum, h.hash, h.commit_date, coalesce(w.count, 0) * 100 / h.totalcount percent 
from hashes h left outer join (select * from wordcounts where word = 'Future') w on w.seqnum = h.seqnum 
order by h.seqnum;
