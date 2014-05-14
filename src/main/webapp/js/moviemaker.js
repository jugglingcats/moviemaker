(function () {
    var MM = angular.module('moviemaker', ['login-ui', 'ngResource']);

    MM.controller('MovieMakerCtrl', function ($scope, $http, $routeParams, $resource, $timeout) {
        var Project = $resource('rest/mm/project/:projectId', { projectId: '@projectId' });

        $scope.previewImage = 0;

        $scope.projectId = $routeParams.projectId;
        if ($scope.projectId == undefined) {
            throw "Project not passed to page!";
        }

        var streaming = false,
            video = document.querySelector('#video'),
            canvas = document.querySelector('#canvas'),
            width = 640,
            height = 0;

        var index = 0;
        var interval = 200;
        var timerId;

        $scope.mode = 'grid';
        $scope.onionEnabled = true;
        $scope.rotated = false;

        $scope.project = Project.get({projectId: $scope.projectId});
        console.log($scope.project);

        navigator.getMedia = ( navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia );

        if ( !navigator.getMedia ) {
            throw "Failed to get media!";
        }

        navigator.getMedia(
            {
                video: true,
                audio: false
            },
            function (stream) {
                if (navigator.mozGetUserMedia) {
                    video.mozSrcObject = stream;
                } else {
                    var vendorURL = window.URL || window.webkitURL;
                    video.src = vendorURL ? vendorURL.createObjectURL(stream) : stream;
                }
                video.play();
            },
            function (err) {
                console.log("An error occured! " + err);
            }
        );

        video.addEventListener('canplay', function (ev) {
            if (!streaming) {
                height = video.videoHeight / (video.videoWidth / width);
                console.log("height of video is: " + height);
                video.setAttribute('width', width);
                video.setAttribute('height', height);
                canvas.setAttribute('width', width);
                canvas.setAttribute('height', height);
                streaming = true;
                $timeout(function() {
                    $scope.webcamEnabled = true;
                }, 50);
            }
        }, false);

        $scope.toggleOnion = function () {
            $scope.onionEnabled = !$scope.onionEnabled;
        }

        $scope.toggleRotate = function () {
            $scope.rotated = !$scope.rotated;
        }

        $scope.togglePlay = function () {
            $scope.autoPlay = !$scope.autoPlay;
        }

        $scope.incrFps = function () {
            if ($scope.project) {
                $scope.project.fps++;
                $scope.project.$save();
            }
        }

        $scope.decrFps = function () {
            if ($scope.project && $scope.project.fps > 1) {
                $scope.project.fps--;
                $scope.project.$save();
            }
        }

        $scope.$watch('project.fps', function (newval) {
            console.log("fps changed: " + newval);
            if (!newval) {
                return;
            }
            if (timerId) {
                clearInterval(timerId);
            }
            timerId = setInterval(function () {
                if ($scope.autoPlay) {
                    $scope.previewImage++;
                    if ($scope.previewImage >= $scope.project.frames.length) {
                        $scope.previewImage = 0;
                    }
                    $scope.$apply();
                }
            }, 1000 / newval);
        })

        $scope.snap = function () {
            canvas.width = width;
            canvas.height = height;
            var context = canvas.getContext('2d');
            if ($scope.rotated) {
                context.translate(width / 2, height / 2);
                context.rotate(Math.PI);
                context.drawImage(video, -width / 2, -height / 2, width, height);
            } else {
                context.drawImage(video, 0, 0, width, height);
            }
            var data = canvas.toDataURL('image/jpeg');
            post(data);
        }

        $scope.keydown = function ($event) {
            if ($event.keyCode == 32) {
                $scope.snap();
                $event.stopPropagation();
                $event.preventDefault();
            }
        }

        $scope.select = function (index) {
            $scope.selectedImage = index;
            $scope.previewImage = index;
        }

        $scope.deleteImage = function () {
            console.log("delete: " + $scope.selectedImage);
            var Frame = $resource('rest/mm/delete/:projectId/:frameNum', { projectId: '@projectId', frameNum: '@frameNum' });
            Frame.delete({projectId: $scope.projectId, frameNum: $scope.selectedImage}, function () {
                $scope.project.frames.splice($scope.selectedImage, 1);
            });
        };

        var post = function (data) {
            var header = "data:image/jpeg;base64";
            if (data.indexOf(header) != 0) {
                throw "expected '" + header + "' at start of data";
            }
            var base64data = data.substr(header.length + 1);

            $http.post('rest/mm/post/' + $scope.projectId, base64data, {
                headers: { 'Content-Type': "text/plain" },
                transformRequest: angular.identity
            }).success(function (result) {
                console.log("posted! " + result);
                $scope.project.frames.push(result);
            }).error(function (result) {
                console.log("error with post!");
                $scope.errorMessage = result;
            });

        };
    });

    MM.controller('ProjectsCtrl', function ($scope, $rootScope, $http, $resource, $window, accountService) {
        var Project = $resource('rest/mm/project/:projectId', { projectId: '@projectId' });
        var ProjectList = $resource('rest/mm/project/list');

        function refreshProjects() {
            ProjectList.query(function (list) {
                $scope.projects = list;
            });
        }

        $rootScope.$watch('controllers', function () {
            if ($rootScope.controllers && $rootScope.controllers.length == 1) {
                refreshProjects();
            }
        });

        $scope.moment = function (d) {
            return moment(d).fromNow();
        };

        $scope.createProject = function () {
            console.log("new project");
            var project = new Project();
            project.name = $scope.newProjectName;
            project.$save(function () {
                refreshProjects();
            });
        };

        $scope.delete = function (projectId) {
            if ($window.confirm("Are you sure you want to delete this project?")) {
                Project.delete({projectId: projectId}, function () {
                    refreshProjects();
                });
            }
        }
    });

    MM.controller('WelcomeCtrl', function ($scope) {
    });

    MM.config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/welcome', {templateUrl: 'partials/welcome.html', controller: 'WelcomeCtrl'})
            .when('/projects', {templateUrl: 'partials/projects.html', controller: 'ProjectsCtrl'})
            .when('/project/:projectId', {templateUrl: 'partials/editor.html', controller: 'MovieMakerCtrl'})
            .otherwise({redirectTo: '/welcome'})
    }]);

    MM.factory('accountService', function () {
        var accountInfo = {};
        return accountInfo;
    })

    MM.directive('navbar', function () {
        return {
            restrict: 'A',
            replace: true,
            scope: { },
            templateUrl: 'partials/navbar.html',
            controller: function ($scope, $http, $resource, accountService) {
                var Account = $resource('rest/mm/account');

                // init the account (if logged in)
                accountService.account = Account.get();

                $scope.$on('event:auth-loginConfirmed', function (event, data) {
                    console.log("login confirmed...");
                    accountService.account = data;
                });

                $scope.logout = function () {
                    $http.post('rest/mm/logout').success(function (result) {
                        accountService.account = undefined;
                        $window.location.reload();
                    });
                };

                $scope.accountService = accountService;
            }
        };
    });
    MM.directive('mmFocus', function () {
        return function (scope, elem, attrs) {
            elem.bind('focus', function () {
                scope.$apply(attrs.mmFocus);
            });
        };
    });
    MM.directive('mmBlur', function () {
        return function (scope, elem, attrs) {
            elem.bind('blur', function () {
                scope.$apply(attrs.mmBlur);
            });
        };
    });
})();