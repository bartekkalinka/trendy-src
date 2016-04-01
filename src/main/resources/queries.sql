select * from wordcounts;


select hash, '.' || word || '.', count from wordcounts order by count desc, hash, word;

WITH agr AS (select word, max(count) maxcnt, min(count) mincnt from wordcounts group by word)
select word, maxcnt, mincnt from agr where maxcnt - mincnt = (select max(maxcnt - mincnt) from agr);