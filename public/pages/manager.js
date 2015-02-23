var app = angular.module("app", [])

app.controller("ManagerCtl", ["$scope", "$http", "$q", function($scope, $http, $q) {
  $scope.pageCount = 1;
  $scope.currentPage = 1;
  $scope.posts = [];

  $scope.getPage = function(page) {
    if (page >= 1 && page <= $scope.pageCount) {
      $q.all([$http.get("manager/page/count"), $http.get("manager/page/" + page)]).then(function(result) {
        $scope.pageCount = result[0].data.pageCount;
        $scope.posts = result[1].data;
        $scope.currentPage = page;
      }, function(reason) {
        console.err('Fetch failed: ' + reason);
      });
    }
  };

  $scope.getPage(1);

  $scope.times = function(num) {
    var t = [];
    for(var i=0; i < num; i++) t.push(i);
    return t;
  };
}]);
