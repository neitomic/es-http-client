import java.io.{BufferedReader, File, FileInputStream, FileReader}
import java.util.Calendar

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.{CsvMapper, CsvSchema}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.collection.mutable

/**
  * Created by 51103 on 011, 11, 6, 2017.
  */
object EsDataPusher {

  final val DELIMITER = "\",\""
  final val YEAR_PATTERN = """(\d\d\d\d)""".r

  def main(args: Array[String]): Unit = {

    val client = new ESHttpClient(Seq("localhost:9202"), NoAuth())

    val file = "D:/TienNT4/working/CirceBoard/data/worldbank_vn/API_VNM_DS2_en_csv_v2.csv"
    val reader = new BufferedReader(new FileReader(file))

    //First line is header
    val header = reader.readLine()
    val keyIndex = 3

    val yearAndIndex = parseHeader(header)
    println(yearAndIndex)

    val data = yearAndIndex.map(t => {
      (t._1, mutable.Map[String, Double]())
    }).toMap
    var line = reader.readLine()
    while ( line != null) {
      val fields = line.split(DELIMITER, -1)
      yearAndIndex.foreach(t => {
        try {
          normalizeQuote(fields(t._2)) match {
            case null => println(line)
            case s if s.nonEmpty => data(t._1).put(normalizeQuote(fields(keyIndex)), s.toDouble)
            case _ =>
          }
        }catch {
          case t : Throwable =>
            println("For line: " + line)
            t.printStackTrace()
        }
      })
      line = reader.readLine()
    }


    val calendar = Calendar.getInstance()
    calendar.set(calendar.get(Calendar.YEAR), 0, 1,0,0,0)
    calendar.set(Calendar.MILLISECOND, 0)
    val seq = data.map(e =>
      {

        val newVal = e._2.toSeq.map(field => {
          val firstIndexOfDot = field._1.indexOf(".")

          val esType = field._1.substring(0, firstIndexOfDot)
          val key = field._1.substring(firstIndexOfDot + 1)
          (esType, Map(key -> field._2))
        })

        val grouped = newVal.groupBy(_._1)
        .mapValues(_.map(_._2).foldLeft(Map.empty[String, Double])((b,e) => b ++ e))

        calendar.set(Calendar.YEAR, e._1.toInt)
        grouped.map(e => e._1 -> (e._2 ++ Map("time" -> calendar.getTimeInMillis)))
      })

    val objectMapper = new ObjectMapper().registerModule(DefaultScalaModule)
    println(objectMapper.writeValueAsString(seq))
    reader.close()
    client.getClient.close()
  }

  def parseHeader(str: String): Seq[(String, Int)] = {
    str.split(DELIMITER).zipWithIndex.flatMap(e =>
      normalizeQuote(e._1.trim) match {
        case YEAR_PATTERN(year) => Some((year, e._2))
        case _ => None
      }
    )
  }

  def normalizeQuote(input: String) : String =
    if (input.startsWith("\"") && input.endsWith("\""))
      normalizeQuote(input.substring(1, input.length -1).trim)
    else
      input
}
