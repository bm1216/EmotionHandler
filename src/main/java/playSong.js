
function playSong(mood){
    var emotion = mood;
    var albumMood = "null";

    switch(emotion) {
        case "anger":
             albumMood = "spotify:user:spotify:playlist:37i9dQZF1DX3YSRoSdA634";
             break;
        case "happiness":
             albumMood = "spotify:user:filtr.ie:playlist:3B31InoJ3c1hE8sIgQiJnT";
             break;
        case "sadness":
             albumMood = "spotify:user:spotify:playlist:37i9dQZF1DX3YSRoSdA634";
             break;
        case "contempt":
             albumMood = "spotify:user:spotify:playlist:37i9dQZF1DWZ0Y50OtuhLO";
             break;
        case "angry":
             albumMood = "spotify:user:spotify:playlist:5s7Sp5OZsw981I2OkQmyrz";
             break;
        case "fear":
             albumMood = "spotify:user:spotify:playlist:37i9dQZF1DXcBbGCLlic3p";
             break;
        default:
             albumMood = "spotify:user:spotify:playlist:37i9dQZF1DX2czWA9hqErK";
             break;
    }

    var Spotify = require('spotify-web-api-js');
    var s = new Spotify();

    var clientID = "dc666fe2d7c54d038e6d8bb4c4095704";
    var clientSecret = "100b28712cba4d699fc4ff543039d259";
     var access_token = "BQCt4Sbgse-eSnUSH6Bi1ufbQXRaz85XJrh_ddvKGKw-KgMPVVrg0Qn3OHXtbDjPIhwaIAbdyrv0AMJ5q0hMYZb0-R_GCWDgbyTEiARXuHnWcz-uIZboGoHRs1bBvxjdGaFPnNkMqVepksPJ1NB2nCv9h8ogDg";


    return app.playTracks = () => $.ajax({
        url: 'https://api.spotify.com/v1/me/player/play',
        headers:
            { 'Authorization': 'Bearer ' + access_token },
                method: 'PUT',
                dataType: 'json',
            body: {
                "context_uri": albumMood,
            },
//                success: function(result) {
//                console.log("success");
//                console.log(result);
//            }
        }
    );
}