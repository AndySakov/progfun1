package forcomp

import scala.io.{Codec, Source}

object Anagrams extends AnagramsInterface:

  /** A word is simply a `String`. */

  /** A sentence is a `List` of words. */
//  type List[String] = List[String]

  /** `List[(Char, Int)]` is a `List` of pairs of characters and positive
    * integers saying how often the character appears. This list is sorted
    * alphabetically w.r.t. to the character in each pair. All characters in the
    * occurrence list are lowercase.
    *
    * Any list of pairs of lowercase characters and their frequency which is not
    * sorted is **not** an occurrence list.
    *
    * Note: If the frequency of some character is zero, then that character
    * should not be in the list.
    */

  /** The dictionary is simply a sequence of words. It is predefined and
    * obtained as a sequence using the utility method `loadDictionary`.
    */
  val dictionary: List[String] = Dictionary.loadDictionary

  /** Converts the word into its character occurrence list.
    *
    * Note: the uppercase and lowercase version of the character are treated as
    * the same character, and are represented as a lowercase character in the
    * occurrence list.
    *
    * Note: you must use `groupBy` to implement this method!
    */
  def wordOccurrences(w: String): List[(Char, Int)] =
    w.toLowerCase
      .groupBy(identity)
      .map((x, y) => x -> y.length)
      .toList
      .sortBy(_._1)

  /** Converts a sentence into its character occurrence list. */
  def sentenceOccurrences(s: List[String]): List[(Char, Int)] =
    wordOccurrences(s.mkString(""))

  /** The `dictionaryByOccurrences` is a `Map` from different occurrences to a
    * sequence of all the words that have that occurrence count. This map serves
    * as an easy way to obtain all the anagrams of a word given its occurrence
    * list.
    *
    * For example, the word "eat" has the following character occurrence list:
    *
    * `List(('a', 1), ('e', 1), ('t', 1))`
    *
    * Incidentally, so do the words "ate" and "tea".
    *
    * This means that the `dictionaryByOccurrences` map will contain an entry:
    *
    * List(('a', 1), ('e', 1), ('t', 1)) -> Seq("ate", "eat", "tea")
    */
  lazy val dictionaryByOccurrences: Map[List[(Char, Int)], List[String]] =
    dictionary
      .map(x => wordOccurrences(x) -> x)
      .groupBy(_._1)
      .map(x => x._1 -> x._2.map(_._2))
      .withDefaultValue(Nil)

  /** Returns all the anagrams of a given word. */
  def wordAnagrams(word: String): List[String] =
    dictionaryByOccurrences(wordOccurrences(word))

  /** Returns the list of all subsets of the occurrence list. This includes the
    * occurrence itself, i.e. `List(('k', 1), ('o', 1))` is a subset of
    * `List(('k', 1), ('o', 1))`. It also include the empty subset `List()`.
    *
    * Example: the subsets of the occurrence list `List(('a', 2), ('b', 2))`
    * are:
    *
    * List( List(), List(('a', 1)), List(('a', 2)), List(('b', 1)), List(('a',
    * 1), ('b', 1)), List(('a', 2), ('b', 1)), List(('b', 2)), List(('a', 1),
    * ('b', 2)), List(('a', 2), ('b', 2)) )
    *
    * Note that the order of the occurrence list subsets does not matter -- the
    * subsets in the example above could have been displayed in some other
    * order.
    */
  def combinations(occurrences: List[(Char, Int)]): List[List[(Char, Int)]] =
    occurrences match
      case Nil => List(Nil)
      case x :: xs =>
        val (char, occurrence) = x
        val combis = for {
          comb <- combinations(xs)
          occ <- (1 to occurrence)
        } yield char -> occ :: comb
        combis ::: combinations(xs)

  /** Subtracts occurrence list `y` from occurrence list `x`.
    *
    * The precondition is that the occurrence list `y` is a subset of the
    * occurrence list `x` -- any character appearing in `y` must appear in `x`,
    * and its frequency in `y` must be smaller or equal than its frequency in
    * `x`.
    *
    * Note: the resulting value is an occurrence - meaning it is sorted and has
    * no zero-entries.
    */
  def subtract(x: List[(Char, Int)], y: List[(Char, Int)]): List[(Char, Int)] =
    x.map { case (letter, count) =>
      letter -> y
        .collectFirst { case (l, c) if l == letter => count - c }
        .getOrElse(count)
    }.filter(_._2 > 0)

  /** Returns a list of all anagram sentences of the given sentence.
    *
    * An anagram of a sentence is formed by taking the occurrences of all the
    * characters of all the words in the sentence, and producing all possible
    * combinations of words with those characters, such that the words have to
    * be from the dictionary.
    *
    * The number of words in the sentence and its anagrams does not have to
    * correspond. For example, the sentence `List("I", "love", "you")` is an
    * anagram of the sentence `List("You", "olive")`.
    *
    * Also, two sentences with the same words but in a different order are
    * considered two different anagrams. For example, sentences `List("You",
    * "olive")` and `List("olive", "you")` are different anagrams of `List("I",
    * "love", "you")`.
    *
    * Here is a full example of a sentence `List("Yes", "man")` and its anagrams
    * for our dictionary:
    *
    * List( List(en, as, my), List(en, my, as), List(man, yes), List(men, say),
    * List(as, en, my), List(as, my, en), List(sane, my), List(Sean, my),
    * List(my, en, as), List(my, as, en), List(my, sane), List(my, Sean),
    * List(say, men), List(yes, man) )
    *
    * The different sentences do not have to be output in the order shown above
    * \- any order is fine as long as all the anagrams are there. Every returned
    * word has to exist in the dictionary.
    *
    * Note: in case that the words of the sentence are in the dictionary, then
    * the sentence is the anagram of itself, so it has to be returned in this
    * list.
    *
    * Note: There is only one anagram of an empty sentence.
    */
  def sentenceAnagrams(sentence: List[String]): List[List[String]] =
    def occurrencesAnagrams(occ: List[(Char, Int)]): List[List[String]] =
      if occ.isEmpty then List(Nil)
      else
        for
          sub <- combinations(occ) // For each sub set of occurrences...
          word <- dictionaryByOccurrences(
            sub
          ) // for each word associated to that sub set
          // if (!word.isEmpty) //skip if word is empty
          sentence <- occurrencesAnagrams(
            subtract(occ, sub)
          ) // for each sentence generated with the rest of occurrences
        yield word :: sentence // yield a new sentence

    occurrencesAnagrams(sentenceOccurrences(sentence))

object Dictionary:
  def loadDictionary: List[String] =
    val wordstream = Option {
      getClass.getResourceAsStream(
        List("forcomp", "linuxwords.txt").mkString("/", "/", "")
      )
    } getOrElse {
      sys.error("Could not load word list, dictionary file not found")
    }
    try
      val s = Source.fromInputStream(wordstream)(Codec.UTF8)
      s.getLines().toList
    catch
      case e: Exception =>
        println("Could not load word list: " + e)
        throw e
    finally wordstream.close()
