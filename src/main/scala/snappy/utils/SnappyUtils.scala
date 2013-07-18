import scala.util.control.Breaks._

import java.io.File
import java.io.{InputStream, BufferedInputStream, FileInputStream}
import java.io.{OutputStream, BufferedOutputStream, FileOutputStream}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.compress.CompressionCodec
import org.apache.hadoop.io.compress.SnappyCodec

object SnappyUtils extends App {
  val codec = new SnappyCodec()
  val extension = codec.getDefaultExtension()

  codec.setConf(new Configuration())

  var inputFile: Option[String] = None
  var outputFile: Option[String] = None
  var isCompression = true

  def printUsage() {
    val usage =
      """Usage: snappy [<options>] <input-file>
        |
        |   -o, --output        Output file name.
        |
        |   -d, --decompression Decompression mode.  If the `--output' option is
        |                       omitted and the input file name ends with
        |                       `.snappy', the default output file name is the
        |                       input file name without the `.snappy' suffix.
        |
        |   -c, --compression   Compression mode, enabled by default.  If the
        |                       `--output' option is omitted, the default
        |                       output file name is the   input file name with a
        |                       `.snappy' suffix.
      """.stripMargin

    println(usage)
    System.exit(0)
  }

  def parse(args: List[String]) {
    args match {
      case ("-o" | "--output") :: value :: tail =>
        outputFile = Some(value)
        parse(tail)

      case ("-d" | "--decompress") :: tail =>
        isCompression = false
        parse(tail)

      case ("-c" | "--compress") :: tail =>
        isCompression = true
        parse(tail)

      case value :: tail =>
        inputFile = Some(value)
        parse(tail)

      case Nil =>
        ()

      case _ =>
        printUsage()
    }
  }

  parse(args.toList)

  (isCompression, inputFile, outputFile) match {
    case (true, Some(in: String), None) =>
      outputFile = Some(in + extension)

    case (false, Some(in: String), None) if in.endsWith(extension) =>
      outputFile = Some(in.stripSuffix(extension))

    case (_, Some(_), Some(_)) =>
      ()

    case _ =>
      printUsage()
  }

  val in = new BufferedInputStream(new FileInputStream(inputFile.get))
  val out = new BufferedOutputStream(new FileOutputStream(outputFile.get))

  if (isCompression)
    copy(in, codec.createOutputStream(out))
  else
    copy(codec.createInputStream(in), out)

  def copy(in: InputStream, out: OutputStream) {
    val buffer = new Array[Byte](8192)

    def loop() {
      val bytesRead = in.read(buffer)

      if (bytesRead > 0) {
        out.write(buffer, 0, bytesRead)
        loop()
      }
    }

    try {
      loop()
    }
    finally {
      in.close()
      out.close()
    }
  }
}
