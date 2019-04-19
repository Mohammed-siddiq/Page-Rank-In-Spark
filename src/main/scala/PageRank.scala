import Jhelpers.XmlInputFormatWithMultipleTags
import com.typesafe.config.ConfigFactory
import commons.{ConfigUtils, PageRankImplementer}
import org.apache.hadoop.conf.Configuration
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.io.{LongWritable, Text}
import org.slf4j.{Logger, LoggerFactory}


object PageRank extends App {

  // Creating spark context
  val sparkConf = new SparkConf().setAppName("Page rank on DBLP")
  val sc = new SparkContext(sparkConf)
  val logger: Logger = LoggerFactory.getLogger(this.getClass)


  sc.hadoopConfiguration.set(XmlInputFormatWithMultipleTags.START_TAG_KEYS, ConfigUtils.START_TAGS)
  sc.hadoopConfiguration.set(XmlInputFormatWithMultipleTags.END_TAG_KEYS, ConfigUtils.END_TAGS)


  logger.info("Reading the Input file from  : ", args(0))
  val records = sc.newAPIHadoopFile(
    args(0),
    classOf[XmlInputFormatWithMultipleTags],
    classOf[LongWritable],
    classOf[Text])



  val nodePairs = records.flatMap(record => {
    PageRankImplementer.generateNodePairs(record)
  }).distinct() //Taking those entries in which there's at least one UIC CS authors


  logger.info("Applying page rank..")
  //Applying page rank on the above generated nodes
  val ranks = PageRankImplementer.applyPageRank(nodePairs)

  // Ranking authors based on Pagerank
  val rankedAuthors = PageRankImplementer.rankAuthors(ranks)

  //Ranking venues based on page rank
  val rankedVenues = PageRankImplementer.rankVenues(ranks)

  //Persisting authors ranks
  rankedAuthors.saveAsTextFile(args(1))

  //Persisting venues ranks
  rankedVenues.saveAsTextFile(args(2))

}
