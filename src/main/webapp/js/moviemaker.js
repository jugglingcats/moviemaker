// TODO: if two images are identical an error is thrown

angular.module('moviemaker', [])
    .controller('MovieMakerCtrl', function ($scope, $http) {
        var streaming = false,
            video = document.querySelector('#video'),
            preview = _V_('preview-player'),
//            cover = document.querySelector('#cover'),
            canvas = document.querySelector('#canvas'),
//            photo = document.querySelector('#photo'),
//            movie = document.querySelector('#movie'),
//            startbutton = document.querySelector('#startbutton'),
//            incrbutton = document.querySelector('#incrbutton'),
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

        $scope.toggleOnion = function() {
            $scope.onionEnabled=!$scope.onionEnabled;
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
                    console.log("posted! "+result);
                    snaps.push(result);
//                    $scope.uploadedImgSrc = result.src;
//                    $scope.sizeInBytes = result.size;
                }).error(function (result) {
                    console.log("error with post!");
                    $scope.errorMessage=result;
                });

        };
    });

(function () {

    function increment() {
        setTimeout(function () {
            increment()
        }, interval);

        if (snaps.length == 0) return;

        index++;
        if (index >= snaps.length) index = 0;

        movie.setAttribute('src', snaps[index]);
    }

    incrbutton.addEventListener('click', function (ev) {
        increment();
        ev.preventDefault();
    }, false);

//    setTimeout(function() {increment()}, interval);

})();

