package org.specs2
package specification
package dsl
package mutable

import org.specs2.execute.AsResult
import control.{ImplicitParameters, Use}
import ImplicitParameters._
import org.specs2.specification.core._
import org.specs2.specification.script.StepParser
import scala.implicits.Not

/**
 * Dsl for creating examples in a mutable specification
 */
trait ExampleDsl extends ExampleDsl1 with dsl.ExampleDsl:

  extension (d: String)(using not: Not[NoBangExamples])
    override def !(execution: Execution): Fragment =
      addFragment(fragmentFactory.example(Text(d), execution))

  extension [R : AsResult](d: String)(using not: Not[NoBangExamples])
    override def !(r: => R): Fragment =
      addFragment(fragmentFactory.example(d, r))

    override def !(r: String => R): Fragment =
      addFragment(fragmentFactory.example(d, r))

  extension [R : AsResult](d: String)(using not: Not[NoBangExamples])
    override def !(r: Env => R)(using p: ImplicitParam): Fragment =
      addFragment(fragmentFactory.example(d, r)(summon[AsResult[R]], p))

private[specs2]
trait ExampleDsl1 extends BlockDsl with ExampleDsl0:
  // deactivate block0
  override def blockExample0(d: String) = super.blockExample0(d)

  implicit def blockExample(d: String): BlockExample =
    new BlockExample(d)

  class BlockExample(d: String) extends BlockExample0(d):
    def >>[R](f: String => R)(implicit asExecution: AsExecution[R]): Fragment =
      >>(asExecution.execute(f(d)))

    def >>(execution: Execution): Fragment =
      addFragment(fragmentFactory.example(Text(d), execution))
      addFragment(fragmentFactory.break)

    def >>[R: AsResult](parser: StepParser[R]): Fragment =
      addFragment(
        fragmentFactory.example(Text(parser.strip(d)),
                                Execution.executed(parser.run(d).fold(execute.Error.apply, AsResult(_)))))
      addFragment(fragmentFactory.break)

    def in[R](f: String => R)(implicit ar: AsExecution[R]): Fragment =
      >>(f)(ar)

    def in(f: =>Fragment): Fragment =
      describe(d) >> f

    def in(fs: =>Fragments)(implicit p1: ImplicitParam1): Fragments =
      describe(d).>>(fs)(p1)

    def in[R: AsResult](parser: StepParser[R]): Fragment =
      d.>>(parser)

    def in(execution: Execution): Fragment =
      d >> execution

/**
 * Lightweight ExampleDsl trait
 */
private[specs2]
trait ExampleDsl0 extends BlockCreation:
  implicit def blockExample0(d: String): BlockExample0 = new BlockExample0(d)

  class BlockExample0(d: String):
    def >>(f: =>Fragment): Fragment =
      addBlock(d, f)

    def >>(fs: =>Fragments)(implicit p1: ImplicitParam1): Fragments =
      Use.ignoring(p1) { addBlock(d, fs) }

    def >>[R : AsExecution](r: =>R): Fragment =
      addFragment(fragmentFactory.example(Text(d), AsExecution.apply[R].execute(r)))
      addFragment(fragmentFactory.break)

    def should(f: => Fragment): Fragment =
      addBlock(s"$d should", f)

    def should(fs: => Fragments)(implicit p1: ImplicitParam1): Fragments =
      Use.ignoring(p1) { addBlock(s"$d should", fs) }

    def can(fs: => Fragments)(implicit p1: ImplicitParam1): Fragments =
      Use.ignoring(p1) { addBlock(s"$d can", fs) }

    def can(f: => Fragment): Fragment =
      addBlock(s"$d can", f)

    def in[R : AsExecution](r: =>R): Fragment = d >> r

/** deactivate the ExampleDsl implicits */
trait NoExampleDsl extends ExampleDsl:
  given NoExampleDsl = ???
