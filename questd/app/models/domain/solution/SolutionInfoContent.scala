package models.domain.solution

import models.domain.common.ContentReference

/**
 * Content of a solution.
 */
case class SolutionInfoContent(
  media: ContentReference,
  icon: Option[ContentReference] = None,
  description: String = "") // TODO: remove default value in 0.50.02 after cleaning up existing database.
