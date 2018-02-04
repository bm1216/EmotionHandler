import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.github.sarxos.webcam.WebcamViewer;
import com.github.sarxos.webcam.util.ImageUtils;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.IO;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.PlaylistRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.Playlist;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.github.sarxos.webcam.Webcam;


public class Tracker {
  // This sample uses the Apache HTTP client library(org.apache.httpcomponents:httpclient:4.2.4)
// and the org.json library (org.json:json:20170516).

  // **********************************************
  // *** Update or verify the following values. ***
  // **********************************************


  /**
   * To update these values, create a class called APIKeys and create respective static fields.
   * For example:
   * static String REGION = "https://westeurope.api.cognitive.microsoft.com/face/v1.0/detect"
   */

  // Replace the subscriptionKey string value with your valid subscription key.
  public static final String subscriptionKey = APIKeys.SUBSCRIPTION_KEY;

  /**
   * Replace or verify the region.
   *
   * You must use the same region in your REST API call as you used to obtain your subscription
   * keys. For example, if you obtained your subscription keys from the westus region, replace
   * "westcentralus" in the URI below with "westus".
   *
   * Example : "https://westeurope.api.cognitive.microsoft.com/face/v1.0/detect"
   * NOTE: Free trial subscription keys are generated in the westcentralus region, so if you are using a free trial
   * subscription key, you should not need to change this region.
   **/
  private static final String uriBase = APIKeys.REGION;

  //Spotify client ID.
  private static final String clientId = APIKeys.CLIENT_ID;

  //Spotify client secret id.
  private static final String clientSecret = APIKeys.SECRET;

  private static Map<Moods, Double> emotions = new HashMap<>();

  private static List<String> songs = new ArrayList<>();


  private static List<Moods> getMood() {
    Double max = 0.0;
    List<Moods> list = new ArrayList<>();

    for (Moods key : emotions.keySet()) {
      if (emotions.get(key) >= 0.9) {
        list.add(key);
      } else if (emotions.get(key) > 0.1) {
        list.add(key);
      }

      if (list.size() == 2) {
        return list;
      }
    }

    if (list.isEmpty()) {
      for (Double value : emotions.values()) {
        if (value > max) {
          max = value;
        }
      }

      for (Moods key : emotions.keySet()) {
        if (emotions.get(key).equals(max)) {
          list.add(key);
        }
      }
    }
    return list;
  }

  /**
   * Takes a picture using the computer's webcam and stores it locally as filename "test3.png"
   */
  private static void takePicture() {
    Webcam webcam = Webcam.getDefault();
    Dimension[] d = webcam.getDevice().getResolutions();
    webcam.setViewSize(new Dimension(d[d.length - 1].width, d[d.length - 1].height));
    webcam.open();
    BufferedImage image = webcam.getImage();
    try {
      ImageIO.write(image, "PNG", new File("test3.png"));
    } catch (IOException e) {
      System.out.println("Could not take the image.");
    }
    webcam.close();
  }

  /**
   * Analyzes the picture, extracting the emotions and connects to Spotify, returning a string
   * containing the playlist data.
   *
   * @return String containing the playlist and the songs to be played.
   */
  private static String useApi() {
    HttpClient httpclient = new DefaultHttpClient();
    StringBuilder sb = new StringBuilder();

    try {
      URIBuilder builder = new URIBuilder(uriBase);

      // Request parameters. All of them are optional.
      builder.setParameter("returnFaceId", "true");
      builder.setParameter("returnFaceLandmarks", "false");
      builder.setParameter("returnFaceAttributes",
          "age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise");

      // Prepare the URI for the REST API call.
      URI uri = builder.build();
      HttpPost request = new HttpPost(uri);

      // Request headers.
      //request.setHeader("Content-Type", "application/json");
      request.setHeader("Content-Type", "application/octet-stream");
      request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

      // Request body.
      //StringEntity reqEntity = new StringEntity(
      //  "{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/c/c3/RH_Louise_Lillian_Gish.jpg\"}");
      File file = new File("test3.png");

      FileEntity reqEntity = new FileEntity(file, ContentType.APPLICATION_OCTET_STREAM);
      request.setEntity(reqEntity);

      // Execute the REST API call and get the response entity.
      HttpResponse response = httpclient.execute(request);
      HttpEntity entity = response.getEntity();

      if (entity != null) {

        String jsonString = EntityUtils.toString(entity).trim();
        System.out.println(jsonString);
        if (jsonString.charAt(0) == '[') {
          JSONArray jsonArray = new JSONArray(jsonString);
          String[] split = jsonArray.toString().split("emotion");
          String[] split1 = split[1].split("exposure");
          String rawEmotions = split1[0];
          String[] split2 = rawEmotions.split("\"");
          Moods key;
          for (int i = 2; i < split2.length; i = i + 2) {
            String rawNum = split2[i + 1];
            if (split2[i].equals("contempt")) {
              key = Moods.CONTEMPT;
            } else if (split2[i].equals("surprise")) {
              key = Moods.SURPRISE;
            } else if (split2[i].equals("happiness")) {
              key = Moods.HAPPINESS;
            } else if (split2[i].equals("neutral")) {
              key = Moods.NEUTRAL;
            } else if (split2[i].equals("sadness")) {
              key = Moods.SADNESS;
            } else if (split2[i].equals("disgust")) {
              key = Moods.DISGUST;
            } else if (split2[i].equals("anger")) {
              key = Moods.ANGER;
            } else {
              key = Moods.FEAR;
            }

            if (rawNum.split(":")[1].contains("}")) {
              emotions.put(key, Double.parseDouble(rawNum.split(":")[1].split("}")[0]));
            } else {
              emotions.put(key, Double.parseDouble(rawNum.split(":")[1].split(",")[0]));
            }

          }

          final Api api = Api.builder()
              .clientId(clientId)
              .clientSecret(clientSecret)
              .build();

          /* Create a request object. */
          final ClientCredentialsGrantRequest request1 = api.clientCredentialsGrant().build();

          /* Use the request object to make the request, either asynchronously (getAsync) or synchronously (get) */
          final SettableFuture<ClientCredentials> responseFuture = request1.getAsync();

          /* Add callbacks to handle success and failure */
          Futures.addCallback(responseFuture, new FutureCallback<ClientCredentials>() {
            @Override
            public void onSuccess(ClientCredentials clientCredentials) {

              /* Set access token on the Api object so that it's used going forward */
              api.setAccessToken(clientCredentials.getAccessToken());

            /* Please note that this flow does not return a refresh token.
              * That's only for the Authorization code flow */
            }

            @Override
            public void onFailure(Throwable throwable) {
            /* An error occurred while getting the access token. This is probably caused by the client id or
              * client secret is invalid. */
            }
          });

          String userId = "spotify";
          String playListId;
          String albumMood;

          List<Moods> emotions = Tracker.getMood();

          for (int i = 0; i < emotions.size(); i++) {
            switch (emotions.get(i).toString().toLowerCase()) {
              case "anger":
                albumMood = "spotify:user:spotify:playlist:5s7Sp5OZsw981I2OkQmyrz";
                playListId = "5s7Sp5OZsw981I2OkQmyrz";
                break;
              case "happiness":
                albumMood = "spotify:user:filtr.ie:playlist:3B31InoJ3c1hE8sIgQiJnT";
                userId = "filtr.ie";
                playListId = "3B31InoJ3c1hE8sIgQiJnT";
                break;
              case "sadness":
                albumMood = "spotify:user:spotify:playlist:37i9dQZF1DX3YSRoSdA634";
                playListId = "37i9dQZF1DX3YSRoSdA634";
                break;
              case "contempt":
                albumMood = "spotify:user:spotify:playlist:37i9dQZF1DX3YSRoSdA634";
                playListId = "37i9dQZF1DX3YSRoSdA634";
                break;
              case "surprise":
                albumMood = "spotify:user:spotify:playlist:37i9dQZF1DXaFIIlnFUS86";
                playListId = "37i9dQZF1DXaFIIlnFUS86";
                break;
              case "fear":
                albumMood = "spotify:user:spotify:playlist:37i9dQZF1DXa2PsvJSPnPf";
                playListId = "37i9dQZF1DXa2PsvJSPnPf";
                break;
              case "neutral":
                albumMood = "spotify:user:spotify:playlist:37i9dQZF1DX2czWA9hqErK";
                playListId = "37i9dQZF1DX2czWA9hqErK";
                break;
              case "disgust":
                albumMood = "spotify:user:spotify:playlist:37i9dQZF1DX2czWA9hqErK";
                playListId = "37i9dQZF1DX2czWA9hqErK ";
                break;
              default:
                albumMood = "spotify:user:filtr.ie:playlist:3B31InoJ3c1hE8sIgQiJnT";
                userId = "filtr.ie";
                playListId = "3B31InoJ3c1hE8sIgQiJnT";
                break;
            }

            final PlaylistRequest request2 = api.getPlaylist(userId, playListId).build();

            try {
              final Playlist playlist = request2.get();

              sb.append("Retrieved playlist: ");
              sb.append(playlist.getName());
              sb.append(System.getProperty("line.separator"));

              sb.append(playlist.getDescription());
              sb.append("\n");

              String numberTracks = "It contains " + playlist.getTracks().getTotal() + " tracks";
              sb.append(numberTracks);
              sb.append(System.getProperty("line.separator"));

              if (emotions.size() == 2) {
                for (int j = 0; j < 3 - i; j++) {
                  songs.add(playlist.getTracks().getItems().get(j).getTrack().getName());
                }
              } else {
                for (int j = 0; j < 5; j++) {
                  songs.add(playlist.getTracks().getItems().get(j).getTrack().getName());
                }
              }

            } catch (Exception e) {
              System.out.println("Something went wrong!" + e.getMessage());
            }
          }

          sb.append("Some recommended songs are: ");
          sb.append(System.getProperty("line.separator"));

          for (String song : songs) {
            sb.append(song);
            sb.append(System.getProperty("line.separator"));
          }

        } else if (jsonString.charAt(0) == '{') {
          JSONObject jsonObject = new JSONObject(jsonString);
        } else {
          System.out.println(jsonString);
        }
      }
    } catch (Exception e) {
      // Display error message.
      System.out.println(e.getMessage());
    }

    return sb.toString();
  }

  public String run() {
    takePicture();
    return useApi();
  }

}




