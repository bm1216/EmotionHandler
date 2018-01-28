
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
        default "neutral":
             albumMood = "spotify:user:spotify:playlist:37i9dQZF1DX2czWA9hqErK";
             break;
    }

    var clientID = "dc666fe2d7c54d038e6d8bb4c4095704";
    var clientSecret = "100b28712cba4d699fc4ff543039d259";

    reuturn app.playTracks = () => $.ajax({
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