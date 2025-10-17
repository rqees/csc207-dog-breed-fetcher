package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
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
        if (breed == null || breed.isBlank()) {
            throw new BreedNotFoundException(breed);
        }

        String apiUrl = "https://dog.ceo/api/breed/" + breed.toLowerCase() + "/list";
        Request apiRequest = new Request.Builder().url(apiUrl).get().build();

        Response apiResponse = null;
        try {
            apiResponse = client.newCall(apiRequest).execute();

            if (apiResponse.body() == null) {
                throw new BreedNotFoundException(breed);
            }

            String responseText = apiResponse.body().string();
            JSONObject jsonResponse = new JSONObject(responseText);

            String status = jsonResponse.optString("status");
            if (!apiResponse.isSuccessful() || !"success".equalsIgnoreCase(status)) {
                throw new BreedNotFoundException(breed);
            }

            JSONArray message = jsonResponse.optJSONArray("message");
            List<String> subBreeds = new ArrayList<>();

            if (message != null) {
                for (int i = 0; i < message.length(); i++) {
                    subBreeds.add(message.getString(i));
                }
            }

            return subBreeds;

        } catch (IOException e) {
            throw new BreedNotFoundException(breed);
        } finally {
            if (apiResponse != null) {
                apiResponse.close();
            }
        }
    }
}
