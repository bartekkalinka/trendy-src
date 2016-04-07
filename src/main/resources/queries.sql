select * from wordcounts;

select count(1) from wordcounts;

select seqnum, hash, '.' || word || '.', count from wordcounts order by count desc, hash, word;

select hash, word, count(1) from wordcounts group by hash, word having count(1) > 1;

WITH agr AS (select word, max(count) maxcnt, min(count) mincnt from wordcounts group by word)
select word, maxcnt, mincnt from agr where maxcnt - mincnt = (select max(maxcnt - mincnt) from agr);

select count(distinct hash) from wordcounts;

select count(distinct word), sum(count), hash from wordcounts group by hash;

select min(seqnum), max(seqnum) from wordcounts;

select distinct seqnum, hash, commit_date from wordcounts order by seqnum, hash, commit_date;