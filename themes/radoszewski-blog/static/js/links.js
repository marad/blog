$(function() {
  function isExternalLink(link) {
    return link.href.startsWith("http") && !link.href.startsWith(document.location.origin);
  }

  function makeOpeningInNewWindow(link) {
    link.target = "about:blank";
  }

  $("a").each(function(index, link) {
    if (isExternalLink(link)) {
      makeOpeningInNewWindow(link);
    }
  });
});
