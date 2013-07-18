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

  var inputFile: String = null
  var outputFile: String = null
  var isCompression = true

  def printUsage() {
    val usage =
      """Usage: snappy [<options>] <input-file>                                 
        |                                                                       
        |   -o, --output        Output file name.                               
        |                                                                       
        |   -d, --decompression Decompression mode, enabled by default.  If the 
        |                       `--output' option is omitted and the input file 
        |                       name ends with `.snappy', the default output    
        |                       file name is the input file name without the    
        |                       `.snappy' suffix.                               
        |                                                                       
        |   -c, --compression   Compression mode.  If the `--output' option is  
        |                       omitted, the default  output file name is the   
        |                       input file name with a `.snappy' suffix.        
      """.stripMargin

    println(usage)
    System.exit(0)
  }

  def parse(args: List[String]) {
    args match {
      case ("-o" | "--output") :: value :: tail =>
        outputFile = value
        parse(tail)

      case ("-d" | "--decompress") :: tail =>
        isCompression = false
        parse(tail)

      case ("-c" | "--compress") :: tail =>
        isCompression = true
        parse(tail)

      case value :: tail =>
        inputFile = value
        parse(tail)

      case Nil =>
        ()

      case _ =>
        printUsage()
    }
  }

  parse(args.toList)

  if (inputFile == null)
    printUsage()

  if (outputFile == null)
    if (isCompression)
      outputFile = inputFile + extension
    else
      if (inputFile.endsWith(extension))
        outputFile = inputFile.stripSuffix(extension)
      else
        printUsage()

  if (isCompression)
    compressFile(codec, inputFile, outputFile)
  else
    decompressFile(codec, inputFile, outputFile)

  def compressFile(codec: CompressionCodec, inputFile: String, outputFile: String) {
    val in = new BufferedInputStream(new FileInputStream(inputFile))
    val out = new BufferedOutputStream(new FileOutputStream(outputFile))
    copy(in, codec.createOutputStream(out))
  }

  def decompressFile(codec: CompressionCodec, inputFile: String, outputFile: String) {
    val in = new BufferedInputStream(new FileInputStream(inputFile))
    val out = new BufferedOutputStream(new FileOutputStream(outputFile))
    copy(codec.createInputStream(in), out)
  }

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
