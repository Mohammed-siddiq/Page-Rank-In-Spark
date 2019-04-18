package commons

import org.apache.hadoop.io.{LongWritable, Text}

class RecordOperations {

  /**
    * For each record or entry in the
    *
    * @param record
    * @return
    */
  def generateNodePairs(record: (LongWritable, Text)) = {
    val xmlEntry = record._2.toString
    //Extracting authors and filtering only UIC CS AUTHORS
    val authorPattern = Utils.AUTHORS_PATTERN.r

    val authors = authorPattern.findAllIn(xmlEntry).toList.map(tag => tag.replaceAll("<.*?>", "").trim).filter(a => {
      val csAuthors = Utils.UIC_CS_AUTHORS

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
  }
}
