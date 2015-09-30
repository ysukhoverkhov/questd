package models.domain.crawlercontext

import models.domain.base.ID

/**
 * Context for crawlers they can use for crawling.
 *
 * Created by Yury on 07.08.2015.
 */
case class CrawlerContext (
  id: String,
  params: Map[String, String]
  ) extends ID
