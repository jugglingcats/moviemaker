(function () {
    var MM = angular.module('moviemaker', ['login-ui', 'ngResource']);

    MM.controller('MovieMakerCtrl', function ($scope, $http, $location, $resource) {
        var Project = $resource('rest/mm/project/:projectId', { projectId: '@projectId' });

        $scope.projectId=($location.search()).project;
        if ( $scope.projectId == undefined ) {
            throw "Project not passed to page!";
        }

        var streaming = false,
            video = document.querySelector('#video'),
            canvas = document.querySelector('#canvas'),
            width = 640,
            height = 0;

        var index = 0;
        var interval = 200;
        $scope.mode = 'grid';
        $scope.onionEnabled = true;

        $scope.project=Project.get({projectId: $scope.projectId});
//        var snaps = $scope.project.frames || [];
        console.log($scope.project);

        navigator.getMedia = ( navigator.getUserMedia ||
            navigator.webkitGetUserMedia ||
            navigator.mozGetUserMedia ||
            navigator.msGetUserMedia);

//        preview.addEvent("error", function() {
//            $scope.errorMessage="There was a video error";
//        });

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
            }
        }, false);

        $scope.toggleOnion = function () {
            $scope.onionEnabled = !$scope.onionEnabled;
        }

        $scope.snap = function () {
            canvas.width = width;
            canvas.height = height;
            canvas.getContext('2d').drawImage(video, 0, 0, width, height);
            var data = canvas.toDataURL('image/jpeg');
            post(data);
//            photo.setAttribute('src', data);
        }

        $scope.keydown = function ($event) {
            if ($event.keyCode == 32) {
                $scope.snap();
                $event.stopPropagation();
                $event.preventDefault();
            }
        }

        var post = function (data) {
            var header = "data:image/jpeg;base64";
            if (data.indexOf(header) != 0) {
                throw "expected '" + header + "' at start of data";
            }
            var base64data = data.substr(header.length + 1);

            $http.post('/rest/mm/post/'+$scope.projectId, base64data, {
                headers: { 'Content-Type': "text/plain" },
                transformRequest: angular.identity
            }).success(function (result) {
                    console.log("posted! " + result);
                    $scope.project.frames.push(result);
//                    $scope.uploadedImgSrc = result.src;
//                    $scope.sizeInBytes = result.size;
                }).error(function (result) {
                    console.log("error with post!");
                    $scope.errorMessage = result;
                });

        };
    });

    MM.controller({
        ProjectsController: function ($scope, $rootScope, $http, $resource, $window) {
            var Project = $resource('rest/mm/project/:projectId', { projectId: '@projectId' });
            var ProjectList = $resource('rest/mm/project/list');
            var Account = $resource('rest/mm/account');

            function refreshProjects() {
                ProjectList.query(function(list) {$scope.projects=list;});
//                $scope.projects = projects;
            }

            $rootScope.$watch('controllers', function () {
                if ( $rootScope.controllers && $rootScope.controllers.length == 1 ) {
                    // init the account (if logged in)
                    $scope.account=Account.get();
                    refreshProjects();
                }
            });

            $scope.$on('event:auth-loginConfirmed', function (event, data) {
                $scope.account = data;
            });

            $scope.testme = function () {
                console.log("test pressed");
                $http.get('rest/mm/test').success(function (result) {
                    console.log("done test");
                });
            }
            $scope.logout = function () {
                $http.post('rest/mm/logout').success(function (result) {
                    $scope.account = undefined;
                    $window.location.reload();
                });
            };

            $scope.createProject = function() {
                console.log("new project");
                var project=new Project();
                project.name=$scope.newProjectName;
                project.$save(function() {
                    refreshProjects();
                });
            }
        }
    });
})();