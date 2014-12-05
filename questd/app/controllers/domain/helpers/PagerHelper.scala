package controllers.domain.helpers

/**
 * Helps to deal with paging and paging limits.
 */
private [domain] object PagerHelper {

  /**
   * Make page of correct size correcting client's request.
   */
  def adjustedPageSize(pageSize: Int): Int = {
    val maxPageSize = 50
    val defaultPageSize = 10

    if (pageSize <= 0)
      defaultPageSize
    else if (pageSize > maxPageSize)
      maxPageSize
    else
      pageSize
  }

  /**
   * Make page number of correct number correcting client's request.
   */
  def adjustedPageNumber(pageNumber: Int): Int = {
    if (pageNumber < 0)
      0
    else
      pageNumber
  }
}
