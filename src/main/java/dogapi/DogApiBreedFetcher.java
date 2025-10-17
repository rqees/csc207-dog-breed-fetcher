package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     *
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (breed == null || breed.trim().isEmpty()) {
            throw new BreedNotFoundException(breed);
        }

        String url = "https://dog.ceo/api/breed/" + breed.trim().toLowerCase(java.util.Locale.ROOT) + "/list";
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("User-Agent", "csc207")
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new BreedNotFoundException(breed);
            }

            String jsonText = response.body().string();
            org.json.JSONObject root = new org.json.JSONObject(jsonText);

            if (!"success".equalsIgnoreCase(root.optString("status", ""))) {
                throw new BreedNotFoundException(breed);
            }

            org.json.JSONArray arr = root.getJSONArray("message");
            java.util.List<String> result = new java.util.ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                result.add(arr.getString(i));
            }
            return result;

        } catch (java.io.IOException | org.json.JSONException e) {
            throw new BreedNotFoundException(breed);
        }
    }
}