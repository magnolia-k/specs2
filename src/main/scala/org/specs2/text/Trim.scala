package org.specs2
package text
import scala.util.matching.Regex

/**
 * Utility methods for trimming text
 */
private[specs2]
trait Trim extends control.Debug {
  /** add trimming methods to a String */
  implicit def trimmed(s: String): Trimmed = new Trimmed(s)

  class Trimmed(s: String) {
    
    def trimStart(start: String) =
      if (s.trim.startsWith(start)) s.trim.drop(start.size) else s.trim
	  
    def trimEnd(end: String) =
      if (s.trim.endsWith(end)) s.trim.dropRight(end.size)  else s.trim
    
	  def trimEnclosing(start: String, end: String) = trimStart(start).trimEnd(end).trim
	  
	  def trimEnclosingXmlTag(t: String) = trimFirst("<"+t+".*?>").trimEnd("</"+t+">")
	  
    def trimNewLines = Seq("\r", "\n").foldLeft(s) { (res, cur) =>
      res.trimStart(cur).trimEnd(cur)
    }
	
    def trimFirst(exp: String) = new Regex(exp).replaceFirstIn(s.trim, "")

    def trimReplace(pairs: Pair[String, String]*) = pairs.foldLeft(s.trim) { (res, cur) =>
      res.replace(cur._1, cur._2)
    } 
    def trimReplaceAll(pairs: Pair[String, String]*) = pairs.foldLeft(s.trim) { (res, cur) =>
      res.replaceAll(cur._1, cur._2)
    } 
    def replaceAll(pairs: Pair[String, String]*) = pairs.foldLeft(s) { (res, cur) =>
      res.replaceAll(cur._1, cur._2)
    } 
    def remove(toRemove: String*) = toRemove.foldLeft(s) { (res, cur) => res.replace(cur, "") }
    def removeAll(toRemove: String*) = toRemove.foldLeft(s) { (res, cur) => res.replaceAll(cur, "") }
  }
}
private[specs2]
object Trim extends Trim
