import Jhelpers.XmlInputFormatWithMultipleTags
import com.typesafe.config.ConfigFactory
import commons.Utils
import org.apache.hadoop.conf.Configuration
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.io.{LongWritable, Text}
import org.slf4j.{Logger, LoggerFactory}


object PageRank extends App {
  val sparkConf = new SparkConf().setAppName("Sample Spark Scala Application")
  val sc = new SparkContext(sparkConf)
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  val csAuthors = Utils.UIC_CS_AUTHORS


  sc.hadoopConfiguration.set(XmlInputFormatWithMultipleTags.START_TAG_KEYS, Utils.START_TAGS)
  sc.hadoopConfiguration.set(XmlInputFormatWithMultipleTags.END_TAG_KEYS, Utils.END_TAGS)


  val records = sc.newAPIHadoopFile(
    args(0),
    classOf[XmlInputFormatWithMultipleTags],
    classOf[LongWritable],
    classOf[Text])

  val nodePairs = records.flatMap(record => {
    val xmlEntry = record._2.toString
    val authorPattern = Utils.AUTHORS_PATTERN.r
    //Extracting authors and filtering only UIC CS AUTHORS
    val authors = authorPattern.findAllIn(xmlEntry).toList.map(a => a.replaceAll("<.*?>", "").trim).filter(a => {
      csAuthors.contains(a.toLowerCase)
    })
    val venuePattern = Utils.VENUES_PATTERN.r

    //Extracting Venues
    val venue = venuePattern.findAllIn(xmlEntry).mkString.replaceAll("<.*?>", "")
    //Returning both venues and authors
    val va = venue :: authors
    for {
      i <- va
      j <- va
      if i != j
    } yield (i, j)
  }).distinct() //Taking those entries in which there's at least one UIC CS authors


  val links = nodePairs.distinct().groupByKey().cache()

  var ranks = links.mapValues(v => 1.0) // create the ranks <key,one> RDD from the links <key, Iter> RDD


  for (i <- 1 to 20) {
    val contribs = links.join(ranks) // join  -> RDD1
      .values // extract values from RDD1 -> RDD2
      .flatMap { case (urls, rank) => // RDD2 -> conbrib RDD
      val size = urls.size
      urls.map(url => (url, rank / size)) // the ranks are distributed equally amongs the various URLs
    }
    ranks = contribs.reduceByKey(_ + _).mapValues(0.15 + 0.85 * _) // ranks RDD
  }


  val rankedAuthors = ranks.filter(x => csAuthors.contains(x._1.toLowerCase)).sortBy(x => x._2, ascending = false)
  val rankedVenues = ranks.filter(x => !csAuthors.contains(x._1.toLowerCase)).sortBy(x => x._2, ascending = false)

  rankedAuthors.saveAsTextFile(args(1))
  rankedVenues.saveAsTextFile(args(2))

}
