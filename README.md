# feedback vertex set solver

This is an exact solver for feedback vertex set problem and was submitted to the 1st Parameterized Algorithms and Computational Experiments Challenge (<https://pacechallenge.wordpress.com/track-b-feedback-vertex-set/>).
Preliminary results against the public data set: [result.tsv](result.tsv)

## Implemented algorithms
 * Linear-time kernel (unpublished) ([ReductionRoot.java](src/ReductionRoot.java))
  * This algorithm runs in O(k^4 m) time, where k is the solution size, and produces an equivalent instance of size at most 2k^2+k vertices and 4k^2 edges.
  * Using the following set of reductions:
    * the basic reductions (self-loop, degree at most 2)
    * s-cycle cover reduction by an efficient augmentaing-path algorithm for solving the k-submodular relaxation ([HalfIntegralRelax.java](src/HalfIntegralRelax.java))
    * + several heuristic reductions (these reductions are correct but would not improve the theoretical worst-case perfomance)
 * FPT branch-and-bound (http://arxiv.org/abs/1310.2841) ([FPTBranchingSolver.java](src/FPTBranchingSolver.java))
  * This algorithm runs in O^*(4^k) time.
  * At each node of the search tree, we apply the following set of reductions ([Reduction.java](src/Reduction.java))
    * the basic reductions
    * persistency reduction
    * + several heuristic reductions
  * Using the following lewer bounds to prune the search
    * k-submodular relaxation lower bound computed by the efficient augmenting-path algorithm
    * lower bound used for bounding the kernel size ([LowerBound.java](src/LowerBound.java))

## Required
Java 1.7 or higher

## How to build
    $ ./build.sh
(ignore warning)

## How to run
    $ ./run.sh < 001.graph

## Authors
 * Yoichi Iwata
 * Kensuke Imanishi
