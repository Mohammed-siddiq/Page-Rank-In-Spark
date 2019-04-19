package commons

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.spark.rdd.RDD

object PageRankImplementer {
  def rankVenues(ranks: RDD[(String, Double)]) = {
    ranks.filter(x => {
      val csAuthors = ConfigUtils.UIC_CS_AUTHORS
      !csAuthors.contains(x._1.toLowerCase)
    }).sortBy(x => x._2, ascending = false)
  }

  def rankAuthors(ranks: RDD[(String, Double)]) = {
    ranks.filter(x => {
      val csAuthors = ConfigUtils.UIC_CS_AUTHORS
      csAuthors.contains(x._1.toLowerCase)
    }).sortBy(x => x._2, ascending = false)

  }


  /**
    *
    * @param nodePairs Individual nodes on which page rank must be applied
    * @return final page rank values of individual nodes
    */
  def applyPageRank(nodePairs: RDD[(String, String)]) = {
    val links = nodePairs.distinct().groupByKey().cache()
    var ranks = links.mapValues(v => 1.0) // create the ranks <key,one> RDD from the links <key, Iter> RDD


    for (i <- 1 to 20) {
      val contributions = links.join(ranks) // join  -> RDD1
        .values // extract values from RDD1 -> RDD2
        .flatMap { case (urls, rank) => // RDD2 -> conbrib RDD
        val size = urls.size
        urls.map(url => (url, rank / size)) // the ranks are distributed equally amongs the various URLs
      }
      ranks = contributions.reduceByKey(_ + _).mapValues(0.15 + 0.85 * _) // ranks RDD
    }
    ranks
  }


  /**
    * For each xml entity in dblp generate nodes for authors and respective venues
    *
    * @param record individual record in the dblp dataset
    * @return list of string pairs representing the node pairs between the authors and the venues
    */
  def generateNodePairs(record: (LongWritable, Text)) = {
    val xmlEntry = record._2.toString
    //Extracting authors and filtering only UIC CS AUTHORS
    val authorPattern = ConfigUtils.AUTHORS_PATTERN.r

    val authors = authorPattern.findAllIn(xmlEntry).toList.map(tag => tag.replaceAll("<.*?>", "").trim).filter(a => {
      val csAuthors = ConfigUtils.UIC_CS_AUTHORS

      csAuthors.contains(a.toLowerCase)
    })

    val venuePattern = ConfigUtils.VENUES_PATTERN.r
    //Extracting Venues
    val venue = venuePattern.findAllIn(xmlEntry).mkString.replaceAll("<.*?>", "")
    //Returning both venues and authors
    val va = venue :: authors
    for {
      i <- va
      j <- va
      if i != j
    } yield (i, j)
  }


}
