package models.store.mongo.dao

import models.domain.crawlercontext.CrawlerContext
import models.store.dao._
import models.store.mongo.helpers._

/**
 * DOA for CrawlerContext objects
 */
private[mongo] class MongoCrawlerContextDAO
  extends BaseMongoDAO[CrawlerContext](collectionName = "crawlercontexts")
  with CrawlerContextDAO {

}

