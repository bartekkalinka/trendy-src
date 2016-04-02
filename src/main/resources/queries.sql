select * from wordcounts;

select count(1) from wordcounts;

select hash, '.' || word || '.', count from wordcounts order by count desc, hash, word;

select hash, word, count(1) from wordcounts group by hash, word having count(1) > 1;

WITH agr AS (select word, max(count) maxcnt, min(count) mincnt from wordcounts group by word)
select word, maxcnt, mincnt from agr where maxcnt - mincnt = (select max(maxcnt - mincnt) from agr);

delete from wordcounts where word = 'val';