import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DuplicatesCheck {

    private Logger logger = MyLogger.getInstance().getLogger();

    private void sendRequestAndCheckDuplicates(String urlAddress, String token, int numberOfPages, List<Picture> pictureList) {
        sendRequestForAllPages(urlAddress, token, numberOfPages, pictureList);
        List<Picture> duplicateList = findDuplicates(pictureList);
        logger.info("Number of duplicates = " + duplicateList.size());
        logDuplicates(duplicateList);

    }

    private void sendRequest(String urlAddress, String token, int page, List<Picture> pictureList) {
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(getUrl(urlAddress, page));
            logger.info("Request URL: " + url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestProperty("X-Auth-Token", token);

            int responseCode = urlConnection.getResponseCode();
            String responseMessage = urlConnection.getResponseMessage();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String responseString = readStream(urlConnection.getInputStream());
                logger.info("Response: " + responseString);
                parsePictureData(responseString, pictureList);
            } else {
                logger.warn("Response code: " + responseCode);
                logger.warn("Response message: " + responseMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private String getUrl(String url, int page) {
        return String.format(url, page);
    }

    private void sendRequestForAllPages(String urlAddress, String token, int numberOfPages, List<Picture> pictureList) {
        for (int page = 0; page < numberOfPages; page++) {
            sendRequest(urlAddress, token, page, pictureList);
        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private void parsePictureData(String jString, List<Picture> pictureList){
        try {
            JSONArray items = new JSONArray(jString);
            if (items != null) {
                for (int i = 0; i < items.length(); i++) {
                    String name = items.getJSONObject(i).getString("name");
                    String uuid = items.getJSONObject(i).getString("uuid");
                    Picture picture = new Picture(name, uuid);
                    pictureList.add(picture);
                }
            } else {
                logger.warn("Empty JSON");
            }
        } catch (JSONException e) {
            logger.error("Unexpected JSON exception: " + e);
        }
    }

    private List<Picture> findDuplicates(List<Picture> pictureList) {
        int numberOfPictures = pictureList.size();
        List<Picture> duplicateList = new ArrayList<>();
        for (int i = 0; i < numberOfPictures - 1; i++) {
            for (int j = i + 1; j < numberOfPictures; j++) {
                if (pictureList.get(i).equals(pictureList.get(j))) {
                    duplicateList.add(pictureList.get(i));
                }
            }
        }
        return duplicateList;
    }

    private void logDuplicates(List<Picture> pictureList) {
        for (Picture picture : pictureList) {
            picture.logIdAndName();
        }
    }
}
