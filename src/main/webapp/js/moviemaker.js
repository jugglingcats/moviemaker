(function () {
    'use strict';

    angular.module('moviemaker', [])
        .controller('MovieMakerCtrl', function ($scope, $http) {
            var streaming = false,
                video = document.querySelector('#video'),
                preview = _V_('preview-player'),
                canvas = document.querySelector('#canvas'),
                width = 640,
                height = 0;

            var snaps = [];
            var index = 0;
            var interval = 200;
            $scope.mode = 'grid';

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

            $scope.snaps = snaps;
            $scope.onionEnabled = true;

            var post = function (data) {
                var header = "data:image/jpeg;base64";
//            var data=snaps[0];
                if (data.indexOf(header) != 0) {
                    throw "expected '" + header + "' at start of data";
                }
                var base64data = data.substr(header.length + 1);

                $http.post('/rest/mm/post', base64data, {
                    headers: { 'Content-Type': "text/plain" },
                    transformRequest: angular.identity
                }).success(function (result) {
                        console.log("posted! " + result);
                        snaps.push(result);
//                    $scope.uploadedImgSrc = result.src;
//                    $scope.sizeInBytes = result.size;
                    }).error(function (result) {
                        console.log("error with post!");
                        $scope.errorMessage = result;
                    });

            };
        });

    angular.module('angular-auth-demo', ['http-auth-interceptor'])
    /**
     * This directive will find itself inside HTML as a class,
     * and will remove that class, so CSS will remove loading image and show app content.
     * It is also responsible for showing/hiding login form.
     */
        .directive('authDemoApplication', function () {
            return {
                restrict: 'C',
                link: function (scope, elem, attrs) {
                    //once Angular is started, remove class:
                    elem.removeClass('waiting-for-angular');

                    var login = elem.find('#login-holder');
                    var main = elem.find('#content');

                    scope.$on('event:auth-loginRequired', function () {
                        console.log("login required");
                        login.modal();

                    });
                    scope.$on('event:auth-loginConfirmed', function (event, data) {
                        scope.account=data;
                        login.hide();
                    });
                }
            }
        });

    angular.module('angular-auth-demo').controller({
        LoginController: function ($scope, $http, authService) {
            $scope.submit = function() {
                $http({
                    method: 'POST',
                    url: 'rest/mm/login',
                    data: 'username='+$scope.username+"&password="+$scope.password,
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function(result) {
                    authService.loginConfirmed(result);
                });
            }
        },
        ProjectsController: function($scope, $http) {
            $scope.testme = function() {
                $http.get('rest/mm/test').success(function(result) {
                    console.log("did test");
                });
            }
        }
    });
})();
