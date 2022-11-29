# Wordle-Solver
This algorithm runs a greedy-best search to calculate the n (default is n=5) best lines. One line is better than another if, after all g guesses (default is g=4), it results in a lower average number of words that could result in any given pattern. 

# Some more thoughts about this algorithm
## Defining the Problem
We can disambiguate two words if they produce a different pattern from our guesses. 

We can deduce that a given word is our word if it is the only one that would result in a given pattern. 

Problem: it is really hard to completely disambiguate a word – is this a good metric? Or should we instead be focusing on the average number of ambiguous words that produce a given pattern?

Solution: instead of just considering the number of ambiguous words (which won’t change much with each guess), we can take the geometric mean of the size of each bucket of patterns. 

## Designing the Algorithm
Backtracking search – try adding words until we get to 6 words. Then, see which one produces the lowest number of ambiguous words. 

Unfortunately, since we have around 10,000 words, this gives us around 10^24 combinations, which is far too many to brute-force. 

Can we do dynamic programming? Unfortunately, the answer is no because this doesn’t follow the optimal substructure property. 

But wait! At the end of our 6 words, we only care about the number of words we’ve disambiguated. We don’t care about the order in which we entered our words. 

So, we can reduce our number of combinations from (10,000)^6 down to 10,000 choose 6, which is “only” 10^21. 

This is still too slow. Can we do any better?

One idea is to potentially incorporate some heuristics and/or conduct a greedy-best search. For certain possibilities, e.g. if we started with epoxy and moved on to epoch, we basically know we’re not getting much information out of these guesses. So, we can either find a way to prune them and prove that they’re not optimal early (before letting them explode into 10^21 combinations), or we can simply ignore these possibilities and conduct a greedy search. 

A greedy search is more than fast enough. However, is it optimal?

I think the answer is probably no, although I can’t think of a proof…

In either case, we can supplement our greedy best search by allowing ourselves to consider the top n=500 combinations (which is fast enough), rather than just the single best combination so far. 

Obviously, as the number of combinations we allow ourselves to consider increases, our answer becomes more and more optimal. In fact, it essentially becomes our backtracking search as we get to n=10^17 possibilities. 

Can we mathematically prove that considering the top 500,000 combinations is good enough? Is there some way to measure the effectiveness of this heuristic? Clearly, most of the words we are skipping would not have been effective, anyway. 

To make an educated guess to answer this question, we can look at a similar type of situation – move ordering in a chess AI. In that case, even a very rough ordering where you prioritize captures by piece value gives you over 90% of the optimal move ordering, and we can probably expect something similar here. The analogy is that most of the bad/nonproductive moves in the chess AI are “quiet,” and similarly, most of the bad words in Wordle are going to be “quiet” in terms of not doing much to help reduce our mean bucket size. 
