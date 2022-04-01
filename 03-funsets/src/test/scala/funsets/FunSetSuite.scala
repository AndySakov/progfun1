package funsets

/** This class is a test suite for the methods in object FunSets.
  *
  * To run this test suite, start "sbt" then run the "test" command.
  */
class FunSetSuite extends munit.FunSuite:

  import FunSets.*

  test("contains is implemented") {
    assert(contains(x => true, 100))
  }

  /** When writing tests, one would often like to re-use certain values for
    * multiple tests. For instance, we would like to create an Int-set and have
    * multiple test about it.
    *
    * Instead of copy-pasting the code for creating the set into every test, we
    * can store it in the test class using a val:
    *
    * val s1 = singletonSet(1)
    *
    * However, what happens if the method "singletonSet" has a bug and crashes?
    * Then the test methods are not even executed, because creating an instance
    * of the test class fails!
    *
    * Therefore, we put the shared values into a separate trait (traits are like
    * abstract classes), and create an instance inside each test method.
    */

  trait TestSets:
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = singletonSet(3)
    val arePositive = (x: Int) => x > 0
    val areNegative = (x: Int) => x < 0
    val areEven = (x: Int) => x % 2 == 0
    val areOdd = (x: Int) => x % 2 != 0

  /** This test is currently disabled (by using @Ignore) because the method
    * "singletonSet" is not yet implemented and the test would fail.
    *
    * Once you finish your implementation of "singletonSet", remove the .ignore
    * annotation.
    */
  test("singleton set one contains one") {

    /** We create a new instance of the "TestSets" trait, this gives us access
      * to the values "s1" to "s3".
      */
    new TestSets:
      /** The string argument of "assert" is a message that is printed in case
        * the test fails. This helps identifying which assertion failed.
        */
      assert(contains(s1, 1), "Singleton")
  }

  test("union contains all elements of each set") {
    new TestSets:
      val s = union(s1, s2)
      assert(contains(s, 1), "Union 1")
      assert(contains(s, 2), "Union 2")
      assert(!contains(s, 3), "Union 3")
  }

  test("intersect contains elements present in both sets") {
    new TestSets:
      val s = intersect(arePositive, areOdd)
      assert(contains(s, 1), "Intersect 1")
      assert(!contains(s, 2), "Intersect 2")
      assert(contains(s, 3), "Intersect 3")
  }

  test(
    "difference contains elements present in only the first of the two sets"
  ) {
    new TestSets:
      val s = diff(s1, s2)
      assert(contains(s, 1), "Intersect 1")
      assert(!contains(s, 2), "Intersect 2")
      assert(!contains(s, 3), "Intersect 3")
  }

  test("filter contains elements present in both the super and sub sets") {
    new TestSets:
      val s = filter(arePositive, areOdd)
      assert(contains(s, 1), "Filter 1")
      assert(!contains(s, 2), "Filter 2")
      assert(contains(s, 3), "Filter 3")
  }

  test("forall positiveNumbers will not satisfy the predicate of evenness") {
    new TestSets:
      assert(!forall(arePositive, areOdd), "Forall positive numbers")
  }

  test("there are oddNumbers in the set of positive numbers") {
    new TestSets:
      assert(
        exists(arePositive, areOdd),
        "Odd numbers in the set of positive numbers"
      )
  }

  test("All positive numbers will satisfy the predicate of evenness when mapped to the number times two") {
    new TestSets:
      val preMap = forall(arePositive, areEven)
      val postMap = exists(map(arePositive, x => x*2), areEven)
      assert(!preMap, "Not all positive numbers will be even")
      assert(postMap, "All positive numbers multiplied by two will be even")
  }

  import scala.concurrent.duration.*
  override val munitTimeout = 10.seconds
