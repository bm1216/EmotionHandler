# EmotionHandler
Personal project that uses Microsoft congnitive services API along with Spotify API to capture emotions in a face, and use it to recommend songs and playlists on Spotify.

# API keys
To add your own custom keys to this project, create a class called APIKeys in src/main/java/ with static fields pertaining to each key.

For example: 

`static String REGION = "https://westeurope.api.cognitive.microsoft.com/face/v1.0/detect";`

There are 4 keys you need to add: 

* `APIKeys.SUBSCRIPTION_KEY`

* `APIKeys.REGION`

* `APIKeys.CLIENT_ID`

* `APIKeys.SECRET`
