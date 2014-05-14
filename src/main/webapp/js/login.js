angular.module('login-ui', ['http-auth-interceptor'])
    .directive('loginForm', function () {
        return {
            restrict: 'A',
            replace: true,
            scope: { },
            templateUrl: 'partials/login.html',
            controller: function ($rootScope, $scope, $http, $timeout, authService) {
                $scope.form={};

                $scope.validate = function() {

                }

                $scope.submit = function () {
                    if ( !$scope.login_register.$valid ) {
                        // let angular show errors
                        return;
                    }
                    $scope.form.error = undefined;
                    $scope.form.help = false;
                    $http({
                        method: 'POST',
                        url: 'rest/mm/login',
                        data: 'username=' + $scope.form.username + "&password=" + $scope.form.password,
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                    }).success(function (result) {
                        authService.loginConfirmed(result);
                    }).error(function (result) {
                        $scope.form.error = result;
                    });
                };

                $scope.register = function () {
                    $scope.form.error = undefined;
                    $scope.form.help = false;
                    $scope.form.registerMode = true;
                };

                $scope.registerNow = function () {
                    if ( !$scope.login_register.$valid ) {
                        // let angular show errors
                        return;
                    }
                    $scope.form.passwordMismatch = $scope.form.password != $scope.form.passwordConfirm;
                    if ( !$scope.form.passwordMismatch ) {
                        $scope.form.error = undefined;
                        $scope.form.help = false;
                        $http({
                            method: 'POST',
                            url: 'rest/mm/register',
                            data: 'username=' + $scope.form.username + "&password=" + $scope.form.password,
                            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                        }).success(function (result) {
                            $scope.form.registerSuccess=true;
                            $timeout(function() {
                                authService.loginConfirmed(result);
                            }, 2000);
                        }).error(function (result) {
                            $scope.form.error = result;
                        });
                    }
                };

                $rootScope.controllers = ($rootScope.controllers || []);
                $rootScope.controllers.push('login-ui');
            },
//            compile: function(element, attrs) {
//                console.log("compile for directive");
//            },
            link: function ($rootScope, element, attrs) {
                $rootScope.$on('event:auth-loginRequired', function () {
                    console.log("login required");
                    $rootScope.form={};
                    element.modal();

                });
                $rootScope.$on('event:auth-loginConfirmed', function (event, data) {
                    element.modal('hide');
                });
            }
        };
    });
