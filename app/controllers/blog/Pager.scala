package controllers.blog

class Pager(_postsPerPage: Int) {
  assume(_postsPerPage >= 1)
  def postsPerPage = _postsPerPage
  def countMaxPages(postCount: Int) =
    if (postCount == 0)
      1
    else
      Math.ceil(postCount.toDouble / _postsPerPage.toDouble).toInt
}
