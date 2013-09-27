angular.module('login-ui', ['http-auth-interceptor'])
    .directive('loginForm', function () {
        return {
            restrict: 'A',
            replace: true,
            scope: { },
            templateUrl: 'partials/login.html',
            controller: function ($scope, $http, authService) {
                $scope.submit = function () {
                    // TODO: guard against code injection
                    $http({
                        method: 'POST',
                        url: 'rest/mm/login',
                        data: 'username=' + $scope.username + "&password=" + $scope.password,
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                    }).success(function (result) {
                            authService.loginConfirmed(result);
                        });
                }
            },
//            compile: function(element, attrs) {
//                console.log("compile for directive");
//            },
            link: function ($rootScope, element, attrs) {
                console.log("login directive init")

                $rootScope.$on('event:auth-loginRequired', function () {
                    console.log("login required");
                    element.modal();

                });
                $rootScope.$on('event:auth-loginConfirmed', function (event, data) {
                    element.modal('hide');
                });
            }
        };
    });
