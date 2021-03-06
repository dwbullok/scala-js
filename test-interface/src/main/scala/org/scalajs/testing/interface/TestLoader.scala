package org.scalajs.testing.interface

import java.io._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobalScope
import scala.scalajs.reflect.Reflect

import org.scalajs.testing.common.IsolatedTestSet

import sbt.testing._

private[scalajs] object TestLoader {
  def loadTests(tests: IsolatedTestSet): Seq[(Framework, Seq[TaskDef])] = {
    for {
      nameAlternatives <- tests.testFrameworkNames
      framework <- FrameworkLoader.tryLoadFramework(nameAlternatives).toList
    } yield {
      val fingerprints = framework.fingerprints()
      val eligibleTaskDefs = tests.definedTests.filter(taskDef =>
          fingerprints.exists(fingerprintMatches(_, taskDef.fingerprint)))
      (framework, eligibleTaskDefs.toSeq)
    }
  }

  // Copied from sbt.TestFramework
  private def fingerprintMatches(a: Fingerprint, b: Fingerprint): Boolean = {
    (a, b) match {
      case (a: SubclassFingerprint, b: SubclassFingerprint) =>
        a.isModule == b.isModule && a.superclassName == b.superclassName

      case (a: AnnotatedFingerprint, b: AnnotatedFingerprint) =>
        a.isModule == b.isModule && a.annotationName == b.annotationName

      case _ => false
    }
  }
}
