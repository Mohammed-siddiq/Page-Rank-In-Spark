package CoreLogicTests

import Jhelpers.XmlInputFormatWithMultipleTags
import org.scalatest.FlatSpec
import commons.{ConfigUtils, PageRankImplementer}
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Tests the core logic of page rank on a test data
  */
class TestPageRankImplementation extends FlatSpec {

  val sparkConf = new SparkConf().setAppName("Page rank on DBLP").setMaster("local[2]")
  val sc = new SparkContext(sparkConf)
  val logger: Logger = LoggerFactory.getLogger(this.getClass)


  val testData = ConfigUtils.TEST_DATA

  val records = testData.map(xmlEntry => (new LongWritable(), new Text(xmlEntry)))


  "Verifying that Generate Pairs" should "extract the required authors and venues return nodes which are connected" in {

    //Creating spark contest


    //Extracting authors and generating the nodes
    val nodePairs = records.flatMap(record => PageRankImplementer.generateNodePairs(record))

    assert(nodePairs != null)
    //Assert that all possible pairs are generated
    assert(nodePairs.size == 10)


  }

  "Verifying that Apply page rank for authors" should "return the page rank values of individual nodes" in {
    val rankedNodes = PageRankImplementer.applyPageRank(sc.parallelize(records.flatMap(record => PageRankImplementer.generateNodePairs(record))))
    assert(rankedNodes != null)
    assert(rankedNodes.collect().size == 5)

  }
  "Verifying that Rank Authors " should "return a list of authors ranked based on their values" in {

    val dummyAuthorRanks = List(("tanya y. berger-wolf", 1.3), ("daniel j. bernstein", 10.4), ("emanuelle burton", 3.2), ("testVenue", 1.5), ("testVenue1", 3.5))

    val rankedAuthors = PageRankImplementer.rankAuthors(sc.parallelize(dummyAuthorRanks))

    assert(rankedAuthors != null)
    //Only CS authors must be filtered and ranked
    assert(rankedAuthors.count() == 3)
    assert(rankedAuthors.collect()(0)._2 == 10.4) //Highest rank first
    assert(rankedAuthors.collect()(1)._2 == 3.2)
    assert(rankedAuthors.collect()(2)._2 == 1.3)
  }
  "Verifying that Rank Venues " should "return a list of Venues ranked based on their values" in {

    val dummyVenueRanks = List(("tanya y. berger-wolf", 1.3), ("daniel j. bernstein", 10.4), ("emanuelle burton", 3.2), ("testVenue", 1.5), ("testVenue1", 3.5))

    val rankedVenues = PageRankImplementer.rankVenues(sc.parallelize(dummyVenueRanks))

    assert(rankedVenues != null)
    //Only CS authors must be filtered and ranked
    assert(rankedVenues.count() == 2)
    assert(rankedVenues.collect()(0)._2 == 3.5)
    assert(rankedVenues.collect()(1)._2 == 1.5)

  }
  "Verifying that Ranked Authors" should "Not return anything when list doesnt UIC CS authors" in {
    val dummyAuthorRanks = List(("testVenue", 1.5), ("testVenue1", 3.5))

    val rankedAuthors = PageRankImplementer.rankAuthors(sc.parallelize(dummyAuthorRanks))

    //No CS authors must be filtered and ranked
    assert(rankedAuthors.count() == 0)

  }


}
