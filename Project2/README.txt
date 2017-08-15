The Document and Output file can be found in /docs folder
The Files used to generate ouput can be found in /results folder

*) ant clean; ant
1)hadoop dfs -mkdir input
2)hadoop dfs -put PageRankDataGenerator/pagerank5000g50.input.0 input
3)hadoop jar dist/HadoopPageRankMooc.jar indiana.cgl.hadoop.pagerank.HadoopPageRank input output 5000 1
4)hadoop dfs -cat output/part-r-00000

Usage: hadoop jar dist/HadoopPageRankMooc.jar [inputDir][outputDir][numUrls][number of iterations]
