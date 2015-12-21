package models.domain.solution

import models.domain.common.ContentReference

/**
 * Content of a solution.
 */
case class SolutionInfoContent(
  media: Option[ContentReference],
  icon: Option[ContentReference] = None,
  description: Option[String] = None)
