package commons

import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf.Configuration

object Utils {
  // a regular expression which matches commas but not commas within double quotations
  val AUTHORS_PATTERN =
    """<author>.*?<\/author>"""
  val configuration = new Configuration
  val conf = ConfigFactory.load("InputFormat")
  val START_TAGS = conf.getString("START_TAGS")
  val END_TAGS = conf.getString("END_TAGS")
  val UIC_CS_AUTHORS = conf.getStringList("UIC_CS_PROFESSORS")
  val VENUES_PATTERN = """<booktitle>[\s\S]*?<\/booktitle>|<journal>[\s\S]*?<\/journal>|<publisher>[\s\S]*?<\/publisher>|<school>[\s\S]*?<\/school>"""

}
