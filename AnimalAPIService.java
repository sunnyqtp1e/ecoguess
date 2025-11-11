import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimalAPIService {
    
    private static final String API_KEY = "MobfoA80OI403mX0k760fg==kGlV8TdOsRnf8D09";
    private static final String API_URL = "https://api.api-ninjas.com/v1/animals?name=";
    
    private Random random;
    
    public AnimalAPIService() {
        this.random = new Random();
    }
    
    public String getRandomAnimalHint(String animalName) {
        try {
            String encodedName = URLEncoder.encode(animalName, "UTF-8");
            String urlString = API_URL + encodedName;
            
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Api-Key", API_KEY);
            connection.setRequestProperty("Accept", "application/json");
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
                );
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                String hint = parseAnimalData(response.toString());
                return hint;
                
            } else {
                System.err.println("API Error: Response code " + responseCode);
                return getFallbackHint();
            }
            
        } catch (Exception e) {
            System.err.println("Error fetching animal data: " + e.getMessage());
            return getFallbackHint();
        }
    }
    
    private String parseAnimalData(String jsonResponse) {
        try {
            List<String> facts = new ArrayList<>();
            
            if (jsonResponse.trim().equals("[]")) {
                return getFallbackHint();
            }
            
            String kingdom = extractValue(jsonResponse, "\"kingdom\"");
            if (kingdom != null) {
                facts.add("Kingdom: " + kingdom);
            }
            
            String classValue = extractValue(jsonResponse, "\"class\"");
            if (classValue != null) {
                facts.add("Class: " + classValue);
            }
            
            String order = extractValue(jsonResponse, "\"order\"");
            if (order != null) {
                facts.add("Order: " + order);
            }
            
            String family = extractValue(jsonResponse, "\"family\"");
            if (family != null) {
                facts.add("Family: " + family);
            }
            
            String locations = extractArrayValues(jsonResponse, "\"locations\"");
            if (locations != null && !locations.isEmpty()) {
                facts.add("Found in: " + locations);
            }
            
            String diet = extractValue(jsonResponse, "\"diet\"");
            if (diet != null) {
                facts.add("Diet: " + diet);
            }
            
            String habitat = extractValue(jsonResponse, "\"habitat\"");
            if (habitat != null) {
                facts.add("Habitat: " + habitat);
            }
            
            String prey = extractValue(jsonResponse, "\"prey\"");
            if (prey != null) {
                facts.add("Preys on: " + prey);
            }
            
            String topSpeed = extractValue(jsonResponse, "\"top_speed\"");
            if (topSpeed != null) {
                facts.add("Top speed: " + topSpeed);
            }
            
            String lifespan = extractValue(jsonResponse, "\"lifespan\"");
            if (lifespan != null) {
                facts.add("Lifespan: " + lifespan);
            }
            
            String weight = extractValue(jsonResponse, "\"weight\"");
            if (weight != null) {
                facts.add("Weight: " + weight);
            }
            
            String height = extractValue(jsonResponse, "\"height\"");
            if (height != null) {
                facts.add("Height: " + height);
            }
            
            String behavior = extractValue(jsonResponse, "\"group_behavior\"");
            if (behavior != null) {
                facts.add("Behavior: " + behavior);
            }
            
            String threat = extractValue(jsonResponse, "\"biggest_threat\"");
            if (threat != null) {
                facts.add("Biggest threat: " + threat);
            }
            
            String feature = extractValue(jsonResponse, "\"most_distinctive_feature\"");
            if (feature != null) {
                facts.add("Distinctive feature: " + feature);
            }
            
            String slogan = extractValue(jsonResponse, "\"slogan\"");
            if (slogan != null) {
                facts.add(slogan);
            }
            
            if (!facts.isEmpty()) {
                int randomIndex = random.nextInt(facts.size());
                return "Hint: " + facts.get(randomIndex);
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing animal data: " + e.getMessage());
        }
        
        return getFallbackHint();
    }
    
    private String extractValue(String json, String key) {
        try {
            int keyIndex = json.indexOf(key);
            if (keyIndex == -1) {
                return null;
            }
            
            int colonIndex = json.indexOf(":", keyIndex);
            if (colonIndex == -1) {
                return null;
            }
            
            int valueStart = colonIndex + 1;
            while (valueStart < json.length() && 
                   (json.charAt(valueStart) == ' ' || json.charAt(valueStart) == '\t')) {
                valueStart++;
            }
            
            if (json.charAt(valueStart) == '"') {
                valueStart++;
                int valueEnd = json.indexOf("\"", valueStart);
                if (valueEnd == -1) {
                    return null;
                }
                return json.substring(valueStart, valueEnd);
            }
            
            int valueEnd = valueStart;
            while (valueEnd < json.length() && 
                   json.charAt(valueEnd) != ',' && 
                   json.charAt(valueEnd) != '}' &&
                   json.charAt(valueEnd) != ']') {
                valueEnd++;
            }
            
            return json.substring(valueStart, valueEnd).trim();
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private String extractArrayValues(String json, String key) {
        try {
            int keyIndex = json.indexOf(key);
            if (keyIndex == -1) {
                return null;
            }
            
            int bracketStart = json.indexOf("[", keyIndex);
            if (bracketStart == -1) {
                return null;
            }
            
            int bracketEnd = json.indexOf("]", bracketStart);
            if (bracketEnd == -1) {
                return null;
            }
            
            String arrayContent = json.substring(bracketStart + 1, bracketEnd);
            
            arrayContent = arrayContent.replace("\"", "");
            String[] items = arrayContent.split(",");
            
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < items.length; i++) {
                result.append(items[i].trim());
                if (i < items.length - 1) {
                    result.append(", ");
                }
            }
            
            return result.toString();
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private String getFallbackHint() {
        return "Hint: Try thinking about endangered animals!";
    }
    
    public boolean isConfigured() {
        return !API_KEY.isEmpty() && API_KEY.length() > 10;
    }
}