angular.module('moviemaker', ['imageupload'])
    .controller('MovieMakerCtrl', function ($scope, $http) {
        var streaming = false,
            video = document.querySelector('#video'),
            cover = document.querySelector('#cover'),
            canvas = document.querySelector('#canvas'),
            photo = document.querySelector('#photo'),
            movie = document.querySelector('#movie'),
            startbutton = document.querySelector('#startbutton'),
            incrbutton = document.querySelector('#incrbutton'),
            width = 200,
            height = 0;

        var snaps = [];
        var index = 0;
        var interval = 200;

        navigator.getMedia = ( navigator.getUserMedia ||
            navigator.webkitGetUserMedia ||
            navigator.mozGetUserMedia ||
            navigator.msGetUserMedia);

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
                video.setAttribute('width', width);
                video.setAttribute('height', height);
                canvas.setAttribute('width', width);
                canvas.setAttribute('height', height);
                streaming = true;
            }
        }, false);

        $scope.snap = function () {
            canvas.width = width;
            canvas.height = height;
            canvas.getContext('2d').drawImage(video, 0, 0, width, height);
            var data = canvas.toDataURL('image/jpeg');
            snaps.push(data);
            photo.setAttribute('src', data);
        }

        $scope.single = function (image) {
            var header="data:image/jpeg;base64";
            var data=snaps[0];
            if ( data.indexOf(header) != 0 ) {
                throw "expected '"+header+"' at start of data";
            }
            var base64data=data.substr(header.length+1);

            console.log("posting data... " + base64data);

//            var formData = new FormData();
//            formData.append('image', image);

            $http.post('/rest/upload/single', base64data, {
                headers: { 'Content-Type': "text/plain" },
                transformRequest: angular.identity
            }).success(function (result) {
                    $scope.uploadedImgSrc = result.src;
                    $scope.sizeInBytes = result.size;
                });

            console.log("posted!");
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

