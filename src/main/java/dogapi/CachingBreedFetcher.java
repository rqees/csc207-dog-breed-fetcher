package dogapi;

import java.util.*;

public class CachingBreedFetcher implements BreedFetcher {

    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache;
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
        this.cache = new HashMap<>();
    }

    @Override
    public List<String> getSubBreeds(String breed) {
        if (breed == null) {
            throw new RuntimeException(new BreedNotFoundException(breed));
        }

        breed = breed.toLowerCase(Locale.ROOT);

        if (cache.containsKey(breed)) {
            return cache.get(breed);
        }

        callsMade++;
        try {
            List<String> result = fetcher.getSubBreeds(breed);
            if (result != null && !result.isEmpty()) {
                cache.put(breed, Collections.unmodifiableList(new ArrayList<>(result)));
            }
            return result;
        } catch (BreedNotFoundException e) {
            // Don’t cache failed results
            throw new RuntimeException(e);
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}
