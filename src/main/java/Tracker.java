import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Tracker {

  private static Map<Moods, Double> emotions = new HashMap<>();

  public static Moods getMood() {
    Double max = 0.0;
    for (Double value : emotions.values()) {
      if (value > max) {
        max = value;
      }
    }

    for (Moods key : emotions.keySet()) {
      if (Objects.equals(emotions.get(key), max)) {
        return key;
      }
    }
    return Moods.ANGER;
  }

  // This sample uses the Apache HTTP client library(org.apache.httpcomponents:httpclient:4.2.4)
// and the org.json library (org.json:json:20170516).

  // **********************************************
  // *** Update or verify the following values. ***
  // **********************************************

  // Replace the subscriptionKey string value with your valid subscription key.
  public static final String subscriptionKey = "93f812339bfc4729bf0fede05dd7c862";

  // Replace or verify the region.
  //
  // You must use the same region in your REST API call as you used to obtain your subscription keys.
  // For example, if you obtained your subscription keys from the westus region, replace
  // "westcentralus" in the URI below with "westus".
  //
  // NOTE: Free trial subscription keys are generated in the westcentralus region, so if you are using
  // a free trial subscription key, you should not need to change this region.
  public static final String uriBase = "https://westeurope.api.cognitive.microsoft.com/face/v1.0/detect";

  public static void main(String[] args) {
    HttpClient httpclient = new DefaultHttpClient();

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
      request.setHeader("Content-Type", "application/json");
      request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

      // Request body.
      StringEntity reqEntity = new StringEntity(
          "{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/c/c3/RH_Louise_Lillian_Gish.jpg\"}");
      request.setEntity(reqEntity);

      // Execute the REST API call and get the response entity.
      HttpResponse response = httpclient.execute(request);
      HttpEntity entity = response.getEntity();

      if (entity != null) {
        // Format and display the JSON response.
        System.out.println("REST Response:\n");

        String jsonString = EntityUtils.toString(entity).trim();
        if (jsonString.charAt(0) == '[') {
          JSONArray jsonArray = new JSONArray(jsonString);
//          System.out.println(jsonArray.toString(2));
          String[] split = jsonArray.toString().split("emotion");
          String[] split1 = split[1].split("exposure");
          String rawEmotions = split1[0];
          String[] split2 = rawEmotions.split("\"");
          for (int i = 2; i < split2.length; i = i + 2) {
            String rawNum = split2[i + 1];
            Moods key;
            if (split2[i].equals("contempt")) {
              key = Moods.CONTEMPT;
            } else if(split2[i].equals("surprise")){
              key = Moods.SURPRISE;
            }else if(split2[i].equals("happiness")){
              key = Moods.HAPPINESS;
            }else if(split2[i].equals("neutral")){
              key = Moods.NEUTRAL;
            }else if(split2[i].equals("sadness")){
              key = Moods.SADNESS;
            }else if(split2[i].equals("disgust")){
              key = Moods.DISGUST;
            }else if(split2[i].equals("anger")){
              key = Moods.ANGER;
            }else {
              key = Moods.FEAR;
            }
            if (rawNum.split(":")[1].contains("}")) {
              emotions.put(key, Double.parseDouble(rawNum.split(":")[1].split("}")[0]));
            } else {
              emotions.put(key, Double.parseDouble(rawNum.split(":")[1].split(",")[0]));
            }

          }

          for (Moods s : emotions.keySet()) {
            System.out.println(s + ": " + emotions.get(s));
          }

          System.out.println(Tracker.getMood());

        } else if (jsonString.charAt(0) == '{') {
          JSONObject jsonObject = new JSONObject(jsonString);
//          System.out.println(jsonObject.toString(2));
        } else {
          System.out.println(jsonString);
        }
      }
    } catch (Exception e) {
      // Display error message.
      System.out.println(e.getMessage());
    }
  }
}


